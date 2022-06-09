package org.optaplanner.test.impl.score.buildin.bendablelong;

import org.optaplanner.core.api.domain.solution.PlanningSolution;
import org.optaplanner.core.api.score.buildin.bendablelong.BendableLongScore;
import org.optaplanner.core.api.score.stream.ConstraintStream;
import org.optaplanner.core.api.solver.SolverFactory;
import org.optaplanner.core.impl.score.buildin.BendableLongScoreDefinition;
import org.optaplanner.test.api.score.stream.ConstraintVerifier;
import org.optaplanner.test.impl.score.AbstractScoreVerifier;

/**
 * To assert the constraints of a {@link SolverFactory}
 * that uses a {@link BendableLongScore}.
 * If you're using {@link ConstraintStream}s, use {@link ConstraintVerifier} instead.
 *
 * @param <Solution_> the solution type, the class with the {@link PlanningSolution} annotation
 * @deprecated Score DRL is deprecated and will be removed in a future major version of OptaPlanner.
 *             See <a href="https://www.optaplanner.org/learn/drl-to-constraint-streams-migration.html">DRL to Constraint
 *             Streams migration recipe</a>.
 */
@Deprecated(forRemoval = true)
public class BendableLongScoreVerifier<Solution_> extends AbstractScoreVerifier<Solution_> {

    protected final int hardLevelsSize;

    /**
     * @param solverFactory never null, the {@link SolverFactory} of which you want to test the constraints.
     */
    public BendableLongScoreVerifier(SolverFactory<Solution_> solverFactory) {
        super(solverFactory, BendableLongScore.class);
        hardLevelsSize = ((BendableLongScoreDefinition) scoreDirectorFactory.getScoreDefinition()).getHardLevelsSize();
    }

    /**
     * Assert that the constraint of {@link PlanningSolution}
     * has the expected weight for that score level.
     *
     * @param constraintName never null, the name of the constraint
     * @param hardLevel {@code 0 <= hardLevel <} {@code hardLevelSize}.
     *        The {@code scoreLevel} is {@code hardLevel} for hard levels and {@code softLevel + hardLevelSize} for soft levels.
     * @param expectedWeight the total weight for all matches of that 1 constraint
     * @param solution never null, the actual {@link PlanningSolution}
     */
    public void assertHardWeight(String constraintName, int hardLevel, long expectedWeight, Solution_ solution) {
        assertHardWeight(null, constraintName, hardLevel, expectedWeight, solution);
    }

    /**
     * Assert that the constraint of {@link PlanningSolution}
     * has the expected weight for that score level.
     *
     * @param constraintPackage sometimes null.
     *        When null, {@code constraintName} for the {@code scoreLevel} must be unique.
     * @param constraintName never null, the name of the constraint
     * @param hardLevel {@code 0 <= hardLevel <} {@code hardLevelSize}.
     *        The {@code scoreLevel} is {@code hardLevel} for hard levels and {@code softLevel + hardLevelSize} for soft levels.
     * @param expectedWeight the total weight for all matches of that 1 constraint
     * @param solution never null, the actual {@link PlanningSolution}
     */
    public void assertHardWeight(String constraintPackage, String constraintName,
            int hardLevel, long expectedWeight,
            Solution_ solution) {
        assertWeight(constraintPackage, constraintName,
                hardLevel, Long.valueOf(expectedWeight), solution);
    }

    /**
     * Assert that the constraint of {@link PlanningSolution}
     * has the expected weight for that score level.
     *
     * @param constraintName never null, the name of the constraint
     * @param softLevel {@code 0 <= softLevel <} {@code softLevelSize}.
     *        The {@code scoreLevel} is {@code hardLevel} for hard levels and {@code softLevel + hardLevelSize} for soft levels.
     * @param expectedWeight the total weight for all matches of that 1 constraint
     * @param solution never null, the actual {@link PlanningSolution}
     */
    public void assertSoftWeight(String constraintName, int softLevel, long expectedWeight, Solution_ solution) {
        assertSoftWeight(null, constraintName, softLevel, expectedWeight, solution);
    }

    /**
     * Assert that the constraint of {@link PlanningSolution}
     * has the expected weight for that score level.
     *
     * @param constraintPackage sometimes null.
     *        When null, {@code constraintName} for the {@code scoreLevel} must be unique.
     * @param constraintName never null, the name of the constraint
     * @param softLevel {@code 0 <= softLevel <} {@code softLevelSize}.
     *        The {@code scoreLevel} is {@code hardLevel} for hard levels and {@code softLevel + hardLevelSize} for soft levels.
     * @param expectedWeight the total weight for all matches of that 1 constraint
     * @param solution never null, the actual {@link PlanningSolution}
     */
    public void assertSoftWeight(String constraintPackage, String constraintName, int softLevel, long expectedWeight,
            Solution_ solution) {
        assertWeight(constraintPackage, constraintName,
                hardLevelsSize + softLevel, Long.valueOf(expectedWeight), solution);
    }

}
