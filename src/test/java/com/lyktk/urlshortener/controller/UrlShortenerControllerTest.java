package com.lyktk.urlshortener.controller;

import com.lyktk.urlshortener.entity.UrlMapping;
import com.lyktk.urlshortener.exception.InvalidDataException;
import com.lyktk.urlshortener.exception.NoDataFoundException;
import com.lyktk.urlshortener.service.UrlService;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.net.URI;
import java.util.Date;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;


@SpringBootTest
public class UrlShortenerControllerTest {
    @Mock
    private UrlService urlService;

    @InjectMocks
    private UrlShortenerController urlShortenerController;

    @Test
    void getUrl_Success() {
        String shortUrl = "abc123";
        String originalUrl = "http://example.com";
        when(urlService.getOriginalUrl(shortUrl)).thenReturn(originalUrl);

        ResponseEntity<HttpStatus> response = urlShortenerController.getUrl(shortUrl);

        assertEquals(HttpStatus.MOVED_PERMANENTLY, response.getStatusCode());
        assertEquals(URI.create(originalUrl), response.getHeaders().getLocation());
    }

    @Test
    void create_ValidUrl() {
        String originalUrl = "http://example.com";
        String shortUrl = "abc123";
        when(urlService.saveMappingUrl(originalUrl)).thenReturn(new UrlMapping(UUID.randomUUID(), originalUrl, shortUrl, new Date()));

        ResponseEntity<?> response = urlShortenerController.create(originalUrl);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(shortUrl, response.getBody());
    }

    @Test
    void create_InvalidUrl() {
        String invalidUrl = "invalidUrl";
        doThrow(InvalidDataException.class).when(urlService).saveMappingUrl(invalidUrl);

        assertThrows(InvalidDataException.class, () -> urlShortenerController.create(invalidUrl));
    }

    @Test
    void getUrl_NullShortUrl() {
        assertThrows(NoDataFoundException.class, () -> urlShortenerController.getUrl(null));
    }

    @Test
    void create_NullUrl() {
        assertThrows(InvalidDataException.class, () -> urlShortenerController.create(null));
    }

    @Test
    void create_EmptyUrl() {
        assertThrows(InvalidDataException.class, () -> urlShortenerController.create(""));
    }
}
