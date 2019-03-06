package org.drools.mvelcompiler.phase1;

import com.github.javaparser.ast.expr.Expression;
import org.drools.constraint.parser.DrlConstraintParser;
import org.drools.mvelcompiler.context.MvelCompilerContext;

public class StringToExpressionPhase {

    private final MvelCompilerContext mvelCompilerContext;

    public StringToExpressionPhase(MvelCompilerContext mvelCompilerContext) {
        this.mvelCompilerContext = mvelCompilerContext;
    }

    public Expression invoke(String expression) {
        DrlConstraintParser parser = new DrlConstraintParser();

        Expression parsed = parser.parse(expression);

        return parsed;
    }
}
