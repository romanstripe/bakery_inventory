package com.bakery.bakeryinventory.service;

import com.bakery.bakeryinventory.repository.InventoryRepository;
import com.bakery.bakeryinventory.model.Inventory;
import com.bakery.bakeryinventory.exception.InvalidInventoryQuantityException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class InventoryServiceTest {

    private InventoryRepository inventoryRepository;
    private InventoryService inventoryService;

    @BeforeEach
    void setUp(){
        inventoryRepository = Mockito.mock(InventoryRepository.class);
        inventoryService = new InventoryService(inventoryRepository);
    }

    /*
    재고 증가 요청 시 기존 재고 수량에 요청 수량이 더해지는지 확인한다.

    테스트 조건: 기존 재고 수량 10, 증가 수량 5
    기대 결과: 재고 수량 15
     */
    @Test
    void increaseQuantity_shouldIncreaseInventoryQuantity(){
        //given
        Inventory inventory = new Inventory();
        inventory.setQuantity(10);
        
        Mockito.when(inventoryRepository.findById(1L))
                .thenReturn(Optional.of(inventory));
        
        Mockito.when(inventoryRepository.save(inventory))
                .thenReturn(inventory);
        
        //when
        Inventory result = inventoryService.increaseQuantity(1L, 5);
        
        //then
        assertEquals(15, result.getQuantity());
        Mockito.verify(inventoryRepository).save(inventory);
    }


    /*
    재고 차감 요청 시 기존 재고 수량에서 요청 수량이 감소하고 저장되는지 확인한다.

    테스트 조건: 기존 재고 수량 10, 차감 수량 3
    기대 결과: 재고 수량 7, 변경된 Inventory가 저장됨
    */
    @Test
    void decreaseQuantity_shouldDecreaseInventoryQuantity() {
        //given
        Inventory inventory = new Inventory();
        inventory.setQuantity(10);

        Mockito.when(inventoryRepository.findById(1L))
                .thenReturn(Optional.of(inventory));

        Mockito.when(inventoryRepository.save(inventory))
                .thenReturn(inventory);

        //when
        Inventory result = inventoryService.decreaseQuantity(1L, 3);

        //then
        assertEquals(7, result.getQuantity());
        Mockito.verify(inventoryRepository).save(inventory);
    }


    /*
    현재 재고보다 큰 수량을 차감하려고 하면 예외가 발생하고 저장되지 않는지 확인한다.

    테스트 조건: 기존 재고 수량 10, 차감 수량 20
    기대 결과: InvalidInventoryQuantityException 발생, save() 호출되지 않음
     */
    @Test
    void decreaseQuantity_shouldThrowExceptionWhenQuantityBecomesNegative() {
        //given
        Inventory inventory = new Inventory();
        inventory.setQuantity(10);

        Mockito.when(inventoryRepository.findById(1L))
                .thenReturn(Optional.of(inventory));

        //when & then
        assertThrows(
                InvalidInventoryQuantityException.class,
                ()-> inventoryService.decreaseQuantity(1L, 20)
        );

        Mockito.verify(inventoryRepository, Mockito.never()).save(inventory);
    }
}
