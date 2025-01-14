package com.example.feedback.service;

import com.example.feedback.dto.FeedbackDTO;
import com.example.feedback.dto.FeedbackResponseDTO;
import com.example.feedback.entity.Customer;
import com.example.feedback.entity.Establishment;
import com.example.feedback.entity.Feedback;
import com.example.feedback.repository.FeedbackRepository;
import com.example.feedback.repository.CustomerRepository;
import com.example.feedback.service.impl.FeedbackServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class FeedbackServiceImplTest {

    @Mock
    private FeedbackRepository feedbackRepository;

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private EstablishmentService establishmentService;

    @InjectMocks
    private FeedbackServiceImpl feedbackService;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        Authentication auth = new UsernamePasswordAuthenticationToken(
                "testuser@example.com",
                "password"
        );
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(auth);
        SecurityContextHolder.setContext(context);

        Customer mockUser = new Customer();
        mockUser.setId(99L);
        mockUser.setEmail("testuser@example.com");
        mockUser.setPassword("encodedPassword");

        when(customerRepository.findByEmail("testuser@example.com"))
                .thenReturn(Optional.of(mockUser));

        // Mock Feedback for feedback repository
        Feedback feedback = new Feedback();
        feedback.setId(1L);

        when(feedbackRepository.findById(1L)).thenReturn(Optional.of(feedback));
    }

    @Nested
    @DisplayName("Tests for findByEstablishmentId")
    class FindByEstablishmentIdTests {

        @Test
        @DisplayName("Should return list of feedbacks for valid establishment ID")
        void testFindByEstablishmentId() {
            Feedback feedback = getFeedback();

            when(feedbackRepository.findByEstablishmentId(1L))
                    .thenReturn(List.of(feedback));

            List<FeedbackResponseDTO> result = feedbackService.findByEstablishmentId(1L);

            assertEquals(1, result.size());
            assertEquals("Great Service", result.get(0).getTitle());
            verify(feedbackRepository, times(1)).findByEstablishmentId(1L);
        }

        private static Feedback getFeedback() {
            Establishment establishment = new Establishment();
            establishment.setName("Restaurant");

            Customer customer = new Customer();
            customer.setEmail("customer@example.com");

            Feedback feedback = new Feedback();
            feedback.setId(1L);
            feedback.setTitle("Great Service");
            feedback.setTextComment("Excellent food");
            feedback.setScore(9);
            feedback.setCustomer(customer);
            feedback.setEstablishment(establishment);
            return feedback;
        }

        @Test
        @DisplayName("Should throw exception when no feedbacks found")
        void testFindByEstablishmentIdNoFeedbacks() {
            when(feedbackRepository.findByEstablishmentId(1L))
                    .thenReturn(Collections.emptyList());

            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                    feedbackService.findByEstablishmentId(1L));

            assertEquals("No feedback found for the given establishment ID: 1", exception.getMessage());
            verify(feedbackRepository, times(1)).findByEstablishmentId(1L);
        }
    }

    @Nested
    @DisplayName("Tests for createFeedback")
    class CreateFeedbackTests {

        @Test
        @DisplayName("Should create feedback successfully")
        void testCreateFeedbackSuccess() {
            Establishment establishment = new Establishment();
            establishment.setId(1L);
            establishment.setName("Restaurant");

            Customer customer = new Customer();
            customer.setEmail("customer@example.com");

            FeedbackDTO feedbackDTO = new FeedbackDTO();
            feedbackDTO.setEstablishmentId(1L);
            feedbackDTO.setTitle("Great Service");
            feedbackDTO.setTextComment("Excellent food");
            feedbackDTO.setScore(9);

            Feedback savedFeedback = new Feedback();
            savedFeedback.setId(1L);
            savedFeedback.setTitle("Great Service");
            savedFeedback.setTextComment("Excellent food");
            savedFeedback.setScore(9);
            savedFeedback.setCustomer(customer);
            savedFeedback.setEstablishment(establishment);

            when(securityContext.getAuthentication()).thenReturn(authentication);
            when(authentication.getPrincipal()).thenReturn("customer@example.com");
            when(customerRepository.findByEmail("customer@example.com")).thenReturn(Optional.of(customer));
            when(establishmentService.findById(1L)).thenReturn(establishment);
            when(feedbackRepository.save(any(Feedback.class))).thenReturn(savedFeedback);

            FeedbackResponseDTO response = feedbackService.createFeedback(feedbackDTO);

            assertEquals("Great Service", response.getTitle());
            assertEquals("customer@example.com", response.getCustomerEmail());
            verify(feedbackRepository, times(1)).save(any(Feedback.class));
        }
    }

    @Nested
    @DisplayName("Tests for deleteFeedbackForAuthenticatedCustomer")
    class DeleteFeedbackTests {

        @Test
        @DisplayName("Should throw exception if feedback not found")
        void testDeleteFeedbackNotFound() {
            when(feedbackRepository.findById(1L)).thenReturn(Optional.empty());

            Exception exception = assertThrows(IllegalArgumentException.class,
                    () -> feedbackService.deleteFeedbackForAuthenticatedCustomer(1L)
            );

            assertEquals("Feedback not found for the given feedback ID: 1",
                    exception.getMessage());

        }
    }
}
