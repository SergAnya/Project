package com.bykov.project.conference.dao.mappers;

import java.sql.ResultSet;
import java.sql.SQLException;

public interface Mapper<T> {
    T mapToObject(ResultSet resultSet,String language) throws SQLException;

}
