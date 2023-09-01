package org.drools.model.codegen.execmodel.errors;

import org.drools.drl.parser.DroolsError;
import org.kie.internal.builder.ResultSeverity;

public class DuplicatedDeclarationError extends DroolsError {

    private String declaration;

    public DuplicatedDeclarationError( String declaration) {
        super();
        this.declaration = declaration;
    }

    @Override
    public ResultSeverity getSeverity() {
        return ResultSeverity.ERROR;
    }

    @Override
    public String getMessage() {
        return "Duplicated declaration: " + declaration;
    }

    @Override
    public int[] getLines() {
        return new int[0];
    }
}
