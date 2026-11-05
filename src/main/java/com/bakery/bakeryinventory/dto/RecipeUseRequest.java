package com.bakery.bakeryinventory.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record RecipeUseRequest(
        @NotNull @Positive Integer batches
) {
}
