package com.bakery.bakeryinventory.repository;

import com.bakery.bakeryinventory.model.Ingredient;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IngredientRepository
    extends JpaRepository<Ingredient, Long>{
    }
