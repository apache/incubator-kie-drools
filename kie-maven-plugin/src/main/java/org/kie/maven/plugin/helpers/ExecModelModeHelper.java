package org.kie.maven.plugin.helpers;

import java.util.List;

import org.apache.maven.model.Dependency;

import static java.util.Arrays.asList;
import static org.kie.maven.plugin.enums.ExecModelMode.WITHANC;
import static org.kie.maven.plugin.enums.ExecModelMode.WITHDRL_MVEL;
import static org.kie.maven.plugin.enums.ExecModelMode.WITHMVEL;
import static org.kie.maven.plugin.enums.ExecModelMode.YES;
import static org.kie.maven.plugin.enums.ExecModelMode.YES_WITHDRL;
import static org.kie.maven.plugin.enums.ExecModelMode.valueOf;

public class ExecModelModeHelper {

    public static boolean execModelParameterEnabled(String s) {
        return asList(YES, YES_WITHDRL, WITHMVEL, WITHDRL_MVEL, WITHANC).contains(valueOf(s.toUpperCase()));
    }

    public static boolean ancEnabled(String s) {
        return List.of(WITHANC).contains(valueOf(s.toUpperCase()));
    }

    public static boolean isModelCompilerInClassPath(List<Dependency> dependencies) {
        return dependencies.stream()
                           .anyMatch(d -> d.getGroupId().equals("org.drools") &&
                                          (d.getArtifactId().equals("drools-model-compiler") ||
                                           d.getArtifactId().equals("drools-engine") ||
                                           d.getArtifactId().equals("drools-ruleunits-engine")));
    }

    public static boolean shouldValidateMVEL(String s) {
        return asList(WITHMVEL, WITHDRL_MVEL).contains(valueOf(s.toUpperCase()));
    }

    public static boolean shouldDeleteFile(String s) {
        return asList(YES, WITHMVEL).contains(valueOf(s.toUpperCase()));
    }
}
