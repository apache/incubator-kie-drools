package org.drools.impl.adapters;

import org.kie.api.runtime.Environment;

public class EnvironmentAdapter implements org.drools.runtime.Environment, org.kie.api.runtime.Environment {

    public final Environment delegate;

    public EnvironmentAdapter(Environment delegate) {
        this.delegate = delegate;
    }

    public Object get(String identifier) {
        return delegate.get(identifier);
    }

    public void set(String identifier, Object object) {
        delegate.set(identifier, object);
    }

    @Override
    public void setDelegate(Environment delegate) {
        setDelegate((org.drools.runtime.Environment) new EnvironmentAdapter(delegate));
    }

    public void setDelegate(org.drools.runtime.Environment delegate) {
        this.delegate.setDelegate(((EnvironmentAdapter)delegate).delegate);
    }

    public Environment getDelegate() {
        return delegate;
    }

    @Override
    public int hashCode() {
        return delegate.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof EnvironmentAdapter && delegate.equals(((EnvironmentAdapter)obj).delegate);
    }
}
