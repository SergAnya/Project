package com.bykov.project.conference.command;

import com.bykov.project.conference.dto.ResourceEnum;

import javax.servlet.http.HttpServletRequest;
import java.util.ResourceBundle;

public interface Command {
    ResourceBundle PAGES = ResourceBundle.getBundle(ResourceEnum.PAGES.getBundleName());
    String execute(HttpServletRequest request);
}
