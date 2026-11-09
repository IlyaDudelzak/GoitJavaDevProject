package com.dudelzak.goit.java.devproject.shortlink.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.GONE)
public class LinkDisabledException extends RuntimeException {
    public LinkDisabledException() {
        super("This link is disabled");
    }
}
