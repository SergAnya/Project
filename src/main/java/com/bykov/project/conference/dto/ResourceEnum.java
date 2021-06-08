package com.bykov.project.conference.dto;


public enum ResourceEnum {
    MESSAGE("messages"),
    REGEXP("regexp"),
    ACCESS("access"),
    PAGES("pages"),
    BUSINESS_LOGIC("businessLogic"),
    EMAIL("email"),
    LOCALE_PATTERNS("localePatterns"),
    SQL_REQUESTS("sqlRequests");

    private final String bundleName;

    ResourceEnum(String bundleName) {
        this.bundleName = bundleName;
    }

    public String getBundleName() {
        return bundleName;
    }
}