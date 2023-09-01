package org.kie.dmn.feel.runtime.functions;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import org.kie.dmn.api.feel.runtime.events.FEELEvent.Severity;
import org.kie.dmn.feel.runtime.events.InvalidParametersEvent;
import org.kie.dmn.feel.runtime.functions.FEELFnResult;

public class CountFunction
        extends BaseFEELFunction {

    public CountFunction() {
        super( "count" );
    }

    public FEELFnResult<BigDecimal> invoke(@ParameterName( "list" ) List list) {
        if ( list == null ) {
            return FEELFnResult.ofError(new InvalidParametersEvent(Severity.ERROR, "list", "cannot be null"));
        }
        
        return FEELFnResult.ofResult( BigDecimal.valueOf( list.size() ) );
    }

    public FEELFnResult<BigDecimal> invoke(@ParameterName( "c" ) Object[] list) {
        if ( list == null ) {
            return FEELFnResult.ofError(new InvalidParametersEvent(Severity.ERROR, "c", "cannot be null"));
        }
        
        return invoke( Arrays.asList( list ) );
    }
}
