package org.kie.dmn.feel.codegen.feel11;

import java.util.List;
import java.util.function.Function;

import org.kie.dmn.feel.lang.EvaluationContext;
import org.kie.dmn.feel.runtime.functions.AbstractCustomFEELFunction;

public class CompiledCustomFEELFunction extends AbstractCustomFEELFunction<Function<EvaluationContext, Object>> {

    public CompiledCustomFEELFunction(String name, List<Param> parameters, Function<EvaluationContext, Object> body, EvaluationContext ctx) {
        super(name, parameters, body, ctx);
    }

    @Override
    protected Object internalInvoke(EvaluationContext ctx) {
        return this.body.apply(ctx);
    }
}
