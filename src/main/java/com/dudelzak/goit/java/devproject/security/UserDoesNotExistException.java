package com.dudelzak.goit.java.devproject.security;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.UNAUTHORIZED)
public class UserDoesNotExistException extends RuntimeException {
    public UserDoesNotExistException() {
        super("Wrong username or password");
    }
}
