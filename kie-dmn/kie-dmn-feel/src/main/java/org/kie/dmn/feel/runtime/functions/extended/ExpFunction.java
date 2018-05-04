package org.kie.dmn.feel.runtime.functions.extended;

import org.kie.dmn.api.feel.runtime.events.FEELEvent;
import org.kie.dmn.feel.runtime.events.InvalidParametersEvent;
import org.kie.dmn.feel.runtime.functions.BaseFEELFunction;
import org.kie.dmn.feel.runtime.functions.FEELFnResult;
import org.kie.dmn.feel.runtime.functions.ParameterName;

import java.math.BigDecimal;
import java.math.MathContext;

public class ExpFunction
        extends BaseFEELFunction {
    public static final ExpFunction INSTANCE = new ExpFunction();

    ExpFunction() {
        super("exp");
    }

    public FEELFnResult<BigDecimal> invoke(@ParameterName( "number" ) BigDecimal number) {
        if ( number == null ) {
            return FEELFnResult.ofError( new InvalidParametersEvent( FEELEvent.Severity.ERROR, "number", "cannot be null" ) );
        }
        return FEELFnResult.ofResult( new BigDecimal( Math.exp( number.doubleValue() ), MathContext.DECIMAL128 ) );
    }
}
