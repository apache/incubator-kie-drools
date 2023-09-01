package org.kie.dmn.trisotech;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.kie.dmn.api.marshalling.DMNExtensionRegister;
import org.kie.dmn.core.compiler.DMNProfile;
import org.kie.dmn.core.compiler.DRGElementCompiler;
import org.kie.dmn.feel.runtime.FEELFunction;
import org.kie.dmn.trisotech.backend.marshalling.v1_3.xstream.TrisotechBoxedExtensionRegister;

public class TrisotechDMNProfile implements DMNProfile {

    @Override
    public List<FEELFunction> getFEELFunctions() {
        return Collections.emptyList();
    }

    @Override
    public List<DMNExtensionRegister> getExtensionRegisters() {
        return List.of(new TrisotechBoxedExtensionRegister());
    }

    @Override
    public List<DRGElementCompiler> getDRGElementCompilers() {
        return Collections.emptyList();
    }

}
