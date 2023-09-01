package org.kie.dmn.feel.runtime.functions;

import ch.obermuhlner.math.big.BigDecimalMath;
import org.kie.dmn.api.feel.runtime.events.FEELEvent;
import org.kie.dmn.feel.runtime.events.InvalidParametersEvent;
import org.kie.dmn.feel.util.EvalHelper;

import java.math.BigDecimal;
import java.math.MathContext;

public class SqrtFunction
        extends BaseFEELFunction {
    public static final SqrtFunction INSTANCE = new SqrtFunction();

    SqrtFunction() {
        super("sqrt");
    }

    public FEELFnResult<BigDecimal> invoke(@ParameterName( "number" ) BigDecimal number) {
        if ( number == null ) {
            return FEELFnResult.ofError( new InvalidParametersEvent( FEELEvent.Severity.ERROR, "number", "cannot be null" ) );
        }
        if ( number.signum() < 0 ) {
            return FEELFnResult.ofError( new InvalidParametersEvent( FEELEvent.Severity.ERROR, "number", "is negative" ) );
        }
        return FEELFnResult.ofResult( sqrt( EvalHelper.getBigDecimalOrNull( number ) ) );
    }

    public static BigDecimal sqrt( BigDecimal arg ) { // can be modified later to short-circuit if precision is not needed
        return BigDecimalMath.sqrt(arg, MathContext.DECIMAL128);
    }
}
