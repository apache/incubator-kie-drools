package org.drools.model.codegen.execmodel.errors;

import org.drools.drl.parser.DroolsError;
import org.kie.internal.builder.ResultSeverity;

public class VariableUsedInBindingError extends DroolsError {

    private String usedDeclaration;
    private String constraintExpressionString;

    private int[] errorLines = new int[1];

    public VariableUsedInBindingError(String usedDeclaration, String constraintExpressionString) {
        super();
        this.usedDeclaration = usedDeclaration;
        this.constraintExpressionString = constraintExpressionString;
        this.errorLines[0] = -1;
    }

    @Override
    public ResultSeverity getSeverity() {
        return ResultSeverity.ERROR;
    }

    @Override
    public String getMessage() {
        return String.format("Variables can not be used inside bindings. Variable [%s] is being used in binding '%s'",
                             usedDeclaration,
                             constraintExpressionString);
    }

    @Override
    public int[] getLines() {
        return errorLines;
    }
}
