package com.tarekAndAli.RecipeManagement.service;

import com.tarekAndAli.RecipeManagement.model.Recipe;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class RecipeServiceSmokeTest {
    @Autowired
    private RecipeService recipeService;

    @Test
    void createAndFetch() {
        Recipe r = new Recipe();
        r.setName("Test");
        r.setIngredients(List.of("A","B"));
        r.setInstructions("Do X");
        r.setCookingTime(5);
        r.setCategory("TestCat");
        Recipe saved = recipeService.create(r);
        Recipe fetched = recipeService.getById(saved.getId());
        assertEquals("Test", fetched.getName());
    }
}