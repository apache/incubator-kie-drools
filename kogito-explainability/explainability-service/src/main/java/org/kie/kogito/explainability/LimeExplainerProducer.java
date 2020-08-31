/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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

package org.kie.kogito.explainability;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.kie.kogito.explainability.local.LocalExplainer;
import org.kie.kogito.explainability.local.lime.LimeExplainer;
import org.kie.kogito.explainability.model.Saliency;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import java.util.Map;

@ApplicationScoped
public class LimeExplainerProducer {

    private static final Logger LOG = LoggerFactory.getLogger(LimeExplainerProducer.class);

    private final Integer numberOfSamples;
    private final Integer numberOfPerturbations;

    @Inject
    public LimeExplainerProducer(
            @ConfigProperty(name = "trusty.explainability.numberOfSamples", defaultValue = "100") Integer numberOfSamples,
            @ConfigProperty(name = "trusty.explainability.numberOfPerturbations", defaultValue = "1") Integer numberOfPerturbations) {
        this.numberOfSamples = numberOfSamples;
        this.numberOfPerturbations = numberOfPerturbations;
    }

    @Produces
    public LocalExplainer<Map<String, Saliency>> produce() {
        LOG.debug("LimeExplainer created (numberOfSamples={}, numberOfPerturbations={})", numberOfSamples, numberOfPerturbations);
        return new LimeExplainer(numberOfSamples, numberOfPerturbations);
    }
}
