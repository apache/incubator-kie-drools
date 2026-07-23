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
package org.kie.kogito.explainability;

import java.net.URI;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.kie.kogito.explainability.model.Feature;
import org.kie.kogito.explainability.model.PredictionOutput;
import org.kie.kogito.explainability.model.Type;
import org.kie.kogito.explainability.model.Value;

import io.vertx.core.json.JsonObject;
import io.vertx.mutiny.core.Vertx;
import io.vertx.mutiny.ext.web.client.WebClient;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyMap;
import static java.util.Collections.singletonList;
import static java.util.Collections.singletonMap;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.kie.kogito.explainability.TestUtils.LIME_REQUEST;

class RemotePredictionProviderTest {

    RemotePredictionProvider predictionProvider = new RemotePredictionProvider(LIME_REQUEST.getServiceUrl(),
            LIME_REQUEST.getModelIdentifier(),
            LIME_REQUEST.getOutputs(),
            null,
            null,
            null) {
        @Override
        protected WebClient getClient(Vertx vertx, URI uri) {
            return null;
        }
    };

    @Test
    void toPredictionOutput() {
        assertNull(predictionProvider.toPredictionOutput(null));
        assertNull(predictionProvider.toPredictionOutput(new JsonObject(emptyMap())));
        Map<String, Object> predictionMap = singletonMap("result", emptyMap());

        PredictionOutput predictionOutput = predictionProvider.toPredictionOutput(new JsonObject(predictionMap));
        assertNotNull(predictionOutput);
        assertEquals(LIME_REQUEST.getOutputs().size(), predictionOutput.getOutputs().size());
        assertEquals(Type.UNDEFINED, predictionOutput.getOutputs().get(0).getType());
    }

    @Test
    void toMap() {
        // simple test
        Feature simple = new Feature("simple", Type.NUMBER, new Value(10));
        Feature undefined = new Feature("undefined", Type.UNDEFINED, new Value(simple));
        Feature composite = new Feature("composite", Type.COMPOSITE, new Value(asList(simple, undefined)));
        List<Feature> features1 = asList(simple, undefined, composite);

        Map<String, Object> result1 = predictionProvider.toMap(features1);

        assertNotNull(result1);
        assertEquals(features1.size(), result1.size());
        assertTrue(result1.containsKey("simple"));
        assertEquals(10, result1.get("simple"));
        assertTrue(result1.containsKey("undefined"));
        assertTrue(result1.containsKey("composite"));
        assertTrue(result1.get("composite") instanceof Map);

        // context test
        Feature context = new Feature("context", Type.COMPOSITE, new Value(singletonList(simple)));
        Feature simple2 = new Feature("simple2", Type.BOOLEAN, new Value(true));
        List<Feature> features2 = asList(simple2, context);

        Map<String, Object> result2 = predictionProvider.toMap(features2);

        assertNotNull(result2);
        assertEquals(1, result2.size());
        assertTrue(result2.containsKey("simple"));
        assertFalse(result2.containsKey("simple2"));

        // multiple nesting test
        Feature nestedComposite = new Feature("nestedComposite", Type.COMPOSITE, new Value(asList(simple, composite)));
        List<Feature> features3 = asList(simple, nestedComposite);
        Map<String, Object> result3 = predictionProvider.toMap(features3);

        assertNotNull(result3);
        assertEquals(features3.size(), result3.size());
        assertTrue(result3.containsKey("simple"));
        assertEquals(10, result3.get("simple"));
        assertTrue(result3.containsKey("nestedComposite"));
        assertTrue(result3.get("nestedComposite") instanceof Map);
        @SuppressWarnings("unchecked")
        Map<String, Object> nestedCompositeMap = (Map<String, Object>) result3.get("nestedComposite");

        assertEquals(2, nestedCompositeMap.size());
        assertTrue(nestedCompositeMap.containsKey("simple"));
        assertEquals(10, nestedCompositeMap.get("simple"));
        assertTrue(nestedCompositeMap.containsKey("composite"));
        assertTrue(nestedCompositeMap.get("composite") instanceof Map);
    }
}
