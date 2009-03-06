package org.drools.runtime.rule;

import java.util.Iterator;

public interface QueryResults extends Iterable<QueryResultsRow> {
    String[] getIdentifiers();
    
    Iterator<QueryResultsRow> iterator();
    
    int size();
}
