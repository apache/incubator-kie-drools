package org.kie.dmn.typesafe;

public class DMNTypeSafeException extends RuntimeException {
    public DMNTypeSafeException(String message) {
        super(message);
    }

    public DMNTypeSafeException(Throwable cause) {
        super(cause);
    }
}
