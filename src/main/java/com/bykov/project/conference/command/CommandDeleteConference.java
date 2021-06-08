package com.bykov.project.conference.command;

import com.bykov.project.conference.dto.ResourceEnum;
import com.bykov.project.conference.exceptions.CommandFailedException;
import com.bykov.project.conference.services.ServiceConference;
import com.bykov.project.conference.utils.UtilsBundle;

import javax.servlet.http.HttpServletRequest;
import java.util.Objects;
import java.util.ResourceBundle;

public class CommandDeleteConference implements Command {
    private final ServiceConference serviceConference;

    public CommandDeleteConference(ServiceConference serviceConference) {
        this.serviceConference = serviceConference;
    }

    @Override
    public String execute(HttpServletRequest request) {
        ResourceBundle messages = UtilsBundle.getBundle(request, ResourceEnum.MESSAGE);

        if (Objects.isNull(request.getParameter("deleteConference"))) {
            return "redirect:/" + request.getSession().getAttribute("role") + PAGES.getString("path.catalog");
        }
        if (!serviceConference.deleteConference(Long.valueOf(request.getParameter("deleteConference")))) {
            throw new CommandFailedException(messages.getString("error.message.conference.not.deleted"));
        }
        return "redirect:/"
                + request.getSession().getAttribute("role")
                + PAGES.getString("path.catalog")
                + "?recordsPerPage=" + request.getParameter("recordsPerPage")
                + "&currentPage=" + request.getParameter("currentPage");

    }
}
