package com.example.api;

import com.example.api.model.UserRequest;
import com.example.api.model.UserResponse;
import com.example.entity.User;
import com.example.service.UserService;
import com.example.service.exception.ServiceException;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.UUID;

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

    @PutMapping("/users/{userId}")
    public void update(@RequestBody @Valid UserRequest request,
                       @PathVariable String userId //TODO create constraint to validate UUID
    ) throws ServiceException {
        var user = User.toDomain(request);
        user.setId(UUID.fromString(userId));
        service.produceUpdate(user);
    }
}
