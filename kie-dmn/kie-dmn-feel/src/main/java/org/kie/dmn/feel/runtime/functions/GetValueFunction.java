package org.kie.dmn.feel.runtime.functions;

import java.util.Map;

import org.kie.dmn.api.feel.runtime.events.FEELEvent.Severity;
import org.kie.dmn.feel.lang.types.BuiltInType;
import org.kie.dmn.feel.lang.types.impl.ImmutableFPAWrappingPOJO;
import org.kie.dmn.feel.runtime.events.InvalidParametersEvent;

public class GetValueFunction extends BaseFEELFunction {

    public GetValueFunction() {
        super("get value");
    }

    public FEELFnResult<Object> invoke(@ParameterName("m") Object m, @ParameterName("key") String key) {
        if (m == null) {
            return FEELFnResult.ofError(new InvalidParametersEvent(Severity.ERROR, "m", "cannot be null"));
        } else if (m instanceof Map) {
            return FEELFnResult.ofResult(((Map<?, ?>) m).get(key));
        } else if (BuiltInType.determineTypeFromInstance(m) == BuiltInType.UNKNOWN) {
            return FEELFnResult.ofResult(new ImmutableFPAWrappingPOJO(m).getFEELProperty(key).toOptional().orElse(null));
        } else {
            return FEELFnResult.ofError(new InvalidParametersEvent(Severity.ERROR, "m", "is not a context"));
        }
    }
}
