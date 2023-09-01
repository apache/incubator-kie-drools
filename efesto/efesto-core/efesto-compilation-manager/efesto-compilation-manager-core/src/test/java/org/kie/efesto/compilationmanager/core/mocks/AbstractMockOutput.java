package org.kie.efesto.compilationmanager.core.mocks;

import java.util.List;

import org.kie.efesto.common.api.identifiers.ModelLocalUriId;
import org.kie.efesto.compilationmanager.api.model.AbstractEfestoCallableCompilationOutput;
import org.kie.efesto.compilationmanager.api.model.EfestoResource;

/**
 * A generic <i>Resource</i> to be processed by specific engine
 */
public abstract class AbstractMockOutput<T> extends AbstractEfestoCallableCompilationOutput implements EfestoResource<T> {

    /**
     * This is the <b>payload</b> to forward to the target compilation-engine
     */
    private final T content;

    protected AbstractMockOutput(ModelLocalUriId modelLocalUriId, T content) {
        super(modelLocalUriId, (List<String>) null);
        this.content = content;
    }

    @Override
    public T getContent() {
        return content;
    }

}
