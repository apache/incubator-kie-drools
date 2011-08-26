package org.drools.planner.core.domain.variable;

import java.util.Collection;

import org.drools.planner.core.solution.director.SolutionDirector;

public interface PlanningValueRangeDescriptor {

    Collection<?> extractValues(SolutionDirector solutionDirector, Object planningEntity);

    boolean isValuesCacheable();

}
