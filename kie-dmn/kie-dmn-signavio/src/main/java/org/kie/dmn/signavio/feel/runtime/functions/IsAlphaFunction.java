package org.kie.dmn.signavio.feel.runtime.functions;

import java.util.regex.Pattern;

import org.kie.dmn.feel.runtime.functions.BaseFEELFunction;
import org.kie.dmn.feel.runtime.functions.FEELFnResult;
import org.kie.dmn.feel.runtime.functions.ParameterName;

public class IsAlphaFunction
        extends BaseFEELFunction {

    private static final Pattern ALPHA_PATTERN = Pattern.compile("[a-zA-Z]+");

    public IsAlphaFunction() {
        super("isAlpha");
    }

    public FEELFnResult<Boolean> invoke(@ParameterName("text") String text) {
        boolean result = ALPHA_PATTERN.matcher(text).matches();
        
        return FEELFnResult.ofResult(result);
    }
}
