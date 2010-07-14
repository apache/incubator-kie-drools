package org.drools.runtime.rule.impl;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import org.drools.runtime.rule.QueryResults;


public class QueryResultsJaxbAdapter extends XmlAdapter<NativeQueryResults, FlatQueryResults>{

    @Override
    public NativeQueryResults marshal(FlatQueryResults v) throws Exception {
        return null;
    }

    @Override
    public FlatQueryResults unmarshal(NativeQueryResults v) throws Exception {
        return new FlatQueryResults(((NativeQueryResults)v).getResults());
    }


}
