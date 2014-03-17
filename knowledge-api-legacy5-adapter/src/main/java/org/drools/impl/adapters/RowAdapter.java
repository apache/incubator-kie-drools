package org.drools.impl.adapters;

import org.drools.runtime.rule.FactHandle;
import org.drools.runtime.rule.Row;

public class RowAdapter implements Row {

    private final org.kie.api.runtime.rule.Row delegate;

    public RowAdapter(org.kie.api.runtime.rule.Row delegate) {
        this.delegate = delegate;
    }

    @Override
    public Object get(String identifier) {
        return delegate.get(identifier);
    }

    @Override
    public FactHandle getFactHandle(String identifier) {
        return new FactHandleAdapter(delegate.getFactHandle(identifier));
    }

    @Override
    public int hashCode() {
        return delegate.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof RowAdapter && delegate.equals(((RowAdapter)obj).delegate);
    }
}
