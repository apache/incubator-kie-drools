package org.kie.efesto.compilationmanager.api.exceptions;

public class EfestoCompilationManagerException extends RuntimeException {

    public EfestoCompilationManagerException() {
    }

    public EfestoCompilationManagerException(String message) {
        super(message);
    }

    public EfestoCompilationManagerException(String message, Throwable cause) {
        super(message, cause);
    }

    public EfestoCompilationManagerException(Throwable cause) {
        super(cause);
    }

    public EfestoCompilationManagerException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
