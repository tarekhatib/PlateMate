package com.tarekAndAli.RecipeManagement.service;

import com.tarekAndAli.RecipeManagement.dto.*;
import com.tarekAndAli.RecipeManagement.model.Recipe;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class RecipeMapper {
    public Recipe toEntity(CreateRecipeRequest req) {
        Recipe r = new Recipe();
        r.setName(req.getName());
        r.setIngredients(req.getIngredients());
        r.setInstructions(req.getInstructions());
        r.setCookingTime(req.getCookingTime());
        r.setCategory(req.getCategory());
        r.setImageUrl(req.getImageUrl());
        return r;
    }

    public Recipe toEntity(UpdateRecipeRequest req, Recipe existing) {
        existing.setName(req.getName());
        existing.setIngredients(req.getIngredients());
        existing.setInstructions(req.getInstructions());
        existing.setCookingTime(req.getCookingTime());
        existing.setCategory(req.getCategory());
        existing.setImageUrl(req.getImageUrl());
        return existing;
    }

    public RecipeDto toDto(Recipe r) {
        RecipeDto dto = new RecipeDto();
        dto.setId(r.getId());
        dto.setName(r.getName());
        dto.setIngredients(r.getIngredients());
        dto.setInstructions(r.getInstructions());
        dto.setCookingTime(r.getCookingTime());
        dto.setCategory(r.getCategory());
        dto.setOwnerId(r.getOwnerId());
        dto.setOwnerUsername(r.getOwnerUsername());
        dto.setCreatedAt(r.getCreatedAt());
        dto.setImageUrl(r.getImageUrl());
        return dto;
    }

    public List<RecipeDto> toDto(List<Recipe> recipes) {
        return recipes.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

}