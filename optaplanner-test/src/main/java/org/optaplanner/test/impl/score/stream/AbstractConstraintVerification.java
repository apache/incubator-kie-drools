package org.optaplanner.test.impl.score.stream;

import java.util.Arrays;
import java.util.Collection;

import org.optaplanner.constraint.streams.common.AbstractConstraintStreamScoreDirectorFactory;
import org.optaplanner.core.api.score.Score;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

abstract class AbstractConstraintVerification<Solution_, Score_ extends Score<Score_>> {

    protected final Logger LOGGER = LoggerFactory.getLogger(getClass());

    protected final AbstractConstraintStreamScoreDirectorFactory<Solution_, Score_> scoreDirectorFactory;
    protected final SessionBasedAssertionBuilder<Solution_, Score_> sessionBasedAssertionBuilder;

    AbstractConstraintVerification(AbstractConstraintStreamScoreDirectorFactory<Solution_, Score_> scoreDirectorFactory) {
        this.scoreDirectorFactory = scoreDirectorFactory;
        this.sessionBasedAssertionBuilder = new SessionBasedAssertionBuilder<>(scoreDirectorFactory);
    }

    protected void assertCorrectArguments(Object... facts) {
        Class<?> solutionClass = scoreDirectorFactory.getSolutionDescriptor().getSolutionClass();
        if (facts.length == 1 && facts[0].getClass() == solutionClass) {
            LOGGER.warn("Called given() with the planning solution instance ({}) as an argument." +
                    "This will treat the solution as a fact, which is likely not intended.\n" +
                    "Maybe call givenSolution() instead?", facts[0]);
        }
        Arrays.stream(facts)
                .filter(fact -> fact instanceof Collection)
                .findFirst()
                .ifPresent(collection -> LOGGER.warn("Called given() with collection ({}) as argument." +
                        "This will treat the collection itself as a fact, and not its contents.\n" +
                        "Maybe enumerate the contents instead?", collection));
    }

}
