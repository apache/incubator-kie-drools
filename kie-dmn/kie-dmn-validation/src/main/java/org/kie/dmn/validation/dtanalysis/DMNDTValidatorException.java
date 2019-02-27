package org.kie.dmn.validation.dtanalysis;

import org.kie.dmn.model.api.DecisionTable;

public class DMNDTValidatorException extends RuntimeException {

    private final DecisionTable dt;

    public DMNDTValidatorException(String message, DecisionTable dt) {
        super(message);
        this.dt = dt;
    }

    public DMNDTValidatorException(Throwable cause, DecisionTable dt) {
        super(cause);
        this.dt = dt;
    }

    @Override
    public String getMessage() {
        return super.getMessage() + "\nIn table:\n" + dt.toString();
    }

}
