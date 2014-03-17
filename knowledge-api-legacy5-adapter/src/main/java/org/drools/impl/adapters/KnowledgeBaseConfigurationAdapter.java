package org.drools.impl.adapters;

import org.drools.conf.KnowledgeBaseOption;
import org.drools.conf.MultiValueKnowledgeBaseOption;
import org.drools.conf.SingleValueKnowledgeBaseOption;
import org.kie.api.KieBaseConfiguration;

import static org.drools.impl.adapters.AdapterUtil.adaptMultiValueBaseOption;
import static org.drools.impl.adapters.AdapterUtil.adaptOption;
import static org.drools.impl.adapters.AdapterUtil.adaptSingleValueBaseOption;

public class KnowledgeBaseConfigurationAdapter implements org.drools.KnowledgeBaseConfiguration {

    private final KieBaseConfiguration delegate;

    public KnowledgeBaseConfigurationAdapter(KieBaseConfiguration delegate) {
        this.delegate = delegate;
    }

    public <T extends KnowledgeBaseOption> void setOption(T option) {
        delegate.setOption(adaptOption(option));
    }

    public <T extends SingleValueKnowledgeBaseOption> T getOption(Class<T> option) {
        return (T)adaptOption(delegate.getOption(adaptSingleValueBaseOption(option)));
    }

    public <T extends MultiValueKnowledgeBaseOption> T getOption(Class<T> option, String key) {
        return (T)adaptOption(delegate.getOption(adaptMultiValueBaseOption(option), key));
    }

    public void setProperty(String name, String value) {
        delegate.setProperty(name, value);
    }

    public String getProperty(String name) {
        return delegate.getProperty(name);
    }

    public KieBaseConfiguration getDelegate() {
        return delegate;
    }

    @Override
    public int hashCode() {
        return delegate.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof KnowledgeBaseConfigurationAdapter && delegate.equals(((KnowledgeBaseConfigurationAdapter)obj).delegate);
    }
}
