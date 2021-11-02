package org.drools.core.base;

import java.util.ArrayList;
import java.util.List;

import org.drools.core.common.InternalFactHandle;
import org.drools.core.common.ReteEvaluator;
import org.drools.core.definitions.rule.impl.RuleImpl;
import org.drools.core.reteoo.LeftTuple;
import org.drools.core.reteoo.LeftTupleNode;
import org.drools.core.reteoo.QueryTerminalNode;

public abstract class AbstractQueryViewListener implements InternalViewChangedEventListener {

    protected List<Object> results;

    public AbstractQueryViewListener() {
        this.results = new ArrayList<Object>(250);
    }

    public List<? extends Object> getResults() {
        return this.results;
    }

    public abstract InternalFactHandle getHandle(InternalFactHandle originalHandle);

    public void rowAdded(RuleImpl rule, LeftTuple tuple, ReteEvaluator reteEvaluator) {
        InternalFactHandle[] handles = new InternalFactHandle[((LeftTupleNode)tuple.getTupleSink()).getObjectCount()];
        LeftTuple entry = (LeftTuple) tuple.skipEmptyHandles();

        // Add all the FactHandles
        int i = handles.length-1;
        while ( entry != null ) {
            InternalFactHandle handle = entry.getFactHandle();
            handles[i--] = getHandle(handle);
            entry = entry.getParent();
        }

        QueryTerminalNode node = tuple.getTupleSink();
        this.results.add( new QueryRowWithSubruleIndex(handles, node.getSubruleIndex()) );
    }

    public void rowRemoved( RuleImpl rule, LeftTuple tuple, ReteEvaluator reteEvaluator ) {
    }

    public void rowUpdated( RuleImpl rule, LeftTuple tuple, ReteEvaluator reteEvaluator ) {
    }

}
