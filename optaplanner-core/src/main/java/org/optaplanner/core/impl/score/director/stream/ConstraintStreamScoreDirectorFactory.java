/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.optaplanner.core.impl.score.director.stream;

import org.optaplanner.core.api.domain.solution.PlanningSolution;
import org.optaplanner.core.api.score.stream.Constraint;
import org.optaplanner.core.api.score.stream.ConstraintProvider;
import org.optaplanner.core.api.score.stream.ConstraintStreamImplType;
import org.optaplanner.core.impl.domain.solution.descriptor.SolutionDescriptor;
import org.optaplanner.core.impl.score.director.AbstractScoreDirectorFactory;
import org.optaplanner.core.impl.score.director.ScoreDirectorFactory;
import org.optaplanner.core.impl.score.stream.ConstraintSession;
import org.optaplanner.core.impl.score.stream.ConstraintSessionFactory;
import org.optaplanner.core.impl.score.stream.InnerConstraintFactory;
import org.optaplanner.core.impl.score.stream.bavet.BavetConstraintFactory;
import org.optaplanner.core.impl.score.stream.drools.DroolsConstraintFactory;

/**
 * FP streams implementation of {@link ScoreDirectorFactory}.
 * @param <Solution_> the solution type, the class with the {@link PlanningSolution} annotation
 * @see ConstraintStreamScoreDirector
 * @see ScoreDirectorFactory
 */
public class ConstraintStreamScoreDirectorFactory<Solution_> extends AbstractScoreDirectorFactory<Solution_> {

    private final ConstraintSessionFactory<Solution_> constraintSessionFactory;

    public ConstraintStreamScoreDirectorFactory(SolutionDescriptor<Solution_> solutionDescriptor,
            ConstraintProvider constraintProvider, ConstraintStreamImplType constraintStreamImplType) {
        super(solutionDescriptor);
        InnerConstraintFactory<Solution_> constraintFactory;
        switch (constraintStreamImplType) {
            case BAVET:
                constraintFactory = new BavetConstraintFactory<>(solutionDescriptor);
                break;
            case DROOLS:
                constraintFactory = new DroolsConstraintFactory<>(solutionDescriptor);
                break;
            default:
                throw new IllegalStateException("The constraintStreamImplType (" + constraintStreamImplType + ") is not implemented.");
        }
        Constraint[] constraints = constraintProvider.defineConstraints(constraintFactory);
        constraintSessionFactory = constraintFactory.buildSessionFactory(constraints);
    }

    // ************************************************************************
    // Complex methods
    // ************************************************************************

    @Override
    public ConstraintStreamScoreDirector<Solution_> buildScoreDirector(
            boolean lookUpEnabled, boolean constraintMatchEnabledPreference) {
        return new ConstraintStreamScoreDirector<>(this, lookUpEnabled, constraintMatchEnabledPreference);
    }

    public ConstraintSession<Solution_> newConstraintStreamingSession(boolean constraintMatchEnabled, Solution_ workingSolution) {
        return constraintSessionFactory.buildSession(constraintMatchEnabled, workingSolution);
    }

}
