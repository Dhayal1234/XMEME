package com.crio.starter.exception;

public class MemeNotFoundException extends RuntimeException {
    public MemeNotFoundException(String message) {
        super(message);
    }
}
