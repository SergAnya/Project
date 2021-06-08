package com.bykov.project.conference.command;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import com.bykov.project.conference.utils.UtilsBundle;
import com.bykov.project.conference.utils.UtilsValidation;
import com.bykov.project.conference.dto.ResourceEnum;
import com.bykov.project.conference.dto.DTOReport;
import com.bykov.project.conference.dao.entity.Conference;
import com.bykov.project.conference.services.ServiceReport;
import com.bykov.project.conference.services.ServiceUser;
import com.bykov.project.conference.services.ServiceConference;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.Locale;
import java.util.Objects;
import java.util.ResourceBundle;

import static com.bykov.project.conference.utils.UtilsValidation.isNullOrEmpty;

public class CommandAddReport implements Command {
    private static final Logger LOG = LogManager.getLogger(CommandAddReport.class);
    private final ServiceUser serviceUser;
    private final ServiceReport serviceReport;
    private final ServiceConference serviceConference;

    public CommandAddReport(ServiceUser serviceUser, ServiceReport serviceReport, ServiceConference serviceConference) {
        this.serviceUser = serviceUser;
        this.serviceReport = serviceReport;
        this.serviceConference = serviceConference;
    }

    @Override
    public String execute(HttpServletRequest request) {
        String pageToReturn = PAGES.getString("page.add.report");
        Locale locale = UtilsBundle.getLocale(request);
        ResourceBundle messages = UtilsBundle.getBundle(request, ResourceEnum.MESSAGE);
        ResourceBundle regexpBundle = UtilsBundle.getBundle(request, ResourceEnum.REGEXP);

        if (Objects.isNull(request.getParameter("conferenceId"))) {
            pageToReturn = "redirect:/" + request.getSession().getAttribute("role") + PAGES.getString("path.catalog");
            return pageToReturn;
        }
        Conference conference = serviceConference.getConferenceById(Long.parseLong(request.getParameter("conferenceId")), locale.toString());

        fillInDetails(request, locale.toString());

        if (Objects.isNull(request.getParameter("submitted"))) {
            LOG.trace("Not Submitted");
            return pageToReturn;
        }
        if (isReportNotNew(request)) {
            return pageToReturn;
        }
        if (isNotCorrectReportTime(request, conference, messages)) {
            return pageToReturn;
        }
        if (isReportNameNotEnglish(request, regexpBundle, messages)) {
            return pageToReturn;
        }
        serviceReport.addNewReportToConference(conference.getId(), new DTOReport.Builder()
                .setTopicEn(request.getParameter("report-name-en-new"))
                .setTopicUa(request.getParameter("report-name-ua-new"))
                .setDateTime(LocalDateTime.parse(request.getParameter("report-date-time-new")))
                .setSpeakerId(Long.parseLong(request.getParameter("report-speaker-new")))
                .build());

        pageToReturn = "redirect:/"
                + request.getSession().getAttribute("role")
                + PAGES.getString("path.catalog")
                + "?recordsPerPage=" + request.getParameter("recordsPerPage")
                + "&scrollPosition=" + request.getParameter("scrollPosition")
                + "&currentPage=" + request.getParameter("currentPage");
        return pageToReturn;
    }

     private boolean isReportNotNew(HttpServletRequest request) {
        return isNullOrEmpty(request.getParameter("report-name-en-new")) ||
                isNullOrEmpty(request.getParameter("report-name-ua-new")) ||
                isNullOrEmpty(request.getParameter("report-date-time-new")) ||
                isNullOrEmpty(request.getParameter("report-speaker-new"));
     }

     private boolean isNotCorrectReportTime(HttpServletRequest request, Conference conference, ResourceBundle bundle) {
         if (LocalDateTime.parse(request.getParameter("report-date-time-new")).compareTo(conference.getDateTime()) < 0) {
             request.setAttribute("report-name-en-new", request.getParameter("report-name-en-new"));
             request.setAttribute("report-name-ua-new", request.getParameter("report-name-ua-new"));
             request.setAttribute("report-date-time-new", request.getParameter("report-date-time-new"));
             request.setAttribute("earlierThanConference", bundle.getString("info.message.earlier.than.conference"));
             return true;
         }
         return false;
     }

     private boolean isReportNameNotEnglish(HttpServletRequest request, ResourceBundle regexpBundle, ResourceBundle bundle) {
         if (UtilsValidation.inValid(request.getParameter("report-name-en-new"), regexpBundle.getString("regexp.text.in.english"))) {
             request.setAttribute("notInEnglish", bundle.getString("info.not.in.english"));
             return true;
         }
         return false;
     }

     private void fillInDetails(HttpServletRequest request, String locale) {
         request.setAttribute("possibleSpeakers", serviceUser.getAllUsers(locale));
         request.setAttribute("conferenceId", request.getParameter("conferenceId"));
         request.setAttribute("recordsPerPage", request.getParameter("recordsPerPage"));
         request.setAttribute("currentPage", request.getParameter("currentPage"));
         request.setAttribute("scrollPosition", request.getParameter("scrollPosition"));
     }
}
