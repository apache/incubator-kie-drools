package org.kie.dmn.feel.runtime.functions;

import org.kie.dmn.api.feel.runtime.events.FEELEvent.Severity;
import org.kie.dmn.feel.runtime.events.InvalidParametersEvent;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

public class MatchesFunction
        extends BaseFEELFunction {

    public MatchesFunction() {
        super( "matches" );
    }

    public FEELFnResult<Boolean> invoke(@ParameterName("input") String input, @ParameterName("pattern") String pattern) {
        return invoke( input, pattern, null );
    }

    public FEELFnResult<Boolean> invoke(@ParameterName("input") String input, @ParameterName("pattern") String pattern, @ParameterName("flags") String flags) {
        if ( input == null ) {
            return FEELFnResult.ofError( new InvalidParametersEvent( Severity.ERROR, "input", "cannot be null" ) );
        }
        if ( pattern == null ) {
            return FEELFnResult.ofError( new InvalidParametersEvent( Severity.ERROR, "pattern", "cannot be null" ) );
        }
        try {
            int f = processFlags( flags );
            Pattern p = Pattern.compile( pattern, f );
            Matcher m = p.matcher( input );
            return FEELFnResult.ofResult( m.find() );
        } catch ( PatternSyntaxException e ) {
            return FEELFnResult.ofError( new InvalidParametersEvent( Severity.ERROR, "pattern", "is invalid and can not be compiled", e ) );
        } catch ( IllegalArgumentException t ) {
            return FEELFnResult.ofError( new InvalidParametersEvent( Severity.ERROR, "flags", "contains unknown flags", t ) );
        } catch ( Throwable t) {
            return FEELFnResult.ofError( new InvalidParametersEvent( Severity.ERROR, "pattern", "is invalid and can not be compiled", t ) );
        }
    }

    private int processFlags(String flags) {
        int f = 0;
        if( flags != null ) {
            if( flags.contains( "s" ) ) {
                f |= Pattern.DOTALL;
            }
            if( flags.contains( "m" ) ) {
                f |= Pattern.MULTILINE;
            }
            if( flags.contains( "i" ) ) {
                f |= Pattern.CASE_INSENSITIVE;
            }
        }
        return f;
    }

}
