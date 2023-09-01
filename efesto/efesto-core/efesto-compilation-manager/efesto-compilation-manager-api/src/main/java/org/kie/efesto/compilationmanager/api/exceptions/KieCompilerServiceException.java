package org.kie.efesto.compilationmanager.api.exceptions;

public class KieCompilerServiceException extends RuntimeException {

    public KieCompilerServiceException() {
    }

    public KieCompilerServiceException(String message) {
        super(message);
    }

    public KieCompilerServiceException(String message, Throwable cause) {
        super(message, cause);
    }

    public KieCompilerServiceException(Throwable cause) {
        super(cause);
    }

    public KieCompilerServiceException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
