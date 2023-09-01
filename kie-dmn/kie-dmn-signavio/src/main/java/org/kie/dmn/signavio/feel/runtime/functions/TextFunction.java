package org.kie.dmn.signavio.feel.runtime.functions;

import java.math.BigDecimal;
import java.text.DecimalFormat;

import org.kie.dmn.api.feel.runtime.events.FEELEvent.Severity;
import org.kie.dmn.feel.runtime.events.InvalidParametersEvent;
import org.kie.dmn.feel.runtime.functions.BaseFEELFunction;
import org.kie.dmn.feel.runtime.functions.FEELFnResult;
import org.kie.dmn.feel.runtime.functions.ParameterName;

public class TextFunction
        extends BaseFEELFunction {

    public TextFunction() {
        super("text");
    }

    public FEELFnResult<String> invoke(@ParameterName("num") BigDecimal num, @ParameterName("format_text") String format_text) {
        if (num == null) {
            return FEELFnResult.ofError(new InvalidParametersEvent(Severity.ERROR, "num", "cannot be null"));
        }
        if (format_text == null) {
            return FEELFnResult.ofError(new InvalidParametersEvent(Severity.ERROR, "format_text", "cannot be null"));
        }

        DecimalFormat df = null;
        try {
            df = new DecimalFormat(format_text);

        } catch (IllegalArgumentException e) {
            return FEELFnResult.ofError(new InvalidParametersEvent(Severity.ERROR, "format_text", "illegal specific format: " + format_text + " because: " + e.getMessage()));
        }

        String result = df.format(num);

        return FEELFnResult.ofResult(result);
    }
}
