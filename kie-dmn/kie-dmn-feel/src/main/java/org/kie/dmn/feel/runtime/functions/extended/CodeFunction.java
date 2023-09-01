package org.kie.dmn.feel.runtime.functions.extended;

import org.kie.dmn.feel.runtime.functions.BaseFEELFunction;
import org.kie.dmn.feel.runtime.functions.FEELFnResult;
import org.kie.dmn.feel.runtime.functions.ParameterName;
import org.kie.dmn.feel.util.TypeUtil;

public class CodeFunction
        extends BaseFEELFunction {

    public CodeFunction() {
        super( "code" );
    }

    public FEELFnResult<String> invoke(@ParameterName("value") Object val) {
        if ( val == null ) {
            return FEELFnResult.ofResult( "null" );
        } else {
            return FEELFnResult.ofResult(TypeUtil.formatValue(val, true) );
        }
    }
}
