package com.bakery.bakeryinventory.repository;

import com.bakery.bakeryinventory.model.Outbound;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface OutboundRepository
    extends JpaRepository<Outbound, Long>{

    @Query("""
           SELECT o FROM Outbound o
           JOIN FETCH o.ingredient
           ORDER BY o.usedAt DESC, o.id DESC
           """)
    List<Outbound> findAllWithIngredient();

    /*
    지정한 재료의 출고 이력을 재료 정보와 함께 최근순으로 조회한다.
    사용 시각이 같으면 출고 ID가 큰 기록을 먼저 반환한다.

    ingredientId: 조회할 재료 ID
    반환값: 해당 재료의 출고 이력 목록
    */
    @Query("""
           SELECT o FROM Outbound o
           JOIN FETCH o.ingredient
           WHERE o.ingredient.id = :ingredientId
           ORDER BY o.usedAt DESC, o.id DESC
           """)
    List<Outbound> findAllWithIngredientByIngredientId(
            @Param("ingredientId") Long ingredientId
    );
}
