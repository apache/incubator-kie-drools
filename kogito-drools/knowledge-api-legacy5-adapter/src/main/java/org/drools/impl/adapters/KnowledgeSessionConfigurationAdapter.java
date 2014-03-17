package org.drools.impl.adapters;

import org.drools.runtime.KnowledgeSessionConfiguration;
import org.drools.runtime.conf.KnowledgeSessionOption;
import org.drools.runtime.conf.MultiValueKnowledgeSessionOption;
import org.drools.runtime.conf.SingleValueKnowledgeSessionOption;
import org.kie.api.runtime.KieSessionConfiguration;

import static org.drools.impl.adapters.AdapterUtil.*;

public class KnowledgeSessionConfigurationAdapter implements KnowledgeSessionConfiguration {

    private final KieSessionConfiguration delegate;

    public KnowledgeSessionConfigurationAdapter(KieSessionConfiguration delegate) {
        this.delegate = delegate;
    }

    public <T extends KnowledgeSessionOption> void setOption(T option) {
        delegate.setOption(adaptOption(option));
    }

    public <T extends SingleValueKnowledgeSessionOption> T getOption(Class<T> option) {
        return (T)adaptOption(delegate.getOption(adaptSingleValueSessionOption(option)));
    }

    public <T extends MultiValueKnowledgeSessionOption> T getOption(Class<T> option, String key) {
        return (T)adaptOption(delegate.getOption(adaptMultiValueSessionOption(option), key));
    }

    public void setProperty(String name, String value) {
        delegate.setProperty(name, value);
    }

    public String getProperty(String name) {
        return delegate.getProperty(name);
    }

    public KieSessionConfiguration getDelegate() {
        return delegate;
    }

    @Override
    public int hashCode() {
        return delegate.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof KnowledgeSessionConfigurationAdapter && delegate.equals(((KnowledgeSessionConfigurationAdapter)obj).delegate);
    }
}
