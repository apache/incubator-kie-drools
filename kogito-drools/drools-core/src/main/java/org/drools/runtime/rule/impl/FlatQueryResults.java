package org.drools.runtime.rule.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.drools.rule.Declaration;
import org.drools.runtime.rule.FactHandle;
import org.drools.runtime.rule.QueryResultsRow;
import org.drools.runtime.rule.QueryResults;

public class FlatQueryResults
    implements
    QueryResults {
    private Map<String, Integer> identifiers;
    private List<List>           results;
    private List<List<FactHandle>> factHandles;

    public FlatQueryResults(Map<String, Integer> identifiers,
                            List<List> results,
                            List<List<FactHandle>> factHandles) {
        this.identifiers = identifiers;
        this.results = results;
        this.factHandles = factHandles;
    }

    public FlatQueryResults(org.drools.QueryResults results) {
        Declaration[] declrs = (Declaration[]) results.getDeclarations().values().toArray( new Declaration[results.getDeclarations().size()] );
        this.results = new ArrayList<List>( results.size() );
        this.factHandles = new ArrayList<List<FactHandle>> ( results.size() );

        int length = declrs.length;

        for ( org.drools.QueryResult result : results ) {
            List<Object> row = new ArrayList<Object>();
            List<FactHandle> rowHandle = new ArrayList<FactHandle>();

            for ( int i = 0; i < length; i++ ) {
                Declaration declr = declrs[i];
                row.add( result.get( declr ) );
                rowHandle.add( result.getFactHandle( declr ) );
            }

            this.results.add( row );
            this.factHandles.add( rowHandle );
        }

        identifiers = new HashMap<String, Integer>( length );
        for ( int i = 0; i < length; i++ ) {
            identifiers.put( declrs[i].getIdentifier(),
                             i );
        }

    }

    public String[] getIdentifiers() {
        return (String[]) identifiers.keySet().toArray( new String[identifiers.size()] );
    }

    public int size() {
        return this.results.size();
    }

    public Iterator<QueryResultsRow> iterator() {
        return new QueryResultsIterator( identifiers,
                                         this.results.iterator(),
                                         this.factHandles.iterator() );
    }

    private class QueryResultsIterator
        implements
        Iterator {
        private Map<String, Integer> identifiers;
        private Iterator             iterator;
        private Iterator<List<FactHandle>> handleIterator;

        public QueryResultsIterator(Map<String, Integer> identifiers,
                                    final Iterator iterator,
                                    final Iterator<List<FactHandle>> handleIterator) {
            this.identifiers = identifiers;
            this.iterator = iterator;
            this.handleIterator = handleIterator;
        }

        public boolean hasNext() {
            return this.iterator.hasNext();
        }

        public Object next() {
            return new FlatQueryResultRow( identifiers,
                                           (List) this.iterator.next(),
                                           (List<FactHandle>) this.handleIterator.next() );
        }

        public void remove() {
            this.iterator.remove();
        }

    }

}
