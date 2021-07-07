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

package org.kie.kogito.explainability.model;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

import org.junit.jupiter.api.Test;
import org.kie.kogito.explainability.local.shap.ShapConfig;

import static org.junit.jupiter.api.Assertions.assertSame;

class ShapPredictionTest {

    @Test
    void getConfig() {
        PerturbationContext pc = new PerturbationContext(new Random(1), 0);
        List<Feature> fs = Arrays.asList(
                FeatureFactory.newNumericalFeature("f", 1.),
                FeatureFactory.newNumericalFeature("f", 2.));
        Output o = new Output("o", Type.NUMBER, new Value(1.), 1.);
        List<Output> os = Arrays.asList(o, o);
        PredictionInput pi = new PredictionInput(fs);
        PredictionOutput po = new PredictionOutput(os);
        List<PredictionInput> pis = Arrays.asList(pi, pi);

        ShapConfig skConfig = ShapConfig.builder()
                .withLink(ShapConfig.LinkType.IDENTITY)
                .withBackground(pis)
                .withPC(pc)
                .withNSamples(100)
                .build();
        ShapPrediction sp = new ShapPrediction(pi, po, skConfig);
        assertSame(skConfig, sp.getConfig());
    }

}
