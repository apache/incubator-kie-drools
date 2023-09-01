package org.kie.dmn.feel.runtime.functions;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import org.kie.dmn.api.feel.runtime.events.FEELEvent.Severity;
import org.kie.dmn.feel.runtime.events.InvalidParametersEvent;

public class RemoveFunction
        extends BaseFEELFunction {

    public RemoveFunction() {
        super( "remove" );
    }

    public FEELFnResult<List<Object>> invoke(@ParameterName( "list" ) List list, @ParameterName( "position" ) BigDecimal position) {
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
        List<Object> result = new ArrayList<>( list );
        if( position.intValue() > 0 ) {
            result.remove( position.intValue()-1 );
        } else {
            result.remove( list.size()+position.intValue() );
        }
        return FEELFnResult.ofResult( result );
    }
}
