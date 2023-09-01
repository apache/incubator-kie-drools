package org.drools.drl.quarkus.util.deployment;

import java.util.Map;

import io.quarkus.builder.item.SimpleBuildItem;

/**
 * Collect Globals by package.
 */
public final class GlobalsBuildItem extends SimpleBuildItem {
    private final Map<String, Map<String, java.lang.reflect.Type>> globals;
    
    public GlobalsBuildItem(Map<String, Map<String, java.lang.reflect.Type>> g) {
        this.globals = g;
    }

    public Map<String, Map<String, java.lang.reflect.Type>> getGlobals() {
        return globals;
    }
}
