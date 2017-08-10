package org.kie.dmn.feel.codegen.feel11;

import com.github.javaparser.ast.expr.Expression;
import org.kie.dmn.feel.lang.Type;

public class DirectCompilerResult {

    public final Expression expression;
    public final Type resultType;

    public DirectCompilerResult(Expression expression,
                                Type resultType) {
        this.expression = expression;
        this.resultType = resultType;
    }

    public static DirectCompilerResult of(Expression expression, Type resultType) {
        return new DirectCompilerResult(expression,
                                        resultType);
    }
}
