package org.drools.model.codegen.execmodel.errors;

import java.util.Optional;

import com.github.javaparser.ast.expr.Expression;
import org.drools.drl.parser.DroolsError;
import org.drools.drl.ast.descr.BaseDescr;
import org.drools.mvel.parser.printer.PrintUtil;
import org.kie.internal.builder.ResultSeverity;

public class ParseExpressionErrorResult extends DroolsError {

    private Expression expr;

    private int[] errorLines = new int[1];

    public ParseExpressionErrorResult(Expression expr) {
        super();
        this.expr = expr;
        this.errorLines[0] = -1;
    }

    public ParseExpressionErrorResult(Expression expr, Optional<BaseDescr> descrOpt) {
        this(expr);
        descrOpt.ifPresent(descr -> this.errorLines[0] = descr.getLine());
    }

    @Override
    public ResultSeverity getSeverity() {
        return ResultSeverity.ERROR;
    }

    @Override
    public String getMessage() {
        return "Unable to Analyse Expression " + PrintUtil.printNode(expr) + ":";
    }

    @Override
    public int[] getLines() {
        return errorLines;
    }
}
