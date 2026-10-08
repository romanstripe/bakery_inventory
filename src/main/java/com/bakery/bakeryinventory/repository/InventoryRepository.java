package com.bakery.bakeryinventory.repository;

import jakarta.persistence.LockModeType;
import com.bakery.bakeryinventory.model.Inventory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface InventoryRepository
    extends JpaRepository<Inventory, Long>{

    /*
    Inventory를 조회할 때 연결된 Ingredient도 함께 조회한다.
    Fetch Join 을 사용해 Ingredient 를 개별 쿼리로 조회하는 N+1 문제를 방지한다.

    반환값: Ingredient가 함께 조회된 Inventory 목록
     */
    @Query("SELECT i FROM Inventory i JOIN FETCH i.ingredient")
    List<Inventory> findAllWithIngredient();

    /*
    재고를 수정하는 동안 다른 트랜잭션이 같은 재고를 동시에 수정하지 못하도록
    PESSIMISTIC_WRITE 락을 걸어 조회한다.

    id: 조회할 재고 ID
    반환값: 해당 ID 의 Inventory
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT i FROM Inventory i WHERE i.id = :id")
    Optional<Inventory> findByIdForUpdate(@Param("id") Long id);

}

