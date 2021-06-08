package com.bykov.project.conference.utils;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import java.util.regex.Pattern;

public final class UtilsValidation {
    private final static Logger LOGGER = LogManager.getLogger(UtilsValidation.class);

    public static boolean inValid(String data, String regexp) {
        Pattern pattern = Pattern.compile(regexp);
        LOGGER.debug("pattern in validation: " + pattern);
        LOGGER.debug("data for validation: " + data);

        return !pattern.matcher(data).find();
    }

    public static boolean nullConferenceParametersPresent(HttpServletRequest request) {
        return isNullOrEmpty(request.getParameter("conference-name-en"))
                || isNullOrEmpty(request.getParameter("conference-name-ua"))
                || isNullOrEmpty(request.getParameter("conference-location-en"))
                || isNullOrEmpty(request.getParameter("conference-location-ua"))
                || isNullOrEmpty(request.getParameter("conference-date-time"));
    }

    public static boolean isNullOrEmpty(String str) {
        return str == null || str.isEmpty();
    }

    private UtilsValidation(){}
}


