package com.bakery.bakeryinventory.service;

import com.bakery.bakeryinventory.exception.InvalidInventoryQuantityException;
import com.bakery.bakeryinventory.model.Ingredient;
import com.bakery.bakeryinventory.model.Inventory;
import com.bakery.bakeryinventory.model.Outbound;
import com.bakery.bakeryinventory.repository.IngredientRepository;
import com.bakery.bakeryinventory.repository.InventoryRepository;
import com.bakery.bakeryinventory.repository.OutboundRepository;
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
class OutboundServiceTest{

    @Autowired
    private OutboundService outboundService;

    @Autowired
    private IngredientRepository ingredientRepository;

    @Autowired
    private InventoryRepository inventoryRepository;

    @MockitoSpyBean
    private OutboundRepository outboundRepository;

    private Long ingredientId;
    private Long inventoryId;
    private Long outboundId;

    /*
    테스트마다 재료와 초기 재고 10을 저장한다.
     */
    @BeforeEach
    void setUp(){
        Ingredient ingredient = new Ingredient();
        ingredient.setName("출고테스트재료");
        ingredientRepository.save(ingredient);
        ingredientId = ingredient.getId();

        Inventory inventory = new Inventory();
        inventory.setIngredient(ingredient);
        inventory.setQuantity(10);
        inventoryId = inventoryRepository.save(inventory).getId();
    }

    /*
     테스트에서 생성한 출고 기록, 재고, 재료를 순서대로 삭제한다.
     기존 데이터는 유지한다.
     */
    @AfterEach
    void tearDown() {
        if (outboundId != null) {
            outboundRepository.deleteById(outboundId);
        }

        if (inventoryId != null) {
            inventoryRepository.deleteById(inventoryId);
        }

        if (ingredientId != null) {
            ingredientRepository.deleteById(ingredientId);
        }
    }

    /*
     사용 수량만큼 재고가 차감되고 출고 기록이 저장되는지 확인한다.

     테스트 조건: 초기 재고 10, 사용 수량 3
     기대 결과: 재고 7, 출고 기록 1건 추가
     */
    @Test
    void createOutbound_shouldSaveRecordAndDecreaseStock() {
        long countBefore = outboundRepository.count();

        Outbound outbound = outboundService.createOutbound(ingredientId, 3);
        outboundId = outbound.getId();

        Inventory savedInventory = inventoryRepository.findById(inventoryId)
                .orElseThrow();

        Outbound savedOutbound = outboundRepository.findById(outboundId)
                .orElseThrow();

        assertEquals(7, savedInventory.getQuantity());
        assertEquals(3, savedOutbound.getQuantity());
        assertEquals(ingredientId, savedOutbound.getIngredient().getId());
        assertNotNull(savedOutbound.getUsedAt());
        assertEquals(countBefore + 1, outboundRepository.count());
    }

    /*
     재고보다 많은 수량을 사용하면 출고를 거부하는지 확인한다.

     테스트 조건: 초기 재고 10, 사용 수량 11
     기대 결과: 수량 예외 발생, 재고와 출고 기록 수 유지
     */
    @Test
    void createOutbound_shouldRejectInsufficientStock() {
        long countBefore = outboundRepository.count();

        assertThrows(
                InvalidInventoryQuantityException.class,
                () -> outboundService.createOutbound(ingredientId, 11)
        );

        Inventory savedInventory = inventoryRepository.findById(inventoryId)
                .orElseThrow();

        assertEquals(10, savedInventory.getQuantity());
        assertEquals(countBefore, outboundRepository.count());
    }

    /*
     출고 기록 저장에 실패하면 재고 차감도 취소되는지 확인한다.
     재고 UPDATE를 DB에 보낸 뒤 출고 저장 시 예외를 발생시킨다.

     테스트 조건: 초기 재고 10, 사용 수량 3, 출고 저장 실패
     기대 결과: 재고 10 유지, 출고 기록 수 유지
     */
    @Test
    void createOutbound_shouldRollbackWhenRecordSaveFails() {
        long countBefore = outboundRepository.count();

        doAnswer(invocation -> {
            inventoryRepository.flush();

            throw new DataIntegrityViolationException(
                    "테스트용 출고 저장 실패"
            );
        }).when(outboundRepository).save(any(Outbound.class));

        assertThrows(
                DataIntegrityViolationException.class,
                () -> outboundService.createOutbound(ingredientId, 3)
        );

        Inventory savedInventory = inventoryRepository.findById(inventoryId)
                .orElseThrow();

        assertEquals(10, savedInventory.getQuantity());
        assertEquals(countBefore, outboundRepository.count());
    }
}
