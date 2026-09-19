package com.smartflow.smartflow.controller;

import com.smartflow.smartflow.dto.UserResponse;
import com.smartflow.smartflow.entity.User;
import com.smartflow.smartflow.service.UserService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import com.smartflow.smartflow.dto.LoginRequest;
import com.smartflow.smartflow.service.JwtService;
import com.smartflow.smartflow.dto.LoginResponse;

@RestController
public class UserController {

    private final UserService userService;
    private final JwtService jwtService;

   public UserController(UserService userService,
                      JwtService jwtService) {
    this.userService = userService;
    this.jwtService = jwtService;
}

    @PostMapping("/api/users")
    public UserResponse createUser(@Valid @RequestBody User user) {

        User savedUser = userService.saveUser(user);

        UserResponse response = new UserResponse();
        response.setId(savedUser.getId());
        response.setName(savedUser.getName());
        response.setEmail(savedUser.getEmail());
        response.setPhone(savedUser.getPhone());
        response.setRole(savedUser.getRole());

        return response;
    }
@PostMapping("/api/login")
public LoginResponse login(@Valid @RequestBody LoginRequest loginRequest) {

    User user = userService.loginUser(
            loginRequest.getEmail(),
            loginRequest.getPassword()
    );

    String token = jwtService.generateToken(user.getEmail());

    LoginResponse response = new LoginResponse();
    response.setToken(token);
    response.setId(user.getId());
    response.setName(user.getName());
    response.setEmail(user.getEmail());
    response.setPhone(user.getPhone());
    response.setRole(user.getRole());

    return response;
}
}