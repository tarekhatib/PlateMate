package com.tarekAndAli.RecipeManagement.service;

import com.tarekAndAli.RecipeManagement.dto.UserDto;
import com.tarekAndAli.RecipeManagement.model.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {
    public UserDto toDto(User u) {
        UserDto d = new UserDto(u.getId(), u.getUsername(), u.getEmail(), u.getFavorites());
        d.setId(u.getId());
        d.setUsername(u.getUsername());
        d.setEmail(u.getEmail());
        d.setFavorites(u.getFavorites());
        return d;
    }
}