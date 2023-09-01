package org.kie.dmn.validation.dtanalysis;

import java.util.List;

import org.kie.dmn.core.compiler.DMNProfile;
import org.kie.dmn.validation.DMNValidatorFactory;

/**
 * For internal optimizations only,
 * end-users are invited to make use of the {@link DMNValidatorFactory} instead.
 */
public class InternalDMNDTAnalyserFactory {

    public static InternalDMNDTAnalyser newDMNDTAnalyser(List<DMNProfile> dmnProfiles) {
        return new DMNDTAnalyser(dmnProfiles);
    }

    private InternalDMNDTAnalyserFactory() {
        // It is forbidden to create new instances of util classes.
    }
}
