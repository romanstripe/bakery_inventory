package com.bakery.bakeryinventory.controller;

import com.bakery.bakeryinventory.model.Ingredient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.ArrayList;
import java.util.List;

@RestController
public class IngredientController {

    private final List<Ingredient> ingredients = new ArrayList<>();

    @GetMapping("/ingredients")
    public List<Ingredient> getIngredients(){
        return ingredients;
    }

    @PostMapping("/ingredients")
    public Ingredient createIngredient(
            @RequestBody Ingredient ingredient
    ){
        ingredients.add(ingredient);
        return ingredient;
    }
}