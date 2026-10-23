package com.bakery.bakeryinventory.exception;

public class InvalidRecipeException extends RuntimeException{

    public InvalidRecipeException(String message){
        super(message);
    }
}
