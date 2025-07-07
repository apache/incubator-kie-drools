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
package org.kie.dmn.core.imports;

import org.junit.jupiter.api.Test;
import org.kie.api.io.Resource;
import org.kie.dmn.api.core.DMNContext;
import org.kie.dmn.api.core.DMNModel;
import org.kie.dmn.api.core.DMNResult;
import org.kie.dmn.api.core.DMNRuntime;
import org.kie.dmn.core.internal.utils.DMNRuntimeBuilder;
import org.kie.internal.io.ResourceFactory;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class DMNInputDataTransitiveImportTest {

    @Test
    void testInputDataTransitiveImport() throws IOException {
        List<Resource> resources = Arrays.asList(
                ResourceFactory.newClassPathResource("valid_models/DMNv1_6/multiple/InputDataModel.dmn"),
                ResourceFactory.newClassPathResource("valid_models/DMNv1_6/multiple/ImportedModel.dmn"),
                ResourceFactory.newClassPathResource("valid_models/DMNv1_6/multiple/ImportingModel.dmn")
        );

        DMNRuntime dmnRuntime = DMNRuntimeBuilder.fromDefaults().buildConfiguration().fromResources(resources).getOrElseThrow(RuntimeException::new);
        DMNModel model = dmnRuntime.getModel("https://kie.org/dmn/_161859A8-6836-427A-A55E-D4F271EEE6B9", "ImportingModel");

        assertThat(model).isNotNull();
        Map<String, Object> person = new HashMap<>();
        person.put("Name", "Klaus");
        person.put("Age", 27);

        DMNContext context = dmnRuntime.newContext();
        Map<String, Object> inputData = new HashMap<>();
        inputData.put("Person", person);

        Map<String, Object> importedModel = new HashMap<>();
        importedModel.put("InputData", inputData);

        context.set("Imported", importedModel);
        context.set("InputData", inputData);
        DMNResult result = dmnRuntime.evaluateAll(model, context);

        assertThat(result.hasErrors()).isFalse();
        assertThat(result.getDecisionResultByName("Imported.DB").getResult()).isEqualTo(true);
        assertThat(result.getDecisionResultByName("DC").getResult()).isEqualTo(true);
    }

    @Test
    void testInputDataWithInvalidName() throws IOException {
        List<Resource> resources = Arrays.asList(
                ResourceFactory.newClassPathResource("valid_models/DMNv1_6/multiple/InputDataModel.dmn"),
                ResourceFactory.newClassPathResource("valid_models/DMNv1_6/multiple/ImportedModel.dmn"),
                ResourceFactory.newClassPathResource("valid_models/DMNv1_6/multiple/ImportingModel.dmn")
        );

        DMNRuntime dmnRuntime = DMNRuntimeBuilder.fromDefaults().buildConfiguration().fromResources(resources).getOrElseThrow(RuntimeException::new);
        DMNModel model = dmnRuntime.getModel("https://kie.org/dmn/_161859A8-6836-427A-A55E-D4F271EEE6B9", "ImportingModel");

        assertThat(model).isNotNull();
        Map<String, Object> person = new HashMap<>();
        person.put("Name", "");
        person.put("Age", 27);

        DMNContext context = dmnRuntime.newContext();
        Map<String, Object> inputData = new HashMap<>();
        inputData.put("Person", person);

        Map<String, Object> importedModel = new HashMap<>();
        importedModel.put("InputData", inputData);

        context.set("Imported", importedModel);
        context.set("InputData", inputData);
        DMNResult result = dmnRuntime.evaluateAll(model, context);

        assertThat(result.hasErrors()).isFalse();
        assertThat(result.getDecisionResultByName("Imported.DB").getResult()).isEqualTo(true);
        assertThat(result.getDecisionResultByName("DC").getResult()).isEqualTo(false);
    }

    @Test
    void testInputDataWithInvalidAge() throws IOException {
        List<Resource> resources = Arrays.asList(
                ResourceFactory.newClassPathResource("valid_models/DMNv1_6/multiple/InputDataModel.dmn"),
                ResourceFactory.newClassPathResource("valid_models/DMNv1_6/multiple/ImportedModel.dmn"),
                ResourceFactory.newClassPathResource("valid_models/DMNv1_6/multiple/ImportingModel.dmn")
        );

        DMNRuntime dmnRuntime = DMNRuntimeBuilder.fromDefaults().buildConfiguration().fromResources(resources).getOrElseThrow(RuntimeException::new);
        DMNModel model = dmnRuntime.getModel("https://kie.org/dmn/_161859A8-6836-427A-A55E-D4F271EEE6B9", "ImportingModel");

        assertThat(model).isNotNull();
        Map<String, Object> person = new HashMap<>();
        person.put("Name", "Klaus");
        person.put("Age", 15);

        DMNContext context = dmnRuntime.newContext();
        Map<String, Object> InputData = new HashMap<>();
        InputData.put("Person", person);

        Map<String, Object> importedModel = new HashMap<>();
        importedModel.put("InputData", InputData);

        context.set("Imported", importedModel);
        context.set("InputData", InputData);
        DMNResult result = dmnRuntime.evaluateAll(model, context);

        assertThat(result.hasErrors()).isFalse();
        assertThat(result.getDecisionResultByName("Imported.DB").getResult()).isEqualTo(false);
        assertThat(result.getDecisionResultByName("DC").getResult()).isEqualTo(false);
    }

    @Test
    void testInputDataWithInvalidModel() throws IOException {
        List<Resource> resources = Arrays.asList(
                ResourceFactory.newClassPathResource("valid_models/DMNv1_6/multiple/InputDataModel.dmn"),
                ResourceFactory.newClassPathResource("invalid_models/DMNv1_6/InvalidModel.dmn"),
                ResourceFactory.newClassPathResource("invalid_models/DMNv1_6/ImportInvalidModel.dmn")
        );

        DMNRuntime dmnRuntime = DMNRuntimeBuilder.fromDefaults().buildConfiguration().fromResources(resources).getOrElseThrow(RuntimeException::new);
        DMNModel model = dmnRuntime.getModel("https://kie.org/dmn/_22506F59-EDB3-455F-A2B5-70E6F7C33ACB", "ImportInvalidModel");

        assertThat(model).isNotNull();
        Map<String, Object> person = new HashMap<>();
        person.put("Name", "Klaus");
        person.put("Age", 15);

        DMNContext context = dmnRuntime.newContext();
        Map<String, Object> InputData = new HashMap<>();
        InputData.put("Person", person);

        Map<String, Object> invalidModel = new HashMap<>();
        invalidModel.put("InputData", InputData);

        context.set("Invalid", invalidModel);
        context.set("InputData", InputData);
        DMNResult result = dmnRuntime.evaluateAll(model, context);

        assertThat(result.hasErrors()).isTrue();
        assertThat(result.getDecisionResultByName("Invalid.InvalidDecision").getResult()).isNull();
        assertThat(result.getDecisionResultByName("New Decision").getResult()).isNull();
    }
}