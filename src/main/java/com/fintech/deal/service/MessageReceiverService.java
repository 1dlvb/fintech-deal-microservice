package com.fintech.deal.service;

import com.fasterxml.jackson.core.JsonProcessingException;

public interface MessageReceiverService {

    void receiveContractorUpdates(String message) throws JsonProcessingException;

}
