package org.drools.runtime.rule.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.drools.rule.Declaration;
import org.drools.runtime.rule.FactHandle;
import org.drools.runtime.rule.QueryResults;
import org.drools.runtime.rule.QueryResultsRow;
import org.drools.xml.jaxb.util.JaxbFlatQueryResultsAdapter;

@XmlAccessorType( XmlAccessType.FIELD )
public class FlatQueryResults
    implements
    QueryResults {
    private HashMap<String, Integer> identifiers;
    @XmlJavaTypeAdapter(JaxbFlatQueryResultsAdapter.class)
	@XmlElement(name="results")
    private ArrayList<ArrayList<Object>>           results;
    @XmlJavaTypeAdapter(JaxbFlatQueryResultsAdapter.class)
	@XmlElement(name="fact-handles")
    private ArrayList<ArrayList<FactHandle>> factHandles;
    
    public FlatQueryResults() {
    	
	}

    public FlatQueryResults(HashMap<String, Integer> identifiers,
                            ArrayList<ArrayList<Object>> results,
                            ArrayList<ArrayList<FactHandle>> factHandles) {
        this.identifiers = identifiers;
        this.results = results;
        this.factHandles = factHandles;
    }
    
    public FlatQueryResults(org.drools.QueryResults results) {
        Declaration[] declrs = results.getDeclarations().values().toArray( new Declaration[results.getDeclarations().size()] );
        this.results = new ArrayList<ArrayList<Object>>( results.size() );
        this.factHandles = new ArrayList<ArrayList<FactHandle>> ( results.size() );

        int length = declrs.length;

        for ( org.drools.QueryResult result : results ) {
        	ArrayList<Object> row = new ArrayList<Object>();
        	ArrayList<FactHandle> rowHandle = new ArrayList<FactHandle>();

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
        return identifiers.keySet().toArray( new String[identifiers.size()] );
    }

    public int size() {
        return this.results.size();
    }

    public Iterator<QueryResultsRow> iterator() {
        return new QueryResultsIterator( identifiers,
                                         this.results.iterator(),
                                         this.factHandles.iterator() );
    }

    private class QueryResultsIterator implements Iterator<QueryResultsRow> {
        private HashMap<String, Integer> identifiers;
        private Iterator<ArrayList<Object>> iterator;
        private Iterator<ArrayList<FactHandle>> handleIterator;

        public QueryResultsIterator(HashMap<String, Integer> identifiers,
                                    final Iterator<ArrayList<Object>> iterator,
                                    final Iterator<ArrayList<FactHandle>> handleIterator) {
            this.identifiers = identifiers;
            this.iterator = iterator;
            this.handleIterator = handleIterator;
        }

        public boolean hasNext() {
            return this.iterator.hasNext();
        }

        public QueryResultsRow next() {
            return new FlatQueryResultRow( identifiers,
                                           this.iterator.next(),
                                           this.handleIterator.next(),
                                           size() );
        }

        public void remove() {
            this.iterator.remove();
        }

    }

}
