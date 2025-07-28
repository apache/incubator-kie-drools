/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.kie.dmn.core.impl;

import org.junit.jupiter.api.Test;
import org.kie.api.io.Resource;
import org.kie.dmn.api.core.DMNModel;
import org.kie.dmn.api.core.DMNRuntime;
import org.kie.dmn.core.internal.utils.DMNRuntimeBuilder;
import org.kie.internal.io.ResourceFactory;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class DMNRuntimeImplTest {

    @Test
    void testRetrieveContext() {
        List<List<String>> importChainAliases = List.of(List.of("Model B", "modelA"));

        Map<String, Object> context = new HashMap<>();
        context.put("Person name", "Klaus");
        Map<String, Object> filteredInputs = new HashMap<>();
        filteredInputs.put("Person name", "Klaus");

        Map<String, Object> expectedContext = new HashMap<>();
        Map<String, Object> modelA = new HashMap<>();
        modelA.put("Person name", "Klaus");
        expectedContext.put("Person name", "Klaus");
        Map<String, Object> modelB = new HashMap<>();
        modelB.put("modelA", modelA);
        expectedContext.put("Model B", modelB);

        Map<String, Object> updatedContext = DMNRuntimeImpl.retrieveContext(importChainAliases, context, filteredInputs);
        assertThat(updatedContext).isEqualTo(expectedContext);
    }

    @Test
    void testPopulateContextUsingAliases() {
        List<Resource> resources = Arrays.asList(
                ResourceFactory.newClassPathResource("valid_models/DMNv1_6/TransitiveInputs.dmn"),
                ResourceFactory.newClassPathResource("valid_models/DMNv1_6/Model_B.dmn"),
                ResourceFactory.newClassPathResource("valid_models/DMNv1_6/Model_B2.dmn"),
                ResourceFactory.newClassPathResource("valid_models/DMNv1_6/Say_hello_1ID1D.dmn")
        );

        DMNRuntime dmnRuntime = DMNRuntimeBuilder.fromDefaults().buildConfiguration().fromResources(resources).getOrElseThrow(RuntimeException::new);
        DMNModel model = dmnRuntime.getModel("https://kie.org/dmn/_E543B95F-4E02-40D4-956A-3F1EE8500EA9", "DMN_21F7093C-EEE5-4DB9-95CE-088789DC1CBF");
        assertThat(model).isNotNull();

        Map<String, Object> context = new HashMap<>();
        context.put("Person name", "Klaus");
        Map<String, Object> baseInputs = new HashMap<>();
        baseInputs.put("Person name", "Klaus");
        Map<String, Object> expectedContext = new HashMap<>();
        Map<String, Object> modelA = new HashMap<>();
        modelA.put("Person name", "Klaus");
        expectedContext.put("Person name", "Klaus");
        Map<String, Object> modelB = new HashMap<>();
        modelB.put("modelA", modelA);
        expectedContext.put("Model B", modelB);

        Map<String, Object> updatedContext = DMNRuntimeImpl.populateContextUsingAliases((DMNModelImpl) model, context, baseInputs);
        assertThat(updatedContext).isEqualTo(expectedContext);
    }

}