package com.bakery.bakeryinventory.dto;

import com.bakery.bakeryinventory.model.Outbound;
import java.time.LocalDateTime;

public record OutboundQueryResponse(
        Long id,
        Long ingredientId,
        String ingredientName,
        int quantity,
        LocalDateTime usedAt
) {
    public static OutboundQueryResponse from(Outbound outbound){
        return new OutboundQueryResponse(
                outbound.getId(),
                outbound.getIngredient().getId(),
                outbound.getIngredient().getName(),
                outbound.getQuantity(),
                outbound.getUsedAt()
        );
    }
}
