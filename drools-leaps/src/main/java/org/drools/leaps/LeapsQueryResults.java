package org.drools.leaps;

/*
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

import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import org.drools.QueryResult;
import org.drools.QueryResults;
import org.drools.WorkingMemory;
import org.drools.rule.Query;
import org.drools.spi.Tuple;

/** 
 *  
 * @author Alexander Bagerman
 * 
 */

public class LeapsQueryResults extends QueryResults {

    public LeapsQueryResults(final List results,
                             final Query query,
                             final WorkingMemory workingMemory) {
        super( results,
               query,
               workingMemory );
    }

    public QueryResult get(final int i) {
        if ( i > this.results.size() ) {
            throw new NoSuchElementException();
        }
        return new LeapsQueryResult( (Tuple) this.results.get( i ),
                                     this.workingMemory,
                                     this );
    }

    public Iterator iterator() {
        return new QueryResultsIterator( this.results.iterator() );
    }

    class QueryResultsIterator
        implements
        Iterator {
        private Iterator iterator;

        public QueryResultsIterator(final Iterator iterator) {
            this.iterator = iterator;
        }

        public boolean hasNext() {
            return this.iterator.hasNext();
        }

        public Object next() {
            return new LeapsQueryResult( (Tuple) this.iterator.next(),
                                         LeapsQueryResults.this.workingMemory,
                                         LeapsQueryResults.this );
        }

        public void remove() {
            this.iterator.remove();
        }

    }
}
