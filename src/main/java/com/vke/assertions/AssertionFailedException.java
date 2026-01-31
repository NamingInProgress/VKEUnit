package com.vke.assertions;

public class AssertionFailedException extends RuntimeException {
    public AssertionFailedException(String message) {
        super(message);
    }
}
