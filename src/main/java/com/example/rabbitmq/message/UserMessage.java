package com.example.rabbitmq.message;


import java.io.Serializable;
import java.util.UUID;

public record UserMessage(
        UUID id,
        String name,
        String document
) implements Serializable {
}
