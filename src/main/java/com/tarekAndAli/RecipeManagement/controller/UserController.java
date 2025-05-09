package com.tarekAndAli.RecipeManagement.controller;

import com.tarekAndAli.RecipeManagement.dto.RecipeDto;
import com.tarekAndAli.RecipeManagement.dto.UserDto;
import com.tarekAndAli.RecipeManagement.model.Recipe;
import com.tarekAndAli.RecipeManagement.model.User;
import com.tarekAndAli.RecipeManagement.repository.UserRepository;
import com.tarekAndAli.RecipeManagement.security.JwtUtil;
import com.tarekAndAli.RecipeManagement.service.UserService;
import com.tarekAndAli.RecipeManagement.service.RecipeMapper;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;
    private final RecipeMapper mapper;
    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;

    public UserController(UserService userService,
                          RecipeMapper mapper,
                          JwtUtil jwtUtil,
                          UserRepository userRepository) {
        this.userService = userService;
        this.mapper = mapper;
        this.jwtUtil = jwtUtil;
        this.userRepository = userRepository;
    }

    // 1. List a user's favorites
    @GetMapping("/me/favorites")
    public List<RecipeDto> getFavorites(@AuthenticationPrincipal UserDetails user) {
        List<Recipe> favorites = userService.getFavorites(user.getUsername()); //HERE
        return mapper.toDto(favorites);
    }

    // 2. Add one to favorites
    @PostMapping("/me/favorites/{recipeId}")
    public ResponseEntity<Void> addFavorite(
            @AuthenticationPrincipal UserDetails user,
            @PathVariable String recipeId) {
        String username = user.getUsername();
        String userId = userService.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found: " + username))
                .getId();
        userService.addFavorite(userId, recipeId);
        return ResponseEntity.ok().build();
    }

    // 3. Remove one from favorites
    @DeleteMapping("/me/favorites/{recipeId}")
    public ResponseEntity<Void> removeFavorite(
            @AuthenticationPrincipal UserDetails user,
            @PathVariable String recipeId) {
        String username = user.getUsername();
        String userId = userService.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found: " + username))
                .getId();
        userService.removeFavorite(userId, recipeId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/me")
    public ResponseEntity<UserDto> getCurrentUser(@RequestHeader("Authorization") String authHeader) {
        String token = authHeader.replace("Bearer ", "");
        String username = jwtUtil.getUsernameFromToken(token);
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        UserDto dto = new UserDto(user.getId(), user.getUsername(), user.getEmail(), user.getFavorites());
        return ResponseEntity.ok(dto);
    }
}