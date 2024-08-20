package com.fintech.deal.service;

import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;

import java.io.IOException;

/**
 * Service interface for receiving and processing messages.
 * @author Matushkin Anton
 */
public interface MessageReceiverService {

    /**
     * Processes messages containing contractor updates.
     * <p>
     * This method is invoked when a message is received from RabbitMQ.
     * </p>
     *
     * @param message the received RabbitMQ message to be processed.
     * @param channel the RabbitMQ channel from which the message was received.
     * @param tag the delivery tag associated with the message, used for acknowledgment.
     * @throws IOException if an I/O error occurs during message processing or acknowledgment.
     */
    void receiveContractorUpdates(Message message,
                                  Channel channel,
                                  @Header(AmqpHeaders.DELIVERY_TAG) long tag) throws IOException;

}
