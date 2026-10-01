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
     저장소에서 재료별 수량을 담은 모든 재고를 조회한다.

     @return 재고 목록, 없으면 빈 목록
     */
    public List<Inventory> getInventories(){
        return inventoryRepository.findAll();
    }

    /*
     전달받은 재고의 재료 연결과 수량을 별도 검증 없이 저장한다.

     @param inventory 저장할 재고 정보
     @return 저장된 재고
     */
    public Inventory createInventory(Inventory inventory){
        return inventoryRepository.save(inventory);
    }

    /*
     음수 수량을 먼저 거부한 뒤 지정한 재고를 조회해 수량을 변경하고 저장한다.
     수량은 기존 값에 더하지 않고 전달된 값으로 대체한다.

     @param id 수정할 재고 ID
     @param quantity 변경할 재고 수량 (0 이상)
     @return 저장된 재고
     @throws InvalidInventoryQuantityException 수량이 음수인 경우
     @throws java.util.NoSuchElementException 해당 ID의 재고가 없는 경우
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
