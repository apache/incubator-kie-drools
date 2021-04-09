/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
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

import org.optaplanner.core.api.score.Score;
import org.optaplanner.core.api.score.stream.ConstraintProvider;
import org.optaplanner.core.impl.score.director.stream.AbstractConstraintStreamScoreDirectorFactory;
import org.optaplanner.core.impl.score.director.stream.BavetConstraintStreamScoreDirectorFactory;
import org.optaplanner.core.impl.score.director.stream.DroolsConstraintStreamScoreDirectorFactory;

/**
 * Drools and Bavet sessions have vastly different interfaces and therefore the assertion generation is generalized
 * using this interface.
 * 
 * @param <Solution_>
 * @param <Score_>
 */
interface SessionBasedAssertionBuilder<Solution_, Score_ extends Score<Score_>> {

    static <Solution_, Score_ extends Score<Score_>> SessionBasedAssertionBuilder<Solution_, Score_> create(
            AbstractConstraintStreamScoreDirectorFactory<Solution_, Score_> scoreDirectorFactory) {
        if (scoreDirectorFactory instanceof DroolsConstraintStreamScoreDirectorFactory) {
            return new DroolsSessionBasedAssertionBuilder<>(
                    (DroolsConstraintStreamScoreDirectorFactory<Solution_, Score_>) scoreDirectorFactory);
        } else if (scoreDirectorFactory instanceof BavetConstraintStreamScoreDirectorFactory) {
            return new BavetSessionBasedAssertionBuilder<>(
                    (BavetConstraintStreamScoreDirectorFactory<Solution_, Score_>) scoreDirectorFactory);
        } else {
            throw new IllegalStateException("Impossible state: unknown score director factory (" +
                    scoreDirectorFactory + ").");
        }
    }

    DefaultMultiConstraintAssertion<Solution_, Score_> multiConstraintGiven(ConstraintProvider constraintProvider,
            Object... facts);

    DefaultSingleConstraintAssertion<Solution_, Score_> singleConstraintGiven(Object... facts);

}
