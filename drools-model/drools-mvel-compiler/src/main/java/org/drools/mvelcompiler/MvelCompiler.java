package org.drools.mvelcompiler;

import com.github.javaparser.ast.expr.Expression;
import org.drools.mvelcompiler.context.MvelCompilerContext;
import org.drools.mvelcompiler.phase1.StringToExpressionPhase;
import org.drools.mvelcompiler.phase2.FlattenExpressionPhase;
import org.drools.mvelcompiler.phase2.FlattenExpressionResult;
import org.drools.mvelcompiler.phase3.ExpressionWithTypePhase;
import org.drools.mvelcompiler.phase3.TypedExpressions;
import org.drools.mvelcompiler.phase4.ToJavaExpressionPhase;
import org.drools.mvelcompiler.phase4.ToJavaExpressionResult;

public class MvelCompiler {

    private final MvelCompilerContext mvelCompilerContext;

    private StringToExpressionPhase stringToExpressionPhase;
    private FlattenExpressionPhase flattenExpressionPhase;
    private ExpressionWithTypePhase expressionWithTypePhase;
    private ToJavaExpressionPhase toJavaExpressionPhase;

    public MvelCompiler(MvelCompilerContext mvelCompilerContext) {
        this.mvelCompilerContext = mvelCompilerContext;
        stringToExpressionPhase = new StringToExpressionPhase(mvelCompilerContext);
        flattenExpressionPhase = new FlattenExpressionPhase(mvelCompilerContext);
        expressionWithTypePhase = new ExpressionWithTypePhase(mvelCompilerContext);
        toJavaExpressionPhase = new ToJavaExpressionPhase(mvelCompilerContext);
    }

    public ParsingResult compile(String stringExpression) {
        Expression mvelExpression = stringToExpressionPhase.invoke(stringExpression);
        FlattenExpressionResult flattenExpressionResult = flattenExpressionPhase.invoke(mvelExpression);
        TypedExpressions expressionsWithType = expressionWithTypePhase.invoke(flattenExpressionResult);
        ToJavaExpressionResult javaExpression = toJavaExpressionPhase.invoke(expressionsWithType);
        return new ParsingResult(mvelCompilerContext, javaExpression.getExpression().toString());
    }
}
