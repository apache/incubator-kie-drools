package org.kie.pmml.evaluator.api.exceptions;

import org.kie.pmml.api.exceptions.KiePMMLInternalException;

/**
 * Exception raised whenever there is an error on the <code>KiePMMLModel</code> as whole (e.g. un unexpected implementation received)
 */
public class KiePMMLModelException extends KiePMMLInternalException {

    public KiePMMLModelException(String message, Throwable cause) {
        super(message, cause);
    }

    public KiePMMLModelException(String message) {
        super(message);
    }
}
