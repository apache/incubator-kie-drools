package org.drools.impl.adapters;

import org.drools.runtime.rule.ActivationGroup;

public class ActivationGroupAdapter implements ActivationGroup {

    private final org.kie.api.runtime.rule.ActivationGroup delegate;

    public ActivationGroupAdapter(org.kie.api.runtime.rule.ActivationGroup delegate) {
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
}
