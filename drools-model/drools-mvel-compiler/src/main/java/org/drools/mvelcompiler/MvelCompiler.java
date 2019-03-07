package org.drools.mvelcompiler;

import com.github.javaparser.ast.expr.Expression;
import org.drools.mvelcompiler.context.MvelCompilerContext;
import org.drools.mvelcompiler.phase1.StringToExpressionPhase;
import org.drools.mvelcompiler.phase3.FlattenExpressionPhase;
import org.drools.mvelcompiler.phase3.FlattenedExpressionResult;
import org.drools.mvelcompiler.phase2.FirstChildProcessPhase;
import org.drools.mvelcompiler.phase2.FirstChildProcessResult;
import org.drools.mvelcompiler.phase4.ExpressionWithTypePhase;
import org.drools.mvelcompiler.phase4.TypedExpressions;
import org.drools.mvelcompiler.phase5.ToJavaExpressionPhase;
import org.drools.mvelcompiler.phase5.ToJavaExpressionResult;

public class MvelCompiler {

    private final MvelCompilerContext mvelCompilerContext;

    private StringToExpressionPhase stringToExpressionPhase;
    private FlattenExpressionPhase flattenExpressionPhase;
    private FirstChildProcessPhase firstChildProcessPhase;
    private ExpressionWithTypePhase expressionWithTypePhase;
    private ToJavaExpressionPhase toJavaExpressionPhase;

    public MvelCompiler(MvelCompilerContext mvelCompilerContext) {
        this.mvelCompilerContext = mvelCompilerContext;
        stringToExpressionPhase = new StringToExpressionPhase(mvelCompilerContext);
        flattenExpressionPhase = new FlattenExpressionPhase(mvelCompilerContext);
        firstChildProcessPhase = new FirstChildProcessPhase(mvelCompilerContext);
        expressionWithTypePhase = new ExpressionWithTypePhase(mvelCompilerContext);
        toJavaExpressionPhase = new ToJavaExpressionPhase(mvelCompilerContext);
    }

    public ParsingResult compile(String stringExpression) {
        Expression mvelExpression = stringToExpressionPhase.invoke(stringExpression);
        FlattenedExpressionResult flattenedExpressionResult = flattenExpressionPhase.invoke(mvelExpression);
        FirstChildProcessResult firstChildProcessResult = firstChildProcessPhase.invoke(flattenedExpressionResult);
        TypedExpressions expressionsWithType = expressionWithTypePhase.invoke(firstChildProcessResult);
        ToJavaExpressionResult javaExpression = toJavaExpressionPhase.invoke(expressionsWithType);
        return new ParsingResult(mvelCompilerContext, javaExpression.getExpression().toString());
    }
}
