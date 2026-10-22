package com.bakery.bakeryinventory.service;

import com.bakery.bakeryinventory.model.Ingredient;
import com.bakery.bakeryinventory.model.Inventory;
import com.bakery.bakeryinventory.model.Inbound;
import com.bakery.bakeryinventory.repository.IngredientRepository;
import com.bakery.bakeryinventory.repository.InventoryRepository;
import com.bakery.bakeryinventory.repository.InboundRepository;
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
class InboundServiceTest {

    @Autowired
    private InboundService inboundService;

    @Autowired
    private IngredientRepository ingredientRepository;

    @Autowired
    private InventoryRepository inventoryRepository;

    @MockitoSpyBean
    private InboundRepository inboundRepository;

    private Long ingredientId;
    private Long inventoryId;
    private Long inboundId;

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
        if (inboundId != null) {
            inboundRepository.deleteById(inboundId);
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
    void createInbound_shouldSaveInboundAndIncreaseStock() {
        long countBefore = inboundRepository.count();

        Inbound inbound = inboundService.createInbound(ingredientId, 10);
        inboundId = inbound.getId();

        Inventory savedInventory = inventoryRepository.findById(inventoryId)
                .orElseThrow();

        Inbound savedInbound = inboundRepository.findById(inboundId)
                .orElseThrow();

        assertEquals(15, savedInventory.getQuantity());
        assertEquals(10, savedInbound.getQuantity());
        assertEquals(ingredientId, savedInbound.getIngredient().getId());
        assertNotNull(savedInbound.getReceivedAt());
        assertEquals(countBefore + 1, inboundRepository.count());
    }

    /*
     입고 기록 저장에 실패하면 재고 변경도 취소되는지 확인한다.
     재고 UPDATE를 DB에 보낸 뒤 입고 저장 시 예외를 발생시킨다.

     테스트 조건: 초기 재고 5, 입고 수량 10, 입고 저장 실패
     기대 결과: 재고 5 유지, 입고 기록 수 유지
     */
    @Test
    void createInbound_shouldRollbackStockWhenInboundSaveFails() {
        long countBefore = inboundRepository.count();

        doAnswer(invocation -> {
            inventoryRepository.flush();

            throw new DataIntegrityViolationException(
                    "테스트용 입고 저장 실패"
            );
        }).when(inboundRepository).save(any(Inbound.class));

        assertThrows(
                DataIntegrityViolationException.class,
                () -> inboundService.createInbound(ingredientId, 10)
        );

        Inventory savedInventory = inventoryRepository.findById(inventoryId)
                .orElseThrow();

        assertEquals(5, savedInventory.getQuantity());
        assertEquals(countBefore, inboundRepository.count());
    }
}