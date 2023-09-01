package org.kie.dmn.signavio.feel.runtime.functions;

import org.kie.dmn.api.feel.runtime.events.FEELEvent.Severity;
import org.kie.dmn.feel.lang.types.BuiltInType;
import org.kie.dmn.feel.runtime.events.InvalidParametersEvent;
import org.kie.dmn.feel.runtime.functions.BaseFEELFunction;
import org.kie.dmn.feel.runtime.functions.FEELFnResult;
import org.kie.dmn.feel.runtime.functions.ParameterName;
import org.kie.dmn.feel.runtime.functions.extended.TimeFunction;
import org.kie.dmn.feel.util.EvalHelper;

import java.math.BigDecimal;
import java.time.DateTimeException;
import java.time.temporal.ChronoField;
import java.time.temporal.TemporalAccessor;
import java.util.function.Function;

public class HourFunction
        extends BaseFEELFunction {

    public HourFunction() {
        super( "hour" );
    }

    public FEELFnResult<BigDecimal> invoke(@ParameterName("timestring") String val) {
        if ( val == null ) {
            return FEELFnResult.ofError( new InvalidParametersEvent( Severity.ERROR, "timestring", "cannot be null" ) );
        }

        try {
            TemporalAccessor r = TimeFunction.INSTANCE.invoke(val).cata(BuiltInType.justNull(), Function.identity());
            if (r instanceof TemporalAccessor) {
                return invoke(r);
            } else {
                return FEELFnResult.ofError( new InvalidParametersEvent( Severity.ERROR, "timestring", "time-parsing exception" ) );
            }
        } catch ( DateTimeException e ) {
            return FEELFnResult.ofError( new InvalidParametersEvent( Severity.ERROR, "timestring", "time-parsing exception", e ) );
        }
    }

    public FEELFnResult<BigDecimal> invoke(@ParameterName("timestring") TemporalAccessor datetime) {
        if ( datetime == null ) {
            return FEELFnResult.ofError( new InvalidParametersEvent( Severity.ERROR, "timestring", "cannot be null" ) );
        }

        try {
            return FEELFnResult.ofResult( EvalHelper.getBigDecimalOrNull( datetime.get( ChronoField.HOUR_OF_DAY ) ) );
        } catch ( DateTimeException e ) {
            return FEELFnResult.ofError( new InvalidParametersEvent( Severity.ERROR, "timestring", "invalid 'time' or 'date and time' parameter", e ) );
        }
    }

}
