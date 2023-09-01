package org.kie.dmn.signavio.feel.runtime.functions;

import org.kie.dmn.feel.runtime.functions.BaseFEELFunction;
import org.kie.dmn.feel.runtime.functions.BuiltInFunctions;
import org.kie.dmn.feel.runtime.functions.FEELFnResult;
import org.kie.dmn.feel.runtime.functions.ParameterName;
import org.kie.dmn.feel.runtime.functions.StartsWithFunction;

public class SignavioStartsWithFunction
        extends BaseFEELFunction {

    public SignavioStartsWithFunction() {
        super("startsWith");
    }

    public FEELFnResult<Boolean> invoke(@ParameterName("string") String string, @ParameterName("match") String match) {
        return BuiltInFunctions.getFunction(StartsWithFunction.class).invoke(string, match);
    }

}
