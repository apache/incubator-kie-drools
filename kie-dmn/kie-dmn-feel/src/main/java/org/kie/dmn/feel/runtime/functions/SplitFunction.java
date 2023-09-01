package org.kie.dmn.feel.runtime.functions;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import org.kie.dmn.api.feel.runtime.events.FEELEvent.Severity;
import org.kie.dmn.feel.runtime.events.InvalidParametersEvent;

public class SplitFunction
        extends BaseFEELFunction {
    public static final SplitFunction INSTANCE = new SplitFunction();

    SplitFunction() {
        super( "split" );
    }

    public FEELFnResult<List<String>> invoke(@ParameterName("string") String string, @ParameterName("delimiter") String delimiter) {
        return invoke(string, delimiter, null);
    }

    public FEELFnResult<List<String>> invoke(@ParameterName("string") String string, @ParameterName("delimiter") String delimiter, @ParameterName("flags") String flags) {
        if (string == null) {
            return FEELFnResult.ofError( new InvalidParametersEvent( Severity.ERROR, "string", "cannot be null" ) );
        }
        if ( delimiter == null ) {
            return FEELFnResult.ofError( new InvalidParametersEvent( Severity.ERROR, "delimiter", "cannot be null" ) );
        }
        try {
            int f = processFlags( flags );
            Pattern p = Pattern.compile( delimiter, f );
            String[] split = p.split(string, -1);
            return FEELFnResult.ofResult( Arrays.asList( split ) );
        } catch ( PatternSyntaxException e ) {
            return FEELFnResult.ofError( new InvalidParametersEvent( Severity.ERROR, "delimiter", "is invalid and can not be compiled", e ) );
        } catch ( IllegalArgumentException t ) {
            return FEELFnResult.ofError( new InvalidParametersEvent( Severity.ERROR, "flags", "contains unknown flags", t ) );
        } catch ( Throwable t) {
            return FEELFnResult.ofError( new InvalidParametersEvent( Severity.ERROR, "delimiter", "is invalid and can not be compiled", t ) );
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
