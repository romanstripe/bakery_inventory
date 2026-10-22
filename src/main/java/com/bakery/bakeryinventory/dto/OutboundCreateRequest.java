package com.bakery.bakeryinventory.dto;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public class OutboundCreateRequest {

    @NotNull
    @Positive
    private Long ingredientId;

    @NotNull
    @Positive
    private Integer quantity;

    public Long getIngredientId(){
        return ingredientId;
    }

    public void setIngredientId(Long ingredientId){
        this.ingredientId = ingredientId;
    }

    public Integer getQuantity(){
        return quantity;
    }

    public void setQuantity(Integer quantity){
        this.quantity = quantity;
    }
}
