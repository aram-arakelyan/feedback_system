package com.example.feedback.repository;

import com.example.feedback.entity.Feedback;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface FeedbackRepository extends JpaRepository<Feedback, Long> {
    List<Feedback> findByEstablishmentId(Long restaurantId);

    boolean existsByCustomerIdAndEstablishmentId(Long customerId, Long establishmentId);

    Optional<Feedback> findByIdAndCustomerId(Long id, Long customerId);
}
