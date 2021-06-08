package com.bykov.project.conference.services;

import com.bykov.project.conference.dao.DaoFactory;
import com.bykov.project.conference.dao.DaoUser;
import com.bykov.project.conference.dto.DTOUser;
import com.bykov.project.conference.dao.entity.User;
import com.bykov.project.conference.dao.entity.User.Role;

import java.util.List;

public class ServiceUser {
    private static final DaoUser DAO_USER = DaoFactory.getInstance().getUserDao();

    public static String getRoleString(String email, String language) {
        return DAO_USER.getByEmail(email, language).getRole().toString().toLowerCase();
    }

    public boolean isUserExist(String email) {
        return DAO_USER.checkUserExist(email);
    }

    public boolean checkPassword(String email, String password) {
        return DAO_USER.checkUserPassword(email, password);
    }

    public Role getUserRole(String email) {
        return DAO_USER.getUserRole(email);
    }

    public Role getUserRole(long id) {
        return DAO_USER.getUserRole(id);
    }

    public void signUpUser(DTOUser user) {
        DAO_USER.addNew(user);
    }

    public long getUserId(String email,String language) {
        return DAO_USER.getByEmail(email,language).getId();
    }

    public List<Long> getUserSubscriptionsIds(long userId) {
        return DAO_USER.getUserSubscriptionsIds(userId);
    }

    public List<User> getAllUsers(String language) {
        return DAO_USER.getAll( language);
    }

    public void changeRole(long id, Role role) {
        DAO_USER.changeRole(id, role);
    }

    public String getNameById(long id, String language) {
        return DAO_USER.getNameById(id, language);
    }

    public String getSurnameById(long id, String language) {
        return DAO_USER.getSurnameById(id, language);
    }

    public List<String> getUserSubscribedEmails() {
        return DAO_USER.getUserSubscriptedEmails();
    }

    public boolean vote(long userId, long speakerId, int rating) {
        return DAO_USER.vote(userId, speakerId, rating);
    }
}
