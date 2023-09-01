package org.kie.dmn.signavio.feel.runtime.functions;

import java.util.regex.Pattern;

import org.kie.dmn.feel.runtime.functions.BaseFEELFunction;
import org.kie.dmn.feel.runtime.functions.FEELFnResult;
import org.kie.dmn.feel.runtime.functions.ParameterName;

public class IsSpacesFunction
        extends BaseFEELFunction {

    private static final Pattern SPACE_PATTERN = Pattern.compile(" +");

    public IsSpacesFunction() {
        super("isSpaces");
    }

    public FEELFnResult<Boolean> invoke(@ParameterName("text") String text) {
        boolean result = SPACE_PATTERN.matcher(text).matches();
        
        return FEELFnResult.ofResult(result);
    }
}
