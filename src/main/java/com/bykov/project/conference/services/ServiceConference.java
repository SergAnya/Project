package com.bykov.project.conference.services;

import com.bykov.project.conference.dao.DaoConference;
import com.bykov.project.conference.dao.DaoFactory;
import com.bykov.project.conference.dao.entity.Conference;
import com.bykov.project.conference.dto.DTOConference;
import com.bykov.project.conference.dto.FilterSortType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;

public class ServiceConference {
    private static final Logger LOG = LogManager.getLogger(ServiceConference.class);

    private static final DaoConference DAO_CONFERENCE = DaoFactory.getInstance().getConferenceDao();

    public boolean addConference(DTOConference conference) {
        return DAO_CONFERENCE.addNew(conference);
    }

    public boolean deleteConference(Long id) {
        return DAO_CONFERENCE.delete(id);
    }

    public Conference getConferenceById(long conferenceId, String language) {
        return DAO_CONFERENCE.getById(conferenceId, language);
    }

    public List<Conference> getPaginatedList(int begin, int recordsPerPage, String language) {
        return sortPaginatedConferencesByDateTime(DAO_CONFERENCE.getPaginatedConferences(begin, recordsPerPage, language));
    }

    public List<Conference> getSortedPaginatedConferences(FilterSortType type, int begin, int recordsPerPage, String language) {
        switch (type){
            case ALL:
                return getPaginatedList(begin, recordsPerPage, language);
            case PAST:
                return getPaginatedPastConferences(begin, recordsPerPage, language);
            case FUTURE:
                return getPaginatedFutureConferences(begin, recordsPerPage, language);
            default:
                return new ArrayList<>();
        }
    }

    private List<Conference> getPaginatedPastConferences(int begin, int recordsPerPage, String language) {
        return sortPaginatedConferencesByDateTime(DAO_CONFERENCE.getPaginatedPastConferences(begin, recordsPerPage, language));
    }

    private List<Conference> getPaginatedFutureConferences(int begin, int recordsPerPage, String language) {
        return sortPaginatedConferencesByDateTime(DAO_CONFERENCE.getPaginatedFutureConferences(begin, recordsPerPage, language));
    }

    public int getConferencesAmount() {
        return DAO_CONFERENCE.getConferencesAmount();
    }
    public int getConferencesAmount(FilterSortType type) {
        switch (type){
            case ALL:
                return getConferencesAmount();
            case PAST:
                return getPastConferencesAmount();
            case FUTURE:
                return getFutureConferencesAmount();
            default:
                return 0;
        }
    }

    private List<Conference> sortPaginatedConferencesByDateTime(List<Conference> conferences) {
        conferences.sort(Comparator.comparing(Conference::getDateTime));
        return conferences;
    }
    public int getFutureConferencesAmount(){
        return DAO_CONFERENCE.getFutureConferencesAmount();
    }
    public int getPastConferencesAmount(){
        return DAO_CONFERENCE.getPastConferencesAmount();
    }

    public void update(DTOConference conference) {
        DAO_CONFERENCE.update(conference);
    }


}
