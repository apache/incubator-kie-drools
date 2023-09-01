package org.kie.dmn.feel.runtime.functions;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.kie.dmn.api.feel.runtime.events.FEELEvent.Severity;
import org.kie.dmn.feel.runtime.events.InvalidParametersEvent;

public class ConcatenateFunction
        extends BaseFEELFunction {

    public ConcatenateFunction() {
        super( "concatenate" );
    }

    public FEELFnResult<List<Object>> invoke(@ParameterName("list") Object[] lists) {
        if ( lists == null ) {
            return FEELFnResult.ofError(new InvalidParametersEvent(Severity.ERROR, "list", "cannot be null"));
        }
        // spec requires us to return a new list
        final List<Object> result = new ArrayList<>();
        for ( Object list : lists ) {
            if ( list == null ) {
                // TODO review accordingly to spec, original behavior was: return null;
                return FEELFnResult.ofError(new InvalidParametersEvent(Severity.ERROR, "lists", "one of the elements in the list is null"));
            } else if ( list instanceof Collection ) {
                result.addAll( (Collection) list );
            } else {
                result.add( list );
            }
        }
        return FEELFnResult.ofResult( result );
    }
}
