package com.example.feedback.dto;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class FeedbackResponseDTO {
    private Long id;
    private String title;
    private String textComment;
    private int score;
    private String customerEmail;
    private String establishmentName;
}
