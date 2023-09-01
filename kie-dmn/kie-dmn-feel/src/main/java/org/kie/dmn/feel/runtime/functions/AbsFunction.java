package org.kie.dmn.feel.runtime.functions;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.Period;

import org.kie.dmn.api.feel.runtime.events.FEELEvent.Severity;
import org.kie.dmn.feel.lang.types.impl.ComparablePeriod;
import org.kie.dmn.feel.runtime.events.InvalidParametersEvent;

public class AbsFunction
        extends BaseFEELFunction {
    public static final AbsFunction INSTANCE = new AbsFunction();

    AbsFunction() {
        super( "abs" );
    }

    public FEELFnResult<BigDecimal> invoke(@ParameterName( "n" ) BigDecimal number) {
        if ( number == null ) {
            return FEELFnResult.ofError(new InvalidParametersEvent(Severity.ERROR, "number", "cannot be null"));
        }
        return FEELFnResult.ofResult( number.abs() );
    }

    public FEELFnResult<Period> invoke(@ParameterName( "n" ) Period duration) {
        if ( duration == null ) {
            return FEELFnResult.ofError(new InvalidParametersEvent(Severity.ERROR, "duration", "cannot be null"));
        }
        return FEELFnResult.ofResult( duration.toTotalMonths() < 0 ? duration.negated() : duration );
    }

    public FEELFnResult<ComparablePeriod> invoke(@ParameterName("n") ComparablePeriod comparablePeriod) {
        Period duration = comparablePeriod.asPeriod();
        if (duration == null) {
            return FEELFnResult.ofError(new InvalidParametersEvent(Severity.ERROR, "duration", "cannot be null"));
        }
        return FEELFnResult.ofResult(new ComparablePeriod(duration.toTotalMonths() < 0 ? duration.negated() : duration));
    }

    public FEELFnResult<Duration> invoke(@ParameterName( "n" ) Duration duration) {
        if ( duration == null ) {
            return FEELFnResult.ofError(new InvalidParametersEvent(Severity.ERROR, "duration", "cannot be null"));
        }
        return FEELFnResult.ofResult( duration.abs() );
    }

}
