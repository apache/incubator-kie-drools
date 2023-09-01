package org.kie.efesto.compilationmanager.api.model;

import java.util.Map;

/**
 * A <code>EfestoClassesContainer</code> containing compiled classes
 */
public class EfestoOutputClassesContainer implements EfestoClassesContainer {

    private final Map<String, byte[]> compiledClassMap;

    public EfestoOutputClassesContainer(Map<String, byte[]> compiledClassMap) {
        this.compiledClassMap = compiledClassMap;
    }

    @Override
    public Map<String, byte[]> getCompiledClassesMap() {
        return compiledClassMap;
    }
}
