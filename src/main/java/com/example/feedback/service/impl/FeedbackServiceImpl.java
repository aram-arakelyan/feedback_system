package com.example.feedback.service.impl;

import com.example.feedback.dto.FeedbackDTO;
import com.example.feedback.dto.FeedbackResponseDTO;
import com.example.feedback.entity.Establishment;
import com.example.feedback.entity.Feedback;
import com.example.feedback.entity.Customer;
import com.example.feedback.repository.FeedbackRepository;
import com.example.feedback.repository.CustomerRepository;
import com.example.feedback.service.EstablishmentService;
import com.example.feedback.service.FeedbackService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Service class for managing feedbacks.
 */
@Service
public class FeedbackServiceImpl implements FeedbackService {

    private final EstablishmentService establishmentService;
    private final FeedbackRepository feedbackRepository;
    private final CustomerRepository customerRepository;

    public FeedbackServiceImpl(EstablishmentService establishmentService, FeedbackRepository feedbackRepository, CustomerRepository customerRepository) {
        this.establishmentService = establishmentService;
        this.feedbackRepository = feedbackRepository;
        this.customerRepository = customerRepository;
    }

    /**
     * Retrieves feedbacks for a specific establishment.
     *
     * @param establishmentId The ID of the establishment.
     * @return A list of feedbacks.
     */
    @Override
    @Transactional(readOnly = true)
    public List<FeedbackResponseDTO> findByEstablishmentId(Long establishmentId) {
        List<Feedback> feedbackList = feedbackRepository.findByEstablishmentId(establishmentId);

        if (feedbackList.isEmpty()) {
            throw new IllegalArgumentException("No feedback found for the given establishment ID: " + establishmentId);
        }

        return feedbackList.stream()
                .map(feedback -> FeedbackResponseDTO.builder()
                        .id(feedback.getId())
                        .title(feedback.getTitle())
                        .textComment(feedback.getTextComment())
                        .score(feedback.getScore())
                        .customerEmail(feedback.getCustomer().getEmail())
                        .establishmentName(feedback.getEstablishment().getName())
                        .build())
                .toList();
    }

    /**
     * Creates feedback for an establishment.
     *
     * @param feedbackDTO The feedback details provided by the user.
     * @return The created feedback response.
     */
    @Override
    @Transactional
    public FeedbackResponseDTO createFeedback(FeedbackDTO feedbackDTO) {
        Customer customer = getAuthenticatedUser();

        // Enforce the one-feedback-per-user constraint
        if (feedbackRepository.existsByCustomerIdAndEstablishmentId(customer.getId(), feedbackDTO.getEstablishmentId())) {
            throw new IllegalStateException("User has already submitted feedback for this establishment.");
        }

        Establishment establishment = establishmentService.findById(feedbackDTO.getEstablishmentId());

        Feedback feedback = new Feedback();
        feedback.setCustomer(customer);
        feedback.setEstablishment(establishment);
        feedback.setTitle(feedbackDTO.getTitle());
        feedback.setTextComment(feedbackDTO.getTextComment());
        feedback.setScore(feedbackDTO.getScore());

        Feedback savedFeedback = feedbackRepository.save(feedback);

        return FeedbackResponseDTO.builder()
                .id(savedFeedback.getId())
                .title(savedFeedback.getTitle())
                .textComment(savedFeedback.getTextComment())
                .score(savedFeedback.getScore())
                .customerEmail(savedFeedback.getCustomer().getEmail())
                .establishmentName(savedFeedback.getEstablishment().getName())
                .build();
    }

    /**
     * Deletes feedback for the authenticated user.
     *
     * @param feedbackId The ID of the feedback to delete.
     */
    @Override
    @Transactional
    public void deleteFeedbackForAuthenticatedCustomer(Long feedbackId) {
        Customer customer = getAuthenticatedUser();

        Feedback feedback = feedbackRepository.findByIdAndCustomerId(feedbackId, customer.getId())
                .orElseThrow(() -> new IllegalArgumentException("Feedback not found for the given feedback ID: " + feedbackId));

        feedbackRepository.delete(feedback);
    }

    private Customer getAuthenticatedUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();

        return customerRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Authenticated user not found."));
    }
}
