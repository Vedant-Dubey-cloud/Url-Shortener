package com.example.urlShortener.service;

import com.example.urlShortener.dto.ShortenUrlRequest;
import com.example.urlShortener.dto.ShortenUrlResponse;
import com.example.urlShortener.entity.Url;
import com.example.urlShortener.exception.ResourceNotFoundException;
import com.example.urlShortener.repository.UrlRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class UrlService {

    private final UrlRepository urlRepository;
    private final RedisTemplate<String, String> redisTemplate;

    public ShortenUrlResponse shortenUrl(ShortenUrlRequest request) {

        Url existingUrl = urlRepository
                .findByOriginalUrl(request.getOriginalUrl())
                .orElse(null);

        if (existingUrl != null) {
            return new ShortenUrlResponse(
                    "http://localhost:8080/api/urls/" + existingUrl.getShortCode()
            );
        }

        Url url = new Url();

        url.setOriginalUrl(request.getOriginalUrl());
        url.setShortCode(UUID.randomUUID().toString().substring(0, 8));
        url.setCreatedAt(LocalDateTime.now());
        url.setClickCount(0L);

        urlRepository.save(url);

        log.info("Short URL created: {}", url.getShortCode());

        redisTemplate.opsForValue().set(
                url.getShortCode(),
                url.getOriginalUrl()
        );

        return new ShortenUrlResponse(
                "http://localhost:8080/api/urls/" + url.getShortCode()
        );
    }

    public ResponseEntity<Void> redirect(String shortCode) {

        String originalUrl = redisTemplate.opsForValue().get(shortCode);

        if (originalUrl == null) {

            Url url = urlRepository.findByShortCode(shortCode)
                    .orElseThrow(() -> new ResourceNotFoundException("Short URL not found"));

            originalUrl = url.getOriginalUrl();

            redisTemplate.opsForValue().set(shortCode, originalUrl);

            url.setClickCount(url.getClickCount() + 1);
            urlRepository.save(url);

        } else {

            Url url = urlRepository.findByShortCode(shortCode)
                    .orElseThrow(() -> new ResourceNotFoundException("Short URL not found"));

            url.setClickCount(url.getClickCount() + 1);
            urlRepository.save(url);
        }

        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.LOCATION, originalUrl);
        log.info("Redirecting short code: {}", shortCode);

        return new ResponseEntity<>(headers, HttpStatus.FOUND);
    }




    public Url getByShortCode(String shortCode) {
        return urlRepository.findByShortCode(shortCode)
                .orElseThrow(() -> new ResourceNotFoundException("Short URL not found"));    }




    public List<Url> getAllUrls() {
        return urlRepository.findAll();
    }


    public void deleteUrl(Long id) {

        Url url = urlRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("URL not found"));

        redisTemplate.delete(url.getShortCode());

        urlRepository.delete(url);
    }
}
