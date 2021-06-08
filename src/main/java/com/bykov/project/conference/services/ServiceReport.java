package com.bykov.project.conference.services;

import com.bykov.project.conference.dao.DaoFactory;
import com.bykov.project.conference.dao.DaoReport;
import com.bykov.project.conference.dto.DTOReport;

public class ServiceReport {
    private final DaoReport daoReport = DaoFactory.getInstance().getReportDao();

    public boolean subscribeUserOnReport(long userId, long reportId) {
        daoReport.setAmountOfSubscribedUsers(reportId, daoReport.getAmountOfSubscribedUsers(reportId) + 1);
        return daoReport.subscribe(userId, reportId);
    }

    public boolean isSubscribed(long userId, long reportId) {
        return daoReport.isSubscribed(userId, reportId);
    }

    public void unsubscribeUserFromReport(long userId, long reportId) {
        daoReport.setAmountOfSubscribedUsers(reportId, daoReport.getAmountOfSubscribedUsers(reportId) - 1);
        daoReport.unsubscribe(userId, reportId);
    }

    public void addNewReportToConference(long conferenceId, DTOReport report) {
        daoReport.addNew(conferenceId, report);
    }

    public void deleteReport(long id) {
        daoReport.delete(id);
    }

    public  void setVisitorsAmount(long reportId, int amount) {
        daoReport.setComersAmount(reportId,amount);
    }
}
