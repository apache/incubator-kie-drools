package org.kie.dmn.core.compiler.profiles;

import org.kie.dmn.api.marshalling.v1_1.DMNExtensionRegister;
import org.kie.dmn.core.compiler.DMNProfile;
import org.kie.dmn.core.compiler.DRGElementCompiler;
import org.kie.dmn.feel.runtime.FEELFunction;
import org.kie.dmn.feel.runtime.functions.extended.DateFunction;
import org.kie.dmn.feel.runtime.functions.extended.DurationFunction;
import org.kie.dmn.feel.runtime.functions.extended.TimeFunction;


import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public final class ExtendedDMNProfile implements DMNProfile {
    @Override
    public List<DMNExtensionRegister> getExtensionRegisters() {
        return Collections.emptyList();
    }

    @Override
    public List<DRGElementCompiler> getDRGElementCompilers() {
        return Collections.emptyList();
    }

    @Override
    public List<FEELFunction> getFEELFunctions() {
        return Arrays.asList(FUNCTIONS);
    }

    private static final FEELFunction[] FUNCTIONS = new FEELFunction[]{
            TimeFunction.INSTANCE,
            DateFunction.INSTANCE,
            DurationFunction.INSTANCE
    };
}
