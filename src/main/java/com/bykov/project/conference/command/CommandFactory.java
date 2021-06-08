package com.bykov.project.conference.command;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import com.bykov.project.conference.services.ServiceConference;
import com.bykov.project.conference.services.ServiceReport;
import com.bykov.project.conference.services.ServiceSpeaker;
import com.bykov.project.conference.services.ServiceUser;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;


public class CommandFactory {
    private final static Logger LOG = LogManager.getLogger(CommandFactory.class);
    private final ServiceUser serviceUser = new ServiceUser();
    private final ServiceReport serviceReport = new ServiceReport();
    private final ServiceConference serviceConference = new ServiceConference();
    private final ServiceSpeaker serviceSpeaker = new ServiceSpeaker();

    private final Map<String, Command> commandMap = new HashMap<>();

    public CommandFactory() {
        commandMap.put("DEFAULT", new CommandDefault());
        commandMap.put("LOGIN", new CommandLogin(serviceUser));
        commandMap.put("LOGOUT", new CommandLogOut());
        commandMap.put("REGISTRATION", new CommandRegistration(serviceUser));
        commandMap.put("CATALOG", new CommandCatalog(serviceUser, serviceConference));
        commandMap.put("SUBSCRIBE", new CommandSubscribe(serviceUser, serviceReport));
        commandMap.put("UNSUBSCRIBE", new CommandUnsubscribe(serviceUser, serviceReport));
        commandMap.put("CHANGELANGUAGE", new CommandChangeLanguage());
        commandMap.put("CREATECONFERENCE", new CommandCreateConference(serviceUser, serviceConference));
        commandMap.put("DELETECONFERENCE", new CommandDeleteConference(serviceConference));
        commandMap.put("CATALOGOFSPEAKERS", new CommandCatalogOfSpeakers(serviceUser, serviceSpeaker));
        commandMap.put("EDITCONFERENCE", new CommandEditConference(serviceUser, serviceReport, serviceConference));
        commandMap.put("DELETEREPORT", new CommandDeleteReport(serviceUser, serviceReport));
        commandMap.put("ADDREPORT", new CommandAddReport(serviceUser, serviceReport, serviceConference));
        commandMap.put("STATISTICS", new CommandStatistics(serviceUser, serviceReport, serviceConference));
    }

    public Optional<Command> getCommand(HttpServletRequest request) {
        String command = request.getRequestURI()
                .replaceAll(".*/guest*.|.*/admin*.|.*/speaker*.|.*/user*.|", "");
        LOG.debug("Command is: " + command);
        return command.isEmpty()
                ? Optional.of(new CommandDefault())
                : Optional.ofNullable(commandMap.get(command.toUpperCase()));
    }
}
