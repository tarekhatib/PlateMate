package com.tarekAndAli.RecipeManagement.controller;

import com.tarekAndAli.RecipeManagement.dto.AuthRequest;
import com.tarekAndAli.RecipeManagement.dto.UserDto;
import com.tarekAndAli.RecipeManagement.model.User;
import com.tarekAndAli.RecipeManagement.security.JwtUtil;
import com.tarekAndAli.RecipeManagement.service.UserMapper;
import com.tarekAndAli.RecipeManagement.service.UserService;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import org.springframework.validation.annotation.Validated;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@Validated
public class AuthController {
    private final UserService userService;
    private final AuthenticationManager authManager;
    private final JwtUtil jwtUtil;
    private final UserMapper userMapper;

    public AuthController(UserService userService,
                          AuthenticationManager authManager,
                          JwtUtil jwtUtil,
                          UserMapper userMapper) {
        this.userService = userService;
        this.authManager = authManager;
        this.jwtUtil = jwtUtil;
        this.userMapper = userMapper;
    }

    @PostMapping("/register")
    public ResponseEntity<UserDto> register(@RequestBody @Valid User user) {
        User created = userService.register(user);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(userMapper.toDto(created));
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@RequestBody @Valid AuthRequest req) {
        authManager.authenticate(
                new UsernamePasswordAuthenticationToken(req.getUsername(), req.getPassword())
        );
        String token = jwtUtil.generateToken(req.getUsername());
        return ResponseEntity.ok(Map.of("token", token));
    }
}