package org.kie.dmn.feel.lang.ast.infixexecutors;

import org.kie.dmn.feel.lang.EvaluationContext;
import org.kie.dmn.feel.lang.ast.InfixOpNode;

public interface InfixExecutor {

    Object evaluate(final Object left, final Object right, EvaluationContext ctx);

    Object evaluate(InfixOpNode infixNode, EvaluationContext ctx);

}
