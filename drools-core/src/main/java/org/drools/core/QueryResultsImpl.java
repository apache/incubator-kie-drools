/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.drools.core;

import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;

import org.drools.base.rule.Declaration;
import org.drools.core.base.QueryRowWithSubruleIndex;
import org.drools.core.common.ReteEvaluator;
import org.kie.api.runtime.rule.QueryResults;
import org.kie.api.runtime.rule.QueryResultsRow;

/**
 * Returned QueryResults instance for a requested named query. from here you can iterate the returned data, or
 * get a specific row. All the available Declarations used in the query can also be accessed.
 */
public class QueryResultsImpl
    implements
    QueryResults {

    private Map<String, Declaration>[]        declarations;

    protected List<QueryRowWithSubruleIndex>  results;
    protected ReteEvaluator                   reteEvaluator;
    protected Declaration[] parameters;

    private String[] identifiers;

    public QueryResultsImpl(final List<QueryRowWithSubruleIndex> results,
                            final Map<String, Declaration>[] declarations,
                            final ReteEvaluator reteEvaluator,
                            final Declaration[] parameters) {
        this.results = results;
        this.reteEvaluator = reteEvaluator;
        this.declarations = declarations;
        this.parameters = parameters;

    }

    public Map<String, Declaration>[] getDeclarations() {
        return this.declarations;
    }

    public Declaration[] getParameters() {
        return this.parameters;
    }

    public Map<String, Declaration> getDeclarations(int subruleIndex) {
        if ( this.declarations == null || this.declarations.length == 0 ) {
            return Collections.emptyMap();
        } else {
            return this.declarations[subruleIndex];
        }
    }

    public QueryResultsRowImpl get(final int i) {
        if ( i > this.results.size() ) {
            throw new NoSuchElementException();
        }
        return new QueryResultsRowImpl( this.results.get( i ),
                                this.reteEvaluator,
                                this );
    }

    public int size() {
        return this.results.size();
    }

    public String[] getIdentifiers() {
        if ( identifiers != null ) {
            return identifiers;
        }
        Declaration[] parameters = getParameters();

        Set<String> idSet  = new HashSet<>();
        for ( Declaration declr : parameters ) {
            idSet.add( declr.getIdentifier() );
        }

        for ( Declaration declr : getDeclarations(0).values() ) {
            idSet.add(declr.getIdentifier());
        }

        identifiers = idSet.toArray(new String[idSet.size()]);
        return identifiers;
    }

    public Iterator<QueryResultsRow> iterator() {
        return new QueryResultsIterator( results.iterator() );
    }

    private class QueryResultsIterator
        implements
        Iterator<QueryResultsRow> {
        private Iterator<QueryRowWithSubruleIndex> iterator;

        public QueryResultsIterator(final Iterator<QueryRowWithSubruleIndex> iterator) {
            this.iterator = iterator;
        }

        public boolean hasNext() {
            return this.iterator.hasNext();
        }

        public QueryResultsRow next() {
            return new QueryResultsRowImpl( this.iterator.next(),
                                    QueryResultsImpl.this.reteEvaluator,
                                    QueryResultsImpl.this );
        }

        public void remove() {
            this.iterator.remove();
        }

    }
}
