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

    @Override
    public int hashCode() {
        return delegate.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof QueryResultsRowAdapter && delegate.equals(((QueryResultsRowAdapter)obj).delegate);
    }
}
