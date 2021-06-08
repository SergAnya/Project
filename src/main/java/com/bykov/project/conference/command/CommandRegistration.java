package com.bykov.project.conference.command;

import com.bykov.project.conference.dao.entity.User;
import com.bykov.project.conference.dto.DTOUser;
import com.bykov.project.conference.dto.ResourceEnum;
import com.bykov.project.conference.services.ServiceUser;
import com.bykov.project.conference.utils.UtilsBundle;
import com.bykov.project.conference.utils.UtilsSecurity;
import com.bykov.project.conference.utils.UtilsTransliteration;
import com.bykov.project.conference.utils.UtilsValidation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

public class CommandRegistration implements Command {
    private final static Logger LOG = LogManager.getLogger(CommandRegistration.class);
    private final ServiceUser serviceUser;

    public CommandRegistration(ServiceUser serviceUser) {
        this.serviceUser = serviceUser;
    }

    @Override
    public String execute(HttpServletRequest request) {
        Locale locale = UtilsBundle.getLocale(request);
        ResourceBundle messages = UtilsBundle.getBundle(request, ResourceEnum.MESSAGE);
        ResourceBundle regexpBundle = UtilsBundle.getBundle(request, ResourceEnum.REGEXP);
        LOG.debug("locale: " + locale.toString());
        if (isEmptyRequest(request)) {
            return PAGES.getString("page.registration");
        }
        if (UtilsValidation.inValid(request.getParameter("name"), regexpBundle.getString("regex.name"))) {
            request.setAttribute("wrongName", messages.getString("info.message.wrong.name"));
            return PAGES.getString("page.registration");
        }
        request.setAttribute("name", request.getParameter("name"));

        if (UtilsValidation.inValid(request.getParameter("surname"), regexpBundle.getString("regex.surname"))) {
            request.setAttribute("wrongSurname", messages.getString("info.message.wrong.surname"));
            return PAGES.getString("page.registration");
        }
        request.setAttribute("surname", request.getParameter("surname"));

        String email = request.getParameter("email");

        if (serviceUser.isUserExist(email)) {
            request.setAttribute("wrongEmail", messages.getString("info.message.user.already.exist"));
            return PAGES.getString("page.registration");
        }

        if (UtilsValidation.inValid(email, regexpBundle.getString("regexp.email"))) {
            request.setAttribute("wrongEmail", messages.getString("info.message.wrong.email.input"));
            return PAGES.getString("page.registration");
        }
        request.setAttribute("email", email);

        String password = request.getParameter("password");
        if (UtilsValidation.inValid(password, regexpBundle.getString("regexp.password"))) {
            request.setAttribute("wrongPassword", messages.getString("info.message.wrong.password.input"));
            return PAGES.getString("page.registration");

        }
        DTOUser user = new DTOUser.Builder()
                .setNameEn(transliterateIfUa(request.getParameter("name"),locale.toString()).get("en"))
                .setNameUa(transliterateIfUa(request.getParameter("name"),locale.toString()).get("ua"))
                .setSurnameEn(transliterateIfUa(request.getParameter("surname"),locale.toString()).get("en"))
                .setSurnameUa(transliterateIfUa(request.getParameter("surname"),locale.toString()).get("ua"))
                .setEmail(email)
                .setPassword(UtilsSecurity.hashPassword(password))
                .setRole(User.Role.USER)
                .build();
        LOG.debug("user: " +user.toString());
        serviceUser.signUpUser(user);
        LOG.info("New user signed up");
        return "redirect:" + PAGES.getString("path.guest.login");
    }

    private boolean isEmptyRequest(HttpServletRequest request) {
        return !request.getParameterNames().hasMoreElements();
    }
    private Map<String,String> transliterateIfUa(String name,String language) {
        Map<String,String> names = new HashMap<>();

        if(language.equals("uk_UA")) {
            names.put("en",  UtilsTransliteration.transliterateUaToEn(name));
        }
        else {
            names.put("en",  name);
        }
        names.put("ua",  name);
        return names;
    }
}
