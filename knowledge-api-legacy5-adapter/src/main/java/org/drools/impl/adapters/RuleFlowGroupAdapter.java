package org.drools.impl.adapters;

import org.drools.runtime.rule.RuleFlowGroup;

public class RuleFlowGroupAdapter implements RuleFlowGroup {

    private final org.kie.api.runtime.rule.RuleFlowGroup delegate;

    public RuleFlowGroupAdapter(org.kie.api.runtime.rule.RuleFlowGroup delegate) {
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
