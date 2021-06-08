package com.bykov.project.conference.command;

import com.bykov.project.conference.services.ServiceReport;
import com.bykov.project.conference.services.ServiceUser;

import javax.servlet.http.HttpServletRequest;
import java.util.Locale;
import java.util.Objects;

public class CommandDeleteReport implements Command {
    private final ServiceUser serviceUser;
    private final ServiceReport serviceReport;

    public CommandDeleteReport(ServiceUser serviceUser, ServiceReport serviceReport) {
        this.serviceUser = serviceUser;
        this.serviceReport = serviceReport;
    }

    @Override
    public String execute(HttpServletRequest request) {
        Locale locale = (Locale) request.getSession().getAttribute("locale");

        if(Objects.isNull(request.getParameter("reportIdForRemoving"))) {
            return "redirect:/" + request.getSession().getAttribute("role") + PAGES.getString("path.catalog");
        }

        long id = Long.parseLong(request.getParameter("reportIdForRemoving"));
        String conferenceIdParameter = "";

        serviceReport.deleteReport(id);
        request.setAttribute("possibleSpeakers", serviceUser.getAllUsers(locale.toString()));

        if (request.getParameter("uri").contains("editconference")) {
            conferenceIdParameter = "&conference=" + request.getParameter("conference");
        }
        return "redirect:" + request.getParameter("uri")
                + "?recordsPerPage=" + request.getParameter("recordsPerPage")
                + "&currentPage=" + request.getParameter("currentPage")
                + "&scrollPosition=" + request.getParameter("scrollPosition")
                + conferenceIdParameter;
    }
}
