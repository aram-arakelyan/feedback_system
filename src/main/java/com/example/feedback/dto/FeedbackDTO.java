package com.example.feedback.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class FeedbackDTO {
    @NotNull
    private Long establishmentId;

    @NotBlank
    @Size(max = 50)
    private String title;

    @Size(max = 1000)
    private String textComment;

    @Min(0)
    @Max(10)
    private int score;
}