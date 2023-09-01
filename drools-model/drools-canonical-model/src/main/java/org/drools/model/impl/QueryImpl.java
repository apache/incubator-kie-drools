package org.drools.model.impl;

import org.drools.model.Query;
import org.drools.model.QueryDef;
import org.drools.model.Variable;
import org.drools.model.View;

public class QueryImpl implements Query, ModelComponent {

    private final QueryDef queryDef;
    private final View view;

    public QueryImpl( QueryDef queryDef, View view ) {
        this.queryDef = queryDef;
        this.view = view;
    }

    @Override
    public View getView() {
        return view;
    }

    @Override
    public String getName() {
        return queryDef.getName();
    }

    @Override
    public String getPackage() {
        return queryDef.getPackage();
    }

    @Override
    public Variable<?>[] getArguments() {
        return queryDef.getArguments();
    }

    @Override
    public boolean isEqualTo( ModelComponent o ) {
        if ( this == o ) return true;
        if ( o == null || getClass() != o.getClass() ) return false;

        QueryImpl query = ( QueryImpl ) o;

        return ModelComponent.areEqualInModel( view, query.view ) && ModelComponent.areEqualInModel( queryDef, query.queryDef );
    }
}
