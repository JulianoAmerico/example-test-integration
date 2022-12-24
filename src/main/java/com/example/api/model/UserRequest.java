package com.example.api.model;

import jakarta.validation.constraints.NotBlank;

public record UserRequest(
        @NotBlank(message = "name cannot be null ou empty")
        String name,
        @NotBlank(message = "document cannot be null ou empty")
        String document
) {
}