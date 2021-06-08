package com.bykov.project.conference.command;

import com.bykov.project.conference.dao.entity.User;
import com.bykov.project.conference.dto.DTOConference;
import com.bykov.project.conference.dto.DTOReport;
import com.bykov.project.conference.dto.ResourceEnum;
import com.bykov.project.conference.exceptions.CommandFailedException;
import com.bykov.project.conference.services.ServiceConference;
import com.bykov.project.conference.services.ServiceUser;
import com.bykov.project.conference.utils.UtilsBundle;
import com.bykov.project.conference.utils.UtilsValidation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.*;

public class CommandCreateConference implements Command {
    private static final Logger LOG = LogManager.getLogger(CommandCreateConference.class);
    private final ServiceUser serviceUser;
    private final ServiceConference serviceConference;

    public CommandCreateConference(ServiceUser serviceUser, ServiceConference serviceConference) {
        this.serviceUser = serviceUser;
        this.serviceConference = serviceConference;
    }

    @Override
    public String execute(HttpServletRequest request) {
        String pageToReturn = PAGES.getString("page.conference");
        List<DTOReport> reports = new ArrayList<>();
        DTOConference conference = new DTOConference();
        Locale locale = UtilsBundle.getLocale(request);
        ResourceBundle messages = UtilsBundle.getBundle(request, ResourceEnum.MESSAGE);
        ResourceBundle regexpBundle = UtilsBundle.getBundle(request, ResourceEnum.REGEXP);

        request.setAttribute("possibleSpeakers", serviceUser.getAllUsers(locale.toString()));

        if (UtilsValidation.nullConferenceParametersPresent(request)) {
            return pageToReturn;
        }
        if (isConferenceNameNotInEnglish(request, regexpBundle, messages)) {
            return pageToReturn;
        }
        if (isConferenceLocationNotInEnglish(request, regexpBundle, messages)) {
            return pageToReturn;
        }
        LocalDateTime conferenceDateTime = LocalDateTime.parse(request.getParameter("conference-date-time"));
        if (isDateInCorrect(conferenceDateTime, request, messages)) {
            return pageToReturn;
        }
        fillInDetails(conferenceDateTime, conference, request);

        List<String> reportsTopicEn = Arrays.asList(request.getParameterValues("report-name-en"));
        List<String> reportsTopicUa = Arrays.asList(request.getParameterValues("report-name-ua"));
        List<String> reportsTime = Arrays.asList(request.getParameterValues("report-date-time"));
        List<String> reportsSpeaker = Arrays.asList(request.getParameterValues("report-speaker"));

        for (int i = 0; i < reportsTopicEn.size(); i++) {
            LocalDateTime reportDateTime = LocalDateTime.parse(reportsTime.get(i));
            long speakerId = Long.parseLong(reportsSpeaker.get(i));

            if (reportDateTime.compareTo(conferenceDateTime) < 0) {
                request.setAttribute("erlierThanConference", messages.getString("info.message.earlier.than.conference"));
                return pageToReturn;
            }
            ifUserChangeRole(speakerId);

            reports.add(new DTOReport.Builder()
                    .setTopicEn(reportsTopicEn.get(i))
                    .setTopicUa(reportsTopicUa.get(i))
                    .setDateTime(reportDateTime)
                    .setSpeakerId(speakerId)
                    .build());
        }
        conference.setReports(reports);

        if (!serviceConference.addConference(conference)) {
            LOG.error("Can't save new conference");
            throw new CommandFailedException(messages.getString("error.message.not.saved"));
        }
        pageToReturn = "redirect:/" + request.getSession().getAttribute("role") + PAGES.getString("path.catalog");
        return pageToReturn;
    }

    private void ifUserChangeRole(long speakerId) {
        if (User.Role.USER == serviceUser.getUserRole(speakerId)) {
            serviceUser.changeRole(speakerId, User.Role.SPEAKER);
        }
    }

    private boolean isConferenceNameNotInEnglish(HttpServletRequest request, ResourceBundle regexpBundle, ResourceBundle bundle) {
        if (UtilsValidation.inValid(request.getParameter("conference-name-en"), regexpBundle.getString("regexp.text.in.english"))) {
            request.setAttribute("conferenceNameEnNotInEnglish", bundle.getString("info.not.in.english"));
            return true;
        }
        return false;
    }

    private boolean isConferenceLocationNotInEnglish(HttpServletRequest request, ResourceBundle regexpBundle, ResourceBundle bundle) {
        if (UtilsValidation.inValid(request.getParameter("conference-location-en"), regexpBundle.getString("regexp.text.in.english"))) {
            request.setAttribute("conferenceLocationEnNotInEnglish", bundle.getString("info.not.in.english"));
            return true;
        }
        return false;
    }

    private void fillInDetails(LocalDateTime conferenceDateTime, DTOConference conference, HttpServletRequest request) {
        conference.setTopicEn(request.getParameter("conference-name-en"));
        conference.setTopicUa(request.getParameter("conference-name-ua"));
        conference.setLocationEn(request.getParameter("conference-location-en"));
        conference.setLocationUa(request.getParameter("conference-location-ua"));
        conference.setDateTime(conferenceDateTime);
    }

    private boolean isDateInCorrect(LocalDateTime conferenceDateTime, HttpServletRequest request, ResourceBundle bundle) {
        if (conferenceDateTime.compareTo(LocalDateTime.now()) < 0) {
            request.setAttribute("wrongDate", bundle.getString("info.message.early.date"));
            return true;
        }
        return false;
    }
}

