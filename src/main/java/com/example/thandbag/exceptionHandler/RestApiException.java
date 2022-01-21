package com.example.thandbag.exceptionHandler;

import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

@Getter
@Setter
public class RestApiException {

    private String errorMessage;
    private HttpStatus httpStatus;
}