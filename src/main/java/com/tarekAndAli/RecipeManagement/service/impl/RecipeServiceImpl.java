package com.tarekAndAli.RecipeManagement.service.impl;

import com.tarekAndAli.RecipeManagement.model.Recipe;
import com.tarekAndAli.RecipeManagement.repository.RecipeRepository;
import com.tarekAndAli.RecipeManagement.service.RecipeService;
import com.tarekAndAli.RecipeManagement.service.UserService;
import com.tarekAndAli.RecipeManagement.service.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class RecipeServiceImpl implements RecipeService {

    private final RecipeRepository recipeRepository;
    private final UserService userService;

    public RecipeServiceImpl(RecipeRepository recipeRepository, UserService userService) {
        this.recipeRepository = recipeRepository;
        this.userService = userService;
    }

    @Override
    public Recipe create(Recipe recipe) {
        recipe.setCreatedAt(LocalDateTime.now());
        return recipeRepository.save(recipe);
    }

    @Override
    public Recipe getById(String id) {
        return recipeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Recipe not found: " + id));
    }

    @Override
    public Recipe update(String id, Recipe updated) {
        Recipe existing = getById(id);
        existing.setName(updated.getName());
        existing.setIngredients(updated.getIngredients());
        existing.setInstructions(updated.getInstructions());
        existing.setCookingTime(updated.getCookingTime());
        existing.setCategory(updated.getCategory());
        return recipeRepository.save(existing);
    }

    @Override
    public void delete(String id) {
        if (!recipeRepository.existsById(id)) {
            throw new ResourceNotFoundException("Recipe not found: " + id);
        }
        recipeRepository.deleteById(id);
    }

    @Override
    public Page<Recipe> list(Pageable pageable) {
        return recipeRepository.findAll(pageable);
    }

    @Override
    public List<Recipe> searchByName(String name) {
        return recipeRepository.findByNameContainingIgnoreCase(name);
    }

    @Override
    public List<Recipe> filterByCategory(String category) {
        return recipeRepository.findByCategory(category);
    }

    @Override
    public List<Recipe> filterByMaxTime(int maxTime) {
        return recipeRepository.findByCookingTimeLessThanEqual(maxTime);
    }

    @Override
    public Page<Recipe> filter(String category, int maxTime, Pageable pageable) {
        return recipeRepository.findByCategoryAndCookingTimeLessThanEqual(category, maxTime, pageable);
    }

    @Override
    public Page<Recipe> findAll(Pageable pageable) {
        return recipeRepository.findAll(pageable);
    }

    @Override
    public Page<Recipe> findByNameContainingIgnoreCase(String name, Pageable pageable) {
        return recipeRepository.findByNameContainingIgnoreCase(name, pageable);
    }

    @Override
    public List<Recipe> getFavorites(String userId) {
        return userService.getFavorites(userId);
    }

    @Override
    public List<Recipe> getByOwnerId(String userId) {
        return recipeRepository.findByOwnerId(userId);
    }
}