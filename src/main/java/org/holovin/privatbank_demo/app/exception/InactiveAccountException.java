package org.holovin.privatbank_demo.app.exception;

public class InactiveAccountException extends RuntimeException {

    public InactiveAccountException(String message) {
        super(message);
    }
}
