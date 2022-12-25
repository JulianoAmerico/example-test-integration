package com.example.api;

import com.example.api.model.UserRequest;
import com.example.api.model.UserResponse;
import com.example.entity.User;
import com.example.service.UserService;
import com.example.service.exception.ServiceException;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@SuppressWarnings("ALL")
@RestController
@RequiredArgsConstructor
public class UserApi {

    private final UserService service;

    @PostMapping("/users")
    public UserResponse save(@RequestBody @Valid UserRequest request) throws ServiceException {
        var savedUser = service.save(User.toDomain(request));
        return User.toResponse(savedUser);
    }
}
