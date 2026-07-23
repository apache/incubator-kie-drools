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
package org.kie.kogito.explainability.handlers;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.context.ManagedExecutor;
import org.kie.kogito.explainability.local.counterfactual.CounterfactualConfig;
import org.kie.kogito.explainability.local.counterfactual.CounterfactualExplainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;
import jakarta.inject.Inject;

@ApplicationScoped
public class CounterfactualExplainerProducer {

    private static final Logger LOG = LoggerFactory.getLogger(CounterfactualExplainerProducer.class);

    private final Double goalThreshold;
    private final ManagedExecutor executor;

    @Inject
    public CounterfactualExplainerProducer(
            @ConfigProperty(name = "trusty.explainability.counterfactuals.goalThreshold",
                    defaultValue = "0.01") Double goalThreshold,
            ManagedExecutor executor) {
        this.goalThreshold = goalThreshold;
        this.executor = executor;
    }

    @Produces
    public CounterfactualExplainer produce() {
        LOG.debug("CounterfactualExplainer created");
        final CounterfactualConfig counterfactualConfig = new CounterfactualConfig()
                .withGoalThreshold(this.goalThreshold)
                .withExecutor(executor);
        return new CounterfactualExplainer(counterfactualConfig);
    }
}
