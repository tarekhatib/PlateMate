package com.tarekAndAli.RecipeManagement.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/test")
public class Temp {

    @GetMapping
    public String hello() {
        return "Hello, Swagger!";
    }
}
