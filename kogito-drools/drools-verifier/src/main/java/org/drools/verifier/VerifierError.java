package org.drools.verifier;

public class VerifierError {

    private final String message;

    public VerifierError(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

}
