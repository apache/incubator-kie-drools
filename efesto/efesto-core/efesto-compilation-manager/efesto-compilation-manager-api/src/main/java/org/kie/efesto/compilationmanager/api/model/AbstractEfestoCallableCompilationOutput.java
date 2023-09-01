package org.kie.efesto.compilationmanager.api.model;

import java.util.Collections;
import java.util.List;

import org.kie.efesto.common.api.identifiers.ModelLocalUriId;

public abstract class AbstractEfestoCallableCompilationOutput implements EfestoCallableOutput {

    private final ModelLocalUriId modelLocalUriId;
    private final List<String> fullClassNames;

    protected AbstractEfestoCallableCompilationOutput(ModelLocalUriId modelLocalUriId, String fullClassName) {
        this(modelLocalUriId, Collections.singletonList(fullClassName));
    }

    protected AbstractEfestoCallableCompilationOutput(ModelLocalUriId modelLocalUriId, List<String> fullClassNames) {
        this.modelLocalUriId = modelLocalUriId;
        this.fullClassNames = fullClassNames;
    }


    /**
     * Returns the <b>full resource identifier</b> to be invoked for execution
     *
     * @return
     */
    @Override
    public ModelLocalUriId getModelLocalUriId() {
        return modelLocalUriId;
    }

    @Override
    public List<String> getFullClassNames() {
        return fullClassNames;
    }

}
