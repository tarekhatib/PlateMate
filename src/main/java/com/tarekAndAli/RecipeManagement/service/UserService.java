package com.tarekAndAli.RecipeManagement.service;

import com.tarekAndAli.RecipeManagement.model.Recipe;
import com.tarekAndAli.RecipeManagement.model.User;

import java.util.List;
import java.util.Optional;

public interface UserService {
    User register(User user);                      // create new user
    Optional<User> findByUsername(String username); // lookup for authentication
    User getById(String id);                        // throw if missing
    void addFavorite(String userId, String recipeId);
    void removeFavorite(String userId, String recipeId);
    List<Recipe> getFavorites(String userId);
}