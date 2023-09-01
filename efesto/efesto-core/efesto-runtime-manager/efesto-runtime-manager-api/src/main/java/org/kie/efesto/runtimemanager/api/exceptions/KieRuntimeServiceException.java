package org.kie.efesto.runtimemanager.api.exceptions;

public class KieRuntimeServiceException extends RuntimeException {

    public KieRuntimeServiceException() {
    }

    public KieRuntimeServiceException(String message) {
        super(message);
    }

    public KieRuntimeServiceException(String message, Throwable cause) {
        super(message, cause);
    }

    public KieRuntimeServiceException(Throwable cause) {
        super(cause);
    }

    public KieRuntimeServiceException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
