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
package org.kie.kogito.trusty.storage.postgresql;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.kie.kogito.explainability.api.CounterfactualExplainabilityRequest;
import org.kie.kogito.explainability.api.CounterfactualExplainabilityResult;
import org.kie.kogito.explainability.api.LIMEExplainabilityResult;
import org.kie.kogito.trusty.storage.api.model.decision.DMNModelWithMetadata;
import org.kie.kogito.trusty.storage.api.model.decision.Decision;
import org.kie.kogito.trusty.storage.common.TrustyStorageService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;

public class PostgresStorageServiceTest {

    private DecisionsStorage decisionsStorage;
    private LIMEResultsStorage limeResultsStorage;
    private DMNModelWithMetadataStorage dmnModelWithMetadataStorage;
    private CounterfactualRequestsStorage counterfactualRequestsStorage;
    private CounterfactualResultsStorage counterfactualResultsStorage;

    private PostgresStorageService storageService;

    @BeforeEach
    public void setup() {
        this.decisionsStorage = mock(DecisionsStorage.class);
        this.limeResultsStorage = mock(LIMEResultsStorage.class);
        this.dmnModelWithMetadataStorage = mock(DMNModelWithMetadataStorage.class);
        this.counterfactualRequestsStorage = mock(CounterfactualRequestsStorage.class);
        this.counterfactualResultsStorage = mock(CounterfactualResultsStorage.class);

        this.storageService = new PostgresStorageService(decisionsStorage,
                limeResultsStorage,
                dmnModelWithMetadataStorage,
                counterfactualRequestsStorage,
                counterfactualResultsStorage);
    }

    @Test
    void testGetCacheWithName() {
        assertThrows(UnsupportedOperationException.class, () -> storageService.getCache("name"));
    }

    @Test
    void testGetCacheWithNameTypeRoot() {
        assertThrows(UnsupportedOperationException.class, () -> storageService.getCache("name", String.class, "String"));
    }

    @Test
    void testGetCacheWithNameType_Decisions() {
        assertEquals(decisionsStorage,
                storageService.getCache(TrustyStorageService.DECISIONS_STORAGE,
                        Decision.class));
    }

    @Test
    void testGetCacheWithNameType_LIMEResults() {
        assertEquals(limeResultsStorage,
                storageService.getCache(TrustyStorageService.LIME_RESULTS_STORAGE,
                        LIMEExplainabilityResult.class));
    }

    @Test
    void testGetCacheWithNameType_Models() {
        assertEquals(dmnModelWithMetadataStorage,
                storageService.getCache(TrustyStorageService.MODELS_STORAGE,
                        DMNModelWithMetadata.class));
    }

    @Test
    void testGetCacheWithNameType_CounterfactualExplainabilityRequests() {
        assertEquals(counterfactualRequestsStorage,
                storageService.getCache(TrustyStorageService.COUNTERFACTUAL_REQUESTS_STORAGE,
                        CounterfactualExplainabilityRequest.class));
    }

    @Test
    void testGetCacheWithNameType_CounterfactualExplainabilityResults() {
        assertEquals(counterfactualResultsStorage,
                storageService.getCache(TrustyStorageService.COUNTERFACTUAL_RESULTS_STORAGE,
                        CounterfactualExplainabilityResult.class));
    }

    @Test
    void testGetCacheWithNameType_UnknownType() {
        assertThrows(UnsupportedOperationException.class,
                () -> storageService.getCache("name",
                        String.class));
    }
}
