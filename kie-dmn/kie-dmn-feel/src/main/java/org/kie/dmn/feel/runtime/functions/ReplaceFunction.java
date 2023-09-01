package org.kie.dmn.feel.runtime.functions;

import org.kie.dmn.api.feel.runtime.events.FEELEvent.Severity;
import org.kie.dmn.feel.runtime.events.InvalidParametersEvent;

public class ReplaceFunction
        extends BaseFEELFunction {

    public ReplaceFunction() {
        super( "replace" );
    }

    public FEELFnResult<Object> invoke(@ParameterName("input") String input, @ParameterName("pattern") String pattern,
                                       @ParameterName( "replacement" ) String replacement ) {
        return invoke(input, pattern, replacement, null);
    }

    public FEELFnResult<Object> invoke(@ParameterName("input") String input, @ParameterName("pattern") String pattern,
                                       @ParameterName( "replacement" ) String replacement, @ParameterName("flags") String flags) {
        if ( input == null ) {
            return FEELFnResult.ofError( new InvalidParametersEvent( Severity.ERROR, "input", "cannot be null" ) );
        }
        if ( pattern == null ) {
            return FEELFnResult.ofError( new InvalidParametersEvent( Severity.ERROR, "pattern", "cannot be null" ) );
        }
        if ( replacement == null ) {
            return FEELFnResult.ofError( new InvalidParametersEvent( Severity.ERROR, "replacement", "cannot be null" ) );
        }

        final String flagsString;
        if (flags != null && !flags.isEmpty()) {
            flagsString = "(?" + flags + ")";
        } else {
            flagsString = "";
        }

        return FEELFnResult.ofResult( input.replaceAll( flagsString + pattern, replacement ) );
    }

}
