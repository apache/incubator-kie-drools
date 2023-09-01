package org.kie.pmml.api.exceptions;

public class KieDataFieldException extends KiePMMLInternalException {

    public KieDataFieldException(String message, Throwable cause) {
        super(message, cause);
    }

    public KieDataFieldException(String message) {
        super(message);
    }
}
