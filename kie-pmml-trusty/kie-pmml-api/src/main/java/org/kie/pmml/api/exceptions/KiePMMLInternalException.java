package org.kie.pmml.api.exceptions;

/**
 * Internal unchecked Exceptions to be wrapped to <b>checked</b> one only at <i>customer</i> API boundaries
 */
public class KiePMMLInternalException extends RuntimeException {

    public KiePMMLInternalException(String message, Throwable cause) {
        super(message, cause);
    }

    public KiePMMLInternalException(String message) {
        super(message);
    }
}
