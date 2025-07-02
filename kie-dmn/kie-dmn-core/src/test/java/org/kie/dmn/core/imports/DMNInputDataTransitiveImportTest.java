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
        DMNModel model = dmnRuntime.getModel("https://kie.org/dmn/_D2213AB5-8DE3-4A44-B4FA-117DE18E82CE", "ImportingModel");

        assertThat(model).isNotNull();
        Map<String, Object> person = new HashMap<>();
        person.put("Name", "Klaus");
        person.put("Age", 27);

        DMNContext context = dmnRuntime.newContext();
        Map<String, Object> modelA = new HashMap<>();
        modelA.put("Person", person);

        Map<String, Object> modelB = new HashMap<>();
        modelB.put("ModelA", modelA);

        context.set("ModelB", modelB);
        context.set("ModelA", modelA);
        System.out.println(context.getAll());
        DMNResult result = dmnRuntime.evaluateAll(model, context);
        System.out.println(result.getDecisionResults());
        result.getMessages().forEach(System.out::println);

        assertThat(result.hasErrors()).isFalse();
        assertThat(result.getDecisionResultByName("ModelB.DB").getResult()).isEqualTo(true);
        assertThat(result.getDecisionResultByName("DC").getResult()).isEqualTo(true);
    }

    @Test
    void testInputDataError() throws IOException {
        List<Resource> resources = Arrays.asList(
                ResourceFactory.newClassPathResource("valid_models/DMNv1_6/multiple/InputDataModel.dmn"),
                ResourceFactory.newClassPathResource("valid_models/DMNv1_6/multiple/ImportedModel.dmn"),
                ResourceFactory.newClassPathResource("valid_models/DMNv1_6/multiple/ImportingModel.dmn")
        );

        DMNRuntime dmnRuntime = DMNRuntimeBuilder.fromDefaults().buildConfiguration().fromResources(resources).getOrElseThrow(RuntimeException::new);
        DMNModel model = dmnRuntime.getModel("https://kie.org/dmn/_D2213AB5-8DE3-4A44-B4FA-117DE18E82CE", "ImportingModel");

        assertThat(model).isNotNull();
        Map<String, Object> person = new HashMap<>();
        person.put("Name", "");
        person.put("Age", 27);

        DMNContext context = dmnRuntime.newContext();
        Map<String, Object> modelA = new HashMap<>();
        modelA.put("Person", person);

        Map<String, Object> modelB = new HashMap<>();
        modelB.put("ModelA", modelA);

        context.set("ModelB", modelB);
        context.set("ModelA", modelA);
        System.out.println(context.getAll());
        DMNResult result = dmnRuntime.evaluateAll(model, context);
        System.out.println(result.getDecisionResults());
        result.getMessages().forEach(System.out::println);

        assertThat(result.hasErrors()).isFalse();
        assertThat(result.getDecisionResultByName("ModelB.DB").getResult()).isEqualTo(true);
        assertThat(result.getDecisionResultByName("DC").getResult()).isEqualTo(false);
    }

    @Test
    void testInputDataTransitiveImportError() throws IOException {
        List<Resource> resources = Arrays.asList(
                ResourceFactory.newClassPathResource("valid_models/DMNv1_6/multiple/InputDataModel.dmn"),
                ResourceFactory.newClassPathResource("valid_models/DMNv1_6/multiple/ImportedModel.dmn"),
                ResourceFactory.newClassPathResource("valid_models/DMNv1_6/multiple/ImportingModel.dmn")
        );

        DMNRuntime dmnRuntime = DMNRuntimeBuilder.fromDefaults().buildConfiguration().fromResources(resources).getOrElseThrow(RuntimeException::new);
        DMNModel model = dmnRuntime.getModel("https://kie.org/dmn/_D2213AB5-8DE3-4A44-B4FA-117DE18E82CE", "ImportingModel");

        assertThat(model).isNotNull();
        Map<String, Object> person = new HashMap<>();
        person.put("Name", "Klaus");
        person.put("Age", 15);

        DMNContext context = dmnRuntime.newContext();
        Map<String, Object> modelA = new HashMap<>();
        modelA.put("Person", person);

        Map<String, Object> modelB = new HashMap<>();
        modelB.put("ModelA", modelA);

        context.set("ModelB", modelB);
        context.set("ModelA", modelA);
        System.out.println(context.getAll());
        DMNResult result = dmnRuntime.evaluateAll(model, context);
        System.out.println(result.getDecisionResults());
        result.getMessages().forEach(System.out::println);

        assertThat(result.hasErrors()).isFalse();
        assertThat(result.getDecisionResultByName("ModelB.DB").getResult()).isEqualTo(false);
        assertThat(result.getDecisionResultByName("DC").getResult()).isEqualTo(false);
    }
}
