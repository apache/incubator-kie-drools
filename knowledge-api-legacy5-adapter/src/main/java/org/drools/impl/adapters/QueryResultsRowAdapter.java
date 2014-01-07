package org.drools.impl.adapters;

import org.drools.runtime.rule.FactHandle;
import org.drools.runtime.rule.QueryResultsRow;

public class QueryResultsRowAdapter implements QueryResultsRow {

    private final org.kie.api.runtime.rule.QueryResultsRow delegate;

    public QueryResultsRowAdapter(org.kie.api.runtime.rule.QueryResultsRow delegate) {
        this.delegate = delegate;
    }

    public Object get(String identifier) {
        return delegate.get(identifier);
    }

    public FactHandle getFactHandle(String identifier) {
        return new FactHandleAdapter(delegate.getFactHandle(identifier));
    }
}
