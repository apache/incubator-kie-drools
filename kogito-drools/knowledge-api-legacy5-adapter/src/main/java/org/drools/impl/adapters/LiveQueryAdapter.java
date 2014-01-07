package org.drools.impl.adapters;

import org.drools.runtime.rule.LiveQuery;

public class LiveQueryAdapter implements LiveQuery {

    private final org.kie.api.runtime.rule.LiveQuery delegate;

    public LiveQueryAdapter(org.kie.api.runtime.rule.LiveQuery delegate) {
        this.delegate = delegate;
    }

    public void close() {
        delegate.close();
    }
}
