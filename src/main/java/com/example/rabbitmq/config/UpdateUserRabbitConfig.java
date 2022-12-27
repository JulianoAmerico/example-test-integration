package com.example.rabbitmq.config;

import lombok.RequiredArgsConstructor;
import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@SuppressWarnings("ALL")
@Configuration
@RequiredArgsConstructor
public class UpdateUserRabbitConfig {

    private final UpdateUserProperties properties;

    @Bean
    public Declarables updateUserQueueDeclarables() {
        final var exchange = new DirectExchange(properties.getExchange());
        final var queue = QueueBuilder.durable(properties.getQueue())
                .deadLetterExchange(properties.getDeadLetter().getExchange())
                .build();
        final var binding = BindingBuilder.bind(queue)
                .to(exchange)
                .with(properties.getRoutingKey());

        return new Declarables(exchange, queue, binding);
    }

    @Bean
    public Declarables updateUserDeadLetterDeclarables() {
        final var deadLetter = properties.getDeadLetter();
        final var exchange = new FanoutExchange(deadLetter.getExchange());
        final var queue = QueueBuilder.durable(deadLetter.getQueue()).build();
        final var binding = BindingBuilder.bind(queue).to(exchange);

        return new Declarables(exchange, queue, binding);
    }

    @Bean
    public Declarables updateUserParkingLotDeclarables() {
        final var exchange = new FanoutExchange(properties.getParkingLot().getExchange());
        final var queue = QueueBuilder.durable(properties.getParkingLot().getQueue()).build();
        final var binding = BindingBuilder.bind(queue).to(exchange);

        return new Declarables(exchange, queue, binding);
    }
}
