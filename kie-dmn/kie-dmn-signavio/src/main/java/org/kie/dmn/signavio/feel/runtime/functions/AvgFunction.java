package org.kie.dmn.signavio.feel.runtime.functions;

import java.math.BigDecimal;
import java.util.List;

import org.kie.dmn.feel.runtime.functions.BaseFEELFunction;
import org.kie.dmn.feel.runtime.functions.BuiltInFunctions;
import org.kie.dmn.feel.runtime.functions.FEELFnResult;
import org.kie.dmn.feel.runtime.functions.MeanFunction;
import org.kie.dmn.feel.runtime.functions.ParameterName;

public class AvgFunction
        extends BaseFEELFunction {

    public AvgFunction() {
        super("avg");
    }

    public FEELFnResult<BigDecimal> invoke(@ParameterName( "list" ) List list) {
        return BuiltInFunctions.getFunction(MeanFunction.class).invoke(list);
    }

    public FEELFnResult<BigDecimal> invoke(@ParameterName("list") Number single) {
        return BuiltInFunctions.getFunction(MeanFunction.class).invoke(single);
    }

    public FEELFnResult<BigDecimal> invoke(@ParameterName("n") Object[] list) {
        return BuiltInFunctions.getFunction(MeanFunction.class).invoke(list);
    }
}
