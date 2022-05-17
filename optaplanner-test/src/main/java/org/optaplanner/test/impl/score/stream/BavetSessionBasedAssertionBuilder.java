/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
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

import org.optaplanner.constraint.streams.bavet.BavetConstraintSession;
import org.optaplanner.constraint.streams.bavet.BavetConstraintStreamScoreDirectorFactory;
import org.optaplanner.core.api.score.Score;
import org.optaplanner.core.api.score.stream.ConstraintProvider;

final class BavetSessionBasedAssertionBuilder<Solution_, Score_ extends Score<Score_>>
        implements SessionBasedAssertionBuilder<Solution_, Score_> {

    private final BavetConstraintStreamScoreDirectorFactory<Solution_, Score_> constraintStreamScoreDirectorFactory;

    public BavetSessionBasedAssertionBuilder(
            BavetConstraintStreamScoreDirectorFactory<Solution_, Score_> constraintStreamScoreDirectorFactory) {
        this.constraintStreamScoreDirectorFactory = Objects.requireNonNull(constraintStreamScoreDirectorFactory);
    }

    @Override
    public DefaultMultiConstraintAssertion<Solution_, Score_> multiConstraintGiven(
            ConstraintProvider constraintProvider, Object... facts) {
        BavetConstraintSession<Score_> constraintSession =
                constraintStreamScoreDirectorFactory.newSession(true, null);
        Arrays.stream(facts).forEach(constraintSession::insert);
        return new DefaultMultiConstraintAssertion<>(constraintProvider, constraintSession.calculateScore(0),
                constraintSession.getConstraintMatchTotalMap(), constraintSession.getIndictmentMap());
    }

    @Override
    public DefaultSingleConstraintAssertion<Solution_, Score_> singleConstraintGiven(Object... facts) {
        BavetConstraintSession<Score_> constraintSession =
                constraintStreamScoreDirectorFactory.newSession(true, null);
        Arrays.stream(facts).forEach(constraintSession::insert);
        return new DefaultSingleConstraintAssertion<>(constraintStreamScoreDirectorFactory,
                constraintSession.calculateScore(0), constraintSession.getConstraintMatchTotalMap(),
                constraintSession.getIndictmentMap());
    }

}
