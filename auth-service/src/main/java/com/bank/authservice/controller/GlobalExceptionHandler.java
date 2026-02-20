package com.bank.authservice.controller;

import com.bank.authservice.dto.response.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ApiResponse<String>> handleException(RuntimeException ex){
        return  ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.<String>builder()
                        .success(false)
                        .message(ex.getMessage())
                        .data(null)
                        .build());

    }
}
