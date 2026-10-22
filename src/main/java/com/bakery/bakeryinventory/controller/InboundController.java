package com.bakery.bakeryinventory.controller;

import com.bakery.bakeryinventory.dto.InboundCreateRequest;
import com.bakery.bakeryinventory.model.Inbound;
import com.bakery.bakeryinventory.service.InboundService;
import com.bakery.bakeryinventory.dto.InboundQueryResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/inbounds")
public class InboundController {

    private final InboundService inboundService;

    public InboundController(InboundService inboundService){
        this.inboundService = inboundService;
    }

    /*
    입고 등록 요청을 받아 재료 ID와 입고 수량을 검증한다.
    입고 처리가 완료되면 HTTP 201과 입고 정보를 반환한다.

    request: 재료 ID와 입고 수량을 담은 요청 DTO
    반환값: 입고 ID, 재료 ID, 입고 수량, 입고 시각을 담은 응답 DTO
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public InboundResponse createInbound(
            @Valid @RequestBody InboundCreateRequest request
    ){
        Inbound inbound = inboundService.createInbound(
                request.getIngredientId(),
                request.getQuantity()
        );

        return new InboundResponse(
                inbound.getId(),
                request.getIngredientId(),
                inbound.getQuantity(),
                inbound.getReceivedAt()
        );
    }

    public record InboundResponse(
            Long id,
            Long ingredientId,
            int quantity,
            LocalDateTime receivedAt
    ){
    }

    /*
    제료 ID가 없으면 전체 입고 이력을 반환한다.
    재료 ID가 있으면 해당 재료의 입고 이력만 반환한다.

    ingredientId: 조회할 재료 ID, 생략 가능
    반환값: 최근 입고 순으로 정렬된 입고 응답 목록
     */
    @GetMapping
    public List<InboundQueryResponse> getInbounds(
            @RequestParam(required = false) Long ingredientId
    ){
        return inboundService.getInbounds(ingredientId);
    }
}
