package com.lyktk.urlshortener.controller;

import com.lyktk.urlshortener.entity.UrlMapping;
import com.lyktk.urlshortener.exception.InvalidDataException;
import com.lyktk.urlshortener.exception.NoDataFoundException;
import com.lyktk.urlshortener.service.UrlService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.validator.routines.UrlValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;
import java.net.URI;

@RestController
@Slf4j
public class UrlShortenerController {

    @Autowired
    UrlService urlService;

    @GetMapping("/{id}")
    public ResponseEntity<HttpStatus> getUrl(@PathVariable String id) {
        String originalUrl= urlService.getOriginalUrl(id);
        if(ObjectUtils.isEmpty(originalUrl)) {
            throw new NoDataFoundException(originalUrl);
        }

        return ResponseEntity.status(HttpStatus.MOVED_PERMANENTLY)
                .location(URI.create(originalUrl))
                .header(HttpHeaders.CONNECTION, "close")
                .build();
    }

    @PostMapping("/create")
    public ResponseEntity<?> create(@RequestBody String url) {
        UrlValidator urlValidator = new UrlValidator(new String[]{"http", "https"});

        if (!urlValidator.isValid(url)) {
            log.error("It's invalid URL");
            throw new InvalidDataException(url);
        }

        UrlMapping urlMapping=  urlService.saveMappingUrl(url);
        return ResponseEntity.ok(urlMapping.getShortUrl());
    }
}
