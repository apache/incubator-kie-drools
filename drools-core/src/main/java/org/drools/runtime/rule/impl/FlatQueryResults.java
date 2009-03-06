package org.drools.runtime.rule.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.drools.rule.Declaration;
import org.drools.runtime.rule.QueryResultsRow;
import org.drools.runtime.rule.QueryResults;

public class FlatQueryResults
    implements
    QueryResults {
    private Map<String, Integer> identifiers;
    private List<List>           results;

    public FlatQueryResults(Map<String, Integer> identifiers,
                            List<List> results) {
        this.identifiers = identifiers;
        this.results = results;
    }

    public FlatQueryResults(org.drools.QueryResults results) {
        Declaration[] declrs = (Declaration[]) results.getDeclarations().values().toArray( new Declaration[results.getDeclarations().size()] );
        this.results = new ArrayList<List>( results.size() );

        int length = declrs.length;

        for ( org.drools.QueryResult result : results ) {
            List<Object> row = new ArrayList<Object>();

            for ( int i = 0; i < length; i++ ) {
                Declaration declr = declrs[i];
                row.add( result.get( declr ) );
            }

            this.results.add( row );
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
                                         this.results.iterator() );
    }

    private class QueryResultsIterator
        implements
        Iterator {
        private Map<String, Integer> identifiers;
        private Iterator             iterator;

        public QueryResultsIterator(Map<String, Integer> identifiers,
                                    final Iterator iterator) {
            this.identifiers = identifiers;
            this.iterator = iterator;
        }

        public boolean hasNext() {
            return this.iterator.hasNext();
        }

        public Object next() {
            return new FlatQueryResultRow( identifiers,
                                           (List) this.iterator.next() );
        }

        public void remove() {
            this.iterator.remove();
        }

    }

}
