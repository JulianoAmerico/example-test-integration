package com.example.service.exception;

import lombok.Getter;

public class ServiceException extends Exception {

    @Getter
    private final Integer status;

    public ServiceException(String message, Throwable cause) {
        super(message, cause);
        status = 400;
    }
}
