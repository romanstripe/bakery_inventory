package com.bakery.bakeryinventory.repository;

import com.bakery.bakeryinventory.model.Inventory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface InventoryRepository
    extends JpaRepository<Inventory, Long>{

    /*
    Inventory를 조회할 때 연결된 Ingredient도 함께 조회한다.
    Fetch Join 을 사용해 Ingredient 를 개별 쿼리로 조회하는 N+1 문제를 방지한다.

    반환값: Ingredient가 함께 조회된 Inventory 목록
     */
    @Query("SELECT i FROM Inventory i JOIN FETCH i.ingredient")
    List<Inventory> findAllWithIngredient();

}

