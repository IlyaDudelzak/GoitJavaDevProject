package com.dudelzak.goit.java.devproject.shortlink.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class ShortLinkWithCodeAlreadyExistsException extends RuntimeException {
    public ShortLinkWithCodeAlreadyExistsException() {
        super("A short link with this code already exists");
    }
}
