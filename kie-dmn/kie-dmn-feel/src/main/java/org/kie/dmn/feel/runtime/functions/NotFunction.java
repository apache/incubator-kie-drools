package org.kie.dmn.feel.runtime.functions;

import org.kie.dmn.api.feel.runtime.events.FEELEvent.Severity;
import org.kie.dmn.feel.runtime.events.InvalidParametersEvent;

/**
 * The not() function is a special case because
 * it doubles both as a function and as a unary
 * test.
 */
public class NotFunction
        extends BaseFEELFunction {

    public NotFunction() {
        super( "not" );
    }

    public FEELFnResult<Boolean> invoke(@ParameterName("negand") Object negand) {
        if ( negand != null && !(negand instanceof Boolean) ) {
            return FEELFnResult.ofError( new InvalidParametersEvent( Severity.ERROR, "negand", "must be a boolean value" ) );
        }
        return FEELFnResult.ofResult( negand == null ? null : !((Boolean) negand) );
    }

}
