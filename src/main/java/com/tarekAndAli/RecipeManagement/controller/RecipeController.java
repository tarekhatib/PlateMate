package com.tarekAndAli.RecipeManagement.controller;

import com.tarekAndAli.RecipeManagement.dto.CreateRecipeRequest;
import com.tarekAndAli.RecipeManagement.dto.RecipeDto;
import com.tarekAndAli.RecipeManagement.dto.UpdateRecipeRequest;
import com.tarekAndAli.RecipeManagement.model.Recipe;
import com.tarekAndAli.RecipeManagement.service.RecipeService;
import com.tarekAndAli.RecipeManagement.service.RecipeMapper;
import com.tarekAndAli.RecipeManagement.service.UserService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

import jakarta.validation.Valid;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/recipes")
public class RecipeController {

    private final RecipeService recipeService;
    private final RecipeMapper mapper;
    private final UserService userService;

    public RecipeController(RecipeService recipeService,
                            RecipeMapper mapper
                            , UserService userService) {
        this.recipeService = recipeService;
        this.mapper = mapper;
        this.userService = userService;
    }

    @PostMapping
    public ResponseEntity<RecipeDto> create(@RequestBody @Valid CreateRecipeRequest req,
                                            @AuthenticationPrincipal UserDetails userDetails) {
        Recipe entity = mapper.toEntity(req);

        // Set ownerId and createdAt
        String username = userDetails.getUsername();
        String userId = userService.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found: " + username))
                .getId();
        entity.setOwnerId(userId);
        entity.setOwnerUsername(username);
        entity.setCreatedAt(LocalDateTime.now());

        Recipe saved = recipeService.create(entity);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(mapper.toDto(saved));
    }

    @GetMapping
    public Page<RecipeDto> list(
            @RequestParam(value="name", required=false) String name,
            Pageable pageable
    ) {
        Page<Recipe> page = (name == null || name.isBlank())
                ? recipeService.findAll(pageable)
                : recipeService.findByNameContainingIgnoreCase(name, pageable);
        return page.map(mapper::toDto);
    }

    @GetMapping("/{id}")
    public ResponseEntity<RecipeDto> getById(@PathVariable String id) {
        Recipe r = recipeService.getById(id);
        return ResponseEntity.ok(mapper.toDto(r));
    }

    /**
     * Get the current user's favorite recipes.
     */
    @GetMapping("/me/favorites")
    public List<RecipeDto> getMyFavorites(@AuthenticationPrincipal UserDetails userDetails) {
        // Look up the User entity to fetch their ID
        String username = userDetails.getUsername();
        String userId = userService.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found: " + username))
                .getId();

        // Fetch and map favorites
        return recipeService.getFavorites(userId)
                .stream()
                .map(mapper::toDto)
                .collect(Collectors.toList());
    }

    @PutMapping("/{id}")
    public ResponseEntity<RecipeDto> update(
            @PathVariable String id,
            @RequestBody @Valid UpdateRecipeRequest req,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        Recipe existing = recipeService.getById(id);
        String username = userDetails.getUsername();
        String userId = userService.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found: " + username))
                .getId();

        if (!existing.getOwnerId().equals(userId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        Recipe toSave = mapper.toEntity(req, existing);
        Recipe updated = recipeService.update(id, toSave);
        return ResponseEntity.ok(mapper.toDto(updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id,
                                       @AuthenticationPrincipal UserDetails userDetails) {
        Recipe existing = recipeService.getById(id);
        String username = userDetails.getUsername();
        String userId = userService.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found: " + username))
                .getId();

        if (!existing.getOwnerId().equals(userId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        recipeService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/me")
    public List<RecipeDto> getMyRecipes(@AuthenticationPrincipal UserDetails userDetails) {
        String username = userDetails.getUsername();
        String userId = userService.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found: " + username))
                .getId();

        return recipeService.getByOwnerId(userId)
                .stream()
                .map(mapper::toDto)
                .collect(Collectors.toList());
    }


    @GetMapping("/filter")
    public Page<RecipeDto> filter(
            @RequestParam("category") String category,
            @RequestParam("maxTime") int maxTime,
            Pageable pageable
    ) {
        return recipeService.filter(category, maxTime, pageable)
                .map(mapper::toDto);
    }
}