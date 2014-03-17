package org.drools.impl.adapters;

import org.drools.definition.rule.Rule;
import org.kie.api.runtime.rule.FactHandle;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class FactHandleAdapter implements org.drools.runtime.rule.FactHandle {

    private final FactHandle delegate;

    public FactHandleAdapter(FactHandle delegate) {
        this.delegate = delegate;
    }

    public String toExternalForm() {
        return delegate.toExternalForm();
    }

    public FactHandle getDelegate() {
        return delegate;
    }

    public static List<org.drools.runtime.rule.FactHandle> adaptFactHandles(Collection<FactHandle> factHandles) {
        List<org.drools.runtime.rule.FactHandle> result = new ArrayList<org.drools.runtime.rule.FactHandle>();
        for (FactHandle factHandle : factHandles) {
            result.add(new FactHandleAdapter(factHandle));
        }
        return result;
    }

    @Override
    public int hashCode() {
        return delegate.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof FactHandleAdapter && delegate.equals(((FactHandleAdapter)obj).delegate);
    }
}
