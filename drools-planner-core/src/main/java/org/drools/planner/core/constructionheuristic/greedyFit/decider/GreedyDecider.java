package org.drools.planner.core.constructionheuristic.greedyFit.decider;

import org.drools.planner.core.constructionheuristic.greedyFit.GreedyStepScope;

public interface GreedyDecider {

    /**
     * Decides the next step
     * @param greedyStepScope never null
     */
    void decideNextStep(GreedyStepScope greedyStepScope);

}
