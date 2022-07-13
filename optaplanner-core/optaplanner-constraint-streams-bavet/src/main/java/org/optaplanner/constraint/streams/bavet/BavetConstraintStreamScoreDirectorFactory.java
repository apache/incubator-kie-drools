package org.optaplanner.constraint.streams.bavet;

import java.util.Arrays;
import java.util.List;

import org.optaplanner.constraint.streams.common.AbstractConstraintStreamScoreDirectorFactory;
import org.optaplanner.constraint.streams.common.inliner.AbstractScoreInliner;
import org.optaplanner.core.api.score.Score;
import org.optaplanner.core.api.score.stream.Constraint;
import org.optaplanner.core.api.score.stream.ConstraintProvider;
import org.optaplanner.core.impl.domain.solution.descriptor.SolutionDescriptor;

public final class BavetConstraintStreamScoreDirectorFactory<Solution_, Score_ extends Score<Score_>>
        extends AbstractConstraintStreamScoreDirectorFactory<Solution_, Score_> {

    private final BavetConstraintSessionFactory<Solution_, Score_> constraintSessionFactory;
    private final List<BavetConstraint<Solution_>> constraintList;

    public BavetConstraintStreamScoreDirectorFactory(SolutionDescriptor<Solution_> solutionDescriptor,
            ConstraintProvider constraintProvider) {
        super(solutionDescriptor);
        BavetConstraintFactory<Solution_> constraintFactory = new BavetConstraintFactory<>(solutionDescriptor);
        constraintList = constraintFactory.buildConstraints(constraintProvider);
        constraintSessionFactory = new BavetConstraintSessionFactory<>(solutionDescriptor, constraintList);
    }

    @Override
    public BavetConstraintStreamScoreDirector<Solution_, Score_> buildScoreDirector(boolean lookUpEnabled,
            boolean constraintMatchEnabledPreference) {
        return new BavetConstraintStreamScoreDirector<>(this, lookUpEnabled, constraintMatchEnabledPreference);
    }

    public BavetConstraintSession<Score_> newSession(boolean constraintMatchEnabled, Solution_ workingSolution) {
        return constraintSessionFactory.buildSession(constraintMatchEnabled, workingSolution);
    }

    @Override
    public AbstractScoreInliner<Score_> fireAndForget(Object... facts) {
        BavetConstraintSession<Score_> session = newSession(true, null);
        Arrays.stream(facts).forEach(session::insert);
        session.calculateScore(0);
        return session.getScoreInliner();
    }

    @Override
    public SolutionDescriptor<Solution_> getSolutionDescriptor() {
        return solutionDescriptor;
    }

    @Override
    public Constraint[] getConstraints() {
        return constraintList.toArray(new Constraint[0]);
    }

}
