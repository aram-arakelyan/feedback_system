package com.example.feedback.service.impl;

import com.example.feedback.entity.Establishment;
import com.example.feedback.repository.EstablishmentRepository;
import com.example.feedback.service.EstablishmentService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Service class for managing establishments.
 * Provides operations for retrieving, saving, and updating establishments.
 */
@Service
public class EstablishmentServiceImpl implements EstablishmentService {

    private final EstablishmentRepository establishmentRepository;

    public EstablishmentServiceImpl(EstablishmentRepository establishmentRepository) {
        this.establishmentRepository = establishmentRepository;
    }

    /**
     * Retrieves an establishment by its ID.
     *
     * @param id The ID of the establishment.
     * @return The establishment with the specified ID.
     * @throws IllegalArgumentException If the establishment is not found.
     */
    @Override
    @Transactional(readOnly = true)
    public Establishment findById(Long id) {
        return establishmentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Establishment with ID " + id + " not found."));
    }

    /**
     * Retrieves establishments by their type.
     *
     * @param type The type of establishments to retrieve.
     * @return A list of establishments of the specified type.
     */
    @Override
    @Transactional(readOnly = true)
    public List<Establishment> findByType(String type) {
        return establishmentRepository.findByType(type);
    }

    /**
     * Saves or updates an establishment.
     *
     * @param establishment The establishment entity to save or update.
     * @return The saved or updated establishment.
     */
    @Override
    @Transactional
    public Establishment save(Establishment establishment) {
        return establishmentRepository.save(establishment);
    }
}
