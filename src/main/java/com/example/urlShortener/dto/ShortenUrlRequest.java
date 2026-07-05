package com.example.urlShortener.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ShortenUrlRequest {

    @NotBlank(message = "Original URL is required")
    private String originalUrl;
}