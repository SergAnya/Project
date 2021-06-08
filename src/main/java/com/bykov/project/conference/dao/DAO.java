package com.bykov.project.conference.dao;

import java.util.List;

public interface DAO<T> {
    T getById(long id, String language);
    List<T> getAll(String language);
    boolean delete(long id);
}
