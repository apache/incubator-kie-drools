package org.drools.planner.core.constructionheuristic.greedyFit.decider;

import org.drools.planner.core.constructionheuristic.greedyFit.GreedyFitStepScope;
import org.drools.planner.core.constructionheuristic.greedyFit.event.GreedySolverPhaseLifecycleListener;

public interface GreedyDecider extends GreedySolverPhaseLifecycleListener {

    /**
     * Decides the next step
     * @param greedyFitStepScope never null
     */
    void decideNextStep(GreedyFitStepScope greedyFitStepScope);

}
