package com.tarekAndAli.RecipeManagement.dto;

import lombok.Data;
import java.util.List;

@Data
public class UserDto {
    private String id;
    private String username;
    private String email;
    private List<String> favorites;

    public UserDto(String id, String username, String email, List<String> favorites) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.favorites = favorites;
    }
}