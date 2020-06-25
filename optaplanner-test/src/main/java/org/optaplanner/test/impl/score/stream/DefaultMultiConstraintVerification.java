/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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

package org.optaplanner.test.impl.score.stream;

import java.util.Arrays;
import java.util.Objects;

import org.optaplanner.core.api.score.Score;
import org.optaplanner.core.api.score.stream.ConstraintProvider;
import org.optaplanner.core.impl.score.director.InnerScoreDirector;
import org.optaplanner.core.impl.score.director.stream.ConstraintStreamScoreDirectorFactory;
import org.optaplanner.core.impl.score.stream.ConstraintSession;
import org.optaplanner.test.api.score.stream.MultiConstraintVerification;

public final class DefaultMultiConstraintVerification<Solution_>
        implements MultiConstraintVerification<Solution_> {

    private final ConstraintStreamScoreDirectorFactory<Solution_> scoreDirectorFactory;
    private final ConstraintProvider constraintProvider;

    protected DefaultMultiConstraintVerification(
            ConstraintStreamScoreDirectorFactory<Solution_> scoreDirectorFactory,
            ConstraintProvider constraintProvider) {
        this.scoreDirectorFactory = scoreDirectorFactory;
        this.constraintProvider = constraintProvider;
    }

    @Override
    public final DefaultMultiConstraintAssertion given(Object... facts) {
        try (ConstraintSession<Solution_> constraintSession = scoreDirectorFactory.newConstraintStreamingSession(true, null)) {
            Arrays.stream(facts).forEach(constraintSession::insert);
            Score<?> score = constraintSession.calculateScore(0);
            return new DefaultMultiConstraintAssertion<>(constraintProvider, score);
        }
    }

    @Override
    public final DefaultMultiConstraintAssertion givenSolution(Solution_ solution) {
        try (InnerScoreDirector<Solution_> scoreDirector = scoreDirectorFactory.buildScoreDirector(true, true)) {
            scoreDirector.setWorkingSolution(Objects.requireNonNull(solution));
            Score<?> score = scoreDirector.calculateScore();
            return new DefaultMultiConstraintAssertion<>(constraintProvider, score);
        }
    }

}
