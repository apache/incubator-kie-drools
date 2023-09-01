package org.kie.dmn.signavio.feel.runtime.functions;

import org.kie.dmn.api.feel.runtime.events.FEELEvent.Severity;
import org.kie.dmn.feel.runtime.events.InvalidParametersEvent;
import org.kie.dmn.feel.runtime.functions.BaseFEELFunction;
import org.kie.dmn.feel.runtime.functions.FEELFnResult;
import org.kie.dmn.feel.runtime.functions.ParameterName;

import java.math.BigDecimal;

public class AbsFunction
        extends BaseFEELFunction {
    public static final AbsFunction INSTANCE = new AbsFunction();

    AbsFunction() {
        super( "abs" );
    }

    public FEELFnResult<BigDecimal> invoke(@ParameterName( "number" ) BigDecimal number) {
        if ( number == null ) {
            return FEELFnResult.ofError(new InvalidParametersEvent(Severity.ERROR, "value", "cannot be null"));
        }
        return FEELFnResult.ofResult( number.abs() );
    }
}
