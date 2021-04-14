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
package org.kie.kogito.explainability.models;

import org.junit.jupiter.api.Test;
import org.kie.kogito.explainability.TestUtils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ExplainabilityRequestTest {

    @Test
    public void testLIMERequestConversion() {
        ExplainabilityRequest request = ExplainabilityRequest.from(TestUtils.LIME_REQUEST_DTO);

        assertEquals(TestUtils.LIME_REQUEST.getExecutionId(), request.getExecutionId());
        assertEquals(TestUtils.LIME_REQUEST.getServiceUrl(), request.getServiceUrl());
        assertEquals(TestUtils.LIME_REQUEST.getModelIdentifier().getResourceId(), request.getModelIdentifier().getResourceId());
        assertEquals(TestUtils.LIME_REQUEST.getModelIdentifier().getResourceType(), request.getModelIdentifier().getResourceType());
        assertEquals(TestUtils.LIME_REQUEST.getInputs(), request.getInputs());
        assertEquals(TestUtils.LIME_REQUEST.getOutputs(), request.getOutputs());
    }

    @Test
    public void testCounterfactualRequestConversion() {
        assertThrows(IllegalArgumentException.class, () -> ExplainabilityRequest.from(TestUtils.COUNTERFACTUAL_REQUEST_DTO));
    }
}
