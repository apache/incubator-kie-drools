package org.kie.dmn.signavio.feel.runtime.functions;

import java.util.List;

import org.kie.dmn.api.feel.runtime.events.FEELEvent.Severity;
import org.kie.dmn.feel.runtime.events.InvalidParametersEvent;
import org.kie.dmn.feel.runtime.functions.BaseFEELFunction;
import org.kie.dmn.feel.runtime.functions.FEELFnResult;
import org.kie.dmn.feel.runtime.functions.ParameterName;

public class NotContainsAnyFunction
        extends BaseFEELFunction {

    public NotContainsAnyFunction() {
        super("notContainsAny");
    }

    public FEELFnResult<Boolean> invoke(@ParameterName("list1") List list1, @ParameterName("list2") List list2) {
        if (list1 == null) {
            return FEELFnResult.ofError(new InvalidParametersEvent(Severity.ERROR, "list1", "cannot be null"));
        }

        if (list2 == null) {
            return FEELFnResult.ofResult(true);
        } else {
            for (Object element : list2) {
                if (list1.contains(element)) {
                    return FEELFnResult.ofResult(false);
                }
            }

            return FEELFnResult.ofResult(true);
        }
    }
}
