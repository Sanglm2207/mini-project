package com.kaidev99.training.miniproject.controller;

import com.kaidev99.training.miniproject.domain.dto.request.LoginRequest;
import com.kaidev99.training.miniproject.domain.dto.request.RefreshTokenRequest;
import com.kaidev99.training.miniproject.domain.dto.request.SignUpRequest;
import com.kaidev99.training.miniproject.domain.dto.response.ApiResponse;
import com.kaidev99.training.miniproject.domain.dto.response.JwtAuthResponse;
import com.kaidev99.training.miniproject.domain.dto.response.UserResponse;
import com.kaidev99.training.miniproject.domain.model.User;
import com.kaidev99.training.miniproject.service.AuthenticationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationService authenticationService;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<UserResponse>> signup(@RequestBody SignUpRequest request) {
        User savedUser = authenticationService.signup(request);
        UserResponse userResponse = UserResponse.fromUser(savedUser);
        return ResponseEntity.ok(ApiResponse.success(userResponse, "User registered successfully"));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<JwtAuthResponse>> login(@RequestBody LoginRequest request) {
        JwtAuthResponse jwtResponse = authenticationService.login(request);
        return ResponseEntity.ok(ApiResponse.success(jwtResponse, "Login successful"));
    }

    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<JwtAuthResponse>> refreshToken(@RequestBody RefreshTokenRequest request) {
        JwtAuthResponse jwtResponse = authenticationService.refreshToken(request);
        return ResponseEntity.ok(ApiResponse.success(jwtResponse, "Token refreshed successfully"));
    }
}
