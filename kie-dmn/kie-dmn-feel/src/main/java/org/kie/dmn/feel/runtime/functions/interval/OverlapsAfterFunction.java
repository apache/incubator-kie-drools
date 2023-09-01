package org.kie.dmn.feel.runtime.functions.interval;

import org.kie.dmn.feel.runtime.Range;
import org.kie.dmn.feel.runtime.functions.BaseFEELFunction;
import org.kie.dmn.feel.runtime.functions.FEELFnResult;
import org.kie.dmn.feel.runtime.functions.ParameterName;

public class OverlapsAfterFunction
        extends BaseFEELFunction {

    public static final OverlapsAfterFunction INSTANCE = new OverlapsAfterFunction();

    public OverlapsAfterFunction() {
        super("overlaps after");
    }

    public FEELFnResult<Boolean> invoke(@ParameterName( "range1" ) Range range1, @ParameterName( "range2" ) Range range2) {
        return OverlapsBeforeFunction.INSTANCE.invoke(range2, range1);
    }
}
