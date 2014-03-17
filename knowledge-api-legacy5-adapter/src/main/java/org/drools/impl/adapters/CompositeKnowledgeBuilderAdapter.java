package org.drools.impl.adapters;

import org.drools.builder.ResourceConfiguration;
import org.drools.builder.ResourceType;
import org.drools.io.Resource;
import org.kie.internal.builder.CompositeKnowledgeBuilder;

public class CompositeKnowledgeBuilderAdapter implements org.drools.builder.CompositeKnowledgeBuilder {

    private final CompositeKnowledgeBuilder delegate;

    public CompositeKnowledgeBuilderAdapter(CompositeKnowledgeBuilder delegate) {
        this.delegate = delegate;
    }

    public org.drools.builder.CompositeKnowledgeBuilder type(ResourceType type) {
        delegate.type(type.toKieResourceType());
        return this;
    }

    public org.drools.builder.CompositeKnowledgeBuilder add(Resource resource) {
        delegate.add(((ResourceAdapter)resource).getDelegate());
        return this;
    }

    public org.drools.builder.CompositeKnowledgeBuilder add(Resource resource, ResourceType type) {
        delegate.add(((ResourceAdapter)resource).getDelegate(), type.toKieResourceType());
        return this;
    }

    public org.drools.builder.CompositeKnowledgeBuilder add(Resource resource, ResourceType type, ResourceConfiguration configuration) {
        delegate.add(((ResourceAdapter)resource).getDelegate(), type.toKieResourceType(), (org.kie.api.io.ResourceConfiguration) null);
        return this;
    }

    public void build() {
        delegate.build();
    }

    @Override
    public int hashCode() {
        return delegate.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof CompositeKnowledgeBuilderAdapter && delegate.equals(((CompositeKnowledgeBuilderAdapter)obj).delegate);
    }
}
