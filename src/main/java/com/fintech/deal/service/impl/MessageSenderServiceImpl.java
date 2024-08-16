package com.fintech.deal.service.impl;

import com.fintech.deal.service.MessageSenderService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MessageSenderServiceImpl implements MessageSenderService {

    @NonNull
    private AmqpTemplate amqpTemplate;

    @Value("${rabbitmq.queue.name}")
    private String queueActiveMainBorrowerQueueName;

    @Override
    public void send(String message) {
        amqpTemplate.convertAndSend(queueActiveMainBorrowerQueueName, message);
    }

}
