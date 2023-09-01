package org.kie.dmn.feel.runtime.functions;

import java.util.List;

import org.kie.dmn.feel.lang.EvaluationContext;
import org.kie.dmn.feel.lang.ast.BaseNode;

public class CustomFEELFunction extends AbstractCustomFEELFunction<BaseNode> {

    public CustomFEELFunction(String name, List<Param> parameters, BaseNode body, EvaluationContext evaluationContext) {
        super(name, parameters, body, evaluationContext);
    }

    @Override
    protected Object internalInvoke(EvaluationContext ctx) {
        return this.body.evaluate(ctx);
    }

}
