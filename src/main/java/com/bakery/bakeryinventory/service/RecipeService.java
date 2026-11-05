package com.bakery.bakeryinventory.service;

import com.bakery.bakeryinventory.dto.RecipeCreateRequest;
import com.bakery.bakeryinventory.dto.RecipeDetailResponse;
import com.bakery.bakeryinventory.dto.RecipeIngredientRequest;
import com.bakery.bakeryinventory.dto.RecipeDetailResponse;
import com.bakery.bakeryinventory.exception.InvalidRecipeException;
import com.bakery.bakeryinventory.model.Ingredient;
import com.bakery.bakeryinventory.model.Recipe;
import com.bakery.bakeryinventory.model.RecipeIngredient;
import com.bakery.bakeryinventory.repository.IngredientRepository;
import com.bakery.bakeryinventory.repository.RecipeRepository;
import com.bakery.bakeryinventory.repository.RecipeIngredientRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Sort;

import java.util.HashSet;
import java.util.Set;
import java.util.List;
import java.util.Optional;

@Service
public class RecipeService {

    private final RecipeRepository recipeRepository;
    private final RecipeIngredientRepository recipeIngredientRepository;
    private final IngredientRepository ingredientRepository;

    public RecipeService(
            RecipeRepository recipeRepository,
            RecipeIngredientRepository recipeIngredientRepository,
            IngredientRepository ingredientRepository
    ) {
        this.recipeRepository = recipeRepository;
        this.recipeIngredientRepository = recipeIngredientRepository;
        this.ingredientRepository = ingredientRepository;
    }

    /*
     레시피 이름과 재료별 1배합 사용량을 저장한다.
     같은 재료를 중복 지정하거나 존재하지 않는 재료를 지정하면 거부한다.
     저장 중 예외가 발생하면 레시피와 재료별 사용량을 함께 롤백한다.
     현재 재고는 변경하지 않는다.

     request: 레시피 이름과 재료별 사용량 목록
     반환값: 저장된 Recipe
     */
    @Transactional
    public Recipe createRecipe(RecipeCreateRequest request) {
        Set<Long> ingredientIds = new HashSet<>();

        for (RecipeIngredientRequest item : request.ingredients()) {
            if (!ingredientIds.add(item.ingredientId())) {
                throw new InvalidRecipeException(
                        "같은 재료를 중복 등록할 수 없습니다."
                );
            }
        }

        Recipe recipe = recipeRepository.save(
                new Recipe(request.name().trim())
        );

        for (RecipeIngredientRequest item : request.ingredients()) {
            Ingredient ingredient = ingredientRepository
                    .findById(item.ingredientId())
                    .orElseThrow(() -> new InvalidRecipeException(
                            "존재하지 않는 재료입니다. ID: "
                                    + item.ingredientId()
                    ));

            RecipeIngredient recipeIngredient = new RecipeIngredient(
                    recipe,
                    ingredient,
                    item.quantity()
            );

            recipeIngredientRepository.save(recipeIngredient);
        }

        return recipe;
    }

    /*
    저장된 레시피 목록을 조회한다.
     */
    @Transactional(readOnly = true)
    public List<Recipe> getRecipes(){
        return recipeRepository.findAll(Sort.by("id"));
    }

    /*
    레시피 ID로 이름과 재료별 1배합 사용량을 조회한다.
    해당 레시피가 없으면 빈 Optional을 반환한다.
     */
    @Transactional(readOnly = true)
    public Optional<RecipeDetailResponse> getRecipe(Long recipeId){
        return recipeRepository.findById(recipeId).map(recipe-> {
            List<RecipeDetailResponse.IngredientItem> ingredients =
                    recipeIngredientRepository.findAllWithIngredientByRecipeId(recipeId)
                            .stream()
                            .map(item -> new RecipeDetailResponse.IngredientItem(
                                    item.getIngredient().getId(),
                                    item.getIngredient().getName(),
                                    item.getQuantity()
                            )).toList();

            return new RecipeDetailResponse(
                    recipe.getId(),
                    recipe.getName(),
                    ingredients
            );
        });
    }
}