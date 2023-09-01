package org.drools.model.codegen.execmodel.errors;

import java.util.Optional;

import org.drools.drl.parser.DroolsError;
import org.drools.drl.ast.descr.BaseDescr;
import org.kie.internal.builder.ResultSeverity;

public class InvalidExpressionErrorResult extends DroolsError {

    private String message;

    private int[] errorLines = new int[1];

    public InvalidExpressionErrorResult(String message) {
        super();
        this.message = message;
        this.errorLines[0] = -1;
    }

    public InvalidExpressionErrorResult(String message, Optional<BaseDescr> descrOpt) {
        this(message);
        descrOpt.ifPresent(descr -> this.errorLines[0] = descr.getLine());
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
        return errorLines;
    }
}

