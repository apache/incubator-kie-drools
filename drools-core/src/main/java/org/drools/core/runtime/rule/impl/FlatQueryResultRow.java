package org.drools.core.runtime.rule.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.kie.api.runtime.rule.FactHandle;
import org.kie.api.runtime.rule.QueryResultsRow;

public class FlatQueryResultRow
    implements
    QueryResultsRow {

    private Map<String, FactHandle> idFacthandleMap;
    private Map<String, Object> idResultMap;

    public FlatQueryResultRow(Map<String, FactHandle> idFactHandleMap,
                              Map<String, Object> idResultMap) {
        this.idFacthandleMap = idFactHandleMap;
        this.idResultMap = idResultMap;
    }

    @Override
    public Object get(String identifier) {
        return this.idResultMap.get( identifier );
    }

    @Override
    public FactHandle getFactHandle(String identifier) {
        return this.idFacthandleMap.get( identifier );
    }

    public List<String> getIdentifiers() {
        return new ArrayList<>(this.idFacthandleMap.keySet());
    }

}
