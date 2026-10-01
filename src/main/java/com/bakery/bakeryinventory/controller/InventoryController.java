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
     모든 재고를 서비스에서 조회해 재료별 수량을 확인할 수 있도록 응답한다.

     @return 재고 목록, 없으면 빈 목록
     */
    @GetMapping("/inventories")
    public List<Inventory> getInventories(){
        return inventoryService.getInventories();
    }

    /*
     경로 변수로 지정한 재고의 수량을 요청 DTO의 값으로 변경한다.
     DTO 검증으로 수량 누락을 거부하며, 서비스에서 발생한 음수 수량 예외는
     전역 예외 처리기가 HTTP 400 응답으로 변환한다.

     @param id URL 경로의 수정할 재고 ID
     @param request 필수 수량을 담은 요청 DTO
     @return 변경된 재고
     @throws com.bakery.bakeryinventory.exception.InvalidInventoryQuantityException 수량이 음수인 경우
     @throws java.util.NoSuchElementException 해당 ID의 재고가 없는 경우
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
