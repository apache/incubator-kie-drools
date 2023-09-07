/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.optaplanner.test.impl.score.stream;

import java.util.Objects;

import org.optaplanner.constraint.streams.common.AbstractConstraintStreamScoreDirectorFactory;
import org.optaplanner.core.api.score.Score;
import org.optaplanner.core.api.score.stream.ConstraintProvider;
import org.optaplanner.core.impl.score.director.InnerScoreDirector;
import org.optaplanner.test.api.score.stream.MultiConstraintVerification;

public final class DefaultMultiConstraintVerification<Solution_, Score_ extends Score<Score_>>
        extends AbstractConstraintVerification<Solution_, Score_>
        implements MultiConstraintVerification<Solution_> {

    private final ConstraintProvider constraintProvider;

    DefaultMultiConstraintVerification(AbstractConstraintStreamScoreDirectorFactory<Solution_, Score_> scoreDirectorFactory,
            ConstraintProvider constraintProvider) {
        super(scoreDirectorFactory);
        this.constraintProvider = constraintProvider;
    }

    @Override
    public DefaultMultiConstraintAssertion<Score_> given(Object... facts) {
        assertCorrectArguments(facts);
        return sessionBasedAssertionBuilder.multiConstraintGiven(constraintProvider, facts);
    }

    @Override
    public DefaultMultiConstraintAssertion<Score_> givenSolution(Solution_ solution) {
        try (InnerScoreDirector<Solution_, Score_> scoreDirector =
                scoreDirectorFactory.buildScoreDirector(true, true)) {
            scoreDirector.setWorkingSolution(Objects.requireNonNull(solution));
            return new DefaultMultiConstraintAssertion<>(constraintProvider, scoreDirector.calculateScore(),
                    scoreDirector.getConstraintMatchTotalMap(), scoreDirector.getIndictmentMap());
        }
    }

}
