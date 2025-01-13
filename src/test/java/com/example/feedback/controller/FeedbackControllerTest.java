package com.example.feedback.controller;

import com.example.feedback.dto.FeedbackDTO;
import com.example.feedback.dto.FeedbackResponseDTO;
import com.example.feedback.exception.GlobalExceptionHandler;
import com.example.feedback.service.FeedbackService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class FeedbackControllerTest {

    @Mock
    private FeedbackService feedbackService;

    @InjectMocks
    private FeedbackController feedbackController;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(feedbackController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Nested
    @DisplayName("Tests for createFeedback")
    class CreateFeedbackTests {

        @Test
        @DisplayName("Should return 200 when feedback is created successfully")
        void testCreateFeedbackSuccess() throws Exception {
            FeedbackResponseDTO mockResponse = FeedbackResponseDTO.builder()
                    .id(1L)
                    .title("Amazing Service")
                    .textComment("The service was outstanding!")
                    .score(5)
                    .customerEmail("user@example.com")
                    .establishmentName("Coffee Shop")
                    .build();

            Mockito.when(feedbackService.createFeedback(any(FeedbackDTO.class)))
                    .thenReturn(mockResponse);

            mockMvc.perform(post("/api/v1/feedback")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("""
                                    {
                                        "title": "Amazing Service",
                                        "textComment": "The service was outstanding!",
                                        "score": 5,
                                        "establishmentId": 123
                                    }
                                    """))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(1L))
                    .andExpect(jsonPath("$.title").value("Amazing Service"))
                    .andExpect(jsonPath("$.textComment").value("The service was outstanding!"))
                    .andExpect(jsonPath("$.score").value(5))
                    .andExpect(jsonPath("$.customerEmail").value("user@example.com"))
                    .andExpect(jsonPath("$.establishmentName").value("Coffee Shop"));
        }

        @Test
        @DisplayName("Should return 400 when input is invalid, ignoring empty textComment")
        void testCreateFeedbackValidationError() throws Exception {
            mockMvc.perform(post("/api/v1/feedback")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("""
                                    {
                                        "title": "",
                                        "textComment": "",
                                        "score": -1,
                                        "establishmentId": null
                                    }
                                    """))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.status").value(400))
                    .andExpect(jsonPath("$.error").value("Validation Error"))
                    .andExpect(jsonPath("$.details").value("Invalid input data"))
                    .andExpect(jsonPath("$.validationErrors.title").value("must not be blank")) // Match default message
                    .andExpect(jsonPath("$.validationErrors.score").value("must be greater than or equal to 0"))
                    .andExpect(jsonPath("$.validationErrors.establishmentId").value("must not be null"));
        }
    }

    @Nested
    @DisplayName("Tests for getFeedbackByEstablishment")
    class GetFeedbackByEstablishmentTests {

        @Test
        @DisplayName("Should return 200 with a list of feedbacks")
        void testGetFeedbackByEstablishmentSuccess() throws Exception {
            FeedbackResponseDTO feedback1 = FeedbackResponseDTO.builder()
                    .id(1L)
                    .title("Great Food")
                    .textComment("Loved the pasta!")
                    .score(5)
                    .customerEmail("user1@example.com")
                    .establishmentName("Italian Restaurant")
                    .build();

            FeedbackResponseDTO feedback2 = FeedbackResponseDTO.builder()
                    .id(2L)
                    .title("Good Service")
                    .textComment("The staff was very friendly.")
                    .score(4)
                    .customerEmail("user2@example.com")
                    .establishmentName("Italian Restaurant")
                    .build();

            List<FeedbackResponseDTO> feedbacks = Arrays.asList(feedback1, feedback2);

            Mockito.when(feedbackService.findByEstablishmentId(123L))
                    .thenReturn(feedbacks);

            mockMvc.perform(get("/api/v1/feedback")
                            .param("establishmentId", "123")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.length()").value(2))
                    .andExpect(jsonPath("$[0].id").value(1L))
                    .andExpect(jsonPath("$[0].title").value("Great Food"))
                    .andExpect(jsonPath("$[0].textComment").value("Loved the pasta!"))
                    .andExpect(jsonPath("$[0].score").value(5))
                    .andExpect(jsonPath("$[1].id").value(2L))
                    .andExpect(jsonPath("$[1].title").value("Good Service"))
                    .andExpect(jsonPath("$[1].textComment").value("The staff was very friendly."))
                    .andExpect(jsonPath("$[1].score").value(4));
        }

        @Test
        @DisplayName("Should return 400 when establishmentId is invalid")
        void testGetFeedbackByEstablishmentInvalidId() throws Exception {
            mockMvc.perform(get("/api/v1/feedback")
                            .param("establishmentId", "invalid")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.status").value(400))
                    .andExpect(jsonPath("$.error").value("Type Mismatch"))
                    .andExpect(jsonPath("$.details").value("Invalid value for parameter: establishmentId"));
        }
    }

    @Nested
    @DisplayName("Tests for deleteFeedback")
    class DeleteFeedbackTests {

        @Test
        @DisplayName("Should return 200 when feedback is deleted successfully")
        void testDeleteFeedbackSuccess() throws Exception {
            Mockito.doNothing().when(feedbackService).deleteFeedbackForAuthenticatedCustomer(eq(1L));

            mockMvc.perform(delete("/api/v1/feedback/1")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(content().string("Feedback deleted successfully."));
        }

        @Test
        @DisplayName("Should return 404 when feedback is not found")
        void testDeleteFeedbackNotFound() throws Exception {
            Mockito.doThrow(new IllegalArgumentException("Feedback not found"))
                    .when(feedbackService).deleteFeedbackForAuthenticatedCustomer(eq(1L));

            mockMvc.perform(delete("/api/v1/feedback/1")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.status").value(404))
                    .andExpect(jsonPath("$.error").value("Not Found"))
                    .andExpect(jsonPath("$.details").value("Feedback not found"));
        }
    }
}
