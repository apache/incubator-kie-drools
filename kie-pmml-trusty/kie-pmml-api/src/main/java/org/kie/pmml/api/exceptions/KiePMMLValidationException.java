package org.kie.pmml.api.exceptions;

public class KiePMMLValidationException extends KiePMMLException {

    public KiePMMLValidationException(String message, Throwable cause) {
        super(message, cause);
    }

    public KiePMMLValidationException(Throwable cause) {
        super(cause);
    }

    public KiePMMLValidationException(String message) {
        super(message);
    }
}