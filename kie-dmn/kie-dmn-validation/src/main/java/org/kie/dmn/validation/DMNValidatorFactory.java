package org.kie.dmn.validation;

import java.util.Collections;
import java.util.List;
import java.util.Properties;

import org.kie.dmn.core.compiler.DMNProfile;

public final class DMNValidatorFactory {

    public static DMNValidator newValidator() {
        return new DMNValidatorImpl(null, Collections.emptyList(), null);
    }

    public static DMNValidator newValidator(List<DMNProfile> dmnProfiles) {
        return new DMNValidatorImpl(null, dmnProfiles, null);
    }

    public static DMNValidator newValidator(ClassLoader cl, List<DMNProfile> dmnProfiles) {
        return new DMNValidatorImpl(cl, dmnProfiles, null);
    }

    public static DMNValidator newValidator(List<DMNProfile> dmnProfiles, Properties p) {
        return new DMNValidatorImpl(null, dmnProfiles, p);
    }

    private DMNValidatorFactory() {
        // Constructing instances is not allowed for this class
    }

}
