package org.drools.model.view;

import org.drools.model.DomainClassMetadata;
import org.drools.model.Variable;
import org.drools.model.constraints.ReactivitySpecs;

import static java.util.UUID.randomUUID;

public abstract class AbstractExprViewItem<T> implements ExprViewItem<T>  {
    private final String exprId;

    private final Variable<T> var;

    private ReactivitySpecs reactivitySpecs = ReactivitySpecs.EMPTY;
    private String[] watchedProps;

    private boolean queryExpression;

    public AbstractExprViewItem(Variable<T> var) {
        this(randomUUID().toString(), var);
    }

    public AbstractExprViewItem(String exprId, Variable<T> var) {
        this.exprId = exprId;
        this.var = var;
    }

    @Override
    public Variable<T> getFirstVariable() {
        return var;
    }

    public AbstractExprViewItem<T> reactOn( String... props ) {
        return reactOn(null, props);
    }

    public AbstractExprViewItem<T> reactOn( DomainClassMetadata metadata, String... props ) {
        this.reactivitySpecs = new ReactivitySpecs( metadata, props );
        return this;
    }

    public AbstractExprViewItem<T> watch(String... props) {
        this.watchedProps = props;
        return this;
    }

    @Override
    public String getExprId() {
        return exprId;
    }

    public ReactivitySpecs getReactivitySpecs() {
        return reactivitySpecs;
    }

    public String[] getWatchedProps() {
        return watchedProps;
    }

    public boolean isQueryExpression() {
        return queryExpression;
    }

    public void setQueryExpression( boolean queryExpression ) {
        this.queryExpression = queryExpression;
    }
}
