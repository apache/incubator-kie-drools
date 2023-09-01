package org.kie.dmn.signavio.feel.runtime.functions;

import org.kie.dmn.api.feel.runtime.events.FEELEvent.Severity;
import org.kie.dmn.feel.runtime.events.InvalidParametersEvent;
import org.kie.dmn.feel.runtime.functions.BaseFEELFunction;
import org.kie.dmn.feel.runtime.functions.FEELFnResult;
import org.kie.dmn.feel.runtime.functions.ParameterName;

public class TrimFunction
        extends BaseFEELFunction {

    public TrimFunction() {
        super("trim");
    }

    public FEELFnResult<String> invoke(@ParameterName("text") String text) {
        if (text == null) {
            return FEELFnResult.ofError(new InvalidParametersEvent(Severity.ERROR, "text", "cannot be null"));
        }

        String trimmed = text.trim();

        String result = trimmed.replaceAll(" +", " ");

        return FEELFnResult.ofResult(result);
    }
}
