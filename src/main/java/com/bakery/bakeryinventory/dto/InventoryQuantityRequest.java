package com.bakery.bakeryinventory.dto;

import jakarta.validation.constraints.NotNull;

public class InventoryQuantityRequest {

    @NotNull
    private Integer quantity;
    //int 값이 안들어오면 자동으로 0이지만 Integer은 NULL 로 받아들인다
    //사용자가 0을 보낸 건지, quantity를 안 보낸 건지 파악하기 위해 Ingeter 사용

    public Integer getQuantity(){
        return quantity;
    }

    public void setQuantity(Integer quantity){
        this.quantity = quantity;
    }
}
