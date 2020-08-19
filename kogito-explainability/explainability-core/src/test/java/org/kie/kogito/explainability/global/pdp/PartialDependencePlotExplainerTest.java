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
package org.kie.kogito.explainability.global.pdp;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import org.junit.jupiter.api.Test;
import org.kie.kogito.explainability.TestUtils;
import org.kie.kogito.explainability.model.DataDistribution;
import org.kie.kogito.explainability.model.Feature;
import org.kie.kogito.explainability.model.FeatureFactory;
import org.kie.kogito.explainability.model.Output;
import org.kie.kogito.explainability.model.PartialDependenceGraph;
import org.kie.kogito.explainability.model.PredictionInput;
import org.kie.kogito.explainability.model.PredictionOutput;
import org.kie.kogito.explainability.model.PredictionProvider;
import org.kie.kogito.explainability.model.PredictionProviderMetadata;
import org.kie.kogito.explainability.model.Type;
import org.kie.kogito.explainability.model.Value;
import org.kie.kogito.explainability.utils.DataUtils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class PartialDependencePlotExplainerTest {

    @Test
    void testPdpTextClassifier() {
        PartialDependencePlotExplainer partialDependencePlotProvider = new PartialDependencePlotExplainer();
        PredictionProvider modelInfo = TestUtils.getDummyTextClassifier();
        PredictionProviderMetadata metadata = new PredictionProviderMetadata() {
            @Override
            public DataDistribution getDataDistribution() {
                return DataUtils.generateRandomDataDistribution(10, 100, new Random());
            }

            @Override
            public PredictionInput getInputShape() {
                List<Feature> features = new LinkedList<>();
                features.add(FeatureFactory.newTextFeature("text", ""));
                return new PredictionInput(features);
            }

            @Override
            public PredictionOutput getOutputShape() {
                List<Output> outputs = new LinkedList<>();
                outputs.add(new Output("spam", Type.BOOLEAN, new Value<>(null), 0d));
                return new PredictionOutput(outputs);
            }
        };
        Collection<PartialDependenceGraph> pdps = partialDependencePlotProvider.explain(modelInfo, metadata);
        assertNotNull(pdps);
        for (PartialDependenceGraph pdp : pdps) {
            assertNotNull(pdp.getFeature());
            assertNotNull(pdp.getX());
            assertNotNull(pdp.getY());
            assertEquals(pdp.getX().length, pdp.getY().length);
        }
    }

}