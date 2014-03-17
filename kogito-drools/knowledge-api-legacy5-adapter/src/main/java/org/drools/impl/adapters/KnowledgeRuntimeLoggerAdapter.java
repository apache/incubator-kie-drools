package org.drools.impl.adapters;

import org.drools.logger.KnowledgeRuntimeLogger;

public class KnowledgeRuntimeLoggerAdapter implements KnowledgeRuntimeLogger {

	protected final org.kie.internal.logger.KnowledgeRuntimeLogger delegate;
	
	public KnowledgeRuntimeLoggerAdapter(org.kie.internal.logger.KnowledgeRuntimeLogger delegate) {
		this.delegate = delegate;
	}
	
	public void close() {
		this.delegate.close();
	}

    @Override
    public int hashCode() {
        return delegate.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof KnowledgeRuntimeLoggerAdapter && delegate.equals(((KnowledgeRuntimeLoggerAdapter)obj).delegate);
    }
}
