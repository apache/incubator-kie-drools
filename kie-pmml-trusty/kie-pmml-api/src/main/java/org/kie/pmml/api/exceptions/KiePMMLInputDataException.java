package org.kie.pmml.api.exceptions;

/**
 * <code>RuntimeException</code>s to be wrapping to <b>unchecked</b> ones at <i>customer</i> API boundaries
 */
public class KiePMMLInputDataException extends KiePMMLException {

    private static final long serialVersionUID = -6638828457762000141L;

    public KiePMMLInputDataException(String message, Throwable cause) {
        super(message, cause);
    }

    public KiePMMLInputDataException(Throwable cause) {
        super(cause);
    }

    public KiePMMLInputDataException(String message) {
        super(message);
    }
}
