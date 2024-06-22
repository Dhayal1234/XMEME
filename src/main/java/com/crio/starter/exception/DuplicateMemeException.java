package com.crio.starter.exception;

public class DuplicateMemeException extends RuntimeException {
    public DuplicateMemeException(String message) {
        super(message);
    }
}
