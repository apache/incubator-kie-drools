/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kie.kogito.explainability.local.counterfactual;

import java.util.ArrayList;
import java.util.List;

import org.kie.kogito.explainability.local.counterfactual.entities.BooleanEntity;
import org.kie.kogito.explainability.local.counterfactual.entities.CategoricalEntity;
import org.kie.kogito.explainability.local.counterfactual.entities.DoubleEntity;
import org.kie.kogito.explainability.local.counterfactual.entities.IntegerEntity;
import org.optaplanner.core.config.localsearch.LocalSearchPhaseConfig;
import org.optaplanner.core.config.localsearch.decider.acceptor.LocalSearchAcceptorConfig;
import org.optaplanner.core.config.localsearch.decider.forager.LocalSearchForagerConfig;
import org.optaplanner.core.config.phase.PhaseConfig;
import org.optaplanner.core.config.score.director.ScoreDirectorFactoryConfig;
import org.optaplanner.core.config.solver.SolverConfig;
import org.optaplanner.core.config.solver.termination.TerminationConfig;

public class CounterfactualConfigurationFactory {

    private static final long DEFAULT_TIME_LIMIT = 30;
    private static final int DEFAULT_TABU_SIZE = 70;
    private static final int DEFAULT_ACCEPTED_COUNT = 5000;

    private CounterfactualConfigurationFactory() {
    }

    public static CounterfactualConfigurationFactory.Builder builder() {
        return new Builder();
    }

    public static class Builder {

        // create a default termination config if none supplied
        private TerminationConfig terminationConfig = new TerminationConfig();
        private int tabuSize = DEFAULT_TABU_SIZE;
        private int acceptedCount = DEFAULT_ACCEPTED_COUNT;

        private Builder() {
            // Set default termination time
            terminationConfig.setSecondsSpentLimit(DEFAULT_TIME_LIMIT);
        }

        public SolverConfig build() {
            SolverConfig solverConfig = new SolverConfig();

            solverConfig.withEntityClasses(IntegerEntity.class, DoubleEntity.class, BooleanEntity.class,
                    CategoricalEntity.class);
            solverConfig.setSolutionClass(CounterfactualSolution.class);

            ScoreDirectorFactoryConfig scoreDirectorFactoryConfig = new ScoreDirectorFactoryConfig();
            scoreDirectorFactoryConfig.setEasyScoreCalculatorClass(CounterFactualScoreCalculator.class);
            solverConfig.setScoreDirectorFactoryConfig(scoreDirectorFactoryConfig);

            solverConfig.setTerminationConfig(terminationConfig);

            LocalSearchAcceptorConfig acceptorConfig = new LocalSearchAcceptorConfig();
            acceptorConfig.setEntityTabuSize(tabuSize);

            LocalSearchForagerConfig localSearchForagerConfig = new LocalSearchForagerConfig();
            localSearchForagerConfig.setAcceptedCountLimit(acceptedCount);

            LocalSearchPhaseConfig localSearchPhaseConfig = new LocalSearchPhaseConfig();
            localSearchPhaseConfig.setAcceptorConfig(acceptorConfig);
            localSearchPhaseConfig.setForagerConfig(localSearchForagerConfig);

            List<PhaseConfig> phaseConfigs = new ArrayList<>();
            phaseConfigs.add(localSearchPhaseConfig);

            solverConfig.setPhaseConfigList(phaseConfigs);
            return solverConfig;
        }

        public Builder withTabuSize(int size) {
            this.tabuSize = size;
            return this;
        }

        public Builder withAcceptedCount(int count) {
            this.acceptedCount = count;
            return this;
        }

        public Builder withTerminationConfig(TerminationConfig terminationConfig) {
            this.terminationConfig = terminationConfig;
            return this;
        }
    }
}
