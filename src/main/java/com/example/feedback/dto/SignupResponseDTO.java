package com.example.feedback.dto;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class SignupResponseDTO {
    private String email;
    private Long id;
}
