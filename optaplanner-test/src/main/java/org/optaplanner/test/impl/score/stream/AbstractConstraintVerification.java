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

import java.util.Arrays;
import java.util.Collection;

import org.optaplanner.constraint.streams.common.AbstractConstraintStreamScoreDirectorFactory;
import org.optaplanner.core.api.score.Score;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

abstract class AbstractConstraintVerification<Solution_, Score_ extends Score<Score_>> {

    protected final Logger LOGGER = LoggerFactory.getLogger(getClass());

    protected final AbstractConstraintStreamScoreDirectorFactory<Solution_, Score_> scoreDirectorFactory;
    protected final SessionBasedAssertionBuilder<Solution_, Score_> sessionBasedAssertionBuilder;

    AbstractConstraintVerification(AbstractConstraintStreamScoreDirectorFactory<Solution_, Score_> scoreDirectorFactory) {
        this.scoreDirectorFactory = scoreDirectorFactory;
        this.sessionBasedAssertionBuilder = new SessionBasedAssertionBuilder<>(scoreDirectorFactory);
    }

    protected void assertCorrectArguments(Object... facts) {
        Class<?> solutionClass = scoreDirectorFactory.getSolutionDescriptor().getSolutionClass();
        if (facts.length == 1 && facts[0].getClass() == solutionClass) {
            LOGGER.warn("Called given() with the planning solution instance ({}) as an argument." +
                    "This will treat the solution as a fact, which is likely not intended.\n" +
                    "Maybe call givenSolution() instead?", facts[0]);
        }
        Arrays.stream(facts)
                .filter(fact -> fact instanceof Collection)
                .findFirst()
                .ifPresent(collection -> LOGGER.warn("Called given() with collection ({}) as argument." +
                        "This will treat the collection itself as a fact, and not its contents.\n" +
                        "Maybe enumerate the contents instead?", collection));
    }

}
