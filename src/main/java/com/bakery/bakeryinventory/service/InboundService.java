package com.bakery.bakeryinventory.service;

import com.bakery.bakeryinventory.dto.InboundQueryResponse;
import com.bakery.bakeryinventory.exception.InventoryNotFoundException;
import com.bakery.bakeryinventory.model.Inventory;
import com.bakery.bakeryinventory.model.Inbound;
import com.bakery.bakeryinventory.repository.InventoryRepository;
import com.bakery.bakeryinventory.repository.InboundRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class InboundService {

    private final InventoryRepository inventoryRepository;
    private final InboundRepository inboundRepository;

    public InboundService(
            InventoryRepository inventoryRepository,
            InboundRepository inboundRepository
    ){
        this.inventoryRepository = inventoryRepository;
        this.inboundRepository = inboundRepository;
    }

    /*
    지정한 재료의 재고를 조회해 입고 수량만큼 증가시키고 입고 기록을 저장한다.
    재고 변경과 입고 기록 저장을 하나의 트랜잭션으로 처리한다.
    해당 재료의 재고가 없으면 InventoryNotFoundException을 반생시킨다.

    ingredientId: 입고된 재료의 ID
    quantity: 입고 수량
    반환값: 저장된 Inbound
     */
    @Transactional
    public Inbound createInbound(Long ingredientId, int quantity){
        Inventory inventory = inventoryRepository
                .findByIngredientIdForUpdate(ingredientId)
                .orElseThrow(() -> new InventoryNotFoundException(
                        "해당 재료의 재고를 찾을 수 없습니다. "
                ));

        inventory.setQuantity(inventory.getQuantity() + quantity);

        Inbound inbound = new Inbound(
                inventory.getIngredient(),
                quantity,
                LocalDateTime.now()
        );

        return inboundRepository.save(inbound);
    }

    /*
    재료 ID가 없으면 전체 입고 이력을 조회한다.
    재료 ID가 있으면 해당 재료의 입고 이력만 조회한다.
    조회한 기록을 응답 DTO로 변환한다.

    ingredientId: 조회할 재료 ID, null이면 전체 조회
    반환값: 최근 입고 순으로 정렬된 입고 응답 목록
    */
    @Transactional(readOnly = true)
    public List<InboundQueryResponse> getInbounds(Long ingredientId){
        List<Inbound> inbounds;

        if(ingredientId == null){
            inbounds = inboundRepository.findAllWithIngredient();
        }else{
            inbounds = inboundRepository
                    .findAllByIngredientIdWithIngredient(ingredientId);
        }

        return inbounds.stream()
                .map(InboundQueryResponse::from)
                .toList();
    }
}
