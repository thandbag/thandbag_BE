package com.example.thandbag.exceptionHandler;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {


    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(value = {IllegalArgumentException.class})
    public RestApiException handleApiRequestException(IllegalArgumentException ex) {
        return new RestApiException(HttpStatus.BAD_REQUEST, ex.getMessage());
    }
}
