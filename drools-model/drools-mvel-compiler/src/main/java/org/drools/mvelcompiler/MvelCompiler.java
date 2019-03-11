package org.drools.mvelcompiler;

import com.github.javaparser.ast.expr.Expression;
import org.drools.constraint.parser.DrlConstraintParser;
import org.drools.mvelcompiler.ast.TypedExpression;
import org.drools.mvelcompiler.context.MvelCompilerContext;

public class MvelCompiler {

    private final MvelCompilerContext mvelCompilerContext;

    private TypedExpressionPhase typedExpressionPhase;

    public MvelCompiler(MvelCompilerContext mvelCompilerContext) {
        this.mvelCompilerContext = mvelCompilerContext;
        typedExpressionPhase = new TypedExpressionPhase(mvelCompilerContext);
    }

    public ParsingResult compile(String stringExpression) {

        Expression mvelExpression = DrlConstraintParser.parse(stringExpression);

        TypedExpression expressionsWithType = typedExpressionPhase.invoke(mvelExpression);

        Expression expression = expressionsWithType.toJavaExpression();
        return new ParsingResult(mvelCompilerContext, expression.toString());
    }
}
