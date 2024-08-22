package com.fintech.deal.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fintech.deal.service.ContractorService;
import com.fintech.deal.service.MessageReceiverService;
import com.rabbitmq.client.Channel;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * An implementation of {@link MessageReceiverService} interface.
 * @author Matushkin Anton
 */
@Service
@RequiredArgsConstructor
public class MessageReceiverServiceImpl implements MessageReceiverService {

    @NonNull
    private ContractorService contractorService;

    @NonNull
    private ObjectMapper objectMapper;
    @Override
    @RabbitListener(queues = {"deals_contractor_queue"})
    public void receiveContractorUpdates(Message message,
                                         Channel channel,
                                         @Header(AmqpHeaders.DELIVERY_TAG) long tag) throws IOException {
        try {
            Map<String, String> contractorMap = new HashMap<>();
            JsonNode jsonNode = objectMapper.readTree(message.getBody());

            contractorMap.put("id", jsonNode.get("id").asText());
            contractorMap.put("name", jsonNode.get("name").asText());
            contractorMap.put("inn", jsonNode.get("inn").asText());
            contractorMap.put("timestamp", message.getMessageProperties().getHeaders().get("timestamp").toString());

            contractorService.updateContractorByReceivedMessage(contractorMap);
            channel.basicAck(tag, false);

        } catch (Exception e) {
            channel.basicReject(tag, false);
        }
    }

}
