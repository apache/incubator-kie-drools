package org.kie.dmn.feel.runtime.functions.extended;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.kie.dmn.api.feel.runtime.events.FEELEvent;
import org.kie.dmn.feel.runtime.events.InvalidParametersEvent;
import org.kie.dmn.feel.runtime.functions.BaseFEELFunction;
import org.kie.dmn.feel.runtime.functions.FEELFnResult;
import org.kie.dmn.feel.runtime.functions.ParameterName;

/**
 * Proposal DMN14-187
 * Experimental for DMN14-182
 * See also: DMN14-181, DMN14-183
 */
public class ContextMergeFunction extends BaseFEELFunction {

    public static final ContextMergeFunction INSTANCE = new ContextMergeFunction();

    public ContextMergeFunction() {
        super("context merge");
    }

    public FEELFnResult<Map<String, Object>> invoke(@ParameterName("contexts") List<Object> contexts) {
        if (contexts == null) {
            return FEELFnResult.ofError(new InvalidParametersEvent(FEELEvent.Severity.ERROR, "contexts", "cannot be null"));
        }

        StringBuilder errors = new StringBuilder();
        Map<String, Object> result = new HashMap<>();
        for (int i = 0; i < contexts.size(); i++) {
            FEELFnResult<Map<String, Object>> ci = ContextPutFunction.toMap(contexts.get(i));
            final int index = i + 1;
            ci.consume(event -> errors.append("context of index " + (index) + " " + event.getMessage()), values -> result.putAll(values));
        }

        return errors.length() == 0 ? FEELFnResult.ofResult(Collections.unmodifiableMap(result)) : FEELFnResult.ofError(new InvalidParametersEvent(FEELEvent.Severity.ERROR, errors.toString()));
    }

}
