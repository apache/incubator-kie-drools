package org.kie.efesto.compilationmanager.api.model;

import java.util.List;
import java.util.Map;

import org.kie.efesto.common.api.identifiers.ModelLocalUriId;

/**
 * A <code>EfestoCallableOutput</code> containing compiled classes
 */
public abstract class EfestoCallableOutputClassesContainer extends AbstractEfestoCallableCompilationOutput implements EfestoClassesContainer {

    private final Map<String, byte[]> compiledClassMap;

    protected EfestoCallableOutputClassesContainer(ModelLocalUriId modelLocalUriId, String fullClassName, Map<String,
            byte[]> compiledClassMap) {
        super(modelLocalUriId, fullClassName);
        this.compiledClassMap = compiledClassMap;
    }

    protected EfestoCallableOutputClassesContainer(ModelLocalUriId modelLocalUriId, List<String> fullClassNames,
                                                   Map<String, byte[]> compiledClassMap) {
        super(modelLocalUriId, fullClassNames);
        this.compiledClassMap = compiledClassMap;
    }

    @Override
    public Map<String, byte[]> getCompiledClassesMap() {
        return compiledClassMap;
    }
}
