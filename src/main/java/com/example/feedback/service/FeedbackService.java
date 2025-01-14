package com.example.feedback.service;

import com.example.feedback.dto.FeedbackDTO;
import com.example.feedback.dto.FeedbackResponseDTO;

import java.util.List;

public interface FeedbackService {
    List<FeedbackResponseDTO> findByEstablishmentId(Long establishmentId);

    FeedbackResponseDTO createFeedback(FeedbackDTO feedbackDTO);

    void deleteFeedbackForAuthenticatedCustomer(Long feedbackId);
}
