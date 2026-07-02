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

package org.optaplanner.core.impl.score.director.incremental;

import java.util.function.Supplier;

import org.optaplanner.core.api.score.Score;
import org.optaplanner.core.api.score.calculator.IncrementalScoreCalculator;
import org.optaplanner.core.config.score.director.ScoreDirectorFactoryConfig;
import org.optaplanner.core.config.solver.EnvironmentMode;
import org.optaplanner.core.config.util.ConfigUtils;
import org.optaplanner.core.impl.domain.solution.descriptor.SolutionDescriptor;
import org.optaplanner.core.impl.score.director.AbstractScoreDirectorFactory;
import org.optaplanner.core.impl.score.director.ScoreDirectorFactoryService;
import org.optaplanner.core.impl.score.director.ScoreDirectorType;

public final class IncrementalScoreDirectorFactoryService<Solution_, Score_ extends Score<Score_>>
        implements ScoreDirectorFactoryService<Solution_, Score_> {

    @Override
    public ScoreDirectorType getSupportedScoreDirectorType() {
        return ScoreDirectorType.INCREMENTAL;
    }

    @Override
    public Supplier<AbstractScoreDirectorFactory<Solution_, Score_>> buildScoreDirectorFactory(ClassLoader classLoader,
            SolutionDescriptor<Solution_> solutionDescriptor, ScoreDirectorFactoryConfig config,
            EnvironmentMode environmentMode) {
        if (config.getIncrementalScoreCalculatorClass() != null) {
            if (!IncrementalScoreCalculator.class.isAssignableFrom(config.getIncrementalScoreCalculatorClass())) {
                throw new IllegalArgumentException(
                        "The incrementalScoreCalculatorClass (" + config.getIncrementalScoreCalculatorClass()
                                + ") does not implement " + IncrementalScoreCalculator.class.getSimpleName() + ".");
            }
            return () -> new IncrementalScoreDirectorFactory<>(solutionDescriptor, () -> {
                IncrementalScoreCalculator<Solution_, Score_> incrementalScoreCalculator = ConfigUtils.newInstance(config,
                        "incrementalScoreCalculatorClass", config.getIncrementalScoreCalculatorClass());
                ConfigUtils.applyCustomProperties(incrementalScoreCalculator, "incrementalScoreCalculatorClass",
                        config.getIncrementalScoreCalculatorCustomProperties(), "incrementalScoreCalculatorCustomProperties");
                return incrementalScoreCalculator;
            });
        } else {
            if (config.getIncrementalScoreCalculatorCustomProperties() != null) {
                throw new IllegalStateException(
                        "If there is no incrementalScoreCalculatorClass (" + config.getIncrementalScoreCalculatorClass()
                                + "), then there can be no incrementalScoreCalculatorCustomProperties ("
                                + config.getIncrementalScoreCalculatorCustomProperties() + ") either.");
            }
            return null;
        }
    }
}
