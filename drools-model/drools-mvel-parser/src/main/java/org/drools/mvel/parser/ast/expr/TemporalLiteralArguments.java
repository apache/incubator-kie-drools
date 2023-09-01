package org.drools.mvel.parser.ast.expr;

import com.github.javaparser.ast.expr.Expression;

public class TemporalLiteralArguments {

    private final Expression arg1;
    private final Expression arg2;
    private final Expression arg3;
    private final Expression arg4;

    public TemporalLiteralArguments(Expression arg1, Expression arg2, Expression arg3, Expression arg4) {
        this.arg1 = arg1;
        this.arg2 = arg2;
        this.arg3 = arg3;
        this.arg4 = arg4;
    }

    public Expression getArg1() {
        return arg1;
    }

    public Expression getArg2() {
        return arg2;
    }

    public Expression getArg3() {
        return arg3;
    }

    public Expression getArg4() {
        return arg4;
    }
}
