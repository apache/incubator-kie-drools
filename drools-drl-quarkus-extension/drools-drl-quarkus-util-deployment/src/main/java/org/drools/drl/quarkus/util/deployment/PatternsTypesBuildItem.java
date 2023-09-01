package org.drools.drl.quarkus.util.deployment;

import java.util.Map;
import java.util.Set;

import io.quarkus.builder.item.SimpleBuildItem;

/**
 * Collect Pattern types by package.
 * 
 * (a Pattern type is generally one referring to a Phreak OTN class)
 */
public final class PatternsTypesBuildItem extends SimpleBuildItem {
    private final Map<String, Set<Class<?>>> patternsClasses;
    
    public PatternsTypesBuildItem(Map<String, Set<Class<?>>> patternsClasses) {
        this.patternsClasses = patternsClasses;
    }

    public Map<String, Set<Class<?>>> getPatternsClasses() {
        return patternsClasses;
    }
}
