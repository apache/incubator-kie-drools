package org.drools.planner.core.constructionheuristic.greedy.decider;

import org.drools.planner.core.constructionheuristic.greedy.GreedyStepScope;

public interface GreedyDecider {

    /**
     * Decides the next step
     * @param greedyStepScope never null
     */
    void decideNextStep(GreedyStepScope greedyStepScope);

}
