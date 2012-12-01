package org.drools.planner.api.domain.solution.cloner;

import org.drools.planner.core.solution.Solution;

/**
 * Clones a {@link Solution} during planning.
 * Used to remember the state of a good {@link Solution} so it can be recalled at a later then
 * when the original {@link Solution} is already modified.
 * Also used in population based heuristics to increase or repopulate the population.
 */
public interface SolutionCloner {

    /**
     * The returned {@link Solution} clone must fulfill these requirements:
     * <ul>
     * <li>The clone must represent the same planning problem.
     * Usually it reuses the same instances of the problem facts and problem fact collections as the {@code original}.
     * </li>
     * <li>The clone must use different, cloned instances of the entities and entity collections.
     * If a cloned entity changes, the original must remain unchanged.
     * If an entity is added or removed in a cloned {@link Solution},
     * the original {@link Solution} must remain unchanged.</li>
     * </ul>
     * @param original never null, the original {@link Solution}
     * @return never null, the cloned {@link Solution}
     */
    <SolutionG extends Solution> SolutionG cloneSolution(SolutionG original);

}
