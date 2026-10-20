package com.bakery.bakeryinventory.controller;

import com.bakery.bakeryinventory.dto.ReceiptCreateRequest;
import com.bakery.bakeryinventory.model.Receipt;
import com.bakery.bakeryinventory.service.ReceiptService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/receipts")
public class ReceiptController {

    private final ReceiptService receiptService;

    public ReceiptController(ReceiptService receiptService){
        this.receiptService = receiptService;
    }

    /*
    입고 등록 요청을 받아 재료 ID와 입고 수량을 검증한다.
    입고 처리가 완료되면 HTTP 201과 입고 정보를 반환한다.

    request: 재료 ID와 입고 수량을 담은 요청 DTO
    반환값: 입고 ID, 재료 ID, 입고 수량, 입고 시각을 담은 응답 DTO
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ReceiptResponse createReceipt(
            @Valid @RequestBody ReceiptCreateRequest request
    ){
        Receipt receipt = receiptService.createReceipt(
                request.getIngredientId(),
                request.getQuantity()
        );

        return new ReceiptResponse(
                receipt.getId(),
                request.getIngredientId(),
                receipt.getQuantity(),
                receipt.getReceivedAt()
        );
    }

    public record ReceiptResponse(
            Long id,
            Long ingredientId,
            int quantity,
            LocalDateTime receivedAt
    ){
    }
}
