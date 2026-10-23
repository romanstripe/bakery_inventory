package com.bakery.bakeryinventory.repository;

import com.bakery.bakeryinventory.model.RecipeIngredient;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RecipeIngredientRepository
        extends JpaRepository<RecipeIngredient, Long>{
}
