package com.bakery.bakeryinventory.exception;

public class InvalidInventoryQuantityException extends RuntimeException {

    public InvalidInventoryQuantityException(String message){
        super(message);
    }
}
