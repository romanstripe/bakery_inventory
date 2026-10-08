package com.bakery.bakeryinventory.service;

import com.bakery.bakeryinventory.model.Ingredient;
import com.bakery.bakeryinventory.model.Inventory;
import com.bakery.bakeryinventory.repository.IngredientRepository;
import com.bakery.bakeryinventory.repository.InventoryRepository;
import com.bakery.bakeryinventory.exception.InvalidInventoryQuantityException;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.concurrent.Future;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class InventoryConcurrencyTest {

    private final InventoryService inventoryService;
    private final InventoryRepository inventoryRepository;
    private final IngredientRepository ingredientRepository;

    @Autowired
    InventoryConcurrencyTest(
            InventoryService inventoryService,
            InventoryRepository inventoryRepository,
            IngredientRepository ingredientRepository
    ) {
        this.inventoryService = inventoryService;
        this.inventoryRepository = inventoryRepository;
        this.ingredientRepository = ingredientRepository;
    }

    /*
     같은 재고에 두 개의 증가 요청이 동시에 들어와도
     모든 증가량이 누락 없이 반영되는지 확인한다.

     테스트 조건: 초기 재고 3, 동시에 7씩 두 번 증가
     기대 결과: 최종 재고 17
     */
    @Test
    void increaseQuantity_shouldHandleConcurrentUpdates() throws Exception {
        // given
        Ingredient ingredient = new Ingredient();
        ingredient.setName("동시성테스트재료");
        Ingredient savedIngredient = ingredientRepository.save(ingredient);

        Inventory inventory = new Inventory();
        inventory.setIngredient(savedIngredient);
        inventory.setQuantity(3);
        Inventory savedInventory = inventoryRepository.save(inventory);

        ExecutorService executor = Executors.newFixedThreadPool(2);
        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch endLatch = new CountDownLatch(2);

        // when
        for (int i = 0; i < 2; i++) {
            executor.submit(() -> {
                try {
                    startLatch.await();
                    inventoryService.increaseQuantity(savedInventory.getId(), 7);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } finally {
                    endLatch.countDown();
                }
            });
        }

        startLatch.countDown();
        endLatch.await();

        // then
        Inventory result = inventoryRepository.findById(savedInventory.getId())
                .orElseThrow();

        assertEquals(17, result.getQuantity());

        executor.shutdown();

        inventoryRepository.delete(savedInventory);
        ingredientRepository.delete(savedIngredient);
    }

    /*
 같은 재고에 두 개의 차감 요청이 동시에 들어왔을 때
 재고보다 많은 수량이 차감되지 않는지 확인한다.

 테스트 조건: 초기 재고 10, 동시에 7씩 두 번 차감
 기대 결과: 한 요청만 성공하고 다른 요청은 재고 부족으로 실패,
          최종 재고 수량은 3
 */
    @Test
    void decreaseQuantity_shouldPreventOverDecreaseWhenConcurrent() throws Exception {
        // given
        Ingredient ingredient = new Ingredient();
        ingredient.setName("동시차감테스트재료");
        Ingredient savedIngredient = ingredientRepository.save(ingredient);

        Inventory inventory = new Inventory();
        inventory.setIngredient(savedIngredient);
        inventory.setQuantity(10);
        Inventory savedInventory = inventoryRepository.save(inventory);

        ExecutorService executor = Executors.newFixedThreadPool(2);
        CountDownLatch startLatch = new CountDownLatch(1);

        try {
            Future<Boolean> result1 = executor.submit(() -> {
                startLatch.await();

                try {
                    inventoryService.decreaseQuantity(savedInventory.getId(), 7);
                    return true;
                } catch (InvalidInventoryQuantityException e) {
                    return false;
                }
            });

            Future<Boolean> result2 = executor.submit(() -> {
                startLatch.await();

                try {
                    inventoryService.decreaseQuantity(savedInventory.getId(), 7);
                    return true;
                } catch (InvalidInventoryQuantityException e) {
                    return false;
                }
            });

            // 두 스레드를 동시에 출발시킨다.
            startLatch.countDown();

            boolean firstSucceeded = result1.get();
            boolean secondSucceeded = result2.get();

            // 둘 중 정확히 하나만 성공해야 한다.
            assertEquals(1,
                    (firstSucceeded ? 1 : 0)
                            + (secondSucceeded ? 1 : 0));

            Inventory result = inventoryRepository.findById(savedInventory.getId())
                    .orElseThrow();

            assertEquals(3, result.getQuantity());

        } finally {
            executor.shutdown();

            inventoryRepository.deleteById(savedInventory.getId());
            ingredientRepository.deleteById(savedIngredient.getId());
        }
    }
}