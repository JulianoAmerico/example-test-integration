package com.example.api;

import com.example.api.model.HandlerResponse;
import com.example.service.exception.ServiceException;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@SuppressWarnings("ALL")
@RestControllerAdvice
public class HandlerController {

    @ExceptionHandler({ServiceException.class})
    public ResponseEntity<HandlerResponse> serviceException(ServiceException e) {
        return new ResponseEntity<>(new HandlerResponse(e.getMessage()),
                HttpStatusCode.valueOf(e.getStatus()));
    }
}
