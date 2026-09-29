package com.bakery.bakeryinventory.model;

import jakarta.persistence.*;

@Entity
public class Inventory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    //one inventory to one ingredient
    @JoinColumn(name = "ingredient_id")
    //using ingredient id as column to inventory DB
    private Ingredient ingredient;

    private int quantity;

    public Inventory(){

    }

    public Long getId(){
        return id;
    }

    public Ingredient getIngredient(){
        return ingredient;
    }

    public void setIngredient(Ingredient ingredient){
        this.ingredient = ingredient;
    }

    public int getQuantity(){
        return quantity;
    }

    public void setQuantity(int quantity){
        this.quantity = quantity;
    }
}