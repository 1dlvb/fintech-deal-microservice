package com.fintech.deal.exception;

/**
 * An exception class for not-active fields in database
 * @author Matushkin Anton
 */
public class NotActiveException extends Throwable {

    public NotActiveException(String message) {
        super(message);
    }

}
