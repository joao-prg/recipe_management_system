package com.joaogoncalves.recipes.exception;

public class UserNotAuthorOfRecipeException extends RuntimeException {
    public UserNotAuthorOfRecipeException(String message){
        super(message);
    }
}
