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

import org.junit.jupiter.api.Test;
import org.kie.kogito.explainability.local.lime.LimeConfig;
import org.kie.kogito.explainability.local.lime.LimeExplainer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class LimeExplainerProducerTest {

    @Test
    void produce() {
        LimeExplainerProducer producer = new LimeExplainerProducer(1, 2, 10);
        LimeExplainer limeExplainer = producer.produce();

        assertNotNull(limeExplainer);
        assertEquals(1, limeExplainer.getLimeConfig().getNoOfSamples());
        assertEquals(2, limeExplainer.getLimeConfig().getPerturbationContext().getNoOfPerturbations());
        assertEquals(LimeConfig.DEFAULT_NO_OF_RETRIES, limeExplainer.getLimeConfig().getNoOfRetries());
    }
}
