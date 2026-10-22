package com.bakery.bakeryinventory.repository;

import com.bakery.bakeryinventory.model.Outbound;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OutboundRepository
    extends JpaRepository<Outbound, Long>{}
