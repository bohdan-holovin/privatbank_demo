package org.holovin.privatbank_demo.domain.exception;

public class InactiveAccountException extends RuntimeException {

    public InactiveAccountException(String message) {
        super(message);
    }
}
