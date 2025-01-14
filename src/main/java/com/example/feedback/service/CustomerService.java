package com.example.feedback.service;

import com.example.feedback.dto.LoginResponseDTO;
import com.example.feedback.dto.SignupResponseDTO;
import com.example.feedback.dto.CustomerDTO;

public interface CustomerService {
    LoginResponseDTO login(CustomerDTO customerDto);

    SignupResponseDTO saveUser(CustomerDTO customerDto);
}
