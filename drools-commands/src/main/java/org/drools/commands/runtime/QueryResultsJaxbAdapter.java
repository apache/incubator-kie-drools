package org.drools.commands.runtime;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import org.drools.core.QueryResultsImpl;


public class QueryResultsJaxbAdapter extends XmlAdapter<QueryResultsImpl, FlatQueryResults>{

    @Override
    public QueryResultsImpl marshal(FlatQueryResults v) throws Exception {
        return null;
    }

    @Override
    public FlatQueryResults unmarshal(QueryResultsImpl v) throws Exception {
        return new FlatQueryResults(v);
    }


}
