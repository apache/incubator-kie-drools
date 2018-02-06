package org.kie.dmn.feel.runtime.impl;

import java.util.Arrays;
import java.util.List;

import org.kie.dmn.feel.lang.FEELProfile;
import org.kie.dmn.feel.runtime.FEELFunction;
import org.kie.dmn.feel.runtime.functions.extended.KieExtendedDMNFunctions;

public class KieExtendedFEELProfile implements FEELProfile {

    @Override
    public List<FEELFunction> getFEELFunctions() {
        return Arrays.asList(KieExtendedDMNFunctions.getFunctions());
    }

}
