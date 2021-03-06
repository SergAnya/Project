package com.bykov.project.conference.dao;

import com.bykov.project.conference.dao.entity.User;
import com.bykov.project.conference.dao.mappers.UserMapper;
import com.bykov.project.conference.dto.DTOUser;
import com.bykov.project.conference.dto.ResourceEnum;
import com.bykov.project.conference.exceptions.DatabaseException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mindrot.jbcrypt.BCrypt;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class JdbcDaoUser implements DaoUser {
    private static final Logger LOG = LogManager.getLogger(JdbcDaoUser.class);
    private final static ResourceBundle BUSINESS_LOGIC_BUNDLE = ResourceBundle.getBundle(ResourceEnum.BUSINESS_LOGIC.getBundleName());

    private final DataSource dataSource = ConnectionPool.getDataSource();
    private final ResourceBundle sqlRequestBundle = ResourceBundle.getBundle(ResourceEnum.SQL_REQUESTS.getBundleName());


    @Override
    public User getById(long id, String language) {
        User user = null;
        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sqlRequestBundle.getString("user.select.by.id"))) {
            preparedStatement.setLong(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                user = new UserMapper().mapToObject(resultSet, language);
            }
            return user;

        } catch (SQLException e) {
            LOG.error("Cant get user  by id:" + e);
            throw new DatabaseException();
        }
    }


    @Override
    public List<User> getAll(String language) {
        List<User> users = new ArrayList<>();
        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection
                     .prepareStatement(sqlRequestBundle.getString("user.select.all"))) {
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                users.add(new UserMapper().mapToObject(resultSet, language));
            }
            return users;
        } catch (SQLException e) {
            LOG.error("Cant get all users: " + e);
            throw new DatabaseException();
        }

    }


    @Override
    public boolean delete(long id) {
        return false;
    }

    @Override
    public boolean addNew(DTOUser user) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection
                     .prepareStatement(sqlRequestBundle.getString("user.insert.new"))) {

            preparedStatement.setString(1, user.getNameEn());
            preparedStatement.setString(2, user.getNameUa());
            preparedStatement.setString(3, user.getSurnameEn());
            preparedStatement.setString(4, user.getSurnameUa());
            preparedStatement.setString(5, user.getRole().toString());
            preparedStatement.setString(6, user.getPassword());
            preparedStatement.setString(7, user.getEmail());

            preparedStatement.execute();
            return true;
        } catch (SQLException e) {
            LOG.error("User was not added: " + e);
            return false;
        }
    }


    @Override
    public User getByEmail(String email, String language) {
        User user = null;
        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sqlRequestBundle.getString("user.select.by.email"))) {
            preparedStatement.setString(1, email);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                user = new UserMapper().mapToObject(resultSet, language);
            }
            return user;

        } catch (SQLException e) {
            LOG.error("Cant get user  by email:" + e);
            throw new DatabaseException();
        }

    }


    @Override
    public boolean checkUserExist(String email) {
        return getByEmail(email, "en_US") != null;
    }


    @Override
    public boolean checkUserPassword(String email, String password) {
        return BCrypt.checkpw(password, getByEmail(email, "en_US").getPassword());
    }


    @Override
    public User.Role getUserRole(String email) {
        return getByEmail(email, "en_US").getRole();
    }


    @Override
    public User.Role getUserRole(long id) {
        return getById(id, "en_US").getRole();
    }


    @Override
    public List<Long> getUserSubscriptionsIds(long userId) {
        List<Long> userSubscriptionsIds = new ArrayList<>();
        try (Connection connection = dataSource.getConnection();

             PreparedStatement preparedStatement = connection
                     .prepareStatement(sqlRequestBundle.getString("user.get.subscriptions.ids"))) {
            preparedStatement.setLong(1, userId);
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                userSubscriptionsIds.add(resultSet.getLong("reports_id_report"));
            }
            return userSubscriptionsIds;
        } catch (SQLException e) {
            LOG.error("Can`y get list of user subscription: " + e);
            throw new DatabaseException();
        }
    }

    @Override
    public void changeRole(long id, User.Role role) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sqlRequestBundle.getString("user.change.role"))) {

            preparedStatement.setString(1, role.toString());
            preparedStatement.setLong(2, id);
            preparedStatement.execute();
        } catch (SQLException e) {
            LOG.error("User was not added: " + e);
            throw new DatabaseException();
        }
    }


    @Override
    public String getNameById(long id, String language) {
        return getById(id, language).getName();
    }


    @Override
    public String getSurnameById(long id, String language) {
        return getById(id, language).getSurname();
    }


    @Override
    public List<String> getUserSubscriptedEmails() {

        ArrayList<String> subscriptedEmails = new ArrayList<>();
        try (Connection connection = dataSource.getConnection();

             PreparedStatement preparedStatement = connection
                     .prepareStatement(sqlRequestBundle.getString("user.get.subscribed.emails"))) {
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                subscriptedEmails.add(resultSet.getString("user_email"));
            }
            return subscriptedEmails;
        } catch (SQLException e) {
            LOG.error("Can`y get list of subscribed emails: " + e);
            throw new DatabaseException();
        }
    }


    @Override
    public boolean vote(long userId, long speakerId, int mark) {
        double rating;
        try (Connection connection = dataSource.getConnection();
             PreparedStatement voteStatement = connection
                     .prepareStatement(sqlRequestBundle.getString("user.vote"));
             PreparedStatement newRatingStatement = connection
                     .prepareStatement(sqlRequestBundle.getString("speaker.get.new.rating"));
             PreparedStatement updateRatingAndBonus = connection
                     .prepareStatement(sqlRequestBundle.getString("speaker.update.rating.and.bonus"))) {
            connection.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);
            connection.setAutoCommit(false);

            voteStatement.setLong(1, userId);
            voteStatement.setLong(2, speakerId);
            voteStatement.setInt(3, mark);
            //on duplicate key update
            voteStatement.setInt(4, mark);
            voteStatement.executeUpdate();

            newRatingStatement.setLong(1, speakerId);
            ResultSet newRatingResultSet = newRatingStatement.executeQuery();
            newRatingResultSet.next();
            rating = newRatingResultSet.getDouble(1);

            updateRatingAndBonus.setDouble(1, rating);
            updateRatingAndBonus.setDouble(2, Double.parseDouble(BUSINESS_LOGIC_BUNDLE.getString("bonus." + (int) rating + ".star")));
            updateRatingAndBonus.setLong(3, speakerId);
            updateRatingAndBonus.executeUpdate();

            connection.commit();
            return true;
        } catch (SQLException e) {
            LOG.error("Cant vote: " + e);
            throw new DatabaseException();
        }

    }
}



