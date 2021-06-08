package com.bykov.project.conference.command;

import javax.servlet.http.HttpServletRequest;

public class CommandDefault implements Command {
    @Override
    public String execute(HttpServletRequest request) {
        return PAGES.getString("page.index");
    }
}
