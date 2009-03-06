package org.drools.runtime.rule.impl;

import java.util.List;
import java.util.Map;

import org.drools.runtime.rule.QueryResultsRow;

public class FlatQueryResultRow
    implements
    QueryResultsRow {
    Map<String, Integer> identifiers;
    private List result;

    public FlatQueryResultRow(Map<String, Integer> identifiers, List result) {
        this.identifiers = identifiers;
        this.result = result;
    }

    public Object get(String identifier) {
        return this.result.get( identifiers.get( identifier ) );
    }


}
