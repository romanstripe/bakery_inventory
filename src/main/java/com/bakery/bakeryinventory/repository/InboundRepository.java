package com.bakery.bakeryinventory.repository;

import com.bakery.bakeryinventory.model.Inbound;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface InboundRepository
        extends JpaRepository<Inbound, Long> {

    /*
    전체 입고 이력을 재료 정보와 함께 조회한다.
    입고 시각이 같으면 입고 ID가 큰 기록을 먼저 반환한다.

    반환값: 최근 입고 순으로 정렬된 Inbound 목록
     */
    @Query("""
           SELECT i
           FROM Inbound i
           JOIN FETCH i.ingredient
           ORDER BY i.receivedAt DESC, i.id DESC
           """)
    List<Inbound> findAllWithIngredient();

    /*
    지정한 재료의 입고 이력을 재료 정보와 함께 조회한다.
    입고 시각과 입고 ID를 기준으로 최근 기록부터 정렬한다.

    ingredientId: 조회할 재료 ID
    반환값: 해당 재료의 입고 목록. 기록이 없으면 빈 목록
     */
    @Query("""
           SELECT i 
           FROM Inbound i 
           JOIN FETCH i.ingredient 
           WHERE i.ingredient.id = :ingredientId 
           ORDER BY i.receivedAt DESC, i.id DESC
           """)
    List<Inbound> findAllByIngredientIdWithIngredient(
            @Param("ingredientId") Long ingredientId
    );
}