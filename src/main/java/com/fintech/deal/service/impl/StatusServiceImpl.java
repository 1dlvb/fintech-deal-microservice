package com.fintech.deal.service.impl;

import com.fintech.deal.model.DealStatus;
import com.fintech.deal.repository.StatusRepository;
import com.fintech.deal.service.StatusService;
import com.onedlvb.advice.LogLevel;
import com.onedlvb.advice.annotation.AuditLog;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class StatusServiceImpl implements StatusService {

    private final StatusRepository repository;

    @Override
    @AuditLog(logLevel = LogLevel.INFO)
    public DealStatus getStatusById(String id) {
        return repository.findById(id).orElse(null);
    }

}
