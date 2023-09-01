package org.kie.dmn.feel.runtime.functions;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import org.kie.dmn.api.feel.runtime.events.FEELEvent.Severity;
import org.kie.dmn.feel.runtime.events.InvalidParametersEvent;

public class UnionFunction
        extends BaseFEELFunction {

    public UnionFunction() {
        super( "union" );
    }

    public FEELFnResult<List<Object>> invoke(@ParameterName("list") Object[] lists) {
        if ( lists == null ) {
            return FEELFnResult.ofError(new InvalidParametersEvent(Severity.ERROR, "lists", "cannot be null"));
        }

        final Set<Object> resultSet = new LinkedHashSet<>();
        for ( final Object list : lists ) {
            if ( list instanceof Collection ) {
                resultSet.addAll((Collection) list);
            } else {
                resultSet.add(list);
            }
        }

        // spec requires us to return a new list
        return FEELFnResult.ofResult( new ArrayList<>(resultSet) );
    }
}
