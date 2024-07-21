package com.fintech.deal.service.impl;

import com.fintech.deal.model.DealType;
import com.fintech.deal.repository.TypeRepository;
import com.fintech.deal.service.TypeService;
import com.onedlvb.advice.LogLevel;
import com.onedlvb.advice.annotation.AuditLog;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TypeServiceImpl implements TypeService {

    @NonNull
    private final TypeRepository typeRepository;

    @Override
    @AuditLog(logLevel = LogLevel.INFO)
    public DealType getStatusById(String id) {
        return typeRepository.getDealTypeById(id);
    }

}
