package org.drools.planner.core.domain.variable;

import org.drools.planner.core.solution.Solution;

/**
 * Creates a strengthWeight for a planning variable.
 * A strengthWeight estimates how strong a planning variable value is.
 * Some algorithms benefit from planning on weaker planning variable values first or from focusing on them.
 */
public interface PlanningVariableStrengthWeightFactory {

    /**
     * @param solution never null, the {@link Solution} to which the planningEntity belongs
     * @param planningEntity never null, the planningEntity which has the variable
     * @param variablePropertyName never null, the planning variable to create the strengthWeight for
     * @return never null
     */
    Comparable createStrengthWeight(Solution solution, Object planningEntity, String variablePropertyName);

}
