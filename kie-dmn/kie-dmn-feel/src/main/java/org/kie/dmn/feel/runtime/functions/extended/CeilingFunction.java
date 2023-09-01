package org.kie.dmn.feel.runtime.functions.extended;

import java.math.BigDecimal;
import java.math.RoundingMode;

import org.kie.dmn.api.feel.runtime.events.FEELEvent.Severity;
import org.kie.dmn.feel.runtime.events.InvalidParametersEvent;
import org.kie.dmn.feel.runtime.functions.BaseFEELFunction;
import org.kie.dmn.feel.runtime.functions.FEELFnResult;
import org.kie.dmn.feel.runtime.functions.ParameterName;

/**
 * provisional access for DMN14-126
 */
public class CeilingFunction
        extends BaseFEELFunction {
    
    public static final CeilingFunction INSTANCE = new CeilingFunction();

    public CeilingFunction() {
        super( "ceiling" );
    }

    public FEELFnResult<BigDecimal> invoke(@ParameterName( "n" ) BigDecimal n) {
        return invoke(n, BigDecimal.ZERO);
    }

    public FEELFnResult<BigDecimal> invoke(@ParameterName( "n" ) BigDecimal n, @ParameterName( "scale" ) BigDecimal scale) {
        if ( n == null ) {
            return FEELFnResult.ofError(new InvalidParametersEvent(Severity.ERROR, "n", "cannot be null"));
        }
        if ( scale == null ) {
            return FEELFnResult.ofError(new InvalidParametersEvent(Severity.ERROR, "scale", "cannot be null"));
        }
        return FEELFnResult.ofResult( n.setScale( scale.intValue(), RoundingMode.CEILING ) );
    }
}
