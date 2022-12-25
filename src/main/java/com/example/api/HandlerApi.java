package com.example.api;

import com.example.api.model.HandlerResponse;
import com.example.service.exception.ServiceException;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@SuppressWarnings("ALL")
@RestControllerAdvice
public class HandlerApi {

    @ExceptionHandler({ServiceException.class})
    public ResponseEntity<HandlerResponse> serviceException(ServiceException e) {
        var headers = new HttpHeaders();
        headers.add("Content-Type", MediaType.APPLICATION_JSON_VALUE);
        return new ResponseEntity<>(new HandlerResponse(e.getMessage(), null),
                headers,
                HttpStatus.valueOf(e.getStatus()));
    }

    @ExceptionHandler({MethodArgumentNotValidException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public HandlerResponse methodArgumentNotValidException(MethodArgumentNotValidException e) {
        var messageErrors = e.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .toList();
        return new HandlerResponse("errors", messageErrors);
    }
}
