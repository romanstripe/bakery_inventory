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

    @GetMapping("/inventories")
    public List<Inventory> getInventories(){
        return inventoryService.getInventories();
    }

    @PatchMapping("/inventories/{id}")
    public Inventory updateQuantity(
            @PathVariable Long id,
            @Valid @RequestBody InventoryQuantityRequest request //검증 추가
    ){
        return inventoryService.updateQuantity(
                id, request.getQuantity());
    }
}
