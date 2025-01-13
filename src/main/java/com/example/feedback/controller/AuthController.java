package com.example.feedback.controller;

import com.example.feedback.dto.LoginResponseDTO;
import com.example.feedback.dto.SignupResponseDTO;
import com.example.feedback.dto.CustomerDTO;
import com.example.feedback.service.CustomerService;
import jakarta.validation.Valid;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final CustomerService customerService;

    public AuthController(CustomerService customerService) {
        this.customerService = customerService;
    }

    /**
     * Endpoint for user signup.
     *
     * @param customerDTO The user details for registration.
     * @return A response indicating the success of the signup operation.
     */
    @Operation(summary = "User signup", description = "Allows a new user to register with the system.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "User registered successfully"),
            @ApiResponse(responseCode = "400", description = "Validation error or invalid input data"),
            @ApiResponse(responseCode = "409", description = "User with the given email already exists"),
            @ApiResponse(responseCode = "500", description = "Unexpected internal server error")
    })
    @PostMapping("/signup")
    public ResponseEntity<SignupResponseDTO> signup(@Valid @RequestBody CustomerDTO customerDTO) {
        return ResponseEntity.ok(customerService.saveUser(customerDTO));
    }

    /**
     * Endpoint for user login.
     *
     * @param customerDTO The user credentials for login.
     * @return A response containing the login token and user details.
     */
    @Operation(summary = "User login", description = "Allows a user to log in and receive a JWT token.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Login successful"),
            @ApiResponse(responseCode = "400", description = "Validation error or invalid input data"),
            @ApiResponse(responseCode = "401", description = "Invalid email or password"),
            @ApiResponse(responseCode = "500", description = "Unexpected internal server error")
    })
    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@Valid @RequestBody CustomerDTO customerDTO) {
        return ResponseEntity.ok(customerService.login(customerDTO));
    }
}
