package com.bakery.bakeryinventory.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

public record RecipeCreateRequest(
        @NotBlank
        @Size(max = 255)
        String name,

        @NotEmpty
        List<@NotNull @Valid RecipeIngredientRequest> ingredients
) {
}
