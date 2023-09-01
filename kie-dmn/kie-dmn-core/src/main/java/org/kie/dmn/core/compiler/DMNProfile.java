package org.kie.dmn.core.compiler;

import java.util.List;

import org.kie.dmn.api.marshalling.DMNExtensionRegister;
import org.kie.dmn.feel.lang.FEELProfile;

public interface DMNProfile extends FEELProfile {

    List<DMNExtensionRegister> getExtensionRegisters();

    List<DRGElementCompiler> getDRGElementCompilers();

}
