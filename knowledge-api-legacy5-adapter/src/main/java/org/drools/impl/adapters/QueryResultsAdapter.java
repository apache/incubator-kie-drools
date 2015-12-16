/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

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

    @Override
    public int hashCode() {
        return delegate.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof QueryResultsAdapter && delegate.equals(((QueryResultsAdapter)obj).delegate);
    }
}
