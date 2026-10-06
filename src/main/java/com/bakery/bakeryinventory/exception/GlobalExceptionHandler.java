package com.bakery.bakeryinventory.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    /*
    잘못된 재고 수량 예외를 HTTP 400 Bad Request로 변환한다.
    서비스에서 발생한 InvalidInventoryQuantityException의 메시지를 응답 본문에 담는다.

    exception: 잘못된 재고 수량 예외 정보
    반환값: 오류 메시지를 담은 HTTP 400 응답
    */
    @ExceptionHandler(InvalidInventoryQuantityException.class)
    public ResponseEntity<Map<String, String>> handleInvalidInventoryQuantity(
            InvalidInventoryQuantityException exception
    ){
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(Map.of(
                        "message", exception.getMessage()
                ));
    }


    /*
    존재하지 않는 재고를 조회하거나 수정하려 할 때 발생한 예외를 HTTP 404 Not Found로 변환한다.
    InventoryNotFoundException의 메시지를 응답 본문에 담는다.

    exception: 존재하지 않는 재고 예외 정보
    반환값: 오류 메시지를 담은 HTTP 404 응답
    */
    @ExceptionHandler(InventoryNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleInventoryNotFound(
            InventoryNotFoundException exception
    ){
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(Map.of(
                        "message", exception.getMessage()
                ));
    }


    /*
    DTO 검증에 실패했을 떄 발생한 첫 번째 검증 메시지를 HTTP 400으로 반환한다.
    @Valid 에서 발생한 오류도 다른 예외 응답과 같은 형식으로 맞춘다.

    exception: DTO 검증 실패 정보
    반환값: 검증 메시지를 담은 HTTP 400 응답
    */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationException(
            MethodArgumentNotValidException exception
    ){
        String message = exception.getBindingResult()
                .getFieldErrors()
                .get(0)
                .getDefaultMessage();

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(Map.of("message", message));
    }
}
