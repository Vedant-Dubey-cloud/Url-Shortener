package com.example.urlShortener.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ShortenUrlResponse {

    private String shortUrl;
}