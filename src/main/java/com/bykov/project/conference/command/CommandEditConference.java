package com.bykov.project.conference.command;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import com.bykov.project.conference.utils.UtilsBundle;
import com.bykov.project.conference.utils.UtilsValidation;
import com.bykov.project.conference.dto.ResourceEnum;
import com.bykov.project.conference.dto.DTOConference;
import com.bykov.project.conference.dto.DTOReport;
import com.bykov.project.conference.dao.entity.User;
import com.bykov.project.conference.services.ServiceConference;
import com.bykov.project.conference.services.ServiceReport;
import com.bykov.project.conference.services.ServiceUser;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.*;

public class CommandEditConference implements Command {
    private final static Logger LOG = LogManager.getLogger(CommandEditConference.class);
    private final ServiceUser serviceUser;
    private final ServiceReport serviceReport;
    private final ServiceConference serviceConference;

    public CommandEditConference(ServiceUser serviceUser, ServiceReport serviceReport, ServiceConference serviceConference) {
        this.serviceUser = serviceUser;
        this.serviceReport = serviceReport;
        this.serviceConference = serviceConference;
    }

    @Override
    public String execute(HttpServletRequest request) {
        Locale locale = UtilsBundle.getLocale(request);
        ResourceBundle messages = UtilsBundle.getBundle(request, ResourceEnum.MESSAGE);
        ResourceBundle regexpBundle = UtilsBundle.getBundle(request, ResourceEnum.REGEXP);

        if(Objects.isNull(request.getParameter("conference"))) {
            return "redirect:/" + request.getSession().getAttribute("role") + PAGES.getString("path.catalog");
        }
        long conferenceId = Long.parseLong(request.getParameter("conference"));
        DTOConference conference =
                new DTOConference(serviceConference.getConferenceById(conferenceId, "en_US"),
                        serviceConference.getConferenceById(conferenceId, "uk_UA"));

        request.setAttribute("possibleSpeakers", serviceUser.getAllUsers(locale.toString()));
        request.setAttribute("conference", conference);
        request.setAttribute("currentPage", request.getParameter("currentPage"));
        request.setAttribute("recordsPerPage", request.getParameter("recordsPerPage"));


        if (Objects.isNull(request.getParameter("submitted"))) {
            LOG.trace("Not Submitted");
            return PAGES.getString("page.edit.conference");
        }

        String[] reportIds = request.getParameterValues("report-id");

        if (nullReportParametersPresent(request, reportIds) || UtilsValidation.nullConferenceParametersPresent(request)) {
            LOG.trace("Some parameter is null");
            return PAGES.getString("page.edit.conference");
        }
        if (UtilsValidation.inValid(request.getParameter("conference-name-en"), regexpBundle.getString("regexp.text.in.english"))) {
            request.setAttribute("conferenceNameEnNotInEnglish", messages.getString("info.not.in.english"));
            return PAGES.getString("page.edit.conference");
        }
        if (UtilsValidation.inValid(request.getParameter("conference-location-en"), regexpBundle.getString("regexp.text.in.english"))) {
            request.setAttribute("conferenceLocationEnNotInEnglish", messages.getString("info.not.in.english"));
            return PAGES.getString("page.edit.conference");
        }
        LocalDateTime conferenceDateTime = LocalDateTime.parse(request.getParameter("conference-date-time"));
        LOG.trace("Checking date");

        if (conferenceDateTime.compareTo(LocalDateTime.now()) < 0) {
            request.setAttribute("wrongDate", messages.getString("info.message.early.date"));
            return PAGES.getString("page.edit.conference");
        }
        conference.setTopicEn(request.getParameter("conference-name-en"));
        conference.setTopicUa(request.getParameter("conference-name-ua"));
        conference.setDateTime(LocalDateTime.parse(request.getParameter("conference-date-time")));
        conference.setLocationEn(request.getParameter("conference-location-en"));
        conference.setLocationEn(request.getParameter("conference-location-ua"));

        List<DTOReport> reports = conference.getReports();
        for (int i = 0; i < reports.size(); i++) {
            long reportId = reports.get(i).getId();
            long speakerId = Long.parseLong(request.getParameter("report-speaker" + reportId));

            LocalDateTime reportDateTime = LocalDateTime.parse(request.getParameter("report-date-time" + reportId));

            if (reportDateTime.compareTo(conferenceDateTime) < 0) {
                request.setAttribute("conference", conference);
                request.setAttribute("earlierThanConference" + reportId, messages.getString("info.message.earlier.than.conference"));
                return PAGES.getString("page.edit.conference");
            }

            ifUserChangeRole(speakerId);
            reports.set(i, buildReportDTOFromRequest(request, reportId, reportDateTime));
            LOG.debug(Arrays.toString(reports.toArray()));
        }

        request.setAttribute("conference", conference);

        if (!nullReportParametersPresent(request, "0")) {
            LocalDateTime newReportDateTime = LocalDateTime.parse(request.getParameter("report-date-time0"));

            if (newReportDateTime.compareTo(conferenceDateTime) < 0) {
                request.setAttribute("report-name-en0", request.getParameter("report-name-en0"));
                request.setAttribute("report-name-ua0", request.getParameter("report-name-ua0"));
                request.setAttribute("report-date-time0", request.getParameter("report-date-time0"));
                request.setAttribute("earlierThanConference0", messages.getString("info.message.earlier.than.conference"));
                return PAGES.getString("page.edit.conference");
            }
            serviceReport.addNewReportToConference(conference.getId(), buildReportDTOFromRequest(request, 0, newReportDateTime));
        }
        serviceConference.update(conference);

        return "redirect:/"
                + request.getSession().getAttribute("role")
                + PAGES.getString("path.catalog")
                + "?recordsPerPage=" + request.getParameter("recordsPerPage")
                + "&currentPage=" + request.getParameter("currentPage");
    }


