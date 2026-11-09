package com.dudelzak.goit.java.devproject.security;

import com.dudelzak.goit.java.devproject.security.dto.AuthRequest;
import com.dudelzak.goit.java.devproject.security.dto.RegisterRequest;
import com.dudelzak.goit.java.devproject.security.dto.AuthResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Authentication", description = "Endpoints for user authentication and registration")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthService authService;

    @Operation(summary = "User login", description = "Authenticates user credentials and returns a JWT access token")
    @PostMapping("/token")
    public AuthResponse login(@Valid @RequestBody AuthRequest request) {
        return authService.authenticate(request);
    }

    @Operation(summary = "Register user", description = "Registers a new user and returns a JWT access token")
    @PostMapping("/register")
    public AuthResponse register(@Valid @RequestBody RegisterRequest request) {
        return authService.register(request);
    }
}
