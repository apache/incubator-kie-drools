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

package org.kie.kogito.explainability.handlers;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.kie.kogito.explainability.local.counterfactual.CounterfactualConfig;
import org.kie.kogito.explainability.local.counterfactual.CounterfactualExplainer;
import org.kie.kogito.explainability.local.counterfactual.SolverConfigBuilder;
import org.optaplanner.core.config.solver.SolverConfig;
import org.optaplanner.core.config.solver.termination.TerminationConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ApplicationScoped
public class CounterfactualExplainerProducer {

    private static final Logger LOG = LoggerFactory.getLogger(CounterfactualExplainerProducer.class);

    private final Long maxRunningTimeSeconds;
    private final Double goalThreshold;

    @Inject
    public CounterfactualExplainerProducer(
            @ConfigProperty(name = "trusty.explainability.counterfactuals.maxRunningTimeSeconds",
                    defaultValue = "60") Integer maxRunningTimeSeconds,
            @ConfigProperty(name = "trusty.explainability.counterfactuals.goalThreshold",
                    defaultValue = "0.01") Double goalThreshold) {
        this.maxRunningTimeSeconds = Long.valueOf(maxRunningTimeSeconds);
        this.goalThreshold = goalThreshold;
    }

    @Produces
    public CounterfactualExplainer produce() {
        LOG.debug("CounterfactualExplainer created");
        final TerminationConfig terminationConfig = new TerminationConfig().withSecondsSpentLimit(this.maxRunningTimeSeconds);
        final SolverConfig solverConfig = SolverConfigBuilder.builder().withTerminationConfig(terminationConfig).build();
        final CounterfactualConfig counterfactualConfig = new CounterfactualConfig()
                .withSolverConfig(solverConfig)
                .withGoalThreshold(this.goalThreshold);
        return new CounterfactualExplainer(counterfactualConfig);
    }
}
