package org.drools.model.codegen.execmodel.errors;

import org.drools.drl.parser.DroolsError;
import org.kie.internal.builder.ResultSeverity;

public class UnknownDeclarationError extends DroolsError {

    private String declaration;

    public UnknownDeclarationError(String declaration) {
        super();
        this.declaration = declaration;
    }

    @Override
    public ResultSeverity getSeverity() {
        return ResultSeverity.ERROR;
    }

    @Override
    public String getMessage() {
        return "Unknown declaration: " + declaration;
    }

    @Override
    public int[] getLines() {
        return new int[0];
    }
}
