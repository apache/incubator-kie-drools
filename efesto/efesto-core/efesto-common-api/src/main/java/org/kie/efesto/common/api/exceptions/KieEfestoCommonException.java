package org.kie.efesto.common.api.exceptions;

public class KieEfestoCommonException extends RuntimeException {

    public KieEfestoCommonException() {
    }

    public KieEfestoCommonException(String message) {
        super(message);
    }

    public KieEfestoCommonException(String message, Throwable cause) {
        super(message, cause);
    }

    public KieEfestoCommonException(Throwable cause) {
        super(cause);
    }

    public KieEfestoCommonException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
