package org.drools.impl.adapters;

import org.drools.runtime.rule.AgendaGroup;

public class AgendaGroupAdapter implements AgendaGroup {

    private final org.kie.api.runtime.rule.AgendaGroup delegate;

    public AgendaGroupAdapter(org.kie.api.runtime.rule.AgendaGroup delegate) {
        this.delegate = delegate;
    }

    @Override
    public String getName() {
        return delegate.getName();
    }

    @Override
    public void clear() {
        delegate.clear();
    }

    @Override
    public void setFocus() {
        delegate.setFocus();
    }

    @Override
    public int hashCode() {
        return delegate.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof AgendaGroupAdapter && delegate.equals(((AgendaGroupAdapter)obj).delegate);
    }
}
