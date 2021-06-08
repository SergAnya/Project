package com.bykov.project.conference.exceptions;

public class CommandFailedException extends RuntimeException{
    public CommandFailedException(String message) {
        super(message);
    }
}
