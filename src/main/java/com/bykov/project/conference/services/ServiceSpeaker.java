package com.bykov.project.conference.services;

import com.bykov.project.conference.dao.DaoFactory;
import com.bykov.project.conference.dao.DaoSpeaker;
import com.bykov.project.conference.dto.DTOSpeaker;

import java.util.List;

public class ServiceSpeaker {
    private final DaoSpeaker daoSpeaker = DaoFactory.getInstance().getSpeakerDao();

    public int getSpeakersAmount() {
        return daoSpeaker.getSpeakersAmount();
    }

    public List<DTOSpeaker> getPaginatedList(Integer begin, Integer recordsPerPage, String language) {
        return daoSpeaker.getPaginatedList(begin, recordsPerPage, language);
    }
}
