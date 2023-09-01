package org.drools.model.codegen.execmodel.errors;

import org.drools.drl.parser.DroolsError;
import org.kie.internal.builder.ResultSeverity;

public class UnsupportedFeatureError extends DroolsError {

    private String message;

    public UnsupportedFeatureError(String message) {
        super();
        this.message = message;
    }

    @Override
    public ResultSeverity getSeverity() {
        return ResultSeverity.ERROR;
    }

    @Override
    public String getMessage() {
        return message;
    }

    @Override
    public int[] getLines() {
        return new int[0];
    }
}
