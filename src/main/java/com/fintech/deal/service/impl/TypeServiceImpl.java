package com.fintech.deal.service.impl;

import com.fintech.deal.model.DealType;
import com.fintech.deal.repository.TypeRepository;
import com.fintech.deal.service.TypeService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TypeServiceImpl implements TypeService {

    @NonNull
    private final TypeRepository typeRepository;

    @Override
    public DealType getStatusById(String id) {
        return typeRepository.getDealTypeById(id);
    }

}
