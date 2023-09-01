package org.kie.dmn.feel.runtime.functions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.kie.dmn.api.feel.runtime.events.FEELEvent.Severity;
import org.kie.dmn.feel.runtime.events.InvalidParametersEvent;

public class ReverseFunction
        extends BaseFEELFunction {

    public ReverseFunction() {
        super( "reverse" );
    }

    public FEELFnResult<List<Object>> invoke(@ParameterName("list") List list) {
        if ( list == null ) {
            return FEELFnResult.ofError(new InvalidParametersEvent(Severity.ERROR, "list", "cannot be null"));
        }
        // spec requires us to return a new list
        final List<Object> result = new ArrayList<>( list );
        Collections.reverse( result );
        return FEELFnResult.ofResult( result );
    }
}
