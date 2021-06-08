package com.bykov.project.conference.command;


import com.bykov.project.conference.dao.entity.User;
import com.bykov.project.conference.dto.FilterSortType;
import com.bykov.project.conference.services.ServiceConference;
import com.bykov.project.conference.services.ServiceUser;
import com.bykov.project.conference.utils.UtilsBundle;
import com.bykov.project.conference.utils.UtilsFilterSort;
import com.bykov.project.conference.utils.UtilsPagination;

import javax.servlet.http.HttpServletRequest;
import java.util.Locale;
import java.util.Map;

public class CommandCatalog implements Command {
    private final ServiceUser serviceUser;
    private final ServiceConference serviceConference;

    public CommandCatalog(ServiceUser serviceUser, ServiceConference serviceConference) {
        this.serviceUser = serviceUser;
        this.serviceConference = serviceConference;
    }

    @Override
    public String execute(HttpServletRequest request) {
        Locale locale = UtilsBundle.getLocale(request);

        fillInConferenceDetails(request, locale.toString());

        isGuest(request, locale.toString());

        return PAGES.getString("page.catalog");
    }


    private void isGuest(HttpServletRequest request, String locale) {
        if (!request.getSession().getAttribute("role").equals(User.Role.GUEST.getStringRole())) {
            long userId = serviceUser.getUserId((String) request.getSession().getAttribute("email"),locale);
            request.setAttribute("userId", userId);
            request.setAttribute("subscriptions", serviceUser.getUserSubscriptionsIds(userId));
        }
    }

    private void fillInConferenceDetails(HttpServletRequest request, String locale) {
        FilterSortType filterSortType = UtilsFilterSort.setFilterSortType(request);
        Map<String, Integer> paginationParameters = UtilsPagination
                .calcPaginationParameters(request, serviceConference.getConferencesAmount(filterSortType));

        request.setAttribute("conferences", serviceConference.getSortedPaginatedConferences(filterSortType, paginationParameters.get("begin"),
                paginationParameters.get("recordsPerPage"), locale));
        request.setAttribute("paginationParameters", paginationParameters);

        request.setAttribute("sortType", request.getParameter("sortType"));
        request.setAttribute("scrollPosition", request.getParameter("scrollPosition"));
    }
}
