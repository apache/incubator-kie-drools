package org.kie.dmn.core.compiler.profiles;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.kie.dmn.api.marshalling.v1_1.DMNExtensionRegister;
import org.kie.dmn.backend.marshalling.v1_1.xstream.extensions.DecisionServicesExtensionRegister;
import org.kie.dmn.core.compiler.DMNProfile;
import org.kie.dmn.core.compiler.DRGElementCompiler;
import org.kie.dmn.feel.parser.feel11.profiles.KieExtendedFEELProfile;

public final class ExtendedDMNProfile extends KieExtendedFEELProfile implements DMNProfile {

    private static final List<DMNExtensionRegister> EXTENSION_REGISTERS = Arrays.asList(new DecisionServicesExtensionRegister());

    @Override
    public List<DMNExtensionRegister> getExtensionRegisters() {
        return EXTENSION_REGISTERS;
    }

    @Override
    public List<DRGElementCompiler> getDRGElementCompilers() {
        return Collections.emptyList();
    }

}
