package org.kie.dmn.feel.runtime.functions;

import org.kie.dmn.api.feel.runtime.events.FEELEvent.Severity;
import org.kie.dmn.feel.runtime.events.InvalidParametersEvent;

public class StringLowerCaseFunction
        extends BaseFEELFunction {

    public static final StringLowerCaseFunction INSTANCE = new StringLowerCaseFunction();

    public StringLowerCaseFunction() {
        super( "lower case" );
    }

    public FEELFnResult<String> invoke(@ParameterName("string") String string) {
        if ( string == null ) {
            return FEELFnResult.ofError(new InvalidParametersEvent(Severity.ERROR, "string", "cannot be null"));
        } else {
            return FEELFnResult.ofResult( string.toLowerCase() );
        }
    }
}
