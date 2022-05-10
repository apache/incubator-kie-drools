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

package org.optaplanner.constraint.drl;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import org.kie.api.KieBase;
import org.optaplanner.core.api.score.Score;
import org.optaplanner.core.config.score.director.ScoreDirectorFactoryConfig;
import org.optaplanner.core.config.util.ConfigUtils;
import org.optaplanner.core.impl.domain.solution.descriptor.SolutionDescriptor;
import org.optaplanner.core.impl.score.director.AbstractScoreDirectorFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class DrlScoreDirectorFactoryService<Solution_, Score_ extends Score<Score_>>
        extends AbstractDrlScoreDirectorFactoryService<Solution_, Score_> {

    private static final Logger LOGGER = LoggerFactory.getLogger(DrlScoreDirectorFactoryService.class);

    @Override
    public Supplier<AbstractScoreDirectorFactory<Solution_, Score_>> buildScoreDirectorFactory(ClassLoader classLoader,
            SolutionDescriptor<Solution_> solutionDescriptor, ScoreDirectorFactoryConfig config) {
        if (isTestGenRequested()) {
            return null; // TestGenDrlScoreDirectorFactoryService will be called.
        }

        if (ConfigUtils.isEmptyCollection(config.getScoreDrlList())
                && ConfigUtils.isEmptyCollection(config.getScoreDrlFileList())) {
            if (config.getKieBaseConfigurationProperties() != null) {
                throw new IllegalArgumentException(
                        "If kieBaseConfigurationProperties (" + config.getKieBaseConfigurationProperties()
                                + ") is not null, the scoreDrlList (" + config.getScoreDrlList()
                                + ") or the scoreDrlFileList (" + config.getScoreDrlFileList() + ") must not be empty.");
            }
            return null;
        }

        LOGGER.info("Score DRL is deprecated and will be removed in a future major version of OptaPlanner.\n" +
                "Consider migrating to the Constraint Streams API.\n" +
                "See migration recipe at https://www.optaplanner.org/learn/drl-to-constraint-streams-migration.html.");

        List<String> scoreDrlList = new ArrayList<>();
        if (config.getGizmoKieBaseSupplier() == null) {
            if (!ConfigUtils.isEmptyCollection(config.getScoreDrlList())) {
                for (String scoreDrl : config.getScoreDrlList()) {
                    if (scoreDrl == null) {
                        throw new IllegalArgumentException("The scoreDrl (" + scoreDrl + ") cannot be null.");
                    }
                    scoreDrlList.add(scoreDrl);
                }
            }
        }
        return () -> buildScoreDirectorFactory(classLoader, solutionDescriptor, config, scoreDrlList);
    }

    @Override
    protected DrlScoreDirectorFactory<Solution_, Score_> createScoreDirectorFactory(ScoreDirectorFactoryConfig config,
            SolutionDescriptor<Solution_> solutionDescriptor, KieBase kieBase) {
        return new DrlScoreDirectorFactory<>(solutionDescriptor, kieBase);
    }
}
