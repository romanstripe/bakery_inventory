package com.bakery.bakeryinventory.controller;

import com.bakery.bakeryinventory.dto.RecipeCreateRequest;
import com.bakery.bakeryinventory.model.Recipe;
import com.bakery.bakeryinventory.service.RecipeService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/recipes")
public class RecipeController {

    private final RecipeService recipeService;

    public RecipeController(RecipeService recipeService) {
        this.recipeService = recipeService;
    }

    /*
     레시피 이름과 재료별 사용량을 받아 등록한다.
     요청값을 검증하고 등록 완료 시 HTTP 201을 반환한다.

     request: 레시피 이름과 재료별 1배합 사용량
     반환값: 등록된 레시피 ID와 이름
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public RecipeResponse createRecipe(
            @Valid @RequestBody RecipeCreateRequest request
    ) {
        Recipe recipe = recipeService.createRecipe(request);

        return new RecipeResponse(
                recipe.getId(),
                recipe.getName()
        );
    }

    public record RecipeResponse(
            Long id,
            String name
    ) {
    }
}