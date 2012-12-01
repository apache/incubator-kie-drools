package org.drools.planner.core.domain.solution.cloner;

import org.drools.planner.api.domain.solution.cloner.SolutionCloner;
import org.drools.planner.core.solution.Solution;

public class DefaultSolutionCloner implements SolutionCloner {

    public <SolutionG extends Solution> SolutionG cloneSolution(SolutionG original) {
        return (SolutionG) original.cloneSolution();
    }

}
