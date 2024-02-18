package com.lyktk.urlshortener.repository;

import com.lyktk.urlshortener.entity.UrlMapping;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface UrlRepository extends MongoRepository<UrlMapping, String> {
    UrlMapping findByShortUrl(String shortUrl);
}