package com.bykov.project.conference.utils;


import org.mindrot.jbcrypt.BCrypt;
import com.bykov.project.conference.dao.entity.User;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class UtilsSecurity {
    private static final Map<String, List<Object>> permittedCommandMap = new ConcurrentHashMap<>();

    private enum AdminCommands {
        CATALOG,
        CHANGELANGUAGE,
        DEFAULT,
        LOGOUT,
        DELETECONFERENCE,
        EDITCONFERENCE,
        EDITREPORT,
        SUBSCRIBE,
        UNSUBSCRIBE,
        CREATECONFERENCE,
        CATALOGOFSPEAKERS,
        DELETEREPORT,
        STATISTICS,
        ADDREPORT
    }

    private enum SpeakerCommands {
        CATALOG,
        CHANGELANGUAGE,
        DEFAULT,
        LOGOUT,
        EDITREPORT,
        SUBSCRIBE,
        UNSUBSCRIBE,
        CATALOGOFSPEAKERS,
        DELETEREPORT,
        ADDREPORT
    }

    private enum UserCommands {
        CATALOG,
        CHANGELANGUAGE,
        DEFAULT,
        LOGOUT,
        SUBSCRIBE,
        UNSUBSCRIBE,
        CATALOGOFSPEAKERS
    }

    private enum GuestCommands {
        LOGIN,
        REGISTRATION,
        CATALOG,
        CHANGELANGUAGE,
        DEFAULT
    }

    static {
        permittedCommandMap.put("admin", Arrays.asList(AdminCommands.values()));
        permittedCommandMap.put("speaker", Arrays.asList(SpeakerCommands.values()));
        permittedCommandMap.put("user", Arrays.asList(UserCommands.values()));
        permittedCommandMap.put("guest", Arrays.asList(GuestCommands.values()));
    }

    public static boolean isSecuredPage(String urlPattern) {
        for (User.Role role : User.Role.values()) {
            if (urlPattern.contains(role.getStringRole())) {
                return true;
            }
        }
        return false;
    }

    public static boolean checkPermission(String urlPattern, String role) {
        if (urlPattern.contains(role)) {
            String command = urlPattern.replaceFirst(".*/", "");
            return permittedCommandMap.get(role)
                    .stream()
                    .anyMatch(o -> o.toString().equals(command.toUpperCase()));
        }
        return false;
    }

    public static String hashPassword( String password)  {
        return BCrypt.hashpw(password,BCrypt.gensalt());
    }

    private UtilsSecurity(){}
}
