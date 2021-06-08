package com.bykov.project.conference.controller;

import com.bykov.project.conference.command.Command;
import com.bykov.project.conference.command.CommandFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.Optional;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class Controller extends HttpServlet {
    private static final Logger LOG = LogManager.getLogger(Controller.class);

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        processRequest(request, response);
    }


    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        processRequest(request, response);
    }

    private void processRequest(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        CommandFactory commandFactory = new CommandFactory();
        Optional<Command> commandOptional = commandFactory.getCommand(request);

        if (commandOptional.isPresent()) {
            String page = commandOptional.get().execute(request);
            if (page.contains("redirect:")) {
                LOG.trace("page with redirect: " + page);
                LOG.trace("path after filtering: " + (page.contains("Conference") ? "" : request.getContextPath()) + page.replace("redirect:", ""));
                response.sendRedirect((page.contains("Conference") ? "" : request.getContextPath()) + page.replace("redirect:", ""));
            } else {
                LOG.trace( "page" + page);
                request.getRequestDispatcher(page).forward(request,response);
            }
        } else {
            LOG.warn("Moving to error page");
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }
}
