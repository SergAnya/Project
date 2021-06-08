package com.bykov.project.conference.dao;

import com.bykov.project.conference.dao.entity.User;
import com.bykov.project.conference.dto.DTOUser;

import java.util.List;

public interface DaoUser extends DAO<User> {

    boolean addNew(DTOUser user);

    User getByEmail(String email, String language);

    boolean checkUserExist(String email);

    boolean checkUserPassword(String email, String password);

    User.Role getUserRole(String email);

    User.Role getUserRole(long id);

    List<Long> getUserSubscriptionsIds(long userId);

    void changeRole(long id, User.Role role);

    String getNameById(long id, String language);

    String getSurnameById(long id, String language);

    List<String> getUserSubscriptedEmails();

    boolean vote(long userId, long speakerId, int newMark);

}
