package org.drools.impl.adapters;

import org.drools.runtime.rule.QueryResults;
import org.drools.runtime.rule.QueryResultsRow;

import java.util.Iterator;

public class QueryResultsAdapter implements QueryResults {

    private final org.kie.api.runtime.rule.QueryResults delegate;

    public QueryResultsAdapter(org.kie.api.runtime.rule.QueryResults delegate) {
        this.delegate = delegate;
    }

    public String[] getIdentifiers() {
        return delegate.getIdentifiers();
    }

    public Iterator<QueryResultsRow> iterator() {
        return new QueryResultsRowIterator(delegate.iterator());
    }

    public int size() {
        return delegate.size();
    }

    private static final class QueryResultsRowIterator implements Iterator<QueryResultsRow> {

        private final Iterator<org.kie.api.runtime.rule.QueryResultsRow> delegate;

        private QueryResultsRowIterator(Iterator<org.kie.api.runtime.rule.QueryResultsRow> delegate) {
            this.delegate = delegate;
        }

        @Override
        public boolean hasNext() {
            return delegate.hasNext();
        }

        @Override
        public QueryResultsRow next() {
            return new QueryResultsRowAdapter(delegate.next());
        }

        @Override
        public void remove() {
            delegate.remove();
        }
    }
}
