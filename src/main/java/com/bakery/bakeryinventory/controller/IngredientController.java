package com.bakery.bakeryinventory.controller;

import com.bakery.bakeryinventory.model.Ingredient;
import com.bakery.bakeryinventory.service.IngredientService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class IngredientController {

    private final IngredientService ingredientService;

    public IngredientController(IngredientService ingredientService){
        this.ingredientService = ingredientService;
    }

    @GetMapping("/ingredients")
    public List<Ingredient> getIngredients(){
        return ingredientService.getIngredients();
    }

    @PostMapping("/ingredients")
    public Ingredient createIngredient(
            @RequestBody Ingredient ingredient
    ){
        return ingredientService.createIngredient(ingredient);
    }
}