package com.bakery.bakeryinventory.model;

import jakarta.persistence.*;
import jakarta.persistence.criteria.CriteriaBuilder;

@Entity
@Table(
        name = "recipe_ingredient",
        uniqueConstraints = @UniqueConstraint(
                columnNames = {"recipe_id", "ingredient_id"}
        )
)
public class RecipeIngredient {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recipe_id", nullable = false)
    private Recipe recipe;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ingredient_id", nullable = false)
    private Ingredient ingredient;

    @Column(nullable = false)
    private int quantity;

    protected RecipeIngredient(){
    }

    public RecipeIngredient(
            Recipe recipe,
            Ingredient ingredient,
            int quantity
    ){
        this.recipe = recipe;
        this.ingredient = ingredient;
        this.quantity = quantity;
    }

    public Long getId(){
        return id;
    }

    public Recipe getRecipe(){
        return recipe;
    }

    public Ingredient getIngredient(){
        return ingredient;
    }

    public int getQuantity(){
        return quantity;
    }
}
