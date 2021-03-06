package com.bykov.project.conference.dao;

import com.bykov.project.conference.dao.entity.Conference;
import com.bykov.project.conference.dao.mappers.ConferenceMapper;
import com.bykov.project.conference.dao.mappers.SubscriptionDtoMapper;
import com.bykov.project.conference.dto.DTOConference;
import com.bykov.project.conference.dto.DTOReport;
import com.bykov.project.conference.dto.DTOSubscription;
import com.bykov.project.conference.dto.ResourceEnum;
import com.bykov.project.conference.exceptions.DatabaseException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class JdbcDaoConference implements DaoConference {
    private final static Logger LOG = LogManager.getLogger(JdbcDaoConference.class);
    private final DataSource dataSource = ConnectionPool.getDataSource();
    private final ResourceBundle sqlRequestBundle = ResourceBundle.getBundle(ResourceEnum.SQL_REQUESTS.getBundleName());


    @Override
    public Conference getById(long id, String language) {
        Conference conference;
        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sqlRequestBundle.getString("conference.select.by.id"))) {
            preparedStatement.setLong(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();

            conference = new ConferenceMapper().mapToObject(resultSet, language);
            return conference;
        } catch (SQLException e) {
            LOG.error("Cant get conference  by id:" + e);
            throw new DatabaseException();
        }
    }


    @Override
    public List<Conference> getAll(String language) {
        List<Conference> conferences = new ArrayList<>();
        try (Connection connection = dataSource.getConnection();
             PreparedStatement conferenceStatement = connection
                     .prepareStatement(sqlRequestBundle.getString("conferences.select.all."))) {

            ResultSet resultSet = conferenceStatement.executeQuery();
            conferences = new ConferenceMapper().mapToList(resultSet, language);
        } catch (SQLException e) {
            LOG.error("Cant get all conferences: " + e);
        }
        return conferences;

    }

    @Override
    public void update(DTOConference conference) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement conferenceQuery = connection
                     .prepareStatement(sqlRequestBundle.getString("conference.update"), Statement.RETURN_GENERATED_KEYS);
             PreparedStatement reportQuery = connection
                     .prepareStatement((sqlRequestBundle.getString("report.update")))) {

            connection.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);
            connection.setAutoCommit(false);
            conferenceQuery.setString(1, conference.getTopicEn());
            conferenceQuery.setString(2, conference.getTopicUa());
            conferenceQuery.setString(3, conference.getLocationEn());
            conferenceQuery.setString(4, conference.getLocationUa());
            conferenceQuery.setTimestamp(5, Timestamp.valueOf(conference.getDateTime()));
            conferenceQuery.setLong(6, conference.getId());
            conferenceQuery.executeUpdate();

            for (DTOReport report : conference.getReports()) {
                reportQuery.setString(1, report.getTopicEn());
                reportQuery.setString(2, report.getTopicUa());
                reportQuery.setTimestamp(3, Timestamp.valueOf(report.getDateTime()));
                reportQuery.setLong(4, report.getSpeakerId());
                reportQuery.setLong(5, report.getId());
                reportQuery.addBatch();
            }
            reportQuery.executeBatch();
            connection.commit();
        } catch (SQLException e) {
            LOG.error("Conference update failed: " + e);
            throw new DatabaseException();
        }
    }

    @Override
    public boolean delete(long id) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement query = connection
                     .prepareStatement(sqlRequestBundle.getString("conference.delete"))) {
            query.setLong(1, id);
            query.executeUpdate();
            return true;
        } catch (SQLException e) {
            LOG.error("Delete conference failed: " + e);
            throw new DatabaseException();
        }
    }

    @Override
    public boolean addNew(DTOConference conference) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement conferenceQuery = connection
                     .prepareStatement(sqlRequestBundle.getString("conference.insert"), Statement.RETURN_GENERATED_KEYS);
             PreparedStatement reportQuery = connection
                     .prepareStatement((sqlRequestBundle.getString("report.insert")))) {
            connection.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);
            connection.setAutoCommit(false);
            conferenceQuery.setString(1, conference.getTopicEn());
            conferenceQuery.setString(2, conference.getTopicUa());

            conferenceQuery.setString(3, conference.getLocationEn());
            conferenceQuery.setString(4, conference.getLocationUa());

            conferenceQuery.setTimestamp(5, Timestamp.valueOf(conference.getDateTime()));
            conferenceQuery.executeUpdate();
            ResultSet idResultSet = conferenceQuery.getGeneratedKeys();
            if (idResultSet.next()) {
                for (DTOReport report : conference.getReports()) {
                    reportQuery.setString(1, report.getTopicEn());
                    reportQuery.setString(2, report.getTopicUa());

                    reportQuery.setLong(3, idResultSet.getLong(1));
                    reportQuery.setLong(4, report.getSpeakerId());
                    reportQuery.setTimestamp(5, Timestamp.valueOf(report.getDateTime()));
                    reportQuery.addBatch();
                }
            }
            reportQuery.executeBatch();
            connection.commit();
            return true;
        } catch (SQLException e) {
            LOG.error("Conference inserting failed: " + e);
        }
        return false;
    }


    @Override
    public List<DTOSubscription> getSubscriptionsList(String language) {
        List<DTOSubscription> subscriptions = new ArrayList<>();
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection
                     .prepareStatement(sqlRequestBundle.getString("subscription.get.all"))
        ) {
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                subscriptions.add(new SubscriptionDtoMapper().mapToObject(resultSet, language));
            }
            return subscriptions;
        } catch (SQLException e) {
            LOG.error("Cant get subscriptions: " + e);
            throw new DatabaseException();
        }
    }

    @Override
    public List<Long> getAllConferenceIdsInSubscriptions() {
        List<Long> conferenceIds = new ArrayList<>();
        try (Connection connection = dataSource.getConnection();
             PreparedStatement conferenceStatement = connection
                     .prepareStatement(sqlRequestBundle.getString("conferences.get.all.conference.ids.in.subscriptions"))) {
            ResultSet resultSet = conferenceStatement.executeQuery();

            while (resultSet.next()) {
                conferenceIds.add(resultSet.getLong("id_conference"));
            }
        } catch (SQLException e) {
            LOG.error("Cant get get All Conference Ids In Subscriptions: " + e);
        }
        return conferenceIds;

    }

    @Override
    public List<Conference> getPaginatedConferences(int begin, int recordsPerPage, String language) {
        ConferenceMapper conferenceMapper = new ConferenceMapper();

        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection
                     .prepareStatement(sqlRequestBundle.getString("conferences.select.paginated"))) {
            return getConferences(begin, recordsPerPage, language, conferenceMapper, statement);
        } catch (SQLException e) {
            LOG.error("Cant get paginated list: " + e);
            throw new DatabaseException();
        }
    }


    @Override
    public List<Conference> getPaginatedPastConferences(int begin, int recordsPerPage, String language) {
        ConferenceMapper conferenceMapper = new ConferenceMapper();
        LOG.debug("begin " + begin);
        LOG.debug("recordsPerPage " + recordsPerPage);
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection
                     .prepareStatement(sqlRequestBundle.getString("conferences.select.paginated.past"))) {
            return getConferences(begin, recordsPerPage, language, conferenceMapper, statement);
        } catch (SQLException e) {
            LOG.error("Cant get paginated past conferences list: " + e);
            throw new DatabaseException();
        }
    }

    @Override
    public List<Conference> getPaginatedFutureConferences(int begin, int recordsPerPage, String language) {
        ConferenceMapper conferenceMapper = new ConferenceMapper();

        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection
                     .prepareStatement(sqlRequestBundle.getString("conferences.select.paginated.future"))) {
            return getConferences(begin, recordsPerPage, language, conferenceMapper, statement);
        } catch (SQLException e) {
            LOG.error("Cant get paginated future conferences list: " + e);
            throw new DatabaseException();
        }
    }

    @Override
    public int getFutureConferencesAmount() {
        int conferenceAmount = 0;
        try (Connection connection = dataSource.getConnection();
             PreparedStatement conferenceStatement = connection
                     .prepareStatement(sqlRequestBundle.getString("conferences.future.count"))) {
            ResultSet resultSet = conferenceStatement.executeQuery();
            if (resultSet.next()) {
                conferenceAmount = resultSet.getInt(1);
            }
            return conferenceAmount;
        } catch (SQLException e) {
            LOG.error("Cant count future conferences: " + e);
            throw new DatabaseException();
        }
    }

    @Override
    public int getPastConferencesAmount() {
        int conferenceAmount = 0;
        try (Connection connection = dataSource.getConnection();
             PreparedStatement conferenceStatement = connection
                     .prepareStatement(sqlRequestBundle.getString("conferences.past.count"))) {
            ResultSet resultSet = conferenceStatement.executeQuery();
            if (resultSet.next()) {
                conferenceAmount = resultSet.getInt(1);
            }
            return conferenceAmount;
        } catch (SQLException e) {
            LOG.error("Cant count past conferences: " + e);
            throw new DatabaseException();
        }
    }

    private List<Conference> getConferences(int begin, int recordsPerPage, String language, ConferenceMapper conferenceMapper, PreparedStatement statement) throws SQLException {
        List<Conference> paginatedList;
        statement.setInt(1, begin);
        statement.setInt(2, recordsPerPage);
        ResultSet resultSet = statement.executeQuery();
        paginatedList = conferenceMapper.mapToList(resultSet, language);
        return paginatedList;
    }

    @Override
    public int getConferencesAmount() {
        int conferenceAmount = 0;
        try (Connection connection = dataSource.getConnection();
             PreparedStatement conferenceStatement = connection
                     .prepareStatement(sqlRequestBundle.getString("conferences.count"))) {
            ResultSet resultSet = conferenceStatement.executeQuery();
            if (resultSet.next()) {
                conferenceAmount = resultSet.getInt(1);
            }
            return conferenceAmount;
        } catch (SQLException e) {
            LOG.error("Cant count conferences: " + e);
            throw new DatabaseException();
        }
    }


}

