package org.kie.dmn.signavio.feel.runtime.functions;

import java.util.List;

import org.kie.dmn.feel.runtime.functions.BaseFEELFunction;
import org.kie.dmn.feel.runtime.functions.BuiltInFunctions;
import org.kie.dmn.feel.runtime.functions.ConcatenateFunction;
import org.kie.dmn.feel.runtime.functions.FEELFnResult;
import org.kie.dmn.feel.runtime.functions.ParameterName;

public class AppendAllFunction
        extends BaseFEELFunction {

    public AppendAllFunction() {
        super("appendAll");
    }

    public FEELFnResult<List> invoke(@ParameterName("list") Object[] lists) {
        return (FEELFnResult) BuiltInFunctions.getFunction(ConcatenateFunction.class).invoke(lists);
    }
}
