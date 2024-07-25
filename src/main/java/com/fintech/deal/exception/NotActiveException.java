package com.fintech.deal.exception;

/**
 * An exception class for not-active fields in database
 * @author Matushkin Anton
 */
public class NotActiveException extends Exception {

    public NotActiveException(String message) {
        super(message);
    }

}
