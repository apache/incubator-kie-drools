package org.drools.planner.core.constructionheuristic.greedyFit.decider;

import org.drools.planner.core.constructionheuristic.greedyFit.GreedyFitStepScope;

public interface GreedyDecider {

    /**
     * Decides the next step
     * @param greedyFitStepScope never null
     */
    void decideNextStep(GreedyFitStepScope greedyFitStepScope);

}
