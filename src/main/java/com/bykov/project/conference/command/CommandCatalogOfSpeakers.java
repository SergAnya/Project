package com.bykov.project.conference.command;

import com.bykov.project.conference.services.ServiceSpeaker;
import com.bykov.project.conference.utils.UtilsBundle;
import com.bykov.project.conference.utils.UtilsPagination;
import com.bykov.project.conference.dto.ResourceEnum;
import com.bykov.project.conference.dto.DTOSpeaker;
import com.bykov.project.conference.services.ServiceUser;

import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.stream.Collectors;

public class CommandCatalogOfSpeakers implements Command {
    private final ServiceUser serviceUser;
    private final ServiceSpeaker serviceSpeaker;

    public CommandCatalogOfSpeakers(ServiceUser serviceUser, ServiceSpeaker serviceSpeaker) {
        this.serviceUser = serviceUser;
        this.serviceSpeaker = serviceSpeaker;
    }

    @Override
    public String execute(HttpServletRequest request) {
        String pageToReturn = PAGES.getString("page.speakers");
        Locale locale = UtilsBundle.getLocale(request);
        ResourceBundle messages = UtilsBundle.getBundle(request, ResourceEnum.MESSAGE);
        long userId = serviceUser.getUserId((String) request.getSession().getAttribute("email"),locale.toString());

        fillInDetails(request, locale.toString());

        if (Objects.isNull(request.getParameter("speakerId"))) {
            return pageToReturn;
        }

        long speakerId = Long.parseLong(request.getParameter("speakerId"));
        if(canSpeakerVote(speakerId, userId, request, messages)) {
            return pageToReturn;
        }

        serviceUser.vote(userId, speakerId, Integer.parseInt(request.getParameter("rating" + speakerId)));

        pageToReturn = "redirect:/"
                + request.getSession().getAttribute("role")
                + PAGES.getString("path.speakers")
                + "?recordsPerPage=" + request.getParameter("recordsPerPage")
                + "&currentPage=" + request.getParameter("currentPage");
        return pageToReturn;
    }

    private boolean canSpeakerVote(long speakerId, long userId, HttpServletRequest request, ResourceBundle bundle) {
        if(speakerId==userId) {
            request.setAttribute("voteForYourself" + speakerId, bundle.getString("info.message.cant.vote.for.yourself"));
            return true;
        }
        return false;
    }

    private void fillInDetails(HttpServletRequest request, String locale) {
        Map<String, Integer> paginationParameters = UtilsPagination.calcPaginationParameters(request, serviceSpeaker.getSpeakersAmount());
        List<DTOSpeaker> speakers = serviceSpeaker.getPaginatedList(paginationParameters.get("begin"),
                paginationParameters.get("recordsPerPage"),locale);
        speakers.sort(Comparator.comparingDouble(DTOSpeaker::getRating).reversed());

        request.setAttribute("paginationParameters", paginationParameters);
        request.setAttribute("speakers", speakers);
        request.setAttribute("ratings", speakers.stream().collect(Collectors.toMap(DTOSpeaker::getId, DTOSpeaker::getRating)));
    }
}
