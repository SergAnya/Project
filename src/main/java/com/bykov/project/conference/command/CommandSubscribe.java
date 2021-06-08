package com.bykov.project.conference.command;

import com.bykov.project.conference.utils.UtilsBundle;
import com.bykov.project.conference.services.ServiceReport;
import com.bykov.project.conference.services.ServiceUser;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Locale;
import java.util.Objects;


public class CommandSubscribe implements Command {
    private final ServiceUser serviceUser;
    private final ServiceReport serviceReport;

    public CommandSubscribe(ServiceUser serviceUser, ServiceReport serviceReport) {
        this.serviceUser = serviceUser;
        this.serviceReport = serviceReport;
    }

    @Override
    public String execute(HttpServletRequest request) {
        Locale locale = UtilsBundle.getLocale(request);
        HttpSession session = request.getSession();
        long userId = serviceUser.getUserId((String) session.getAttribute("email"), locale.toString());

        if (Objects.isNull(request.getParameter("reportForSubscription"))) {
            return "redirect:/" + request.getSession().getAttribute("role") + PAGES.getString("path.catalog");
        }

        long reportId = Long.parseLong(request.getParameter("reportForSubscription"));

        serviceReport.subscribeUserOnReport(userId, reportId);
        request.setAttribute(String.valueOf(reportId), "subscribed");
        request.setAttribute("subscriptions", serviceUser.getUserSubscriptionsIds(userId));
        return "redirect:/"
                + session.getAttribute("role")
                + PAGES.getString("path.catalog")
                + "?recordsPerPage=" + request.getParameter("recordsPerPage")
                + "&currentPage=" + request.getParameter("currentPage")
                + "&scrollPosition=" + request.getParameter("scrollPosition")
                + "&sortType=" + (request.getParameter("sortType").isEmpty()?"all":request.getParameter("sortType"));

    }
}
