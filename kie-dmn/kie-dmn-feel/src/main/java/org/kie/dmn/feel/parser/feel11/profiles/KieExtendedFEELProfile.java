package org.kie.dmn.feel.parser.feel11.profiles;

import java.util.Arrays;
import java.util.List;

import org.kie.dmn.feel.runtime.FEELFunction;
import org.kie.dmn.feel.runtime.functions.extended.KieExtendedDMNFunctions;

public class KieExtendedFEELProfile extends FEELv12Profile {

    @Override
    public List<FEELFunction> getFEELFunctions() {
        return Arrays.asList(KieExtendedDMNFunctions.getFunctions());
    }
}
