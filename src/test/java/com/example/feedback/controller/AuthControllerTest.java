package com.example.feedback.controller;

import com.example.feedback.dto.CustomerDTO;
import com.example.feedback.dto.LoginResponseDTO;
import com.example.feedback.dto.SignupResponseDTO;
import com.example.feedback.exception.GlobalExceptionHandler;
import com.example.feedback.service.CustomerService;
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

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Mock
    private CustomerService customerService;

    @InjectMocks
    private AuthController authController;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(authController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Nested
    @DisplayName("Tests for signup endpoint")
    class SignupTests {

        @Test
        @DisplayName("Should return 200 when signup is successful")
        void testSignupSuccess() throws Exception {
            SignupResponseDTO mockResponse = SignupResponseDTO.builder()
                    .email("test@example.com")
                    .id(1L)
                    .build();

            Mockito.when(customerService.saveUser(any(CustomerDTO.class)))
                    .thenReturn(mockResponse);

            mockMvc.perform(post("/api/v1/auth/signup")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("""
                                    {
                                      "email": "test@example.com",
                                      "password": "Pass123!",
                                      "firstName": "John",
                                      "lastName": "Doe"
                                    }
                                    """))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.email").value("test@example.com"))
                    .andExpect(jsonPath("$.id").value(1L));
        }

        @Test
        @DisplayName("Should return 409 when user with the given email already exists")
        void testSignupConflict() throws Exception {
            // Given
            Mockito.when(customerService.saveUser(any(CustomerDTO.class)))
                    .thenThrow(new IllegalStateException("User with the given email already exists"));

            // When & Then
            mockMvc.perform(post("/api/v1/auth/signup")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("""
                                    {
                                      "email": "existing@example.com",
                                      "password": "Pass123!",
                                      "firstName": "Jane",
                                      "lastName": "Doe"
                                    }
                                    """))
                    .andExpect(status().isConflict())
                    .andExpect(jsonPath("$.status").value(409))
                    .andExpect(jsonPath("$.error").value("Conflict"))
                    .andExpect(jsonPath("$.details").value("User with the given email already exists"));
        }
    }

    @Nested
    @DisplayName("Tests for login endpoint")
    class LoginTests {

        @Test
        @DisplayName("Should return 200 when login is successful")
        void testLoginSuccess() throws Exception {
            LoginResponseDTO mockResponse = LoginResponseDTO.builder()
                    .token("fake-jwt-token")
                    .build();

            Mockito.when(customerService.login(any(CustomerDTO.class)))
                    .thenReturn(mockResponse);

            mockMvc.perform(post("/api/v1/auth/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("""
                                    {
                                      "email": "test@example.com",
                                      "password": "Pass123!"
                                    }
                                    """))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.token").value("fake-jwt-token"));
        }

        @Test
        @DisplayName("Should return 401 when login credentials are invalid")
        void testLoginInvalidCredentials() throws Exception {
            Mockito.when(customerService.login(any(CustomerDTO.class)))
                    .thenThrow(new SecurityException("Invalid email or password"));

            mockMvc.perform(post("/api/v1/auth/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("""
                                    {
                                      "email": "test@example.com",
                                      "password": "WrongPass!"
                                    }
                                    """))
                    .andExpect(status().isUnauthorized())
                    .andExpect(jsonPath("$.status").value(401))
                    .andExpect(jsonPath("$.error").value("Unauthorized"))
                    .andExpect(jsonPath("$.details").value("Invalid email or password"));
        }
    }
}
