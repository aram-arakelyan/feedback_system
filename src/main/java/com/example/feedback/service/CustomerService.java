package com.example.feedback.service;

import com.example.feedback.dto.LoginResponseDTO;
import com.example.feedback.dto.SignupResponseDTO;
import com.example.feedback.dto.CustomerDTO;
import org.springframework.transaction.annotation.Transactional;

public interface CustomerService {
    @Transactional
    LoginResponseDTO login(CustomerDTO customerDto);

    @Transactional
    SignupResponseDTO saveUser(CustomerDTO customerDto);
}
