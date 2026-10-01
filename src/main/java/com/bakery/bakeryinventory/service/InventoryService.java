package com.bakery.bakeryinventory.service;

import com.bakery.bakeryinventory.exception.InvalidInventoryQuantityException;
import com.bakery.bakeryinventory.model.Inventory;
import com.bakery.bakeryinventory.repository.InventoryRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class InventoryService {
    private final InventoryRepository inventoryRepository;

    public InventoryService(InventoryRepository inventoryRepository){
        this.inventoryRepository = inventoryRepository;
    }

    public List<Inventory> getInventories(){
        return inventoryRepository.findAll();
    }

    public Inventory createInventory(Inventory inventory){
        return inventoryRepository.save(inventory);
    }

    public Inventory updateQuantity(Long id, int quantity){

        if(quantity < 0){
            throw new InvalidInventoryQuantityException("Stock amount cannot be less than 0.");
        }

        Inventory inventory = inventoryRepository.findById(id).orElseThrow();

        inventory.setQuantity(quantity);

        return inventoryRepository.save(inventory);
    }
}
