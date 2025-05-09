package com.tarekAndAli.RecipeManagement.dto;

import jakarta.validation.constraints.*;
import lombok.Data;
import java.util.List;

@Data
public class CreateRecipeRequest {
    @NotBlank
    private String name;

    @Size(min = 1)
    private List<@NotBlank String> ingredients;

    @NotBlank
    private String instructions;

    @Min(1)
    private int cookingTime;

    @NotBlank
    private String category;

    @NotBlank
    private String imageUrl;
}