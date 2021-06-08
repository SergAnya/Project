package com.bykov.project.conference.utils;

import com.bykov.project.conference.dto.ResourceEnum;

import javax.servlet.http.HttpServletRequest;
import java.util.Locale;
import java.util.ResourceBundle;

public final class UtilsBundle {
    private UtilsBundle() {}

    public static ResourceBundle getBundle(HttpServletRequest request, ResourceEnum resourceEnum) {
        Locale locale = getLocale(request);
        return ResourceBundle.getBundle(resourceEnum.getBundleName(), locale);
    }

    public static Locale getLocale(HttpServletRequest request) {
        return (Locale) request.getSession().getAttribute("locale");
    }
}
