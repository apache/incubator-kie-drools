package org.kie.pmml.api.exceptions;

/**
 * <code>RuntimeException</code>s wrapping all <b>not-KiePMML</b> ones at <i>customer</i> API boundaries
 */
public class ExternalException extends RuntimeException {

    public ExternalException(String message, Throwable cause) {
        super(message, cause);
    }

    public ExternalException(String message) {
        super(message);
    }

    public ExternalException(Throwable cause) {
        super(cause);
    }
}
