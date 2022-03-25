package com.example.thandbag.exceptionHandler;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public class RestApiException {

    private HttpStatus httpStatus;
    private String errorMessage;

}