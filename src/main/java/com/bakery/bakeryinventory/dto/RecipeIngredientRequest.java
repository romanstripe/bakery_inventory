package com.bakery.bakeryinventory.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record RecipeIngredientRequest(
        @NotNull @Positive Long ingredientId,
        @NotNull @Positive Integer quantity
) {
}
