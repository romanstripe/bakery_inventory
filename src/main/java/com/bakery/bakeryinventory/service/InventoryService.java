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

    /*
     모든 재고를 조회한다.
     반환값: 재고 목록
     */
    public List<Inventory> getInventories(){
        return inventoryRepository.findAll();
    }

    /*
     전달받은 재고의 재료 연결과 수량을 별도 검증 없이 저장한다.

     inventory: 저장할 재고 정보
     반환값: 저장된 재고
     */
    public Inventory createInventory(Inventory inventory){
        return inventoryRepository.save(inventory);
    }

    /*
     음수 수량을 거부한 뒤 지정한 재고를 조회해 수량을 변경하고 저장한다.
     기존 수량에 더하는 방식이 아니라 전달받은 값으로 교체한다.

     id: 수정할 재고 ID
     quantity: 변경할 재고 수량
     반환값: 수정된 Inventory
     */
    public Inventory updateQuantity(Long id, int quantity){

        if(quantity < 0){
            throw new InvalidInventoryQuantityException("Stock amount cannot be less than 0.");
        }

        Inventory inventory = inventoryRepository.findById(id).orElseThrow();

        inventory.setQuantity(quantity);

        return inventoryRepository.save(inventory);
    }
}
