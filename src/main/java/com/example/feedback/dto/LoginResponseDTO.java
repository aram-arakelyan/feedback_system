package com.example.feedback.dto;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class LoginResponseDTO {
    private String token;

    //TODO add expiration time
}
