package com.bykov.project.conference.dao;

import com.bykov.project.conference.dao.entity.Report;
import com.bykov.project.conference.dto.DTOReport;

public interface DaoReport extends DAO<Report>{
    boolean subscribe(long userId, long reportId);

    boolean isSubscribed(long userId, long reportId);

    void unsubscribe(long userId, long reportId);

    void addNew(long conferenceId, Report report);

    void addNew(long conferenceId, DTOReport report);

    int getAmountOfSubscribedUsers(long reportId);

    void setAmountOfSubscribedUsers(long reportId, int amount);

    void setComersAmount(long reportId, int amount);
}
