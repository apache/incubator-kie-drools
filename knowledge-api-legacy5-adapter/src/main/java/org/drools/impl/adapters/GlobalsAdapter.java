package org.drools.impl.adapters;

import org.kie.api.runtime.Globals;

public class GlobalsAdapter implements org.drools.runtime.Globals {

    private final Globals delegate;

    public GlobalsAdapter(Globals delegate) {
        this.delegate = delegate;
    }

    public Object get(String identifier) {
        return delegate.get(identifier);
    }

    public void set(String identifier, Object value) {
        delegate.set(identifier, value);
    }

    public void setDelegate(org.drools.runtime.Globals delegate) {
        this.delegate.setDelegate(((GlobalsAdapter)delegate).delegate);
    }

    @Override
    public int hashCode() {
        return delegate.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof GlobalsAdapter && delegate.equals(((GlobalsAdapter)obj).delegate);
    }
}
