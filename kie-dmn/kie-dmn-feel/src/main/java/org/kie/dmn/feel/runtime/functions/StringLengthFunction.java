package org.kie.dmn.feel.runtime.functions;

import java.math.BigDecimal;

import org.kie.dmn.api.feel.runtime.events.FEELEvent.Severity;
import org.kie.dmn.feel.runtime.events.InvalidParametersEvent;
import org.kie.dmn.feel.util.EvalHelper;

public class StringLengthFunction
        extends BaseFEELFunction {

    public StringLengthFunction() {
        super( "string length" );
    }

    public FEELFnResult<BigDecimal> invoke(@ParameterName("string") String string) {
        if ( string == null ) {
            return FEELFnResult.ofError(new InvalidParametersEvent(Severity.ERROR, "string", "cannot be null"));
        } else {
            return FEELFnResult.ofResult(EvalHelper.getBigDecimalOrNull(string.codePointCount(0, string.length())));
        }
    }
}
