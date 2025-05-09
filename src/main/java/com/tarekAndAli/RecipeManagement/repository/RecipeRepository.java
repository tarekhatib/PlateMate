package com.tarekAndAli.RecipeManagement.repository;

import com.tarekAndAli.RecipeManagement.model.Recipe;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;

public interface RecipeRepository extends MongoRepository<Recipe, String> {
    List<Recipe> findByOwnerId(String ownerId);

    List<Recipe> findByNameContainingIgnoreCase(String name);

    Page<Recipe> findByNameContainingIgnoreCase(String name, Pageable pageable);

    List<Recipe> findByCategory(String category);

    List<Recipe> findByCookingTimeLessThanEqual(int maxTime);

    Page<Recipe> findAll(Pageable pageable);

    Page<Recipe> findByCategoryAndCookingTimeLessThanEqual(
            String category,
            int maxTime,
            Pageable pageable
    );

}