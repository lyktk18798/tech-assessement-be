package com.lyktk.urlshortener.service;

import com.lyktk.urlshortener.entity.UrlMapping;
import com.lyktk.urlshortener.exception.InvalidDataException;
import com.lyktk.urlshortener.exception.NoDataFoundException;
import com.lyktk.urlshortener.repository.UrlRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.Date;
import java.util.UUID;

import static com.lyktk.urlshortener.utils.UrlUtils.generateShortUrl;

@Service
@Slf4j
public class UrlService {
    @Autowired
    UrlRepository urlRepository;

    @Autowired
    StringRedisTemplate redisTemplate;

    public UrlMapping saveMappingUrl(String originalUrl) {
        if (ObjectUtils.isEmpty(originalUrl)) {
            throw new InvalidDataException("Original URL cannot be null or empty");
        }

        // Check if the original URL already exists in the database
        UrlMapping existingMapping = urlRepository.findByOriginalUrl(originalUrl);
        if (existingMapping != null) {
            log.info("URL mapping already exists for Original URL: {}", originalUrl);
            return existingMapping;
        }

        // Generate short URL
        String shortUrl = generateUniqueShortUrl(originalUrl, 6);

        // Save the original and short URL in the database
        UrlMapping urlMapping = new UrlMapping();
        urlMapping.setId(UUID.randomUUID());
        urlMapping.setOriginalUrl(originalUrl);
        urlMapping.setShortUrl(shortUrl);
        urlMapping.setCreationDate(new Date());
        urlRepository.save(urlMapping);
        log.info("URL shorten URL generated = {}", urlMapping.getShortUrl());
        redisTemplate.opsForValue().set(shortUrl, originalUrl);
        return urlMapping;
    }

    public String getOriginalUrl(String shortUrl) {
        if (ObjectUtils.isEmpty(shortUrl)) {
            throw new InvalidDataException("Original URL cannot be null or empty");
        }
        String url = redisTemplate.opsForValue().get(shortUrl);
        if (url != null) {
            log.info("Retrieved from Cache: {}", url);
            return url;
        }
        UrlMapping urlMapping = urlRepository.findByShortUrl(shortUrl);
        if(urlMapping == null) {
            throw new NoDataFoundException(shortUrl);
        }
        return urlMapping.getOriginalUrl();
    }

    private String generateUniqueShortUrl(String originalUrl, int length) {
        String shortUrl = generateShortUrl(originalUrl, length);
        if(ObjectUtils.isEmpty(shortUrl)) {
            throw new RuntimeException("Error occurred");
        }
        UrlMapping existingMapping = urlRepository.findByShortUrl(shortUrl);

        // If the generated short URL already exists, modify it until it's unique
        while (existingMapping != null) {
            // Modify the shortened URL to create a new one
            shortUrl = generateShortUrl(originalUrl + System.currentTimeMillis(), length);
            existingMapping = urlRepository.findByShortUrl(shortUrl);
        }

        return shortUrl;
    }

}
