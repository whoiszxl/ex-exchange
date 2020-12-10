package com.whoiszxl.core.exception;

public class SystemException extends RuntimeException{

    private static final long serialVersionUID = 1L;

    public SystemException() {
        super();
    }
    public SystemException(String message) {
        super(message);
    }
}
