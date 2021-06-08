package com.bykov.project.conference.dao;

import com.bykov.project.conference.dao.entity.Conference;
import com.bykov.project.conference.dto.DTOConference;
import com.bykov.project.conference.dto.DTOSubscription;

import java.util.List;

public interface DaoConference extends DAO<Conference> {
    List<Conference> getAll(String language);

    void update(DTOConference conference);

    boolean addNew(DTOConference conference);

    List<DTOSubscription> getSubscriptionsList(String language);

    List<Long> getAllConferenceIdsInSubscriptions();

    List<Conference> getPaginatedConferences(int begin, int recordsPerPage, String language);

    int getConferencesAmount();

    List<Conference> getPaginatedPastConferences(int begin, int recordsPerPage, String language);

    List<Conference> getPaginatedFutureConferences(int begin, int recordsPerPage, String language);

    int getFutureConferencesAmount();

    int getPastConferencesAmount();
}
