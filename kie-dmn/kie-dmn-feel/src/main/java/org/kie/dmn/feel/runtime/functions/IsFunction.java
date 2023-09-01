package org.kie.dmn.feel.runtime.functions;

import java.time.chrono.ChronoPeriod;
import java.time.temporal.TemporalAccessor;

import org.kie.dmn.feel.lang.types.BuiltInType;
import org.kie.dmn.feel.util.EvalHelper;

public class IsFunction extends BaseFEELFunction {
    public static final IsFunction INSTANCE = new IsFunction();

    IsFunction() {
        super("is");
    }

    public FEELFnResult<Boolean> invoke(@ParameterName("value1") Object value1, @ParameterName("value2") Object value2) {
        if (value1 instanceof ChronoPeriod && value2 instanceof ChronoPeriod) {
            // special check for YM durations
            return FEELFnResult.ofResult(value1.equals(value2));
        } else if (value1 instanceof TemporalAccessor && value2 instanceof TemporalAccessor) {
            // Handle specific cases when both time / datetime
            TemporalAccessor left = (TemporalAccessor) value1;
            TemporalAccessor right = (TemporalAccessor) value2;
            if (BuiltInType.determineTypeFromInstance(left) == BuiltInType.TIME && BuiltInType.determineTypeFromInstance(right) == BuiltInType.TIME) {
                return FEELFnResult.ofResult(EvalHelper.isEqualTimeInSemanticD(left, right));
            } else if (BuiltInType.determineTypeFromInstance(left) == BuiltInType.DATE_TIME && BuiltInType.determineTypeFromInstance(right) == BuiltInType.DATE_TIME) {
                return FEELFnResult.ofResult(EvalHelper.isEqualDateTimeInSemanticD(left, right));
            } // fallback; continue:
        }
        Boolean fallback = EvalHelper.isEqual(value1, value2, null); // if null implying they are not the same semantic domain value.
        return FEELFnResult.ofResult(fallback != null ? fallback : Boolean.FALSE);
    }

}
