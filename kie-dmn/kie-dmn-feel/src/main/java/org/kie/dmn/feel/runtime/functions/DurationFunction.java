package org.kie.dmn.feel.runtime.functions;

import java.time.Duration;
import java.time.format.DateTimeParseException;
import java.time.temporal.TemporalAmount;
import java.util.Arrays;
import java.util.List;

import org.kie.dmn.api.feel.runtime.events.FEELEvent.Severity;
import org.kie.dmn.feel.lang.types.impl.ComparablePeriod;
import org.kie.dmn.feel.runtime.events.InvalidParametersEvent;

public class DurationFunction
        extends BaseFEELFunction {

    public static final DurationFunction INSTANCE = new DurationFunction();

    public DurationFunction() {
        super(FEELConversionFunctionNames.DURATION);
    }

    public FEELFnResult<TemporalAmount> invoke(@ParameterName( "from" ) String val) {
        if ( val == null ) {
            return FEELFnResult.ofError(new InvalidParametersEvent(Severity.ERROR, "from", "cannot be null"));
        }

        if ( val.indexOf("-") > 0) {
            return FEELFnResult.ofError( new InvalidParametersEvent(Severity.ERROR, "from", "negative values for units are not allowed.") );
        }

        try {
            // try to parse as days/hours/minute/seconds
            return FEELFnResult.ofResult( Duration.parse( val ) );
        } catch( DateTimeParseException e ) {
            // if it failed, try to parse as years/months
            try {
                return FEELFnResult.ofResult(ComparablePeriod.parse(val).normalized());
            } catch( DateTimeParseException e2 ) {
                // failed to parse, so return null according to the spec
                return FEELFnResult.ofError(new InvalidParametersEvent(Severity.ERROR, "from", "date-parsing exception", 
                                        new RuntimeException(new Throwable() { public final List<Throwable> causes = Arrays.asList( new Throwable[]{e, e2} );  } ))); 
            }
        }
        
    }

    /**
     * This is the identity function implementation
     *
     * @param val
     * @return
     */
    public FEELFnResult<TemporalAmount> invoke(@ParameterName( "from" ) TemporalAmount val) {
        if ( val == null ) {
            return FEELFnResult.ofError(new InvalidParametersEvent(Severity.ERROR, "from", "cannot be null"));
        }
        return FEELFnResult.ofResult( val );
    }
}
