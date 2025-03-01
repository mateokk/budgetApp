package com.example.budget.controllers;

import com.example.budget.dto.UserLoginDTO;
import com.example.budget.dto.UserRegisterDTO;
import com.example.budget.dto.UserResponseDTO;
import com.example.budget.mappers.UserMapper;
import com.example.budget.model.User;
import com.example.budget.services.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
@RequestMapping("/auth")
@RestController
public class UserController {

    private final UserService userService;
    private final UserMapper userMapper;


    public UserController(UserService userService, UserMapper userMapper) {
        this.userService = userService;
        this.userMapper = userMapper;
    }

    @PostMapping("/register")
    public ResponseEntity<UserResponseDTO> registerUser(@Valid @RequestBody UserRegisterDTO userRegisterDTO) {
        User user = userMapper.toUser(userRegisterDTO);
        User registeredUser = userService.registerUser(user);
        return ResponseEntity.ok(userMapper.toUserResponseDTO(registeredUser));
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody UserLoginDTO userLoginDTO) {
        String token = userService.verify(userLoginDTO.getUsername(), userLoginDTO.getPassword());
        return ResponseEntity.ok(token);
    }

    @GetMapping("/me")
    public ResponseEntity<UserResponseDTO> getCurrentUser(Principal principal) {
        User user = userService.findByUsername(principal.getName())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        return ResponseEntity.ok(userMapper.toUserResponseDTO(user));
    }

}
