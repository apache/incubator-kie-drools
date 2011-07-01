package org.drools.planner.core.solver;

import org.drools.FactHandle;
import org.drools.WorkingMemory;
import org.drools.planner.core.Solver;
import org.drools.planner.core.solution.Solution;

/**
 * A PlanningFactChange represents a change in 1 or more planning facts of a solution.
 * Planning facts used by a {@link Solver} must not be changed while it is solving,
 * but by scheduling this command to the {@link Solver}, you can change them when the time is right.
 * </p>
 * Any change should be done on the planning facts and planning entities referenced by the {@link Solver}.
 * Note that the {@link Solver} clones a {@link Solution} at will.
 * On that change it should also notify the {@link WorkingMemory} accordingly.
 */
public interface PlanningFactChange {

    /**
     * Does the Move and updates the {@link Solution} and its {@link WorkingMemory} accordingly.
     * When the solution is modified, the {@link WorkingMemory}'s {@link FactHandle}s should be correctly notified,
     * otherwise the score(s) calculated will be corrupted.
     * @param solution never null, the solution which contains the planning facts (and planning entities) to change
     * @param workingMemory never null, the {@link WorkingMemory} that needs to get notified of the changes.
     */
    void doChange(Solution solution, WorkingMemory workingMemory);
    
}
