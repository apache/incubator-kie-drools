/**
 * Copyright 2005 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import org.drools.rule.Declaration;
import org.drools.rule.Query;

/**
 * Returned QueryResults instance for a requested named query. from here you can iterate the returned data, or
 * get a specific row. All the available Declarations used in the query can also be accessed.
 *
 */
public class QueryResults
    implements
    Iterable<QueryResult> {
    private Map<String, Declaration> declarations;

    protected List<FactHandle[]>     results;
    protected WorkingMemory          workingMemory;

    public QueryResults() {
    }

    public QueryResults(final List<FactHandle[]> results,
                        final Declaration[] declArray,
                        final WorkingMemory workingMemory) {
        this.results = results;
        this.workingMemory = workingMemory;

        if ( declArray.length > 0 ) {
            final Map<String, Declaration> map = new HashMap<String, Declaration>( declArray.length );
            for ( int i = 0, length = declArray.length; i < length; i++ ) {
                map.put( declArray[i].getIdentifier(),
                         declArray[i] );
            }
            this.declarations = map;
        } else {
            this.declarations = Collections.emptyMap();
        }
    }

    public QueryResult get(final int i) {
        if ( i > this.results.size() ) {
            throw new NoSuchElementException();
        }
        return new QueryResult( this.results.get( i ),
                                this.workingMemory,
                                this );
    }

    /**
     * Returns an Iterator for the results.
     * 
     * @return
     */
    public Iterator<QueryResult> iterator() {
        return new QueryResultsIterator( this.results.iterator() );
    }

    public Map<String, Declaration> getDeclarations() {
        return this.declarations;
    }

    /**
     * The results size
     * @return
     */
    public int size() {
        return this.results.size();
    }

    private class QueryResultsIterator
        implements
        Iterator<QueryResult> {
        private Iterator<FactHandle[]> iterator;

        public QueryResultsIterator(final Iterator<FactHandle[]> iterator) {
            this.iterator = iterator;
        }

        public boolean hasNext() {
            return this.iterator.hasNext();
        }

        public QueryResult next() {
            return new QueryResult( this.iterator.next(),
                                    QueryResults.this.workingMemory,
                                    QueryResults.this );
        }

        public void remove() {
            this.iterator.remove();
        }

    }
}
