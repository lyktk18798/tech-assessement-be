package com.lyktk.urlshortener.exception;

public class NoDataFoundException extends RuntimeException {
    public NoDataFoundException(String url) {
        super(String.format("Url mapping %s not found", url));
    }
}
