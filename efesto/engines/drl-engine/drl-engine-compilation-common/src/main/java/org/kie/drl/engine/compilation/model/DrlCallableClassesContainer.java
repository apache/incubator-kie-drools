package org.kie.drl.engine.compilation.model;

import org.kie.efesto.common.api.model.FRI;
import org.kie.efesto.compilationmanager.api.model.EfestoCallableOutputClassesContainer;

import java.util.List;
import java.util.Map;

public class DrlCallableClassesContainer extends EfestoCallableOutputClassesContainer {
    public DrlCallableClassesContainer(FRI fri, List<String> fullClassNames, Map<String, byte[]> compiledClassMap) {
        super(fri, fullClassNames, compiledClassMap);
    }
}
