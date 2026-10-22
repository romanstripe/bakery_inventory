package com.bakery.bakeryinventory.service;

import com.bakery.bakeryinventory.exception.InvalidInventoryQuantityException;
import com.bakery.bakeryinventory.exception.InventoryNotFoundException;
import com.bakery.bakeryinventory.model.Inventory;
import com.bakery.bakeryinventory.model.Outbound;
import com.bakery.bakeryinventory.repository.InventoryRepository;
import com.bakery.bakeryinventory.repository.OutboundRepository;
import jakarta.persistence.criteria.CriteriaBuilder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class OutboundService {

    private final InventoryRepository inventoryRepository;
    private final OutboundRepository outboundRepository;

    public OutboundService(
            InventoryRepository inventoryRepository,
            OutboundRepository outboundRepository
    ){
        this.inventoryRepository = inventoryRepository;
        this.outboundRepository = outboundRepository;
    }/*
     지정한 재료의 재고를 사용 수량만큼 차감하고 출고 기록을 저장한다.
     현재 재고가 부족하면 예외를 발생시킨다.
     재고 변경과 출고 기록 저장을 하나의 트랜잭션으로 처리한다.

     ingredientId: 사용한 재료의 ID
     quantity: 사용 수량
     반환값: 저장된 Outbound
     */
    @Transactional
    public Outbound createOutbound(Long ingredientId, int quantity) {
        if (quantity <= 0) {
            throw new InvalidInventoryQuantityException(
                    "사용 수량은 0보다 커야 합니다."
            );
        }

        Inventory inventory = inventoryRepository
                .findByIngredientIdForUpdate(ingredientId)
                .orElseThrow(() -> new InventoryNotFoundException(
                        "해당 재료의 재고를 찾을 수 없습니다."
                ));

        int remainingQuantity = inventory.getQuantity() - quantity;

        if (remainingQuantity < 0) {
            throw new InvalidInventoryQuantityException(
                    "현재 재고보다 많은 수량을 사용할 수 없습니다."
            );
        }

        inventory.setQuantity(remainingQuantity);

        Outbound outbound = new Outbound(
                inventory.getIngredient(),
                quantity,
                LocalDateTime.now()
        );

        return outboundRepository.save(outbound);
    }
}
