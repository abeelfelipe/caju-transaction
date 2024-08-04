package com.caju.exceptions;

public class InsufficientFundsTransactionException extends Throwable {
    public InsufficientFundsTransactionException(String message) {
        super(message);
    }
}
