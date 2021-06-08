package com.bykov.project.conference.command;


import javax.servlet.http.HttpServletRequest;

public class CommandChangeLanguage implements Command {
    @Override
    public String execute(HttpServletRequest request) {
        request.getSession().setAttribute("lang",request.getParameter("lang"));
        return "redirect:/";
    }
}
