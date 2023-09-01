package org.kie.efesto.runtimemanager.api.exceptions;

public class EfestoRuntimeManagerException extends RuntimeException {

    public EfestoRuntimeManagerException() {
    }

    public EfestoRuntimeManagerException(String message) {
        super(message);
    }

    public EfestoRuntimeManagerException(String message, Throwable cause) {
        super(message, cause);
    }

    public EfestoRuntimeManagerException(Throwable cause) {
        super(cause);
    }

    public EfestoRuntimeManagerException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
