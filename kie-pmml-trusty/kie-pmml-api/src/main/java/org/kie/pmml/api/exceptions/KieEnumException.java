package org.kie.pmml.api.exceptions;

public class KieEnumException extends KiePMMLInternalException {

    public KieEnumException(String message, Throwable cause) {
        super(message, cause);
    }

    public KieEnumException(String message) {
        super(message);
    }
}
