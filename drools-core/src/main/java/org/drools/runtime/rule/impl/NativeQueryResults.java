package org.drools.runtime.rule.impl;

import java.util.Iterator;
import java.util.Map;

import org.drools.command.Command;
import org.drools.result.GenericResult;
import org.drools.rule.Declaration;
import org.drools.runtime.rule.QueryResultsRow;
import org.drools.runtime.rule.QueryResults;

public class NativeQueryResults
    implements
	QueryResults {
    
    private org.drools.QueryResults results;
    private Command<?> command;

    public NativeQueryResults(org.drools.QueryResults results) {
        this.results = results;
    }

    public String[] getIdentifiers() {
        return (String[]) this.results.getDeclarations().keySet().toArray( new String[this.results.getDeclarations().size()] );
    }
    
    public Map<String, Declaration> getDeclarations() {
        return this.results.getDeclarations();
    }

    public int size() {
        return this.results.size();
    }
    
    public Iterator<QueryResultsRow> iterator() {
        return new QueryResultsIterator( this.results.iterator() );
    }

    public void setCommand(Command<?> command){
	this.command = command;
    }

    public Command<?> getCommand(){
        return this.command;
    }

    private class QueryResultsIterator
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
            return new NativeQueryResultRow( (org.drools.QueryResult) this.iterator.next() );
        }

        public void remove() {
            this.iterator.remove();
        }

    }

}
