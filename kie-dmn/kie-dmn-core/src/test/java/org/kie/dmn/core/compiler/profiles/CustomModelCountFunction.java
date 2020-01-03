package org.kie.dmn.core.compiler.profiles;

import java.math.BigDecimal;

import org.kie.dmn.feel.lang.EvaluationContext;
import org.kie.dmn.feel.runtime.functions.BaseFEELFunction;
import org.kie.dmn.feel.runtime.functions.FEELFnResult;
import org.kie.dmn.feel.runtime.functions.ParameterName;

public class CustomModelCountFunction extends BaseFEELFunction {

    public CustomModelCountFunction() {
        super("customModelCount");
    }

    public FEELFnResult<Object> invoke(@ParameterName("ctx") EvaluationContext ctx) {
        return FEELFnResult.ofResult(new BigDecimal(ctx.getDMNRuntime().getModels().size()));
    }

    @Override
    protected boolean isCustomFunction() {
        return super.isCustomFunction(); // explicit: standard behavior of BaseFEELFunction.
    }

}