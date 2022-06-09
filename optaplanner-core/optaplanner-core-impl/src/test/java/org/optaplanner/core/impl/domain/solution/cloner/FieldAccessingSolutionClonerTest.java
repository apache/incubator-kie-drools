package org.optaplanner.core.impl.domain.solution.cloner;

import org.optaplanner.core.api.domain.solution.cloner.SolutionCloner;
import org.optaplanner.core.impl.domain.solution.descriptor.SolutionDescriptor;

class FieldAccessingSolutionClonerTest extends AbstractSolutionClonerTest {

    @Override
    protected <Solution_> SolutionCloner<Solution_> createSolutionCloner(
            SolutionDescriptor<Solution_> solutionDescriptor) {
        return new FieldAccessingSolutionCloner<>(solutionDescriptor);
    }
}
