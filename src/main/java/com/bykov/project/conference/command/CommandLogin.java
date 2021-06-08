package com.bykov.project.conference.command;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import com.bykov.project.conference.utils.UtilsBundle;
import com.bykov.project.conference.dto.ResourceEnum;
import com.bykov.project.conference.dao.entity.User;
import com.bykov.project.conference.services.ServiceUser;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Locale;
import java.util.Objects;
import java.util.ResourceBundle;


public class CommandLogin implements Command {
    private final static Logger LOG = LogManager.getLogger(CommandLogin.class);
    private final ServiceUser serviceUser;

    public CommandLogin(ServiceUser serviceUser) {
        this.serviceUser = serviceUser;
    }

    @Override
    public String execute(HttpServletRequest request) {
        ResourceBundle messages = UtilsBundle.getBundle(request, ResourceEnum.MESSAGE);
        String pageToReturn = PAGES.getString("page.login");
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        Locale locale = UtilsBundle.getLocale(request);
        LOG.trace("locale " + locale);

        if (isEmailNotExists(email)) {
            return pageToReturn;
        }
        if (isPasswordNotExists(email, password)) {
            return pageToReturn;
        }
        if (isEmailEmpty(email, messages, request)) {
            return pageToReturn;
        }
        if (isPasswordEmpty(password, messages, request)) {
            return pageToReturn;
        }
        if (isGuest(request)) {
            return "redirect:/" + ServiceUser.getRoleString(email,locale.toString());
        }
        if (isUserNotExists(email, messages, request)) {
            return pageToReturn;
        }
        if (isPasswordValid(email, password, request)) {
            return "redirect:/" + ServiceUser.getRoleString(email,locale.toString()) + PAGES.getString("path.catalog");
        } else {
            request.setAttribute("wrongPassword", messages.getString("info.message.wrong.password"));
            LOG.warn("Wrong password");
        }
        return pageToReturn;
    }
    
    private boolean isEmailNotExists(String email) {
        if (Objects.isNull(email)) {
            LOG.trace("Email is null");
            return true;
        }
        return false;
    }
    
    private boolean isPasswordNotExists(String email, String password) {
        if (Objects.isNull(email) || Objects.isNull(password)) {
            LOG.trace("Email or password is null");
            return true;
        }
        return false;
    }
    
    private boolean isEmailEmpty(String email, ResourceBundle bundle, HttpServletRequest request) {
        if (email.isEmpty()) {
            LOG.info("Empty email ");
            request.setAttribute("wrongEmail", bundle.getString( "info.message.empty.email"));
            return true;
        }
        return false;
    }

    private boolean isPasswordEmpty(String password, ResourceBundle bundle, HttpServletRequest request) {
        if (password.isEmpty()) {
            LOG.info("Empty password");
            request.setAttribute("wrongPassword", bundle.getString( "info.message.empty.password"));
            return true;
        }
        return false;
    }

    private boolean isGuest(HttpServletRequest request) {
        if (!request.getSession().getAttribute("role").equals(User.Role.GUEST.getStringRole())) {
            LOG.warn("Already logged in user tried to enter");
            return true;
        }
        return false;
    }

    private boolean isPasswordValid(String email, String password, HttpServletRequest request) {
        if (serviceUser.checkPassword(email, password)) {
            checkSession(request, email);
            request.getSession().setAttribute("email", email);
            request.getSession().setAttribute("role", serviceUser.getUserRole(email).getStringRole());
            request.getSession().getServletContext().setAttribute(email, request.getSession());
            LOG.info("User " + email + " signed in");
            return true;
        }
        return false;
    }

    private boolean isUserNotExists(String email, ResourceBundle bundle, HttpServletRequest request) {
        if (!serviceUser.isUserExist(email)) {
            LOG.info("User" + email + " dose not exist");
            request.setAttribute("wrongEmail", bundle.getString("info.message.no.user.with.email"));
            return true;
        }
        return false;
    }
    
    
    private void checkSession(HttpServletRequest request, String email) {
        try {
            if (request.getSession().getServletContext().getAttribute(email) != null) {
                ((HttpSession) request.getSession().getServletContext().getAttribute(email)).invalidate();
            }
        } catch (IllegalStateException ise) {
            LOG.error("Session already invalidated, but email still in context: " + ise);
        }
    }
}
