package org.kie.dmn.validation.dtanalysis;

import org.kie.dmn.model.api.DecisionTable;

public class DMNDTAnalysisException extends RuntimeException {

    private final DecisionTable dt;

    public DMNDTAnalysisException(String message, DecisionTable dt) {
        super(message);
        this.dt = dt;
    }

    public DecisionTable getDt() {
        return dt;
    }

}
