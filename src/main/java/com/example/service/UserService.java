package com.example.service;

import com.example.entity.User;
import com.example.repository.UserRepository;
import com.example.service.exception.ServiceException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository repository;

    @Transactional(rollbackFor = Exception.class)
    public User save(User user) throws ServiceException {
        try {
            user.isValidToSave(() -> {
                log.error("Error when try to save user, invalid data {}", user);
                throw new IllegalArgumentException("Invalid data");
            });
            return repository.save(user);
        } catch (Exception e) {
            log.error("Erro when try to save user", e);
            throw new ServiceException("Error when try to save user", e);
        }
    }
}
