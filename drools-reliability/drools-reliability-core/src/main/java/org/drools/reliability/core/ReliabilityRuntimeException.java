package org.drools.reliability.core;

public class ReliabilityRuntimeException extends RuntimeException {

    public ReliabilityRuntimeException(String message) {
        super(message);
    }

    public ReliabilityRuntimeException(Throwable cause) {
        super(cause);
    }
}
