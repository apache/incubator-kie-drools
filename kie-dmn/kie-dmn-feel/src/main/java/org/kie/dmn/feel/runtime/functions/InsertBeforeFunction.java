package org.kie.dmn.feel.runtime.functions;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import org.kie.dmn.api.feel.runtime.events.FEELEvent.Severity;
import org.kie.dmn.feel.runtime.events.InvalidParametersEvent;

public class InsertBeforeFunction
        extends BaseFEELFunction {

    public InsertBeforeFunction() {
        super( "insert before" );
    }

    public FEELFnResult<List> invoke(@ParameterName( "list" ) List list, @ParameterName( "position" ) BigDecimal position, @ParameterName( "newItem" ) Object newItem) {
        if ( list == null ) { 
            return FEELFnResult.ofError(new InvalidParametersEvent(Severity.ERROR, "list", "cannot be null"));
        }
        if ( position == null ) {
            return FEELFnResult.ofError(new InvalidParametersEvent(Severity.ERROR, "position", "cannot be null"));
        }
        if ( position.intValue() == 0 ) {
            return FEELFnResult.ofError(new InvalidParametersEvent(Severity.ERROR, "position", "cannot be zero (parameter 'position' is 1-based)"));
        }
        if ( position.abs().intValue() > list.size() ) {
            return FEELFnResult.ofError(new InvalidParametersEvent(Severity.ERROR, "position", "inconsistent with 'list' size"));
        }

        // spec requires us to return a new list
        final List<Object> result = new ArrayList<>( list );
        if( position.intValue() > 0 ) {
            result.add( position.intValue() - 1, newItem );
        } else {
            result.add( list.size() + position.intValue(), newItem );
        }
        return FEELFnResult.ofResult( result );
    }
}
