package org.drools.impl.adapters;

import org.drools.builder.ResultSeverity;
import org.drools.io.Resource;
import org.kie.internal.builder.KnowledgeBuilderResult;

import static org.drools.impl.adapters.AdapterUtil.adaptResultSeverity;

public class KnowledgeBuilderResultAdapter implements org.drools.builder.KnowledgeBuilderResult {

    private final KnowledgeBuilderResult delegate;

    public KnowledgeBuilderResultAdapter(KnowledgeBuilderResult delegate) {
        this.delegate = delegate;
    }

    public ResultSeverity getSeverity() {
        return adaptResultSeverity(delegate.getSeverity());
    }

    public String getMessage() {
        return delegate.getMessage();
    }

    public int[] getLines() {
        return delegate.getLines();
    }

    public Resource getResource() {
        return new ResourceAdapter(delegate.getResource());
    }

    @Override
    public int hashCode() {
        return delegate.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof KnowledgeBuilderResultAdapter && delegate.equals(((KnowledgeBuilderResultAdapter)obj).delegate);
    }
}
