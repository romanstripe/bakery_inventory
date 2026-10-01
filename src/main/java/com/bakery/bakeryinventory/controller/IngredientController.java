package com.bakery.bakeryinventory.controller;

import com.bakery.bakeryinventory.model.Ingredient;
import com.bakery.bakeryinventory.service.IngredientService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class IngredientController {

    private final IngredientService ingredientService;

    public IngredientController(IngredientService ingredientService){
        this.ingredientService = ingredientService;
    }

    /*
     등록된 모든 재료를 서비스에서 조회해 응답한다.

     @return 등록된 재료 목록, 없으면 빈 목록
     */
    @GetMapping("/ingredients")
    public List<Ingredient> getIngredients(){
        return ingredientService.getIngredients();
    }

    /*
     요청 본문의 재료를 등록하고 수량 0의 초기 재고 생성을 서비스에 위임한다.

     @param ingredient 요청 본문에서 변환된 재료 정보
     @return 저장된 재료
     */
    @PostMapping("/ingredients")
    public Ingredient createIngredient(
            @RequestBody Ingredient ingredient
    ){
        return ingredientService.createIngredient(ingredient);
    }
}