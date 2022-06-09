package org.optaplanner.test.api.score.stream;

public interface MultiConstraintVerification<Solution_> {

    /**
     * As defined by {@link SingleConstraintVerification#given(Object...)}.
     *
     * @param facts never null, at least one
     * @return never null
     */
    MultiConstraintAssertion given(Object... facts);

    /**
     * As defined by {@link SingleConstraintVerification#givenSolution(Object)}.
     *
     * @param solution never null
     * @return never null
     */
    MultiConstraintAssertion givenSolution(Solution_ solution);

}
