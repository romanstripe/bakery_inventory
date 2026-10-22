package com.bakery.bakeryinventory.controller;

import com.bakery.bakeryinventory.dto.OutboundCreateRequest;
import com.bakery.bakeryinventory.model.Outbound;
import com.bakery.bakeryinventory.service.OutboundService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/outbounds")
public class OutboundController {

    private final OutboundService outboundService;

    public OutboundController(OutboundService outboundService) {
        this.outboundService = outboundService;
    }

    /*
     재료 ID와 사용 수량을 받아 출고를 등록한다.
     요청값을 검증하고 처리 완료 시 HTTP 201을 반환한다.

     request: 재료 ID와 사용 수량을 담은 요청 DTO
     반환값: 출고 ID, 재료 ID, 사용 수량, 사용 시각을 담은 응답 DTO
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public OutboundResponse createOutbound(
            @Valid @RequestBody OutboundCreateRequest request
    ) {
        Outbound outbound = outboundService.createOutbound(
                request.getIngredientId(),
                request.getQuantity()
        );

        return new OutboundResponse(
                outbound.getId(),
                outbound.getIngredient().getId(),
                outbound.getQuantity(),
                outbound.getUsedAt()
        );
    }

    public record OutboundResponse(
            Long id,
            Long ingredientId,
            int quantity,
            LocalDateTime usedAt
    ) {
    }
}