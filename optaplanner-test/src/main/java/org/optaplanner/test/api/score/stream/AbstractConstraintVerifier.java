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

package org.optaplanner.test.api.score.stream;

import java.util.Arrays;
import java.util.Map;
import java.util.Objects;

import org.optaplanner.core.api.score.Score;
import org.optaplanner.core.api.score.constraint.ConstraintMatchTotal;
import org.optaplanner.core.impl.score.director.ScoreDirector;
import org.optaplanner.core.impl.score.director.stream.ConstraintStreamScoreDirectorFactory;
import org.optaplanner.core.impl.score.stream.ConstraintSession;

public abstract class AbstractConstraintVerifier<Solution_,
        Assertion_ extends AbstractAssertion<Solution_, Assertion_, Verifier_>,
        Verifier_ extends AbstractConstraintVerifier<Solution_, Assertion_, Verifier_>> {

    private final ConstraintStreamScoreDirectorFactory<Solution_> constraintStreamScoreDirectorFactory;

    protected AbstractConstraintVerifier(
            ConstraintStreamScoreDirectorFactory<Solution_> constraintStreamScoreDirectorFactory) {
        this.constraintStreamScoreDirectorFactory = constraintStreamScoreDirectorFactory;
    }

    public ConstraintStreamScoreDirectorFactory<Solution_> getConstraintStreamScoreDirectorFactory() {
        return constraintStreamScoreDirectorFactory;
    }

    protected abstract Assertion_ createAssertion(Score<?> score,
            Map<String, ConstraintMatchTotal> constraintMatchTotalMap);

    public final Assertion_ given(Object... facts) {
        try (ConstraintSession<Solution_> constraintSession =
                constraintStreamScoreDirectorFactory.newConstraintStreamingSession(true, null)) {
            Arrays.stream(facts).distinct().forEach(constraintSession::insert);
            Map<String, ConstraintMatchTotal> constraintMatches = constraintSession.getConstraintMatchTotalMap();
            return createAssertion(constraintSession.calculateScore(0), constraintMatches);
        }
    }

    public final Assertion_ given(Solution_ solution) {
        try (ScoreDirector<Solution_> scoreDirector =
                constraintStreamScoreDirectorFactory.buildScoreDirector(true, true)) {
            scoreDirector.setWorkingSolution(Objects.requireNonNull(solution));
            Map<String, ConstraintMatchTotal> constraintMatches = scoreDirector.getConstraintMatchTotalMap();
            return createAssertion(scoreDirector.calculateScore(), constraintMatches);
        }
    }

}
