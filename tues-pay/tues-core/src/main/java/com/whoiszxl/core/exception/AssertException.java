package com.whoiszxl.core.exception;

public class AssertException extends RuntimeException{

    private static final long serialVersionUID = 1L;

    public AssertException() {
        super();
    }
    public AssertException(String message) {
        super(message);
    }
}
