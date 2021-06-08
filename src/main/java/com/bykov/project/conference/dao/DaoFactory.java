package com.bykov.project.conference.dao;

public abstract class DaoFactory {
    private static volatile DaoFactory daoFactory;

    public abstract DaoUser getUserDao();

    public abstract DaoReport getReportDao();

    public abstract DaoSpeaker getSpeakerDao();

    public abstract DaoConference getConferenceDao();

   static public DaoFactory getInstance() {
        if (daoFactory == null) {
            synchronized (DaoFactory.class) {
                if (daoFactory == null) {
                    daoFactory = new JdbcDaoFactory();
                }
            }
        }
        return daoFactory;
    }
}

