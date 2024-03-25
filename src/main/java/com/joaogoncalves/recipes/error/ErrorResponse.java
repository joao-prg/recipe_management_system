package com.joaogoncalves.recipes.error;

import lombok.Getter;

@Getter
public class ErrorResponse {
    private final int statusCode;
    private final String message;

    public ErrorResponse(final int statusCode, final String message) {
        this.statusCode = statusCode;
        this.message = message;
    }
}