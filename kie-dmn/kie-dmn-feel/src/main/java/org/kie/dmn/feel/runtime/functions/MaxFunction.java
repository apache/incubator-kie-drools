package org.kie.dmn.feel.runtime.functions;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.kie.dmn.api.feel.runtime.events.FEELEvent.Severity;
import org.kie.dmn.feel.lang.types.impl.InterceptNotComparableComparator;
import org.kie.dmn.feel.runtime.events.InvalidParametersEvent;

public class MaxFunction
        extends BaseFEELFunction {

    public MaxFunction() {
        super( "max" );
    }

    public FEELFnResult<Object> invoke(@ParameterName("list") List list) {
        if ( list == null || list.isEmpty() ) {
            return FEELFnResult.ofError(new InvalidParametersEvent(Severity.ERROR, "list", "cannot be null or empty"));
        } else {
            try {
                return FEELFnResult.ofResult(Collections.max(list, new InterceptNotComparableComparator()));
            } catch (ClassCastException e) {
                return FEELFnResult.ofError(new InvalidParametersEvent(Severity.ERROR, "list", "contains items that are not comparable"));
            }
        }
    }

    public FEELFnResult<Object> invoke(@ParameterName("c") Object[] list) {
        if ( list == null || list.length == 0 ) {
            // Arrays.asList does not accept null as parameter
            return FEELFnResult.ofError(new InvalidParametersEvent(Severity.ERROR, "c", "cannot be null or empty"));
        }
        
        return invoke( Arrays.asList( list ) );
    }

}
