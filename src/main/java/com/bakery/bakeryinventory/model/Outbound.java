package com.bakery.bakeryinventory.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "outbound")
public class Outbound {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ingredient_id", nullable = false)
    private Ingredient ingredient;

    @Column(nullable = false)
    private int quantity;

    @Column(nullable = false)
    private LocalDateTime usedAt;

    protected Outbound(){
    }

    public Outbound(
            Ingredient ingredient,
            int quantity,
            LocalDateTime usedAt
    ){
        this.ingredient = ingredient;
        this.quantity = quantity;
        this.usedAt = usedAt;
    }

    public Long getId(){
        return id;
    }

    public Ingredient getIngredient(){
        return ingredient;
    }

    public int getQuantity(){
        return quantity;
    }

    public LocalDateTime getUsedAt(){
        return usedAt;
    }
}
