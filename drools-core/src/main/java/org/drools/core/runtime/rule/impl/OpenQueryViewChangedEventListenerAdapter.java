package org.drools.core.runtime.rule.impl;

import java.util.List;

import org.drools.core.base.InternalViewChangedEventListener;
import org.drools.core.common.ReteEvaluator;
import org.drools.base.definitions.rule.impl.RuleImpl;
import org.drools.core.reteoo.LeftTuple;
import org.kie.api.runtime.rule.ViewChangedEventListener;

public class OpenQueryViewChangedEventListenerAdapter
    implements
    InternalViewChangedEventListener {

    private ViewChangedEventListener viewEventListener;
    
    public OpenQueryViewChangedEventListenerAdapter(ViewChangedEventListener viewEventListener) {
        this.viewEventListener = viewEventListener;
    }

    public void rowAdded(RuleImpl rule, LeftTuple tuple, ReteEvaluator reteEvaluator ) {
        RowAdapter rowAdapter = new RowAdapter(rule, tuple);
        tuple.setContextObject( rowAdapter );
        this.viewEventListener.rowInserted( rowAdapter );
    }

    public void rowRemoved(RuleImpl rule, LeftTuple tuple, ReteEvaluator reteEvaluator ) {
        RowAdapter rowAdapter = (RowAdapter) tuple.getContextObject();
        this.viewEventListener.rowDeleted( rowAdapter );
    }

    public void rowUpdated(RuleImpl rule, LeftTuple tuple, ReteEvaluator reteEvaluator ) {
        RowAdapter rowAdapter = (RowAdapter) tuple.getContextObject();
        this.viewEventListener.rowUpdated( rowAdapter );
    }

    public List< ? extends Object> getResults() {
        throw new UnsupportedOperationException(getClass().getCanonicalName()+" does not support the getResults() method.");
    }

}
