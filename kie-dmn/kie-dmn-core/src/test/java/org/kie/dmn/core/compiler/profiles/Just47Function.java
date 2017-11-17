package org.kie.dmn.core.compiler.profiles;

import org.kie.dmn.feel.lang.EvaluationContext;
import org.kie.dmn.feel.runtime.functions.BaseFEELFunction;
import org.kie.dmn.feel.runtime.functions.FEELFnResult;
import org.kie.dmn.feel.runtime.functions.ParameterName;
import org.kie.dmn.feel.util.EvalHelper;

public class Just47Function extends BaseFEELFunction {

    public Just47Function() {
        super("just47");
    }

    public FEELFnResult<Object> invoke(@ParameterName("ctx") EvaluationContext ctx) {
        return FEELFnResult.ofResult(EvalHelper.coerceNumber(47));
    }

    @Override
    protected boolean isCustomFunction() {
        return super.isCustomFunction(); // explicit: ensure to use non-custom function.
    }

}