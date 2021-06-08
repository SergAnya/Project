package com.bykov.project.conference.dao.mappers;

import com.bykov.project.conference.dto.DTOSubscription;

import java.sql.ResultSet;
import java.sql.SQLException;

public class SubscriptionDtoMapper implements Mapper<DTOSubscription> {
    @Override
    public DTOSubscription mapToObject(ResultSet resultSet, String language) throws SQLException {
        return new DTOSubscription.Builder()
                .setConferenceDateTime(resultSet.getTimestamp("conference_timestamp").toLocalDateTime())
                .setConferenceLocation(resultSet.getString("conference_location_" + language))
                .setConferenceTopic(resultSet.getString("conference_topic_" + language))
                .setReportTopic(resultSet.getString("report_topic_" + language))
                .setReportDateTime(resultSet.getTimestamp("report_datetime").toLocalDateTime())
                .setUserEmail(resultSet.getString("user_email"))
                .setUserName(resultSet.getString("user_name_" + language))
                .setSpeakerSurname(resultSet.getString("speaker_surname_" + language))
                .setSpeakerName(resultSet.getString("speaker_name_" + language))
                .setUserSurname(resultSet.getString("user_surname_" + language))
                .setReportId(resultSet.getLong("id_report"))
                .setConferenceId(resultSet.getLong("id_conference"))
                .build();
    }


}
