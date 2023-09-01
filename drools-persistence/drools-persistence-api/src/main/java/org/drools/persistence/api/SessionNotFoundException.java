package org.drools.persistence.api;

public class SessionNotFoundException extends RuntimeException {

    public SessionNotFoundException(String message) {
        super(message);
    }

    public SessionNotFoundException(String message, Exception cause) {
        super(message, cause);
    }
}
