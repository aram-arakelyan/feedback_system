package com.example.feedback.service;

import com.example.feedback.dto.CustomerDTO;
import com.example.feedback.dto.SignupResponseDTO;
import com.example.feedback.entity.Customer;
import com.example.feedback.repository.CustomerRepository;
import com.example.feedback.service.impl.CustomerServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class CustomerServiceImplTest {

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private CustomerServiceImpl customerService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Inject a valid Base64-encoded secret key
        ReflectionTestUtils.setField(customerService, "jwtSecretKey", "dGVzdF9zZWNyZXRfa2V5Cg==");
        ReflectionTestUtils.setField(customerService, "jwtExpirationTime", 86400000L); // 24 hours in milliseconds
    }

    @Nested
    @DisplayName("Tests for login")
    class LoginTests {

//        @Test
//        @DisplayName("Should return LoginResponseDTO when login is successful")
//        void testLoginSuccess() {
//            // Given
//            Customer customer = new Customer();
//            customer.setEmail("test@example.com");
//            customer.setPassword("encodedPassword");
//
//            CustomerDTO customerDTO = new CustomerDTO();
//            customerDTO.setEmail("test@example.com");
//            customerDTO.setPassword("rawPassword");
//
//            when(customerRepository.findByEmail("test@example.com")).thenReturn(Optional.of(customer));
//            when(passwordEncoder.matches("rawPassword", "encodedPassword")).thenReturn(true);
//
//            // When
//            LoginResponseDTO response = customerService.login(customerDTO);
//
//            // Then
//            assertEquals("fake-jwt-token", response.getToken()); // Replace with actual token logic if applicable
//            verify(customerRepository, times(1)).findByEmail("test@example.com");
//            verify(passwordEncoder, times(1)).matches("rawPassword", "encodedPassword");
//        }

        @Test
        @DisplayName("Should throw IllegalArgumentException when email is not found")
        void testLoginEmailNotFound() {
            when(customerRepository.findByEmail("nonexistent@example.com")).thenReturn(Optional.empty());

            CustomerDTO customerDTO = new CustomerDTO();
            customerDTO.setEmail("nonexistent@example.com");

            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> customerService.login(customerDTO));
            assertEquals("Email not found", exception.getMessage());
        }

        @Test
        @DisplayName("Should throw SecurityException when password is incorrect")
        void testLoginInvalidPassword() {
            Customer customer = new Customer();
            customer.setEmail("test@example.com");
            customer.setPassword("encodedPassword");

            when(customerRepository.findByEmail("test@example.com")).thenReturn(Optional.of(customer));
            when(passwordEncoder.matches("wrongPassword", "encodedPassword")).thenReturn(false);

            CustomerDTO customerDTO = new CustomerDTO();
            customerDTO.setEmail("test@example.com");
            customerDTO.setPassword("wrongPassword");

            SecurityException exception = assertThrows(SecurityException.class, () -> customerService.login(customerDTO));
            assertEquals("Invalid email or password", exception.getMessage());
        }
    }

    @Nested
    @DisplayName("Tests for saveUser")
    class SaveUserTests {

        @Test
        @DisplayName("Should save and return SignupResponseDTO when user does not already exist")
        void testSaveUserSuccess() {
            // Given
            CustomerDTO customerDTO = new CustomerDTO();
            customerDTO.setEmail("test@example.com");
            customerDTO.setPassword("rawPassword");

            Customer savedCustomer = new Customer();
            savedCustomer.setId(1L);
            savedCustomer.setEmail("test@example.com");

            when(customerRepository.findByEmail("test@example.com")).thenReturn(Optional.empty());
            when(passwordEncoder.encode("rawPassword")).thenReturn("encodedPassword");
            when(customerRepository.save(any(Customer.class))).thenReturn(savedCustomer);

            SignupResponseDTO response = customerService.saveUser(customerDTO);

            assertEquals("test@example.com", response.getEmail());
            assertEquals(1L, response.getId());
        }

        @Test
        @DisplayName("Should throw IllegalStateException when email is already registered")
        void testSaveUserConflict() {
            Customer existingCustomer = new Customer();
            existingCustomer.setEmail("test@example.com");

            when(customerRepository.findByEmail("test@example.com")).thenReturn(Optional.of(existingCustomer));

            CustomerDTO customerDTO = new CustomerDTO();
            customerDTO.setEmail("test@example.com");

            IllegalStateException exception = assertThrows(IllegalStateException.class, () -> customerService.saveUser(customerDTO));
            assertEquals("Email already registered", exception.getMessage());
        }
    }
}
