package com.bykov.project.conference.command;

import com.bykov.project.conference.utils.UtilsBundle;
import com.bykov.project.conference.dto.ResourceEnum;
import com.bykov.project.conference.services.ServiceReport;
import com.bykov.project.conference.services.ServiceUser;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Locale;
import java.util.Objects;
import java.util.ResourceBundle;

public class CommandUnsubscribe implements Command {
    private final ServiceUser serviceUser;
    private final ServiceReport serviceReport;

    public CommandUnsubscribe(ServiceUser serviceUser, ServiceReport serviceReport) {
        this.serviceUser = serviceUser;
        this.serviceReport = serviceReport;
    }

    @Override
    public String execute(HttpServletRequest request) {
        HttpSession session = request.getSession();
        Locale locale = UtilsBundle.getLocale(request);
        ResourceBundle messages = UtilsBundle.getBundle(request, ResourceEnum.MESSAGE);
        long userId = serviceUser.getUserId((String) session.getAttribute("email"),locale.toString());

        if (Objects.isNull(request.getParameter("reportForUnsubscription"))) {
            return "redirect:/" + request.getSession().getAttribute("role") + PAGES.getString("path.catalog");
        }

        long reportId = Long.parseLong(request.getParameter("reportForUnsubscription"));

        if (serviceReport.isSubscribed(userId, reportId)) {
            serviceReport.unsubscribeUserFromReport(userId, reportId);
            request.setAttribute(String.valueOf(reportId), "unsubscribed");
            request.setAttribute("subscriptions", serviceUser.getUserSubscriptionsIds(userId));
        } else {
            request.setAttribute("notSubscribed", messages.getString("info.message.not.subscribed"));
        }
        return "redirect:/"
                + session.getAttribute("role")
                + PAGES.getString("path.catalog")
                + "?recordsPerPage=" + request.getParameter("recordsPerPage")
                + "&currentPage=" + request.getParameter("currentPage")
                + "&scrollPosition=" + request.getParameter("scrollPosition")
                + "&sortType=" + (request.getParameter("sortType").isEmpty()?"all":request.getParameter("sortType"));


    }
}
