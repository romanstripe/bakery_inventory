package com.bakery.bakeryinventory.exception;

public class InventoryNotFoundException extends RuntimeException{

    public InventoryNotFoundException(String message){
        super(message);
    }
}
