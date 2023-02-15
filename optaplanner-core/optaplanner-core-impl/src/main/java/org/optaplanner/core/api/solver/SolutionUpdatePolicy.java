package org.optaplanner.core.api.solver;

/**
 * To fully de-normalize a planning solution freshly loaded from persistent storage,
 * two operations need to happen:
 *
 * <ul>
 * <li>Variable listeners need to run,
 * reading the state of all entities and computing values for their shadow variables.</li>
 * <li>Score needs to be calculated and stored on the planning solution.</li>
 * </ul>
 *
 * <p>
 * Each of these operations has its own performance cost,
 * and for certain use cases, only one of them may be actually necessary.
 * Advanced users therefore get a choice of which to perform.
 *
 * <p>
 * If unsure, pick {@link #UPDATE_ALL}.
 *
 */
public enum SolutionUpdatePolicy {

    /**
     * Combines the effects of {@link #UPDATE_SCORE_ONLY} and {@link #UPDATE_SHADOW_VARIABLES_ONLY},
     * in effect fully updating the solution.
     */
    UPDATE_ALL(true, true),
    /**
     * Calculates the score based on the entities in the solution,
     * and writes it back to the solution.
     * Does not trigger shadow variables;
     * if score calculation requires shadow variable values,
     * {@link NullPointerException} is likely to be thrown.
     * To avoid this, use {@link #UPDATE_ALL} instead.
     */
    UPDATE_SCORE_ONLY(true, false),
    /**
     * Runs variable listeners on all planning entities and problem facts,
     * updates shadow variables.
     * Does not update score;
     * the solution will keep the current score, even if it is stale or null.
     * To avoid this, use {@link #UPDATE_ALL} instead.
     */
    UPDATE_SHADOW_VARIABLES_ONLY(false, true),
    /**
     * Does not run anything.
     * Improves performance during {@link SolutionManager#explain(Object, SolutionUpdatePolicy)},
     * where the user can guarantee that the solution is already up to date.
     * Otherwise serves no purpose.
     */
    NO_UPDATE(false, false);

    private final boolean scoreUpdateEnabled;
    private final boolean shadowVariableUpdateEnabled;

    SolutionUpdatePolicy(boolean scoreUpdateEnabled, boolean shadowVariableUpdateEnabled) {
        this.scoreUpdateEnabled = scoreUpdateEnabled;
        this.shadowVariableUpdateEnabled = shadowVariableUpdateEnabled;
    }

    public boolean isScoreUpdateEnabled() {
        return scoreUpdateEnabled;
    }

    public boolean isShadowVariableUpdateEnabled() {
        return shadowVariableUpdateEnabled;
    }
}
