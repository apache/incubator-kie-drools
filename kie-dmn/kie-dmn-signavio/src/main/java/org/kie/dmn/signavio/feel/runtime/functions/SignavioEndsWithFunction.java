package org.kie.dmn.signavio.feel.runtime.functions;

import org.kie.dmn.feel.runtime.functions.BaseFEELFunction;
import org.kie.dmn.feel.runtime.functions.BuiltInFunctions;
import org.kie.dmn.feel.runtime.functions.EndsWithFunction;
import org.kie.dmn.feel.runtime.functions.FEELFnResult;
import org.kie.dmn.feel.runtime.functions.ParameterName;

public class SignavioEndsWithFunction
        extends BaseFEELFunction {

    public SignavioEndsWithFunction() {
        super("endsWith");
    }

    public FEELFnResult<Boolean> invoke(@ParameterName("string") String string, @ParameterName("match") String match) {
        return BuiltInFunctions.getFunction(EndsWithFunction.class).invoke(string, match);
    }
}
