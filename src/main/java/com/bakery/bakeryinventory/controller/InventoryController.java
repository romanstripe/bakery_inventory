package com.bakery.bakeryinventory.controller;

import jakarta.validation.Valid;

import com.bakery.bakeryinventory.dto.InventoryQuantityRequest;
import com.bakery.bakeryinventory.model.Inventory;
import com.bakery.bakeryinventory.service.InventoryService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@RestController
public class InventoryController {

    private final InventoryService inventoryService;

    public InventoryController(InventoryService inventoryService){
        this.inventoryService = inventoryService;
    }

    /*
     모든 재고를 조회한다.
     반환값: 재고 목록
     */
    @GetMapping("/inventories")
    public List<Inventory> getInventories(){
        return inventoryService.getInventories();
    }

    /*
     경로 변수로 지정한 재고의 수량을 요청 값으로 변경한다.
     요청 DTO를 검증한 뒤 서비스에 재고 수정을 위임한다.

     id: 수정할 재고 ID
     request: 변경할 수량을 담은 요청 DTO
     반환값: 수정된 Inventory
     */
    @PatchMapping("/inventories/{id}")
    public Inventory updateQuantity(
            @PathVariable Long id,
            @Valid @RequestBody InventoryQuantityRequest request //검증 추가
    ){
        return inventoryService.updateQuantity(
                id, request.getQuantity());
    }
}
