package org.kie.dmn.feel.runtime.functions;

import java.util.stream.IntStream;

import org.kie.dmn.api.feel.runtime.events.FEELEvent.Severity;
import org.kie.dmn.feel.runtime.events.InvalidParametersEvent;

public class SubstringFunction
        extends BaseFEELFunction {

    public SubstringFunction() {
        super( "substring" );
    }

    public FEELFnResult<String> invoke(@ParameterName("string") String string, @ParameterName("start position") Number start) {
        return invoke(string, start, null);
    }

    public FEELFnResult<String> invoke(@ParameterName("string") String string, @ParameterName("start position") Number start, @ParameterName("length") Number length) {
        if ( string == null ) {
            return FEELFnResult.ofError( new InvalidParametersEvent( Severity.ERROR, "string", "cannot be null" ) );
        }
        if ( start == null ) {
            return FEELFnResult.ofError( new InvalidParametersEvent( Severity.ERROR, "start position", "cannot be null" ) );
        }
        if ( length != null && length.intValue() <= 0 ) {
            return FEELFnResult.ofError( new InvalidParametersEvent( Severity.ERROR, "length", "must be a positive number when specified" ) );
        }
        if ( start.intValue() == 0 ) {
            return FEELFnResult.ofError( new InvalidParametersEvent( Severity.ERROR, "start position", "cannot be zero" ) );
        }
        int stringLength = string.codePointCount(0, string.length());
        if ( Math.abs( start.intValue() ) > stringLength ) {
            return FEELFnResult.ofError( new InvalidParametersEvent( Severity.ERROR, "parameter 'start position' inconsistent with the actual length of the parameter 'string'" ) );
        }

        int skip = start.intValue() > 0 ? start.intValue() - 1 : stringLength + start.intValue();
        IntStream stream = string.codePoints().skip(skip);
        if (length != null) {
            stream = stream.limit(length.longValue());
        }
        StringBuilder result = stream.mapToObj(Character::toChars).collect(StringBuilder::new, StringBuilder::append, StringBuilder::append);
        return FEELFnResult.ofResult(result.toString());
    }
}
