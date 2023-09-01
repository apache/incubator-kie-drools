package org.kie.maven.plugin.helpers;

import org.kie.maven.plugin.enums.DMNModelMode;

import java.util.List;

import static java.util.Arrays.asList;
import static org.kie.maven.plugin.enums.DMNModelMode.YES;

public class DMNModelModeHelper {

    private DMNModelModeHelper() {
    }

    public static boolean dmnModelParameterEnabled(String s) {
        return List.of(YES).contains(DMNModelMode.valueOf(s.toUpperCase()));
    }
}

