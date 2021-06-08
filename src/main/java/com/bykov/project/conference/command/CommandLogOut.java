package com.bykov.project.conference.command;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

public class CommandLogOut implements Command {
    private static final Logger LOG = LogManager.getLogger(CommandLogOut.class);
    @Override
    public String execute(HttpServletRequest request) {
        HttpSession session = request.getSession();
        if (session != null) {
            LOG.info("User " + session.getAttribute("email") + " logged out");
            session.getServletContext().removeAttribute((String) session.getAttribute("email"));
            session.invalidate();
        }
        return  "redirect:" + PAGES.getString("page.index");

    }
}
