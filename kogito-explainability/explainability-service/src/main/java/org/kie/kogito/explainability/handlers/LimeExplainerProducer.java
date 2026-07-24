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

import java.security.SecureRandom;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.kie.kogito.explainability.local.lime.LimeConfig;
import org.kie.kogito.explainability.local.lime.LimeExplainer;
import org.kie.kogito.explainability.local.lime.optim.RecordingLimeExplainer;
import org.kie.kogito.explainability.model.PerturbationContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;
import jakarta.inject.Inject;

@ApplicationScoped
public class LimeExplainerProducer {

    private static final Logger LOG = LoggerFactory.getLogger(LimeExplainerProducer.class);

    private final Integer numberOfSamples;
    private final Integer numberOfPerturbations;
    private final Integer recordedPredictions;

    @Inject
    public LimeExplainerProducer(
            @ConfigProperty(name = "trusty.explainability.numberOfSamples", defaultValue = "100") Integer numberOfSamples,
            @ConfigProperty(name = "trusty.explainability.numberOfPerturbations", defaultValue = "1") Integer numberOfPerturbations,
            @ConfigProperty(name = "trusty.explainability.recordedPredictions", defaultValue = "10") Integer recordedPredictions) {
        this.numberOfSamples = numberOfSamples;
        this.numberOfPerturbations = numberOfPerturbations;
        this.recordedPredictions = recordedPredictions;
    }

    @Produces
    public LimeExplainer produce() {
        LOG.debug("LimeExplainer created (numberOfSamples={}, numberOfPerturbations={})", numberOfSamples, numberOfPerturbations);
        LimeConfig limeConfig = new LimeConfig()
                .withSamples(numberOfSamples)
                .withPerturbationContext(new PerturbationContext(new SecureRandom(), numberOfPerturbations));
        return new RecordingLimeExplainer(limeConfig, recordedPredictions);
    }
}
