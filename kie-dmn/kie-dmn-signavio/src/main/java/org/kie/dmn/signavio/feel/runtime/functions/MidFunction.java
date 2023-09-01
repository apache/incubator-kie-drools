package org.kie.dmn.signavio.feel.runtime.functions;

import org.kie.dmn.feel.runtime.functions.BaseFEELFunction;
import org.kie.dmn.feel.runtime.functions.BuiltInFunctions;
import org.kie.dmn.feel.runtime.functions.FEELFnResult;
import org.kie.dmn.feel.runtime.functions.ParameterName;
import org.kie.dmn.feel.runtime.functions.SubstringFunction;

public class MidFunction
        extends BaseFEELFunction {

    public MidFunction() {
        super("mid");
    }

    public FEELFnResult<String> invoke(@ParameterName("text") String text, @ParameterName("start") Number start, @ParameterName("num_chars") Number num_chars) {
        return BuiltInFunctions.getFunction(SubstringFunction.class).invoke(text, start, num_chars);
    }
}
