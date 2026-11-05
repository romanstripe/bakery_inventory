package com.bakery.bakeryinventory.dto;

import java.util.List;

public record RecipeDetailResponse(
        Long id,
        String name,
        List<IngredientItem> ingredientIds
) {
    public record IngredientItem(
            Long ingredientId,
            String ingredientName,
            int quantity
    ){
    }
}
