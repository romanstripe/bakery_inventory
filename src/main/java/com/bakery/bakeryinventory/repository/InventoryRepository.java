package com.bakery.bakeryinventory.repository;

import com.bakery.bakeryinventory.model.Inventory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InventoryRepository
    extends JpaRepository<Inventory, Long>{

}

