package com.joaogoncalves.recipes.exception;

public class RecipeNotFoundException extends RuntimeException {
    public RecipeNotFoundException(String message){
        super(message);
    }
}
