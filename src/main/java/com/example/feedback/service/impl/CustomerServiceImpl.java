package com.example.feedback.service.impl;

import com.example.feedback.dto.LoginResponseDTO;
import com.example.feedback.dto.SignupResponseDTO;
import com.example.feedback.dto.CustomerDTO;
import com.example.feedback.entity.Customer;
import com.example.feedback.repository.CustomerRepository;
import com.example.feedback.service.CustomerService;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.util.Base64;
import java.util.Date;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class CustomerServiceImpl implements CustomerService {
    private final CustomerRepository customerRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${security.jwt.secret-key:default_secret_key}")
    private String jwtSecretKey;

    @Value("${security.jwt.expiration-time:86400000}") // Default: 24 hours in milliseconds
    private long jwtExpirationTime;

    /**
     * Authenticates a user and generates a JWT token upon successful login.
     *
     * @param customerDto The login credentials provided by the user.
     * @return A {@link LoginResponseDTO} containing the generated JWT token.
     * @throws IllegalArgumentException If the email is not found.
     * @throws SecurityException        If the password is invalid.
     */
    @Override
    public LoginResponseDTO login(CustomerDTO customerDto) {
        Customer customer = customerRepository.findByEmail(customerDto.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("Email not found"));

        if (!passwordEncoder.matches(customerDto.getPassword(), customer.getPassword())) {
            throw new SecurityException("Invalid email or password");
        }

        String token = generateToken(customer);

        return LoginResponseDTO.builder()
                .token(token)
                .build();
    }

    /**
     * Registers a new user with the provided details.
     *
     * @param customerDto The user details for registration.
     * @return A {@link SignupResponseDTO} containing the registered user's information.
     * @throws IllegalStateException If the email is already registered.
     */
    @Override
    public SignupResponseDTO saveUser(CustomerDTO customerDto) {
        customerRepository.findByEmail(customerDto.getEmail())
                .ifPresent(customer -> {
                    throw new IllegalStateException("Email already registered");
                });

        Customer customer = new Customer();
        customer.setEmail(customerDto.getEmail());
        customer.setPassword(passwordEncoder.encode(customerDto.getPassword()));

        return Optional.of(customerRepository.save(customer))
                .map(u -> SignupResponseDTO.builder()
                        .id(u.getId())
                        .email(u.getEmail())
                        .build())
                .orElseThrow(() -> new RuntimeException("Error occurred while saving user"));
    }

    private String generateToken(Customer customer) {
        // Convert secret key to Key object
        byte[] keyBytes = Base64.getDecoder().decode(jwtSecretKey);
        Key key = new SecretKeySpec(keyBytes, SignatureAlgorithm.HS512.getJcaName());

        // Generate token
        return Jwts.builder()
                .setSubject(customer.getEmail())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + jwtExpirationTime))
                .signWith(key, SignatureAlgorithm.HS512)
                .compact();
    }
}
