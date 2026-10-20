package com.bakery.bakeryinventory.service;

import com.bakery.bakeryinventory.model.Ingredient;
import com.bakery.bakeryinventory.model.Inventory;
import com.bakery.bakeryinventory.model.Receipt;
import com.bakery.bakeryinventory.repository.IngredientRepository;
import com.bakery.bakeryinventory.repository.InventoryRepository;
import com.bakery.bakeryinventory.repository.ReceiptRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;

@SpringBootTest
class ReceiptServiceTest {

    @Autowired
    private ReceiptService receiptService;

    @Autowired
    private IngredientRepository ingredientRepository;

    @Autowired
    private InventoryRepository inventoryRepository;

    @MockitoSpyBean
    private ReceiptRepository receiptRepository;

    private Long ingredientId;
    private Long inventoryId;
    private Long receiptId;

    /*
     테스트마다 재료와 초기 재고 5를 새로 저장한다.
     */
    @BeforeEach
    void setUp() {
        Ingredient ingredient = new Ingredient();
        ingredient.setName("입고테스트재료");
        ingredientId = ingredientRepository.save(ingredient).getId();

        Inventory inventory = new Inventory();
        inventory.setIngredient(ingredient);
        inventory.setQuantity(5);
        inventoryId = inventoryRepository.save(inventory).getId();
    }

    /*
     테스트에서 생성한 입고 기록, 재고, 재료를 순서대로 삭제한다.
     기존에 저장되어 있던 데이터는 유지한다.
     */
    @AfterEach
    void tearDown() {
        if (receiptId != null) {
            receiptRepository.deleteById(receiptId);
        }

        if (inventoryId != null) {
            inventoryRepository.deleteById(inventoryId);
        }

        if (ingredientId != null) {
            ingredientRepository.deleteById(ingredientId);
        }
    }

    /*
     입고 등록 시 입고 기록과 증가한 재고가 DB에 저장되는지 확인한다.

     테스트 조건: 초기 재고 5, 입고 수량 10
     기대 결과: 입고 기록 1건 추가, 최종 재고 15
     */
    @Test
    void createReceipt_shouldSaveReceiptAndIncreaseStock() {
        long countBefore = receiptRepository.count();

        Receipt receipt = receiptService.createReceipt(ingredientId, 10);
        receiptId = receipt.getId();

        Inventory savedInventory = inventoryRepository.findById(inventoryId)
                .orElseThrow();

        Receipt savedReceipt = receiptRepository.findById(receiptId)
                .orElseThrow();

        assertEquals(15, savedInventory.getQuantity());
        assertEquals(10, savedReceipt.getQuantity());
        assertEquals(ingredientId, savedReceipt.getIngredient().getId());
        assertNotNull(savedReceipt.getReceivedAt());
        assertEquals(countBefore + 1, receiptRepository.count());
    }

    /*
     입고 기록 저장에 실패하면 재고 변경도 취소되는지 확인한다.
     재고 UPDATE를 DB에 보낸 뒤 입고 저장 시 예외를 발생시킨다.

     테스트 조건: 초기 재고 5, 입고 수량 10, 입고 저장 실패
     기대 결과: 재고 5 유지, 입고 기록 수 유지
     */
    @Test
    void createReceipt_shouldRollbackStockWhenReceiptSaveFails() {
        long countBefore = receiptRepository.count();

        doAnswer(invocation -> {
            inventoryRepository.flush();

            throw new DataIntegrityViolationException(
                    "테스트용 입고 저장 실패"
            );
        }).when(receiptRepository).save(any(Receipt.class));

        assertThrows(
                DataIntegrityViolationException.class,
                () -> receiptService.createReceipt(ingredientId, 10)
        );

        Inventory savedInventory = inventoryRepository.findById(inventoryId)
                .orElseThrow();

        assertEquals(5, savedInventory.getQuantity());
        assertEquals(countBefore, receiptRepository.count());
    }
}