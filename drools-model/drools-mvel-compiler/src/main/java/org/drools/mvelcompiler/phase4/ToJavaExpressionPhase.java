package org.drools.mvelcompiler.phase4;

import org.drools.mvelcompiler.context.MvelCompilerContext;
import org.drools.mvelcompiler.phase3.TypedExpression;
import org.drools.mvelcompiler.phase3.TypedExpressions;

public class ToJavaExpressionPhase {

    private final MvelCompilerContext mvelCompilerContext;

    public ToJavaExpressionPhase(MvelCompilerContext mvelCompilerContext) {

        this.mvelCompilerContext = mvelCompilerContext;
    }

    public ToJavaExpressionResult invoke(TypedExpressions expressionsWithType) {

        TypedExpression lastExpression = expressionsWithType.last();
        return new ToJavaExpressionResult(lastExpression.toJavaExpression());
    }
}
