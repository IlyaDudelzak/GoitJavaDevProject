package com.dudelzak.goit.java.devproject.shortlink.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class NoSuchLinkException extends RuntimeException {
    public NoSuchLinkException() {
        super("A short link with this code does not exist");
    }
}
