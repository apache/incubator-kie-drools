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
import java.util.Map;
import java.util.Objects;

import org.optaplanner.core.api.score.constraint.ConstraintMatchTotal;
import org.optaplanner.core.impl.score.director.InnerScoreDirector;
import org.optaplanner.core.impl.score.director.stream.ConstraintStreamScoreDirectorFactory;
import org.optaplanner.core.impl.score.stream.ConstraintSession;
import org.optaplanner.test.api.score.stream.SingleConstraintVerification;

public final class DefaultSingleConstraintVerification<Solution_>
        implements SingleConstraintVerification<Solution_> {

    private final ConstraintStreamScoreDirectorFactory<Solution_> scoreDirectorFactory;

    protected DefaultSingleConstraintVerification(ConstraintStreamScoreDirectorFactory<Solution_> scoreDirectorFactory) {
        this.scoreDirectorFactory = scoreDirectorFactory;
    }

    @Override
    public final DefaultSingleConstraintAssertion given(Object... facts) {
        try (ConstraintSession<Solution_> constraintSession = scoreDirectorFactory.newConstraintStreamingSession(true, null)) {
            Arrays.stream(facts).forEach(constraintSession::insert);
            Map<String, ConstraintMatchTotal> constraintMatchTotalMap = constraintSession.getConstraintMatchTotalMap();
            return new DefaultSingleConstraintAssertion<>(scoreDirectorFactory, constraintMatchTotalMap);
        }
    }

    @Override
    public final DefaultSingleConstraintAssertion givenSolution(Solution_ solution) {
        try (InnerScoreDirector<Solution_> scoreDirector = scoreDirectorFactory.buildScoreDirector(true, true)) {
            scoreDirector.setWorkingSolution(Objects.requireNonNull(solution));
            Map<String, ConstraintMatchTotal> constraintMatchTotalMap = scoreDirector.getConstraintMatchTotalMap();
            return new DefaultSingleConstraintAssertion<>(scoreDirectorFactory, constraintMatchTotalMap);
        }
    }

}
