package com.caju.exceptions;

public class AccountNotFoundException extends Throwable {

    public AccountNotFoundException (String message) {
        super(message);
    }
}
