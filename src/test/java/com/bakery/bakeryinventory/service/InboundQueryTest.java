package com.bakery.bakeryinventory.service;

import com.bakery.bakeryinventory.dto.InboundQueryResponse;
import com.bakery.bakeryinventory.model.Inbound;
import com.bakery.bakeryinventory.model.Ingredient;
import com.bakery.bakeryinventory.repository.InboundRepository;
import com.bakery.bakeryinventory.repository.IngredientRepository;
import jakarta.persistence.EntityManager;
import org.hibernate.boot.model.source.internal.hbm.EntityHierarchyBuilder;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class InboundQueryTest {

    @Autowired
    private InboundService inboundService;

    @Autowired
    private InboundRepository inboundRepository;

    @Autowired
    private IngredientRepository ingredientRepository;

    @Autowired
    private EntityManager entityManager;

    /*
    전체 조회에서 여러 재료의 기록이 최근 입고 순으로 나오는지 확인한다.
    입고 시각이 같으면 ID가 큰 기록이 먼저 나와야 한다.
    기존 데이터가 있어도 테스트에서 만든 기록만 골라 순서를 검증한다.
     */
    @Test
    void getInbounds_shouldReturnAllInRecentOrder(){
        Ingredient flour = saveIngredient("조회테스트밀가루");
        Ingredient butter = saveIngredient("조회테스트버터");

        LocalDateTime time = LocalDateTime.of(2026,1,1,10,0);

        Inbound oldInbound = inboundRepository.save(
                new Inbound(flour, 10, time)
        );

        Inbound newerInbound = inboundRepository.save(
                new Inbound(butter, 20, time.plusHours(1))
        );

        Inbound sameTimeInbound = inboundRepository.save(
                new Inbound(flour, 30, time.plusHours(1))
        );

        List<Long> testIds = List.of(
                oldInbound.getId(),
                newerInbound.getId(),
                sameTimeInbound.getId()
        );

        flushAndClear();

        List<InboundQueryResponse> result = inboundService.getInbounds(null);

        List<Long> actualIds = result.stream()
                .map(InboundQueryResponse::id)
                .filter(testIds::contains)
                .toList();

        assertEquals(
                List.of(
                        sameTimeInbound.getId(),
                        newerInbound.getId(),
                        oldInbound.getId()
                ), actualIds
        );
    }


    /*
     재료 ID를 지정하면 해당 재료의 기록만 반환하는지 확인한다.
     응답에 포함된 재료 이름, 수량, 입고 시각도 검증한다.
     */
    @Test
    void getInbounds_shouldReturnOnlyRequestedIngredient() {
        Ingredient flour = saveIngredient("필터테스트밀가루");
        Ingredient butter = saveIngredient("필터테스트버터");

        LocalDateTime time = LocalDateTime.of(2026, 1, 1, 10, 0);

        Inbound flourInbound = inboundRepository.save(
                new Inbound(flour, 10, time)
        );

        inboundRepository.save(
                new Inbound(butter, 20, time.plusHours(1))
        );

        Long flourId = flour.getId();
        Long flourInboundId = flourInbound.getId();

        flushAndClear();

        List<InboundQueryResponse> result =
                inboundService.getInbounds(flourId);

        assertEquals(1, result.size());

        InboundQueryResponse response = result.getFirst();

        assertEquals(flourInboundId, response.id());
        assertEquals(flourId, response.ingredientId());
        assertEquals("필터테스트밀가루", response.ingredientName());
        assertEquals(10, response.quantity());
        assertEquals(time, response.receivedAt());
    }

    /*
     입고 기록이 없는 재료를 조회하면 빈 목록을 반환하는지 확인한다.
     */
    @Test
    void getInbounds_shouldReturnEmptyListWhenNoRecords() {
        Ingredient ingredient = saveIngredient("입고없는재료");
        Long ingredientId = ingredient.getId();

        flushAndClear();

        List<InboundQueryResponse> result =
                inboundService.getInbounds(ingredientId);

        assertTrue(result.isEmpty());
    }

    private Ingredient saveIngredient(String name) {
        Ingredient ingredient = new Ingredient();
        ingredient.setName(name);
        return ingredientRepository.save(ingredient);
    }

    private void flushAndClear() {
        entityManager.flush();
        entityManager.clear();
    }
}