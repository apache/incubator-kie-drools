package org.kie.dmn.signavio.feel.runtime.functions;

import org.kie.dmn.feel.runtime.functions.BaseFEELFunction;
import org.kie.dmn.feel.runtime.functions.BuiltInFunctions;
import org.kie.dmn.feel.runtime.functions.FEELFnResult;
import org.kie.dmn.feel.runtime.functions.ParameterName;
import org.kie.dmn.feel.runtime.functions.StringLowerCaseFunction;

public class LowerFunction
        extends BaseFEELFunction {

    public LowerFunction() {
        super("lower");
    }

    public FEELFnResult<String> invoke(@ParameterName("text") String text) {
        return BuiltInFunctions.getFunction(StringLowerCaseFunction.class).invoke(text);
    }
}
