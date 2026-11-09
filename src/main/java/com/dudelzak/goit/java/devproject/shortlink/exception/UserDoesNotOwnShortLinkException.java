package com.dudelzak.goit.java.devproject.shortlink.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.FORBIDDEN)
public class UserDoesNotOwnShortLinkException extends RuntimeException {
    public UserDoesNotOwnShortLinkException() {
        super("You do not have permission to access or modify this link");
    }
}
