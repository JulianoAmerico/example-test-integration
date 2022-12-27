package com.example.service;

import com.example.entity.User;
import com.example.rabbitmq.config.UpdateUserProperties;
import com.example.repository.UserRepository;
import com.example.service.exception.ServiceException;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class UserService {

    private final UserRepository repository;
    private final RabbitTemplate rabbitTemplate;
    private final UpdateUserProperties properties;

    @Transactional(rollbackFor = Exception.class)
    public User save(User user) throws ServiceException {
        try {
            user.isValidToSave(() -> new IllegalArgumentException("Invalid data"));
            return repository.save(user);
        } catch (Exception e) {
            throw new ServiceException("Error when try to save user", e);
        }
    }

    public void produceUpdate(User user) throws ServiceException {
        try {
            rabbitTemplate.convertAndSend(
                    properties.getExchange(),
                    properties.getRoutingKey(),
                    User.toMessage(user)
            );
        } catch (Exception e) {
            throw new ServiceException("Error when try to update user", e);
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public void update(User user) throws ServiceException {
        try {
            var userTarget = repository.getReferenceById(user.getId()).copy(user.getName(), user.getDocument());
            repository.save(userTarget);
        } catch (Exception e) {
            throw new ServiceException("Error when try to update user", e);
        }
    }
}
