package org.drools.model.view;

import org.drools.model.Argument;
import org.drools.model.QueryDef;
import org.drools.model.Variable;

public class QueryCallViewItemImpl implements QueryCallViewItem {

    private final QueryDef query;
    private final boolean open;
    private final Argument<?>[] arguments;

    public QueryCallViewItemImpl( QueryDef query, boolean open, Argument<?>... arguments ) {
        this.query = query;
        this.open = open;
        this.arguments = arguments;
    }

    @Override
    public QueryDef getQuery() {
        return query;
    }

    @Override
    public Argument<?>[] getArguments() {
        return arguments;
    }

    @Override
    public boolean isOpen() {
        return open;
    }

    @Override
    public Variable getFirstVariable() {
        return null;
    }

    @Override
    public Variable<?>[] getVariables() {
        throw new UnsupportedOperationException();
    }
}
