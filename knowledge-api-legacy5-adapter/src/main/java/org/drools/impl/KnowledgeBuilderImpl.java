package org.drools.impl;

import org.drools.KnowledgeBase;
import org.drools.builder.CompositeKnowledgeBuilder;
import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.KnowledgeBuilderErrors;
import org.drools.builder.KnowledgeBuilderResults;
import org.drools.builder.ResourceConfiguration;
import org.drools.builder.ResourceType;
import org.drools.builder.ResultSeverity;
import org.drools.compiler.compiler.PackageBuilder;
import org.drools.definition.KnowledgePackage;
import org.drools.impl.adapters.CompositeKnowledgeBuilderAdapter;
import org.drools.impl.adapters.KnowledgeBaseAdapter;
import org.drools.impl.adapters.KnowledgeBuilderErrorsAdapter;
import org.drools.impl.adapters.KnowledgeBuilderResultsAdapter;
import org.drools.impl.adapters.ResourceAdapter;
import org.drools.io.Resource;

import java.util.Collection;

import static org.drools.impl.adapters.AdapterUtil.adaptResultSeverity;
import static org.drools.impl.adapters.KnowledgePackageAdapter.adaptKnowledgePackages;

public class KnowledgeBuilderImpl implements KnowledgeBuilder {

    private final org.drools.compiler.builder.impl.KnowledgeBuilderImpl delegate;

    public KnowledgeBuilderImpl(PackageBuilder pkgBuilder) {
        delegate = new org.drools.compiler.builder.impl.KnowledgeBuilderImpl(pkgBuilder);
    }

    public void add(Resource resource, ResourceType type) {
        delegate.add(((ResourceAdapter)resource).getDelegate(), type.toKieResourceType());
    }

    public void add(Resource resource, ResourceType type, ResourceConfiguration configuration) {
        delegate.add(((ResourceAdapter)resource).getDelegate(), type.toKieResourceType(), null);
    }

    public Collection<KnowledgePackage> getKnowledgePackages() {
        return adaptKnowledgePackages(delegate.getKnowledgePackages());
    }

    public KnowledgeBase newKnowledgeBase() {
        return new KnowledgeBaseAdapter(delegate.newKnowledgeBase());
    }

    public boolean hasErrors() {
        return delegate.hasErrors();
    }

    public KnowledgeBuilderErrors getErrors() {
        return new KnowledgeBuilderErrorsAdapter(delegate.getErrors());
    }

    public KnowledgeBuilderResults getResults(ResultSeverity... severities) {
        return new KnowledgeBuilderResultsAdapter(delegate.getResults(adaptResultSeverity(severities)));
    }

    public boolean hasResults(ResultSeverity... severities) {
        return delegate.hasResults(adaptResultSeverity(severities));
    }

    public void undo() {
        delegate.undo();
    }

    public CompositeKnowledgeBuilder batch() {
        return new CompositeKnowledgeBuilderAdapter(delegate.batch());
    }
}
