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
}
