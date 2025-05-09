package com.tarekAndAli.RecipeManagement.service;

import com.tarekAndAli.RecipeManagement.model.Recipe;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;

public interface RecipeService {
    Recipe create(Recipe recipe);
    Recipe getById(String id);
    Recipe update(String id, Recipe recipe);
    void delete(String id);
    Page<Recipe> list(Pageable pageable);
    List<Recipe> searchByName(String name);
    List<Recipe> filterByCategory(String category);
    List<Recipe> filterByMaxTime(int maxTime);
    Page<Recipe> filter(String category, int maxTime, Pageable pageable);
    Page<Recipe> findAll(Pageable pageable);
    Page<Recipe> findByNameContainingIgnoreCase(String name, Pageable pageable);
    List<Recipe> getFavorites(String userId);
    List<Recipe> getByOwnerId(String userId);
}