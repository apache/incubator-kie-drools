package org.kie.dmn.feel.parser.feel11.profiles;

import java.util.Arrays;
import java.util.List;

import org.kie.dmn.feel.lang.FEELProfile;
import org.kie.dmn.feel.runtime.FEELFunction;
import org.kie.dmn.feel.runtime.functions.BuiltInFunctions;

public class FEELv12Profile implements FEELProfile {

    @Override
    public List<FEELFunction> getFEELFunctions() {
        return Arrays.asList(BuiltInFunctions.getFunctions());
    }

}
