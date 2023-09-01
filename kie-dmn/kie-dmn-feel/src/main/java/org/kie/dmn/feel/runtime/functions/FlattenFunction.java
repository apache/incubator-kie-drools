package org.kie.dmn.feel.runtime.functions;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.kie.dmn.api.feel.runtime.events.FEELEvent.Severity;
import org.kie.dmn.feel.runtime.events.InvalidParametersEvent;

public class FlattenFunction
        extends BaseFEELFunction {

    public FlattenFunction() {
        super( "flatten" );
    }

    public FEELFnResult<List> invoke(@ParameterName( "list" ) Object list) {
        if ( list == null ) {
            return FEELFnResult.ofError(new InvalidParametersEvent(Severity.ERROR, "list", "cannot be null"));
        }
        // spec requires us to return a new list
        final List<Object> result = new ArrayList<>();
        flattenList( list, result );
        return FEELFnResult.ofResult( result );
    }

    private void flattenList(Object list, List<Object> result) {
        if( list instanceof Collection ) {
            for( Object element : ((Collection)list) ) {
                if( element instanceof Collection ) {
                    flattenList( element, result );
                } else {
                    result.add( element );
                }
            }
        } else {
            result.add( list );
        }
    }

}
