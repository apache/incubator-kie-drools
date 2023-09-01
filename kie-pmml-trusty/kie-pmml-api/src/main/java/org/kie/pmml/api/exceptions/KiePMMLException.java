package org.kie.pmml.api.exceptions;

/**
 * <code>RuntimeException</code>s to be wrapping to <b>unchecked</b> ones at <i>customer</i> API boundaries
 */
public class KiePMMLException extends RuntimeException {

    private static final long serialVersionUID = -6638828457762000141L;

    public KiePMMLException(String message, Throwable cause) {
        super(message, cause);
    }

    public KiePMMLException(Throwable cause) {
        super(cause);
    }

    public KiePMMLException(String message) {
        super(message);
    }
}
