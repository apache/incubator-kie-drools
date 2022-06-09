package org.optaplanner.constraint.streams.bavet;

import org.junit.jupiter.api.Assumptions;
import org.optaplanner.constraint.streams.common.ConstraintStreamImplSupport;
import org.optaplanner.core.api.score.Score;
import org.optaplanner.core.api.score.stream.ConstraintProvider;
import org.optaplanner.core.impl.domain.solution.descriptor.SolutionDescriptor;
import org.optaplanner.core.impl.score.director.InnerScoreDirector;

public final class BavetConstraintStreamImplSupport
        implements ConstraintStreamImplSupport {

    private final boolean constraintMatchEnabled;

    public BavetConstraintStreamImplSupport(boolean constraintMatchEnabled) {
        this.constraintMatchEnabled = constraintMatchEnabled;
    }

    @Override
    public void assumeBavet() {
        // This is Bavet.
    }

    @Override
    public void assumeDrools() {
        Assumptions.assumeTrue(false, "This functionality is not yet supported in Bavet constraint streams.");
    }

    @Override
    public boolean isConstreamMatchEnabled() {
        return constraintMatchEnabled;
    }

    @Override
    public <Score_ extends Score<Score_>, Solution_> InnerScoreDirector<Solution_, Score_> buildScoreDirector(
            SolutionDescriptor<Solution_> solutionDescriptorSupplier, ConstraintProvider constraintProvider) {
        return (InnerScoreDirector<Solution_, Score_>) new BavetConstraintStreamScoreDirectorFactory<>(
                solutionDescriptorSupplier, constraintProvider)
                        .buildScoreDirector(false, constraintMatchEnabled);
    }
}
