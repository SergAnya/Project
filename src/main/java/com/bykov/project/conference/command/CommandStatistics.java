package com.bykov.project.conference.command;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import com.bykov.project.conference.utils.UtilsBundle;
import com.bykov.project.conference.utils.UtilsFilterSort;
import com.bykov.project.conference.utils.UtilsPagination;
import com.bykov.project.conference.services.ServiceConference;
import com.bykov.project.conference.services.ServiceReport;
import com.bykov.project.conference.services.ServiceUser;
import com.bykov.project.conference.dto.FilterSortType;

import javax.servlet.http.HttpServletRequest;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

public class CommandStatistics implements Command {
    private static final Logger LOG = LogManager.getLogger(CommandStatistics.class);
    private final ServiceUser serviceUser;
    private final ServiceReport serviceReport;
    private final ServiceConference serviceConference;

    public CommandStatistics(ServiceUser serviceUser, ServiceReport serviceReport, ServiceConference serviceConference) {
        this.serviceUser = serviceUser;
        this.serviceReport = serviceReport;
        this.serviceConference = serviceConference;
    }

    @Override
    public String execute(HttpServletRequest request) {
        LOG.debug("type" + request.getParameter("sortType"));
        Locale locale = UtilsBundle.getLocale(request);
        FilterSortType filterSortType = UtilsFilterSort.setFilterSortType(request);
        Map<String, Integer> paginationParameters = UtilsPagination
                .calcPaginationParameters(request, serviceConference.getConferencesAmount(filterSortType));

        request.setAttribute("paginationParameters", paginationParameters);
        request.setAttribute("conferences", serviceConference.getSortedPaginatedConferences(filterSortType, paginationParameters.get("begin"),
                paginationParameters.get("recordsPerPage"), locale.toString()));
        request.setAttribute("scrollPosition", request.getParameter("scrollPosition"));
        request.setAttribute("sortType", request.getParameter("sortType"));

        if (!Objects.isNull(request.getParameter("submitted"))) {
            setVisitorsAmountForAllReports(request);
        }

        long userId = serviceUser.getUserId((String) request.getSession().getAttribute("email"), locale.toString());

        request.setAttribute("userId", userId);
        request.setAttribute("subscriptions", serviceUser.getUserSubscriptionsIds(userId));
        request.setAttribute("conferences", serviceConference.getSortedPaginatedConferences(filterSortType, paginationParameters.get("begin"),
                paginationParameters.get("recordsPerPage"), locale.toString()));
        return PAGES.getString("page.statistic");
    }



    private void setVisitorsAmountForAllReports(HttpServletRequest request) {
        for (String reportId : request.getParameterValues("report-id")) {
            serviceReport.setVisitorsAmount(Long.parseLong(reportId), Integer.parseInt(request.getParameter("visitors-amount" + reportId)));
        }
    }
}

