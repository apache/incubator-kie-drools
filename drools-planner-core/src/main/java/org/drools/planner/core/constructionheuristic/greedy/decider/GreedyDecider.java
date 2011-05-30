package org.drools.planner.core.constructionheuristic.greedy.decider;

import org.drools.planner.core.constructionheuristic.greedy.GreedyStepScope;
import org.drools.planner.core.localsearch.LocalSearchStepScope;

public interface GreedyDecider {

    /**
     * Decides the next step
     * @param greedyStepScope never null
     */
    void decideNextStep(GreedyStepScope greedyStepScope);

}
