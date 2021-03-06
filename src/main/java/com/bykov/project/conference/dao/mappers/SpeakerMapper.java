package com.bykov.project.conference.dao.mappers;

import com.bykov.project.conference.dao.entity.Speaker;

import java.sql.ResultSet;
import java.sql.SQLException;

public class SpeakerMapper implements Mapper<Speaker> {
    @Override
    public Speaker mapToObject(ResultSet resultSet, String language) throws SQLException {
        return new Speaker.Builder()
                .setBonus(resultSet.getDouble("speakers_bonus"))
                .setRating(resultSet.getDouble("speakers_rating"))
                .setId(resultSet.getLong("user_id"))
                .setName(resultSet.getString("user_name_"+language))
                .setSurname(resultSet.getString("user_surname_"+language))
                .setEmail(resultSet.getString("user_email"))
                .build();
    }
}
