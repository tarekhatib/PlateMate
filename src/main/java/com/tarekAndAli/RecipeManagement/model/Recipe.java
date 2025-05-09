package com.tarekAndAli.RecipeManagement.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;

@Document(collection = "recipes")
@Data
@NoArgsConstructor
@AllArgsConstructor

public class Recipe {
    @Id
    private String id;
    private String name;
    private List<String> ingredients;
    private String instructions;
    private int cookingTime;         // in minutes
    private String category;
    private String ownerId;
    private String ownerUsername;
    private LocalDateTime createdAt;
    private String imageUrl;
}
