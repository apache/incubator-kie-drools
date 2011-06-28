package org.drools.planner.core.domain.entity;

import org.drools.planner.core.solution.Solution;

/**
 * Creates a difficultyWeight for a PlanningEntity.
 * A difficultyWeight estimates how hard is to plan a certain PlanningEntity.
 * Some algorithms benefit from planning on more difficult planning entities first or from focusing on them.
 */
public interface PlanningEntityDifficultyWeightFactory {

    /**
     * @param solution never null, the {@link Solution} to which the planningEntity belongs
     * @param planningEntity never null, the planningEntity to create the difficultyWeight for.
     * @return never null
     */
    Comparable createPlanningEntityDifficultyWeight(Solution solution, Object planningEntity);

}
