package com.example.feedback.service;

import com.example.feedback.dto.FeedbackDTO;
import com.example.feedback.dto.FeedbackResponseDTO;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface FeedbackService {
    @Transactional(readOnly = true)
    List<FeedbackResponseDTO> findByEstablishmentId(Long establishmentId);

    @Transactional
    FeedbackResponseDTO createFeedback(FeedbackDTO feedbackDTO);

    @Transactional
    void deleteFeedbackForAuthenticatedCustomer(Long feedbackId);
}
