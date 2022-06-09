package org.optaplanner.test.api.score.stream;

public interface SingleConstraintVerification<Solution_> {

    /**
     * @param facts never null, at least one
     * @return never null
     */
    SingleConstraintAssertion given(Object... facts);

    /**
     * @param solution never null
     * @return never null
     */
    SingleConstraintAssertion givenSolution(Solution_ solution);

}
