package org.drools.model.impl;

import org.drools.model.Query;
import org.drools.model.QueryDef;
import org.drools.model.view.AbstractExprViewItem;
import org.drools.model.view.ViewItemBuilder;

import static org.drools.model.impl.RuleBuilder.DEFAULT_PACKAGE;

public abstract class QueryDefImpl implements QueryDef {

    private final ViewBuilder viewBuilder;

    private final String pkg;
    private final String name;

    public QueryDefImpl( ViewBuilder viewBuilder, String name ) {
        this(viewBuilder, DEFAULT_PACKAGE, name);
    }

    public QueryDefImpl( ViewBuilder viewBuilder, String pkg, String name ) {
        this.viewBuilder = viewBuilder;
        this.pkg = pkg;
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getPackage() {
        return pkg;
    }

    @Override
    public Query build( ViewItemBuilder... viewItemBuilders ) {
        return new QueryImpl( this, viewBuilder.apply( asQueryExpresssion( viewItemBuilders ) ) );
    }

    private static ViewItemBuilder[] asQueryExpresssion( ViewItemBuilder[] viewItemBuilders ) {
        for (ViewItemBuilder item : viewItemBuilders) {
            if (item instanceof AbstractExprViewItem ) {
                ( (AbstractExprViewItem) item ).setQueryExpression( true );
            }
        }
        return viewItemBuilders;
    }
}
