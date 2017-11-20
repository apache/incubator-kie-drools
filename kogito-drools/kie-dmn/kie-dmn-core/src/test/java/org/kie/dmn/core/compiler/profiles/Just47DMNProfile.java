package org.kie.dmn.core.compiler.profiles;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.kie.dmn.api.marshalling.v1_1.DMNExtensionRegister;
import org.kie.dmn.core.compiler.DMNProfile;
import org.kie.dmn.core.compiler.DRGElementCompiler;
import org.kie.dmn.feel.runtime.FEELFunction;

public class Just47DMNProfile implements DMNProfile {

    @Override
    public List<FEELFunction> getFEELFunctions() {
        return Arrays.asList(new Just47Function());
    }

    @Override
    public List<DMNExtensionRegister> getExtensionRegisters() {
        return Collections.emptyList();
    }

    @Override
    public List<DRGElementCompiler> getDRGElementCompilers() {
        return Collections.emptyList();
    }

}