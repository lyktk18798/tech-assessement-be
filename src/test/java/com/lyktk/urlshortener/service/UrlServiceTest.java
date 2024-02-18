package com.lyktk.urlshortener.service;
import com.lyktk.urlshortener.entity.UrlMapping;
import com.lyktk.urlshortener.exception.InvalidDataException;
import com.lyktk.urlshortener.exception.NoDataFoundException;
import com.lyktk.urlshortener.repository.UrlRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.util.Date;

import static com.lyktk.urlshortener.utils.UrlUtils.generateShortUrl;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@SpringBootTest
public class UrlServiceTest {
    @Mock
    private UrlRepository urlRepository;

    @Mock
    private StringRedisTemplate redisTemplate;

    @Mock
    private ValueOperations<String, String> valueOperations;

    @InjectMocks
    private UrlService urlService;

    @Test
    public void saveMappingUrl_Success() {
        String originalUrl = "http://example.com";
        String shortUrl = generateShortUrl(originalUrl, 6);

        UrlMapping savedMapping = new UrlMapping();
        savedMapping.setOriginalUrl(originalUrl);
        savedMapping.setShortUrl(shortUrl);
        savedMapping.setCreationDate(new Date());

        when(urlRepository.save(any(UrlMapping.class))).thenReturn(savedMapping);
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);

        UrlMapping result = urlService.saveMappingUrl(originalUrl);

        assertEquals(originalUrl, result.getOriginalUrl());
        assertEquals(shortUrl, result.getShortUrl());
        verify(valueOperations).set(shortUrl, originalUrl);
    }

    @Test
    void saveMappingUrl_NullOriginalUrl() {
        Assertions.assertThrows(InvalidDataException.class, () -> urlService.saveMappingUrl(null));
    }

    @Test
    void saveMappingUrl_EmptyOriginalUrl() {
        Assertions.assertThrows(InvalidDataException.class, () -> urlService.saveMappingUrl(""));
    }

    @Test
    void getOriginalUrl_NullShortUrl() {
        Assertions.assertThrows(InvalidDataException.class, () -> urlService.getOriginalUrl(null));
    }

    @Test
    void getOriginalUrl_EmptyShortUrl() {
        Assertions.assertThrows(InvalidDataException.class, () -> urlService.getOriginalUrl(""));
    }

    @Test
    public void getOriginalUrl_FromCache() {
        String originalUrl = "http://example.com";
        String shortUrl = generateShortUrl(originalUrl, 6);

        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get(shortUrl)).thenReturn(originalUrl);

        String result = urlService.getOriginalUrl(shortUrl);

        assertEquals(originalUrl, result);
    }


    @Test
    public void getOriginalUrl_FromDatabase() {
        String originalUrl = "http://example.com";
        String shortUrl = generateShortUrl(originalUrl, 6);

        UrlMapping urlMapping = new UrlMapping();
        urlMapping.setOriginalUrl(originalUrl);

        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get(shortUrl)).thenReturn(null);
        when(urlRepository.findByShortUrl(shortUrl)).thenReturn(urlMapping);

        String result = urlService.getOriginalUrl(shortUrl);

        assertEquals(originalUrl, result);
    }


    @Test
    public void getOriginalUrl_NotFound() {
        String shortUrl = "abc123";

        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get(shortUrl)).thenReturn(null);
        when(urlRepository.findByShortUrl(shortUrl)).thenReturn(null);

        assertThrows(NoDataFoundException.class, () -> urlService.getOriginalUrl(shortUrl));
    }

    @Test
    void getOriginalUrl_FromCacheAndDatabase() {
        String originalUrl = "http://example.com";
        String shortUrl = generateShortUrl(originalUrl, 6);

        // Mock the behavior of StringRedisTemplate to return a value from the cache
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get(shortUrl)).thenReturn(originalUrl);

        // Mock the behavior of UrlRepository to return null (indicating not found in the database)
        when(urlRepository.findByShortUrl(shortUrl)).thenReturn(null);

        String retrievedUrl = urlService.getOriginalUrl(shortUrl);

        Assertions.assertEquals(originalUrl, retrievedUrl);

        // Verify that opsForValue().get(shortUrl) is invoked
        verify(redisTemplate.opsForValue()).get(shortUrl);

        // Verify that findByShortUrl(shortUrl) is not invoked
        verify(urlRepository, never()).findByShortUrl(shortUrl);
    }

    @Test
    void generateShortUrl_Success() {
        String originalUrl = "http://example.com";
        int length = 6;

        String shortUrl = generateShortUrl(originalUrl, length);

        Assertions.assertNotNull(shortUrl);
        Assertions.assertEquals(length, shortUrl.length());
    }

    @Test
    void generateShortUrl_NullOriginalUrl() {
        Assertions.assertThrows(InvalidDataException.class, () -> generateShortUrl(null, 6));
    }

    @Test
    void generateShortUrl_EmptyOriginalUrl() {
        Assertions.assertThrows(InvalidDataException.class, () -> generateShortUrl("", 6));
    }

    @Test
    void generateShortUrl_NegativeLength() {
        Assertions.assertThrows(StringIndexOutOfBoundsException.class, () -> generateShortUrl("http://example.com", -1));
    }
}
