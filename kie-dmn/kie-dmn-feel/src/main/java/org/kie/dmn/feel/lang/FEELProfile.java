package org.kie.dmn.feel.lang;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.kie.dmn.feel.runtime.FEELFunction;

public interface FEELProfile {

    List<FEELFunction> getFEELFunctions();
    
    default Map<String,Object> getValues() {
        return Collections.emptyMap();
    };
}
