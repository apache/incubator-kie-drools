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
package org.kie.kogito.explainability.handlers;

import org.junit.jupiter.api.Test;
import org.kie.kogito.explainability.local.LocalExplainer;
import org.kie.kogito.explainability.local.counterfactual.CounterfactualExplainer;
import org.kie.kogito.explainability.local.counterfactual.CounterfactualResult;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CounterfactualExplainerProducerTest {

    @Test
    void produce() {
        CounterfactualExplainerProducer producer = new CounterfactualExplainerProducer(0.01);
        LocalExplainer<CounterfactualResult> counterfactualExplainer = producer.produce();

        assertNotNull(counterfactualExplainer);
        assertTrue(counterfactualExplainer instanceof CounterfactualExplainer);
        assertEquals(0.01, ((CounterfactualExplainer) counterfactualExplainer).getCounterfactualConfig().getGoalThreshold());
    }

}
