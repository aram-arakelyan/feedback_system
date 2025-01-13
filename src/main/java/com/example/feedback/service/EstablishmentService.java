package com.example.feedback.service;

import com.example.feedback.entity.Establishment;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface EstablishmentService {
    @Transactional(readOnly = true)
    Establishment findById(Long id);

    @Transactional(readOnly = true)
    List<Establishment> findByType(String type);

    @Transactional
    Establishment save(Establishment establishment);
}
