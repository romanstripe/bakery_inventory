package com.bakery.bakeryinventory.repository;

import com.bakery.bakeryinventory.model.RecipeIngredient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface RecipeIngredientRepository
        extends JpaRepository<RecipeIngredient, Long>{

    @Query("""
           SELECT ri
           FROM RecipeIngredient ri
           JOIN FETCH ri.ingredient
           WHERE ri.recipe.id = :recipeId
           ORDER BY ri.id
            """)
    List<RecipeIngredient> findAllWithIngredientByRecipeId(
            @Param("recipeId") Long recipeId
    );
}
