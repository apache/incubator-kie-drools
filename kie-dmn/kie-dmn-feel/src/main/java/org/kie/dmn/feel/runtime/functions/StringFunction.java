package org.kie.dmn.feel.runtime.functions;

import org.kie.dmn.api.feel.runtime.events.FEELEvent.Severity;
import org.kie.dmn.feel.runtime.events.InvalidParametersEvent;
import org.kie.dmn.feel.util.TypeUtil;

public class StringFunction
        extends BaseFEELFunction {

    public static final StringFunction INSTANCE = new StringFunction();

    public StringFunction() {
        super(FEELConversionFunctionNames.STRING);
    }

    public FEELFnResult<String> invoke(@ParameterName("from") Object val) {
        if ( val == null ) {
            return FEELFnResult.ofResult( null );
        } else {
            return FEELFnResult.ofResult( TypeUtil.formatValue(val, false) );
        }
    }

    public FEELFnResult<String> invoke(@ParameterName( "mask" ) String mask, @ParameterName("p") Object[] params) {
        if ( mask == null ) {
            return FEELFnResult.ofError(new InvalidParametersEvent(Severity.ERROR, "mask", "cannot be null"));
        } else {
            return FEELFnResult.ofResult( String.format( mask, params ) );
        }
    }
}
