package com.dudelzak.goit.java.devproject.shortlink.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.GONE)
public class LinkExpiredException extends RuntimeException {
    public LinkExpiredException() {
        super("This link has passed its expiration date");
    }
}
