package com.example.feedback.controller;

import com.example.feedback.dto.FeedbackDTO;
import com.example.feedback.dto.FeedbackResponseDTO;
import com.example.feedback.service.FeedbackService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for managing feedbacks.
 */
@RestController
@RequestMapping("/api/v1/feedback")
public class FeedbackController {

    private final FeedbackService feedbackService;

    public FeedbackController(FeedbackService feedbackService) {
        this.feedbackService = feedbackService;
    }

    /**
     * Creates feedback for an establishment.
     *
     * @param feedbackDTO The feedback details provided by the user.
     * @return The created feedback response.
     */
    @Operation(summary = "Create feedback for an establishment")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Feedback created successfully"),
            @ApiResponse(responseCode = "400", description = "Validation error or invalid input data"),
            @ApiResponse(responseCode = "409", description = "Duplicate feedback submission"),
            @ApiResponse(responseCode = "500", description = "Unexpected internal server error")
    })
    @PostMapping
    public ResponseEntity<FeedbackResponseDTO> createFeedback(@Valid @RequestBody FeedbackDTO feedbackDTO) {
        FeedbackResponseDTO feedbackResponse = feedbackService.createFeedback(feedbackDTO);
        return ResponseEntity.ok(feedbackResponse);
    }

    /**
     * Retrieves feedbacks for a specific establishment.
     *
     * @param establishmentId The ID of the establishment.
     * @return A list of feedbacks for the establishment.
     */
    @Operation(summary = "Retrieve feedback for a specific establishment")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Feedbacks retrieved successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid establishment ID"),
            @ApiResponse(responseCode = "404", description = "No feedback found for the given establishment"),
            @ApiResponse(responseCode = "500", description = "Unexpected internal server error")
    })
    @GetMapping
    public ResponseEntity<List<FeedbackResponseDTO>> getFeedbackByEstablishment(@RequestParam Long establishmentId) {
        List<FeedbackResponseDTO> feedbacks = feedbackService.findByEstablishmentId(establishmentId);
        return ResponseEntity.ok(feedbacks);
    }

    /**
     * Deletes feedback by ID for the authenticated user.
     *
     * @param id The ID of the feedback to delete.
     * @return A success message.
     */
    @Operation(summary = "Delete feedback by ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Feedback deleted successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid feedback ID"),
            @ApiResponse(responseCode = "404", description = "Feedback not found"),
            @ApiResponse(responseCode = "500", description = "Unexpected internal server error")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFeedback(@PathVariable Long id) {
        feedbackService.deleteFeedbackForAuthenticatedCustomer(id);
        return ResponseEntity.noContent().build();
    }
}
