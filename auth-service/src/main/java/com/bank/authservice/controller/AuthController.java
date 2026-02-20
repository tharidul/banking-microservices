package com.bank.authservice.controller;

import com.bank.authservice.dto.request.LoginRequest;
import com.bank.authservice.dto.request.RegisterRequest;
import com.bank.authservice.dto.response.ApiResponse;
import com.bank.authservice.dto.response.AuthResponse;
import com.bank.authservice.service.AuthService;
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

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<AuthResponse>> register(@RequestBody RegisterRequest request){

        AuthResponse authResponse = authService.register(request);

        return  ResponseEntity.ok(
                ApiResponse.<AuthResponse>builder()
                        .success(true)
                        .data(authResponse)
                        .message("User registered successfully")
                        .build()
        );
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@RequestBody LoginRequest request){
        AuthResponse authResponse = authService.login(request);
        return ResponseEntity.ok(
                ApiResponse.<AuthResponse>builder()
                        .success(true)
                        .data(authResponse)
                        .message("User logged in successfully")
                        .build()
        );
    }

}
