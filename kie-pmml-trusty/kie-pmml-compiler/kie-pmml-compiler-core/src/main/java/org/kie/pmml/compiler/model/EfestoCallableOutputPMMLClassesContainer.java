package org.kie.pmml.compiler.model;

import java.util.Map;

import org.kie.efesto.common.api.identifiers.ModelLocalUriId;
import org.kie.efesto.compilationmanager.api.model.EfestoCallableOutputClassesContainer;

public class EfestoCallableOutputPMMLClassesContainer extends EfestoCallableOutputClassesContainer {

    public EfestoCallableOutputPMMLClassesContainer(ModelLocalUriId modelLocalUriId, String fullClassName,
                                                    Map<String, byte[]> compiledClassMap) {
        super(modelLocalUriId, fullClassName, compiledClassMap);
    }
}
