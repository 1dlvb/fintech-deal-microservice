package com.fintech.deal.service;

/**
 * Service interface for sending messages to a message broker.
 * <p>
 * @author Matushkin Anton
 */
public interface MessageSenderService {

    /**
     * Sends a message to the RabbitMQ.
     * <p>
     * This method is used to publish a message in a message broker.
     * </p>
     * @param message the content of the message to be sent.
     */
    void send(String message);

}
