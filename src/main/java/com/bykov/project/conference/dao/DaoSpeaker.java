package com.bykov.project.conference.dao;

import com.bykov.project.conference.dao.entity.Speaker;
import com.bykov.project.conference.dto.DTOSpeaker;

import java.util.List;

public interface DaoSpeaker extends DAO<Speaker>{

    int getSpeakersAmount();

    List<DTOSpeaker> getPaginatedList(Integer begin, Integer recordsPerPage, String language);
}
