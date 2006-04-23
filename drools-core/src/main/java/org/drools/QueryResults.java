package org.drools;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import org.drools.rule.Declaration;
import org.drools.rule.Query;
import org.drools.spi.Tuple;

public class QueryResults {
    private Query         query;

    private Map           declarations;

    protected List          results;
    protected WorkingMemory workingMemory;

    public QueryResults(List results,
                        Query query,
                        WorkingMemory workingMemory) {
        this.results = results;
        this.query = query;
        this.workingMemory = workingMemory;
    }

    public QueryResult get(int i) {
        if ( i > this.results.size() ) {
            throw new NoSuchElementException();
        }
        return new QueryResult( (Tuple) this.results.get( i ),
                                workingMemory,
                                this );
    }

    public Iterator iterator() {
        return new QueryResultsIterator( this.results.iterator() );
    }

    public Map getDeclarations() {

        Declaration[] declarations = this.query.getDeclarations();
        Map map = new HashMap( declarations.length );
        for ( int i = 0, length = declarations.length; i < length; i++ ) {
            map.put( declarations[i].getIdentifier(),
                     declarations );
        }
        this.declarations = map;

        return this.declarations;
    }

    public int size() {
        return this.results.size();
    }
    
    class QueryResultsIterator implements Iterator {
        private Iterator iterator;
        
        public QueryResultsIterator(Iterator iterator) {
            this.iterator = iterator;
        }

        public boolean hasNext() {
            return this.iterator.hasNext();
        }

        public Object next() {
            return new QueryResult( ( Tuple ) this.iterator.next(), QueryResults.this.workingMemory, QueryResults.this );
        }

        public void remove() {
            this.iterator.remove();
        }
        
    }
}
