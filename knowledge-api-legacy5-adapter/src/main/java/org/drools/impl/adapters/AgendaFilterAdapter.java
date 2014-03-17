package org.drools.impl.adapters;

import org.kie.api.runtime.rule.AgendaFilter;
import org.kie.api.runtime.rule.Match;

public class AgendaFilterAdapter implements AgendaFilter {

    private final org.drools.runtime.rule.AgendaFilter delegate;

    public AgendaFilterAdapter(org.drools.runtime.rule.AgendaFilter delegate) {
        this.delegate = delegate;
    }

    @Override
    public boolean accept(Match match) {
        return delegate.accept(new ActivationAdapter(match));
    }

    @Override
    public int hashCode() {
        return delegate.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof AgendaFilterAdapter && delegate.equals(((AgendaFilterAdapter)obj).delegate);
    }
}
