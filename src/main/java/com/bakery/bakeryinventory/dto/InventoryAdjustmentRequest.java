package com.bakery.bakeryinventory.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public class InventoryAdjustmentRequest {

    @NotNull(message = "quantity is required.")
    @Positive(message = "quantity must be greater than 0.")
    private Integer quantity;

    public Integer getQuantity(){
        return quantity;
    }

    public void setQuantity(Integer quantity){
        this.quantity = quantity;
    }
}
