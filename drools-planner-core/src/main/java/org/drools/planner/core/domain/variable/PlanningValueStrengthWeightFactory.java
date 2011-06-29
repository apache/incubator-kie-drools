package org.drools.planner.core.domain.variable;

import org.drools.planner.core.solution.Solution;

/**
 * Creates a strengthWeight for a planning variable value.
 * A strengthWeight estimates how strong a planning value is.
 * Some algorithms benefit from planning on weaker planning values first or from focusing on them.
 */
public interface PlanningValueStrengthWeightFactory {

    /**
     * @param solution never null, the {@link Solution} to which the planningEntity belongs
     * @param planningValue never null, the planning value to create the strengthWeight for
     * @return never null
     */
    Comparable createStrengthWeight(Solution solution, Object planningValue);

}
