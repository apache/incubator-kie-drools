package org.kie.dmn.signavio.feel.runtime.functions;

import java.math.BigDecimal;

import org.kie.dmn.feel.runtime.functions.BaseFEELFunction;
import org.kie.dmn.feel.runtime.functions.BuiltInFunctions;
import org.kie.dmn.feel.runtime.functions.FEELFnResult;
import org.kie.dmn.feel.runtime.functions.NumberFunction;
import org.kie.dmn.feel.runtime.functions.ParameterName;

public class IsNumericFunction
        extends BaseFEELFunction {

    public IsNumericFunction() {
        super("isNumeric");
    }

    public FEELFnResult<Boolean> invoke(@ParameterName("text") String text) {
        FEELFnResult<BigDecimal> delegate = BuiltInFunctions.getFunction(NumberFunction.class).invoke(text, null, null);
        
        return FEELFnResult.ofResult(delegate.cata(e -> Boolean.FALSE, value -> Boolean.TRUE));
    }
}
