package com.bakery.bakeryinventory.repository;

import com.bakery.bakeryinventory.model.Receipt;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReceiptRepository
        extends JpaRepository<Receipt, Long> {
}