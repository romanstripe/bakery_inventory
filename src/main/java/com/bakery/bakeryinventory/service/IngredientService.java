package com.bakery.bakeryinventory.service;

import com.bakery.bakeryinventory.model.Ingredient;
import com.bakery.bakeryinventory.model.Inventory;
import com.bakery.bakeryinventory.repository.IngredientRepository;
import com.bakery.bakeryinventory.repository.InventoryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class IngredientService {
    private final IngredientRepository ingredientRepository;
    private final InventoryRepository inventoryRepository;

    public IngredientService(
            IngredientRepository ingredientRepository,
            InventoryRepository inventoryRepository){
        this.ingredientRepository = ingredientRepository;
        this.inventoryRepository = inventoryRepository;
    }

    /*
     등록된 모든 재료를 조회한다.
     반환값: 재료 목록
     */
    public List<Ingredient> getIngredients(){
        return ingredientRepository.findAll();
    }

    /*
     재료를 저장하고 해당 재료에 연결된 수량 0의 초기 재고를 생성한다.
     두 저장을 하나의 트랜잭션으로 처리해 재고 생성 실패 시 재료만 남지 않도록 한다.

     ingredient: 저장할 재료 정보
     반환값: 저장된 재료
     */
    @Transactional
    public Ingredient createIngredient(Ingredient ingredient){
        Ingredient savedIngredient = ingredientRepository.save(ingredient);

        Inventory inventory = new Inventory();
        inventory.setIngredient(savedIngredient);
        inventory.setQuantity(0);

        inventoryRepository.save(inventory);

        return savedIngredient;
    }
}
