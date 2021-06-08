package com.bykov.project.conference.listeners;


import javax.servlet.http.HttpSessionAttributeListener;
import javax.servlet.http.HttpSessionBindingEvent;
import java.util.Locale;

public class ListenerLanguage implements HttpSessionAttributeListener {
    @Override
    public void attributeAdded(HttpSessionBindingEvent httpSessionBindingEvent) {

    }

    @Override
    public void attributeRemoved(HttpSessionBindingEvent httpSessionBindingEvent) {

    }

    @Override
    public void attributeReplaced(HttpSessionBindingEvent event) {
        if (event.getName().equals("lang")) {
            String internalizationParameters = String.valueOf(event.getSession().getAttribute("lang"));
            event.getSession().setAttribute("locale",
                    new Locale(internalizationParameters.substring(0, 2), internalizationParameters.substring(3, 5)));
        }
    }
}