package org.drools.result;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.drools.runtime.rule.QueryResults;

public class QueryResult extends AbstractResult implements GenericResult {

    private QueryResults results;

    public QueryResult( String identifier, QueryResults results ){
        super( identifier );
        this.results = results;
    }

    public QueryResults getResults(){
        return this.results;
    }

    public Object getValue(){
	return this.results;
    }
}
