package com.bykov.project.conference.exceptions;

public class FailedToSendException extends RuntimeException{
    public FailedToSendException(Throwable cause) {
        super(cause);
    }
}
