package com.bakery.bakeryinventory.service;

import com.bakery.bakeryinventory.exception.InvalidInventoryQuantityException;
import com.bakery.bakeryinventory.exception.InventoryNotFoundException;
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

        Inventory inventory = inventoryRepository.findById(id)
                .orElseThrow(()-> new InventoryNotFoundException(
                        "Cannot find that stock."
                ));

        inventory.setQuantity(quantity);

        return inventoryRepository.save(inventory);
    }


    /*
    현재 재고 수량에 전달받은 수량을 더해 저장한다.
    지정한 ID의 재고가 없으면 InventoryNotFoundException을 발생시킨다.
    기존 재고 값을 조회한 뒤, 증가분을 더해 새로운 재고 수량을 계산한다.

    id: 수량을 증고시킬 재고 ID
    quantity: 추가할 재고 수량
    반환값: 수량이 증가된 Inventory
    */
    public Inventory increaseQuantity(Long id, int quantity){

        Inventory inventory = inventoryRepository.findById(id)
                .orElseThrow(()-> new InventoryNotFoundException(
                        "Cannot find that stock."
                ));

        inventory.setQuantity(
                inventory.getQuantity() + quantity
        );

        return inventoryRepository.save(inventory);
    }


    /*
    현재 재고 수량에서 전달받은 수량을 차감해 저장한다.
    지정한 ID의 재고가 없으면 InventoryNotFoundException을 발생시킨다.
    차감 후 재고가 0보다 작아지는 경우 예외를 발생시켜 저장하지 않는다.

    id: 수량을 차감할 재고 ID
    quantity: 차감할 재고 수량
    반환값: 수량이 차감된 Inventory
    */
    public Inventory decreaseQuantity(Long id, int quantity){

        Inventory inventory = inventoryRepository.findById(id)
                .orElseThrow(()-> new InventoryNotFoundException(
                        "Cannot find that stock."
                ));

        int newQuantity = inventory.getQuantity() - quantity;

        if(newQuantity < 0){
            throw new InvalidInventoryQuantityException(
                    "stock should not be less than 0."
            );
        }

        inventory.setQuantity(newQuantity);

        return inventoryRepository.save(inventory);
    }
}
