package com.example.rabbitmq.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "rabbit.listener.update-user")
@Data
public class UpdateUserProperties {

    private String queue;
    private String exchange;
    private String routingKey;
    private DeadLetter deadLetter;
    private ParkingLot parkingLot;

    @Data
    public static class DeadLetter {
        private String queue;
        private String exchange;
    }

    @Data
    public static class ParkingLot {
        private String queue;
        private String exchange;
    }
}
