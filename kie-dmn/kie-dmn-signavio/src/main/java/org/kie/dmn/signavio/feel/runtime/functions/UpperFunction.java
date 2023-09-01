package org.kie.dmn.signavio.feel.runtime.functions;

import org.kie.dmn.feel.runtime.functions.BaseFEELFunction;
import org.kie.dmn.feel.runtime.functions.BuiltInFunctions;
import org.kie.dmn.feel.runtime.functions.FEELFnResult;
import org.kie.dmn.feel.runtime.functions.ParameterName;
import org.kie.dmn.feel.runtime.functions.StringUpperCaseFunction;

public class UpperFunction
        extends BaseFEELFunction {

    public UpperFunction() {
        super("upper");
    }

    public FEELFnResult<String> invoke(@ParameterName("text") String text) {
        return BuiltInFunctions.getFunction(StringUpperCaseFunction.class).invoke(text);
    }
}
