package com.example.rabbitmq.listener;

import com.example.entity.User;
import com.example.rabbitmq.message.UserMessage;
import com.example.service.UserService;
import com.example.service.exception.ServiceException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class UpdateUserListener {

    private final UserService service;

    @RabbitListener(queues = "${rabbit.listener.update-user.queue}")
    public void execute(UserMessage message) throws ServiceException {
        log.info("Consuming user message to update...");
        service.update(User.toDomain(message));
    }
}
