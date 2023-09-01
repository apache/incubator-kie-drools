package org.drools.core.concurrent;

import org.drools.core.common.ActivationsManager;

public class SequentialGroupEvaluator extends AbstractGroupEvaluator {

    public SequentialGroupEvaluator(ActivationsManager activationsManager) {
        super(activationsManager);
    }
}

