package com.lyktk.urlshortener.exception;

public class InvalidDataException extends RuntimeException {
    public InvalidDataException(String originalUrl) {
        super(String.format("Original URL %s invalid", originalUrl));
    }
}
