package com.tarekAndAli.RecipeManagement.dto;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonFormat;

@Data
public class RecipeDto {
    private String id;
    private String name;
    private List<String> ingredients;
    private String instructions;
    private int cookingTime;
    private String category;
    private String ownerId;
    private String ownerUsername;
    @JsonFormat(pattern = "dd-MM-yyyy")
    private LocalDateTime createdAt;
    private String imageUrl;
}