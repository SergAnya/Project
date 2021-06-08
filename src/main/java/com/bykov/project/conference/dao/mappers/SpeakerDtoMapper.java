package com.bykov.project.conference.dao.mappers;

import com.bykov.project.conference.dao.entity.Report;
import com.bykov.project.conference.dao.entity.Speaker;
import com.bykov.project.conference.dto.DTOSpeaker;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class SpeakerDtoMapper {
    private ReportMapper reportMapper = new ReportMapper();
    private SpeakerMapper speakerMapper = new SpeakerMapper();

    public List<DTOSpeaker> mapToSpeakersListWithReports(ResultSet resultSet, String language) throws SQLException {
        List<DTOSpeaker> speakersDto = new ArrayList<>();
        Set<Speaker> speakers = new HashSet<>();

        Map<Speaker, List<Report>> speakerReports = new HashMap<>();
        Speaker speaker;

        while (resultSet.next()) {
            speaker = speakerMapper.mapToObject(resultSet, language);
            speakers.add(speaker);
            speakerReports.putIfAbsent(speaker, new ArrayList<>());
            speakerReports.get(speaker).add(reportMapper.mapToObject(resultSet, language));
        }
        speakers.forEach(currentSpeaker -> speakersDto.add(new DTOSpeaker(currentSpeaker, speakerReports.get(currentSpeaker))));
        return speakersDto;
    }
}
