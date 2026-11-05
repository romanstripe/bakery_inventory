package com.bakery.bakeryinventory.service;

import com.bakery.bakeryinventory.dto.RecipeCreateRequest;
import com.bakery.bakeryinventory.dto.RecipeDetailResponse;
import com.bakery.bakeryinventory.dto.RecipeIngredientRequest;
import com.bakery.bakeryinventory.dto.RecipeDetailResponse;
import com.bakery.bakeryinventory.exception.InvalidRecipeException;
import com.bakery.bakeryinventory.model.Ingredient;
import com.bakery.bakeryinventory.model.Outbound;
import com.bakery.bakeryinventory.model.Recipe;
import com.bakery.bakeryinventory.model.RecipeIngredient;
import com.bakery.bakeryinventory.repository.IngredientRepository;
import com.bakery.bakeryinventory.repository.RecipeRepository;
import com.bakery.bakeryinventory.repository.RecipeIngredientRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Sort;

import java.util.*;
import java.util.ArrayList;
import java.util.Comparator;

@Service
public class RecipeService {

    private final RecipeRepository recipeRepository;
    private final RecipeIngredientRepository recipeIngredientRepository;
    private final IngredientRepository ingredientRepository;
    private final OutboundService outboundService;

    public RecipeService(
            RecipeRepository recipeRepository,
            RecipeIngredientRepository recipeIngredientRepository,
            IngredientRepository ingredientRepository,
            OutboundService outboundService
    ) {
        this.recipeRepository = recipeRepository;
        this.recipeIngredientRepository = recipeIngredientRepository;
        this.ingredientRepository = ingredientRepository;
        this.outboundService = outboundService;
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

    /*
    레시피의 1배합 사용량에 배합 수를 곱해 재료별 출고를 등록한다.
    재료 하나라도 출고에 실패하면 이번 배합의 출고를 전부 취소한다.
    반환값: 생성된 출고 기록 ID 목록
     */
    @Transactional
    public List<Long> useRecipe(Long recipeId, int batches) {
        if (batches <= 0) {
            throw new InvalidRecipeException("배합 수는 1 이상이어야 합니다.");
        }

        if (!recipeRepository.existsById(recipeId)) {
            throw new InvalidRecipeException("존재하지 않는 레시피입니다.");
        }

        List<RecipeIngredient> ingredients = recipeIngredientRepository
                .findAllWithIngredientByRecipeId(recipeId)
                .stream()
                .sorted(Comparator.comparing(item -> item.getIngredient().getId()))
                .toList();

        if (ingredients.isEmpty()) {
            throw new InvalidRecipeException("레시피에 등록된 재료가 없습니다.");
        }

        List<Long> outboundIds = new ArrayList<>();

        for (RecipeIngredient item : ingredients) {
            int quantity;
            try {
                quantity = Math.multiplyExact(item.getQuantity(), batches);
            } catch (ArithmeticException exception) {
                throw new InvalidRecipeException("계산된 사용량이 지정 범위를 초과합니다.");
            }

            Long ingredientId = item.getIngredient().getId();
            outboundIds.add(
                    outboundService.createOutbound(ingredientId, quantity).getId()
            );
        }
        return outboundIds;
    }
}