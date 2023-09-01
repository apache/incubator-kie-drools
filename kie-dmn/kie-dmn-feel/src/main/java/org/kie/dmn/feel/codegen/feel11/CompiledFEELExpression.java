package org.kie.dmn.feel.codegen.feel11;

import java.util.function.Function;

import org.kie.dmn.feel.lang.CompiledExpression;
import org.kie.dmn.feel.lang.EvaluationContext;

public interface CompiledFEELExpression extends CompiledExpression, Function<EvaluationContext, Object> {
    
}
