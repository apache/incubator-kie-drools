package org.kie.dmn.signavio.feel.runtime.functions;

import java.math.BigDecimal;

import org.kie.dmn.feel.runtime.functions.BaseFEELFunction;
import org.kie.dmn.feel.runtime.functions.BuiltInFunctions;
import org.kie.dmn.feel.runtime.functions.FEELFnResult;
import org.kie.dmn.feel.runtime.functions.NumberFunction;
import org.kie.dmn.feel.runtime.functions.ParameterName;

public class SignavioNumberFunction
        extends BaseFEELFunction {

    public SignavioNumberFunction() {
        super( "number" );
    }

    public FEELFnResult<BigDecimal> invoke(@ParameterName("text") String text) {
        return BuiltInFunctions.getFunction(NumberFunction.class).invoke(text, null, ".");
    }

    public FEELFnResult<BigDecimal> invoke(@ParameterName("text") String text, @ParameterName("default_value") BigDecimal default_value) {
        FEELFnResult<BigDecimal> delegated = BuiltInFunctions.getFunction(NumberFunction.class).invoke(text, null, ".");

        return FEELFnResult.ofResult(delegated.getOrElse(default_value));
    }

}
