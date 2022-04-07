/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
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

package org.kie.kogito.explainability.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.junit.jupiter.api.Test;
import org.kie.kogito.explainability.model.Feature;
import org.kie.kogito.explainability.model.PerturbationContext;
import org.kie.kogito.explainability.model.PredictionInput;
import org.kie.kogito.explainability.model.Type;
import org.kie.kogito.explainability.model.Value;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class OneHotterTest {
    @Test
    void CollisionTest() {
        List<Value> fruits = List.of(
                new Value("avocado_"),
                new Value("banana"),
                new Value("carrot"),
                new Value("dragonfruit"),
                new Value(""));
        Random rn = new Random(101);

        List<PredictionInput> data = new ArrayList<>();
        for (int i = 0; i < 101; i++) {
            List<Feature> fs = new ArrayList<>();
            for (int j = 0; j < 8; j++) {
                fs.add(new Feature(String.format("Fruit _OHE_ %d", j), Type.CATEGORICAL, fruits.get(rn.nextInt(5))));
            }
            data.add(new PredictionInput(fs));
        }
        PerturbationContext pc = new PerturbationContext(new Random(), 0);
        OneHotter oh = new OneHotter(data, pc);
        assertTrue(true);
    }

    @Test
    void CollisionProxyTest() {
        List<Value> fruits = List.of(
                new Value("avocado_"),
                new Value("banana"),
                new Value("carrot"),
                new Value("dragonfruit"),
                new Value(""));
        Random rn = new Random(101);

        List<PredictionInput> data = new ArrayList<>();
        for (int i = 0; i < 101; i++) {
            List<Feature> fs = new ArrayList<>();
            for (int j = 0; j < 8; j++) {
                fs.add(new Feature(String.format("Fruit _OHEPROXY %d", j), Type.CATEGORICAL, fruits.get(rn.nextInt(5))));
            }
            data.add(new PredictionInput(fs));
        }
        PerturbationContext pc = new PerturbationContext(new Random(), 0);
        OneHotter oh = new OneHotter(data, pc);
        assertTrue(true);
    }

    @Test
    void MixedFeatureTest() {
        List<Value> fruits = List.of(
                new Value("avocado_"),
                new Value("banana"),
                new Value("carrot"),
                new Value("dragonfruit"),
                new Value(""));
        Random rn = new Random(101);

        List<PredictionInput> data = new ArrayList<>();
        for (int i = 0; i < 101; i++) {
            List<Feature> fs = new ArrayList<>();
            for (int j = 0; j < 8; j++) {
                fs.add(new Feature(String.format("Fruit %d", j), Type.CATEGORICAL, fruits.get(rn.nextInt(5))));
            }
            fs.add(new Feature("Numeric", Type.NUMBER, new Value(i)));
            data.add(new PredictionInput(fs));
        }
        PerturbationContext pc = new PerturbationContext(new Random(), 0);
        OneHotter oh = new OneHotter(data, pc);
        List<PredictionInput> encoded = oh.oneHotEncode(data, false);
        List<PredictionInput> encodedProxy = oh.oneHotEncode(data, true);
        List<PredictionInput> decoded = oh.oneHotDecode(encoded, false);
        List<PredictionInput> decodedProxy = oh.oneHotDecode(encodedProxy, true);

        assertEquals(decoded, data);
        assertEquals(decodedProxy, data);
    }

}
