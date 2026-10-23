package com.bakery.bakeryinventory.repository;

import com.bakery.bakeryinventory.model.Recipe;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RecipeRepository
    extends JpaRepository<Recipe, Long>{
}
