package com.bakery.bakeryinventory.service;

import com.bakery.bakeryinventory.model.Ingredient;
import com.bakery.bakeryinventory.model.Inventory;
import com.bakery.bakeryinventory.repository.IngredientRepository;
import com.bakery.bakeryinventory.repository.InventoryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class IngredientService {
    private final IngredientRepository ingredientRepository;
    private final InventoryRepository inventoryRepository;

    public IngredientService(
            IngredientRepository ingredientRepository,
            InventoryRepository inventoryRepository){
        this.ingredientRepository = ingredientRepository;
        this.inventoryRepository = inventoryRepository;
    }

    public List<Ingredient> getIngredients(){
        return ingredientRepository.findAll();
    }

    @Transactional
    //하나의 묶음으로 병렬 처리
    public Ingredient createIngredient(Ingredient ingredient){
        Ingredient savedIngredient = ingredientRepository.save(ingredient);

        Inventory inventory = new Inventory();
        inventory.setIngredient(savedIngredient);
        inventory.setQuantity(0);

        inventoryRepository.save(inventory);

        return savedIngredient;
    }
}
