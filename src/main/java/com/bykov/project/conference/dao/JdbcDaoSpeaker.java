package com.bykov.project.conference.dao;

import com.bykov.project.conference.dao.entity.Speaker;
import com.bykov.project.conference.dao.mappers.SpeakerDtoMapper;
import com.bykov.project.conference.dto.DTOSpeaker;
import com.bykov.project.conference.exceptions.DatabaseException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import com.bykov.project.conference.dto.ResourceEnum;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.ResourceBundle;

public class JdbcDaoSpeaker implements DaoSpeaker {
    private static final Logger LOG = LogManager.getLogger(JdbcDaoSpeaker.class);
    private final DataSource dataSource = ConnectionPool.getDataSource();
    private final ResourceBundle sqlRequestBundle = ResourceBundle.getBundle(ResourceEnum.SQL_REQUESTS.getBundleName());


    @Override
    public Speaker getById(long id, String language) {
        return null;
    }

    @Override
    public List<Speaker> getAll( String language) {
        return null;
    }

    @Override
    public boolean delete(long id) {
        return false;
    }

    @Override
    public int getSpeakersAmount() {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection
                     .prepareStatement(sqlRequestBundle.getString("speaker.count"))) {
            ResultSet resultSet = preparedStatement.executeQuery();
            resultSet.next();
            return resultSet.getInt(1);
        } catch (SQLException e) {
            LOG.error("Cant count:" + e);
            throw new DatabaseException();
        }
    }

    @Override
    public List<DTOSpeaker> getPaginatedList(Integer begin, Integer recordsPerPage, String language) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection
                     .prepareStatement(sqlRequestBundle.getString("speaker.get.paginated.speakers"))) {
            preparedStatement.setInt(1,begin);
            preparedStatement.setInt(2,recordsPerPage);
            ResultSet resultSet = preparedStatement.executeQuery();

            return new SpeakerDtoMapper().mapToSpeakersListWithReports(resultSet, language);
        } catch (SQLException e) {
            LOG.error("Can`t get paginated list of speakers with reports: " + e);
            throw new DatabaseException();
        }
    }
}
