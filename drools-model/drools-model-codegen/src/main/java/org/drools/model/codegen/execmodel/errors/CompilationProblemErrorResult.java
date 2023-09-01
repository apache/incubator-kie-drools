package org.drools.model.codegen.execmodel.errors;

import org.drools.drl.parser.DroolsError;
import org.kie.internal.builder.ResultSeverity;
import org.kie.internal.jci.CompilationProblem;

public class CompilationProblemErrorResult extends DroolsError {

    private CompilationProblem compilationProblem;

    public CompilationProblemErrorResult(CompilationProblem compilationProblem) {
        super();
        this.compilationProblem = compilationProblem;
    }

    @Override
    public ResultSeverity getSeverity() {
        return ResultSeverity.ERROR;
    }

    @Override
    public String getMessage() {
        return compilationProblem.getMessage();
    }

    @Override
    public int[] getLines() {
        return new int[]{compilationProblem.getStartLine(), compilationProblem.getEndLine()};
    }
}