    private DTOReport buildReportDTOFromRequest(HttpServletRequest request, long reportId, LocalDateTime reportDateTime) {
        long speakerId = Long.parseLong(request.getParameter("report-speaker" + reportId));

        return new DTOReport.Builder()
                .setId(reportId)
                .setTopicEn(request.getParameter("report-name-en" + reportId))
                .setTopicUa(request.getParameter("report-name-ua" + reportId))
                .setDateTime(reportDateTime)
                .setSpeakerId(Long.parseLong(request.getParameter("report-speaker" + reportId)))
                .setSpeakerNameEn(serviceUser.getNameById(speakerId, "en_US"))
                .setSpeakerNameUa(serviceUser.getNameById(speakerId, "uk_UA"))
                .setSpeakerSurnameEn(serviceUser.getSurnameById(Long.parseLong(request.getParameter("report-speaker" + reportId)), "en_US"))
                .setSpeakerSurnameUa(serviceUser.getSurnameById(Long.parseLong(request.getParameter("report-speaker" + reportId)), "uk_UA"))
                .build();
    }

    private void ifUserChangeRole(long speakerId) {
        if (User.Role.USER == serviceUser.getUserRole(speakerId)) {
            serviceUser.changeRole(speakerId, User.Role.SPEAKER);
        }
    }

    private boolean nullReportParametersPresent(HttpServletRequest request, String... reportIds) {
        for (String reportId : reportIds) {
            if (UtilsValidation.isNullOrEmpty(request.getParameter("report-name-en" + reportId))
                    || UtilsValidation.isNullOrEmpty(request.getParameter("report-name-ua" + reportId))
                    || UtilsValidation.isNullOrEmpty(request.getParameter("report-date-time" + reportId))
                    || UtilsValidation.isNullOrEmpty(request.getParameter("report-speaker" + reportId))) {
                return true;
            }
        }
        return false;
    }

}

