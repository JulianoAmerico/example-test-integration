package com.example.api.model;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.LocalDateTime;
import java.util.UUID;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record UserResponse(
        UUID id,
        String name,
        String document,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
