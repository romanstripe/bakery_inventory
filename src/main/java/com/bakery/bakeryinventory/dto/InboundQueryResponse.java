package com.bakery.bakeryinventory.dto;

import com.bakery.bakeryinventory.model.Inbound;

import java.time.LocalDateTime;

public record InboundQueryResponse(
        Long id,
        Long ingredientId,
        String ingredeintName,
        int quantity,
        LocalDateTime receicedAt
){
    /*
    입고 엔티티를 조회 응답 DTO로 변환한다.
    연결된 재료에서 ID 와 이름을 가져온다.

    inbound: 조회한 입고 기록
    반환값: 입고 정보와 재료 정보가 담긴 응답 DTO
     */
    public static InboundQueryResponse from(Inbound inbound){
        return new InboundQueryResponse(
                inbound.getId(),
                inbound.getIngredient().getId(),
                inbound.getIngredient().getName(),
                inbound.getQuantity(),
                inbound.getReceivedAt()
        );
    }
}