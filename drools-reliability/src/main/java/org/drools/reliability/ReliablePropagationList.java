package org.drools.reliability;

import java.io.Serializable;

import org.drools.core.common.ReteEvaluator;
import org.drools.core.phreak.SynchronizedPropagationList;

public class ReliablePropagationList extends SynchronizedPropagationList implements Serializable {
    public ReliablePropagationList(ReteEvaluator reteEvaluator) {
        super(reteEvaluator);
    }

    public ReliablePropagationList(ReteEvaluator reteEvaluator, ReliablePropagationList originalList) {
        super(reteEvaluator);
        this.head = originalList.head;
        this.tail = originalList.tail;
    }
}
