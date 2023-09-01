package org.kie.dmn.signavio.feel.runtime.functions;

import java.math.BigDecimal;

import org.kie.dmn.feel.runtime.functions.BaseFEELFunction;
import org.kie.dmn.feel.runtime.functions.BuiltInFunctions;
import org.kie.dmn.feel.runtime.functions.FEELFnResult;
import org.kie.dmn.feel.runtime.functions.ParameterName;
import org.kie.dmn.feel.runtime.functions.StringLengthFunction;

public class LenFunction
        extends BaseFEELFunction {

    public LenFunction() {
        super("len");
    }

    public FEELFnResult<BigDecimal> invoke(@ParameterName("text") String text) {
        return BuiltInFunctions.getFunction(StringLengthFunction.class).invoke(text);
    }
}
