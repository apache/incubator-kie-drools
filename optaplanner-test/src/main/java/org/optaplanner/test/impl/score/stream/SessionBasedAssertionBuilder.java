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

import java.util.Objects;

import org.optaplanner.constraint.streams.common.AbstractConstraintStreamScoreDirectorFactory;
import org.optaplanner.constraint.streams.common.inliner.AbstractScoreInliner;
import org.optaplanner.core.api.score.Score;
import org.optaplanner.core.api.score.stream.ConstraintProvider;

final class SessionBasedAssertionBuilder<Solution_, Score_ extends Score<Score_>> {

    private final AbstractConstraintStreamScoreDirectorFactory<Solution_, Score_> constraintStreamScoreDirectorFactory;

    public SessionBasedAssertionBuilder(
            AbstractConstraintStreamScoreDirectorFactory<Solution_, Score_> constraintStreamScoreDirectorFactory) {
        this.constraintStreamScoreDirectorFactory = Objects.requireNonNull(constraintStreamScoreDirectorFactory);
    }

    public DefaultMultiConstraintAssertion<Score_> multiConstraintGiven(ConstraintProvider constraintProvider,
            Object... facts) {
        AbstractScoreInliner<Score_> scoreInliner = constraintStreamScoreDirectorFactory.fireAndForget(facts);
        return new DefaultMultiConstraintAssertion<>(constraintProvider, scoreInliner.extractScore(0),
                scoreInliner.getConstraintMatchTotalMap(), scoreInliner.getIndictmentMap());
    }

    public DefaultSingleConstraintAssertion<Solution_, Score_> singleConstraintGiven(Object... facts) {
        AbstractScoreInliner<Score_> scoreInliner = constraintStreamScoreDirectorFactory.fireAndForget(facts);
        return new DefaultSingleConstraintAssertion<>(constraintStreamScoreDirectorFactory,
                scoreInliner.extractScore(0), scoreInliner.getConstraintMatchTotalMap(),
                scoreInliner.getIndictmentMap());
    }

}
