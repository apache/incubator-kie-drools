package org.kie.drl.engine.compilation.model;

import java.util.List;
import java.util.Map;

import org.kie.efesto.common.api.identifiers.ModelLocalUriId;
import org.kie.efesto.compilationmanager.api.model.EfestoCallableOutputClassesContainer;

public class ExecutableModelClassesContainer extends EfestoCallableOutputClassesContainer {
    public ExecutableModelClassesContainer(ModelLocalUriId modelLocalUriId, List<String> fullClassNames, Map<String, byte[]> compiledClassMap) {
        super(modelLocalUriId, fullClassNames, compiledClassMap);
    }
}
