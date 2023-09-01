package org.drools.core.reteoo;

import org.drools.core.common.ReteEvaluator;

public class AsyncMessage {

    private final ReteEvaluator reteEvaluator;
    private final Object object;

    public AsyncMessage(ReteEvaluator reteEvaluator, Object object ) {
        this.reteEvaluator = reteEvaluator;
        this.object = object;
    }

    public ReteEvaluator getReteEvaluator() {
        return reteEvaluator;
    }

    public Object getObject() {
        return object;
    }
}
