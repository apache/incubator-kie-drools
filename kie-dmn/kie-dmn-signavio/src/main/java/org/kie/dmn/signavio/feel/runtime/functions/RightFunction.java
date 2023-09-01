package org.kie.dmn.signavio.feel.runtime.functions;

import java.math.BigDecimal;

import org.kie.dmn.api.feel.runtime.events.FEELEvent.Severity;
import org.kie.dmn.feel.runtime.events.InvalidParametersEvent;
import org.kie.dmn.feel.runtime.functions.BaseFEELFunction;
import org.kie.dmn.feel.runtime.functions.FEELFnResult;
import org.kie.dmn.feel.runtime.functions.ParameterName;

public class RightFunction
        extends BaseFEELFunction {

    public RightFunction() {
        super("right");
    }

    public FEELFnResult<String> invoke(@ParameterName("text") String text, @ParameterName("num_chars") BigDecimal num_chars) {
        if (text == null) {
            return FEELFnResult.ofError(new InvalidParametersEvent(Severity.ERROR, "text", "cannot be null"));
        }
        if (num_chars == null) {
            return FEELFnResult.ofError(new InvalidParametersEvent(Severity.ERROR, "num_chars", "cannot be null"));
        }

        String result = text.substring(text.length() - num_chars.intValue(), text.length());

        return FEELFnResult.ofResult(result);
    }
}
