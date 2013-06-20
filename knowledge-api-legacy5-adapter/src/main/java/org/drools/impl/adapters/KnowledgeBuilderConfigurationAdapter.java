package org.drools.impl.adapters;

import org.drools.builder.conf.KnowledgeBuilderOption;
import org.drools.builder.conf.MultiValueKnowledgeBuilderOption;
import org.drools.builder.conf.SingleValueKnowledgeBuilderOption;
import org.kie.internal.builder.KnowledgeBuilderConfiguration;

import java.util.Set;

public class KnowledgeBuilderConfigurationAdapter implements org.drools.builder.KnowledgeBuilderConfiguration {

    private final KnowledgeBuilderConfiguration delegate;

    public KnowledgeBuilderConfigurationAdapter(KnowledgeBuilderConfiguration delegate) {
        this.delegate = delegate;
    }

    public <T extends KnowledgeBuilderOption> void setOption(T option) {
        throw new UnsupportedOperationException("org.drools.impl.adapters.KnowledgeBuilderConfigurationAdapter.setOption -> TODO");
    }

    public <T extends SingleValueKnowledgeBuilderOption> T getOption(Class<T> option) {
        throw new UnsupportedOperationException("org.drools.impl.adapters.KnowledgeBuilderConfigurationAdapter.getOption -> TODO");

    }

    public <T extends MultiValueKnowledgeBuilderOption> T getOption(Class<T> option, String key) {
        throw new UnsupportedOperationException("org.drools.impl.adapters.KnowledgeBuilderConfigurationAdapter.getOption -> TODO");

    }

    public <T extends MultiValueKnowledgeBuilderOption> Set<String> getOptionKeys(Class<T> option) {
        throw new UnsupportedOperationException("org.drools.impl.adapters.KnowledgeBuilderConfigurationAdapter.getOptionKeys -> TODO");

    }

    public void setProperty(String name, String value) {
        delegate.setProperty(name, value);
    }

    public String getProperty(String name) {
        return delegate.getProperty(name);
    }

    public KnowledgeBuilderConfiguration getDelegate() {
        return delegate;
    }
}
