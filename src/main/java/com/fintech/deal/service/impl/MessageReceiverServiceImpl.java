package com.fintech.deal.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fintech.deal.service.ContractorService;
import com.fintech.deal.service.MessageReceiverService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class MessageReceiverServiceImpl implements MessageReceiverService {

    @NonNull
    private ContractorService contractorService;

    @NonNull
    private ObjectMapper objectMapper;
    @Override
    @RabbitListener(queues = {"deals_contractor_queue"})
    public void receiveContractorUpdates(String message) throws JsonProcessingException {
        Map<String, String> contractorMap = new HashMap<>();
        JsonNode jsonNode = objectMapper.readTree(message);

        contractorMap.put("id", jsonNode.get("id").asText());
        contractorMap.put("name", jsonNode.get("name").asText());
        contractorMap.put("inn", jsonNode.get("inn").asText());

        contractorService.updateContractorByReceivedMessage(contractorMap);

    }

}