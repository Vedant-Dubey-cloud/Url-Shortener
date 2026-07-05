package com.example.urlShortener.controller;

import com.example.urlShortener.dto.ShortenUrlRequest;
import com.example.urlShortener.dto.ShortenUrlResponse;
import com.example.urlShortener.entity.Url;
import com.example.urlShortener.service.UrlService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/urls")
@RequiredArgsConstructor
public class UrlController {

    private final UrlService urlService;

    @PostMapping
    public ShortenUrlResponse shortenUrl(@RequestBody @Valid ShortenUrlRequest request) {
        return urlService.shortenUrl(request);
    }

    @GetMapping
    public List<Url> getAllUrls() {
        return urlService.getAllUrls();
    }

    @GetMapping("/details/{shortCode}")
    public Url getUrl(@PathVariable String shortCode) {
        return urlService.getByShortCode(shortCode);
    }

    @DeleteMapping("/{id}")
    public void deleteUrl(@PathVariable Long id) {
        urlService.deleteUrl(id);
    }

    @GetMapping("/{shortCode}")
    public ResponseEntity<Void> redirect(@PathVariable String shortCode) {
        return urlService.redirect(shortCode);
    }
}