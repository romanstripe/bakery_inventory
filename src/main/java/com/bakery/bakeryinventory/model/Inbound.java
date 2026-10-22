package com.bakery.bakeryinventory.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name="inbound")
public class Inbound {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ingredient_id", nullable = false)
    private Ingredient ingredient;

    @Column(nullable = false)
    private int quantity;

    @Column(nullable = false)
    private LocalDateTime receivedAt;

    protected Inbound(){
    }

    public Inbound(Ingredient ingredient, int quantity, LocalDateTime receivedAt) {
        this.ingredient = ingredient;
        this.quantity = quantity;
        this.receivedAt = receivedAt;
    }

    public Long getId() {
        return id;
    }

    public Ingredient getIngredient() {
        return ingredient;
    }

    public int getQuantity() {
        return quantity;
    }

    public LocalDateTime getReceivedAt() {
        return receivedAt;
    }
}
