package com.example.feedback.service;

import com.example.feedback.entity.Establishment;

import java.util.List;

public interface EstablishmentService {
    Establishment findById(Long id);

    List<Establishment> findByType(String type);

    Establishment save(Establishment establishment);
}
