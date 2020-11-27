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

import org.junit.jupiter.api.Test;
import org.kie.kogito.explainability.local.LocalExplainer;
import org.kie.kogito.explainability.local.lime.LimeConfig;
import org.kie.kogito.explainability.local.lime.LimeExplainer;
import org.kie.kogito.explainability.model.Saliency;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class LimeExplainerProducerTest {

    @Test
    void produce() {
        LimeExplainerProducer producer = new LimeExplainerProducer(1, 2);
        LocalExplainer<Map<String, Saliency>> limeExplainer = producer.produce();

        assertNotNull(limeExplainer);
        assertTrue(limeExplainer instanceof LimeExplainer);
        assertEquals(1, ((LimeExplainer) limeExplainer).getLimeConfig().getNoOfSamples());
        assertEquals(2, ((LimeExplainer) limeExplainer).getLimeConfig().getPerturbationContext().getNoOfPerturbations());
        assertEquals(LimeConfig.DEFAULT_NO_OF_RETRIES, ((LimeExplainer) limeExplainer).getLimeConfig().getNoOfRetries());
    }
}