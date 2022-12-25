package com.example.api.model;

import java.util.List;

public record HandlerResponse(String message, List<String> messageErrors) {
}
