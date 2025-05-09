package com.tarekAndAli.RecipeManagement.service.impl;

import com.tarekAndAli.RecipeManagement.model.Recipe;
import com.tarekAndAli.RecipeManagement.model.User;
import com.tarekAndAli.RecipeManagement.repository.UserRepository;
import com.tarekAndAli.RecipeManagement.repository.RecipeRepository;
import com.tarekAndAli.RecipeManagement.service.UserService;
import com.tarekAndAli.RecipeManagement.service.exception.ResourceNotFoundException;
import com.tarekAndAli.RecipeManagement.service.exception.DuplicateResourceException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final RecipeRepository recipeRepository;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepository, RecipeRepository recipeRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.recipeRepository = recipeRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public User register(User user) {
        if (userRepository.findByUsername(user.getUsername()).isPresent())
            throw new DuplicateResourceException("Username already in use");
        user.setFavorites(new ArrayList<>());
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    @Override
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @Override
    public User getById(String id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + id));
    }

    @Override
    public List<Recipe> getFavorites(String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return recipeRepository.findAllById(user.getFavorites());
    }

    @Override
    public void addFavorite(String userId, String recipeId) {
        User user = getById(userId);
        if (!recipeRepository.existsById(recipeId))
            throw new ResourceNotFoundException("Recipe not found: " + recipeId);
        if (!user.getFavorites().contains(recipeId)) {
            user.getFavorites().add(recipeId);
            userRepository.save(user);
        }
    }

    @Override
    public void removeFavorite(String userId, String recipeId) {
        User user = getById(userId);
        if (user.getFavorites().remove(recipeId)) {
            userRepository.save(user);
        }
    }
}