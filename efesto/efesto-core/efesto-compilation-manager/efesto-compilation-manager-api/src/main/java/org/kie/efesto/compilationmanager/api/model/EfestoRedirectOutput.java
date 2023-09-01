package org.kie.efesto.compilationmanager.api.model;

import java.util.List;

import org.kie.efesto.common.api.exceptions.KieEfestoCommonException;
import org.kie.efesto.common.api.identifiers.ModelLocalUriId;

/**
 * A  <code>CompilationOutput</code> from one engine that will
 * be an <code>EfestoResource</code> for another one.
 * This will be translated to a <code>GeneratedRedirectResource</code>,
 * that has a specif json-format and semantic.
 */
public abstract class EfestoRedirectOutput<T> extends AbstractEfestoCallableCompilationOutput implements EfestoResource<T> {

    private final String targetEngine;

    /**
     * This is the <b>payload</b> to forward to the target compilation-engine
     */
    private final T content;

    protected EfestoRedirectOutput(ModelLocalUriId modelLocalUriId, String targetEngine, T content) {
        super(modelLocalUriId, (List<String>) null);
        if (targetEngine == null || targetEngine.isEmpty()) {
            throw new KieEfestoCommonException("Missing required target");
        }
        this.targetEngine = targetEngine;
        this.content = content;
    }

    public String getTargetEngine() {
        return targetEngine;
    }

    @Override
    public T getContent() {
        return content;
    }

}
