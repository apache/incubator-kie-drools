package org.optaplanner.test.api.score.stream;

import org.optaplanner.core.api.score.Score;
import org.optaplanner.core.api.score.stream.ConstraintProvider;

public interface MultiConstraintAssertion {

    /**
     * Asserts that the {@link ConstraintProvider} under test, given a set of facts, results in a specific {@link Score}.
     *
     * @param score total score calculated for the given set of facts
     * @throws AssertionError when the expected score does not match the calculated score
     */
    default void scores(Score<?> score) {
        scores(score, null);
    }

    /**
     * As defined by {@link #scores(Score)}.
     *
     * @param score total score calculated for the given set of facts
     * @param message sometimes null, description of the scenario being asserted
     * @throws AssertionError when the expected score does not match the calculated score
     */
    void scores(Score<?> score, String message);

}
