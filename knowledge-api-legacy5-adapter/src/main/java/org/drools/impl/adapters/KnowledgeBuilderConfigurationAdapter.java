package org.drools.impl.adapters;

import org.drools.builder.conf.KnowledgeBuilderOption;
import org.drools.builder.conf.MultiValueKnowledgeBuilderOption;
import org.drools.builder.conf.SingleValueKnowledgeBuilderOption;
import org.kie.internal.builder.KnowledgeBuilderConfiguration;

import java.util.Set;

import static org.drools.impl.adapters.AdapterUtil.adaptMultiValueBuilderOption;
import static org.drools.impl.adapters.AdapterUtil.adaptOption;
import static org.drools.impl.adapters.AdapterUtil.adaptSingleValueBuilderOption;

public class KnowledgeBuilderConfigurationAdapter implements org.drools.builder.KnowledgeBuilderConfiguration {

    private final KnowledgeBuilderConfiguration delegate;

    public KnowledgeBuilderConfigurationAdapter(KnowledgeBuilderConfiguration delegate) {
        this.delegate = delegate;
    }

    public <T extends KnowledgeBuilderOption> void setOption(T option) {
        delegate.setOption(adaptOption(option));
    }

    public <T extends SingleValueKnowledgeBuilderOption> T getOption(Class<T> option) {
        return (T)delegate.getOption(adaptSingleValueBuilderOption(option));
    }

    public <T extends MultiValueKnowledgeBuilderOption> T getOption(Class<T> option, String key) {
        return (T)delegate.getOption(adaptMultiValueBuilderOption(option), key);
    }

    public <T extends MultiValueKnowledgeBuilderOption> Set<String> getOptionKeys(Class<T> option) {
        return delegate.getOptionKeys(adaptMultiValueBuilderOption(option));
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

    @Override
    public int hashCode() {
        return delegate.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof KnowledgeBuilderConfigurationAdapter && delegate.equals(((KnowledgeBuilderConfigurationAdapter)obj).delegate);
    }
}
