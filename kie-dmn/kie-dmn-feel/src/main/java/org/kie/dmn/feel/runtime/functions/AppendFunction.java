package org.kie.dmn.feel.runtime.functions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import org.kie.dmn.api.feel.runtime.events.FEELEvent.Severity;
import org.kie.dmn.feel.runtime.events.InvalidParametersEvent;

public class AppendFunction
        extends BaseFEELFunction {

    public AppendFunction() {
        super( "append" );
    }

    public FEELFnResult<List<Object>> invoke( @ParameterName( "list" ) List list, @ParameterName( "item" ) Object[] items ) {
        return invoke((Object) list, items);
    }

    public FEELFnResult<List<Object>> invoke( @ParameterName( "list" ) Object appendTo, @ParameterName( "item" ) Object[] items ) {
        if (appendTo == null) {
            return FEELFnResult.ofError(new InvalidParametersEvent(Severity.ERROR, "list", "cannot be null"));
        }
        if (items == null) {
            return FEELFnResult.ofError(new InvalidParametersEvent(Severity.ERROR, "item", "cannot be null"));
        }
        // spec requires us to return a new list
        final List<Object> result = new ArrayList<>();
        if (appendTo instanceof Collection) {
            result.addAll((Collection) appendTo);
        } else {
            result.add(appendTo);
        }
        result.addAll(Arrays.asList(items));
        return FEELFnResult.ofResult(result);
    }

}
