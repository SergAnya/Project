package com.bykov.project.conference.dao;

public class JdbcDaoFactory extends DaoFactory {
    private final DaoUser daoUser = new JdbcDaoUser();
    private final DaoReport daoReport = new JdbcDaoReport();
    private final DaoSpeaker daoSpeaker = new JdbcDaoSpeaker();
    private final DaoConference daoConference = new JdbcDaoConference();

    @Override
    public DaoUser getUserDao() {
        return daoUser;
    }

    @Override
    public DaoReport getReportDao() {
        return daoReport;
    }

    @Override
    public DaoSpeaker getSpeakerDao() {
        return daoSpeaker;
    }

    @Override
    public DaoConference getConferenceDao() {
        return daoConference;
    }

}
