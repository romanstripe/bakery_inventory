package com.bakery.bakeryinventory.controller;

import com.bakery.bakeryinventory.dto.OutboundCreateRequest;
import com.bakery.bakeryinventory.model.Outbound;
import com.bakery.bakeryinventory.service.OutboundService;
import com.bakery.bakeryinventory.dto.OutboundQueryResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

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

    /*
    재료 ID를 생략하면 전체 출고 이력을 최근순으로 반환한다.
    재료 ID를 지정하면 해당 재료의 출고 이력만 최근순으로 반환한다.

    ingredientId: 조회할 재료 ID (생략 가능)
    반환값: 출고 ID, 재료 ID, 재료 이름, 사용 수량, 사용 시각을 담은 응답 DTO 목록
    */
    @GetMapping
    public List<OutboundQueryResponse> getOutbounds(
            @RequestParam(name = "ingredientId", required = false) Long ingredientId
    ){
        return outboundService.getOutbounds(ingredientId);
    }
}