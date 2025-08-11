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
import org.kie.dmn.api.core.DMNContext;
import org.kie.dmn.api.core.DMNModel;
import org.kie.dmn.api.core.DMNRuntime;
import org.kie.dmn.core.internal.utils.DMNRuntimeBuilder;
import org.kie.internal.io.ResourceFactory;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.kie.dmn.core.impl.DMNRuntimeImpl.getTopmostModel;

class DMNRuntimeImplTest {


    @Test
    void testGetTopmostModel() {
        List<Resource> resources = Arrays.asList(
                ResourceFactory.newClassPathResource("valid_models/DMNv1_6/ImportingNestedInputData.dmn"),
                ResourceFactory.newClassPathResource("valid_models/DMNv1_6/Child_A.dmn"),
                ResourceFactory.newClassPathResource("valid_models/DMNv1_6/Child_B.dmn"),
                ResourceFactory.newClassPathResource("valid_models/DMNv1_6/ParentModel.dmn")
        );

        DMNRuntime dmnRuntime =
                DMNRuntimeBuilder.fromDefaults().buildConfiguration().fromResources(resources).getOrElseThrow(RuntimeException::new);
        DMNModel model = dmnRuntime.getModel("http://www.trisotech.com/definitions/_10435dcd-8774-4575-a338" +
                "-49dd554a0928", "ImportingNestedInputData");
        Optional<Set<DMNModelImpl.ModelImportTuple>> topmostModel = getTopmostModel((DMNModelImpl) model);
        topmostModel.ifPresent(set ->
                assertThat(set)
                        .extracting(DMNModelImpl.ModelImportTuple::getImportName)
                        .containsOnly("parentModel")
        );
    }

    @Test
    void testPopulateResultContextWithTopmostParentsValues() {
        List<Resource> resources = Arrays.asList(
                ResourceFactory.newClassPathResource("valid_models/DMNv1_6/ImportingNestedInputData.dmn"),
                ResourceFactory.newClassPathResource("valid_models/DMNv1_6/Child_A.dmn"),
                ResourceFactory.newClassPathResource("valid_models/DMNv1_6/Child_B.dmn"),
                ResourceFactory.newClassPathResource("valid_models/DMNv1_6/ParentModel.dmn")
        );

        DMNRuntime dmnRuntime =
                DMNRuntimeBuilder.fromDefaults().buildConfiguration().fromResources(resources).getOrElseThrow(RuntimeException::new);
        DMNModel model = dmnRuntime.getModel("http://www.trisotech.com/definitions/_10435dcd-8774-4575-a338" +
                "-49dd554a0928", "ImportingNestedInputData");
        DMNResultImplFactory dmnResultFactory = new DMNResultImplFactory();
        DMNContext context = dmnRuntime.newContext();
        context.set("Person name", "Klaus");
        DMNResultImpl result = dmnResultFactory.newDMNResultImpl(model);
        result.setContext(context);
        DMNRuntimeImpl.populateResultContextWithTopmostParentsValues(result, (DMNModelImpl) model);

        DMNContext context2 = dmnRuntime.newContext();
        Map<String, Object> parentModel = Map.of("Person name", "Klaus");
        Map<String, Object> childA = Map.of("parentModel", parentModel);
        Map<String, Object> childB = Map.of("parentModel", parentModel);
        context2.set("Person name", "Klaus");
        context2.set("Child A", childA);
        context2.set("Child B", childB);

        assertThat(result.getContext()).usingRecursiveComparison().isEqualTo(context2);
    }

    @Test
    void testPopulateInputsFromTopmostModel() {
        List<Resource> resources = Arrays.asList(
                ResourceFactory.newClassPathResource("valid_models/DMNv1_6/ImportingNestedInputData.dmn"),
                ResourceFactory.newClassPathResource("valid_models/DMNv1_6/Child_A.dmn"),
                ResourceFactory.newClassPathResource("valid_models/DMNv1_6/Child_B.dmn"),
                ResourceFactory.newClassPathResource("valid_models/DMNv1_6/ParentModel.dmn")
        );

        DMNRuntime dmnRuntime =
                DMNRuntimeBuilder.fromDefaults().buildConfiguration().fromResources(resources).getOrElseThrow(RuntimeException::new);
        DMNModel importingModel = dmnRuntime.getModel("http://www.trisotech.com/definitions/_10435dcd-8774-4575-a338" +
                "-49dd554a0928", "ImportingNestedInputData");
        DMNModel dmnModel = dmnRuntime.getModel("http://www.trisotech.com/definitions/_ae5b3c17-1ac3-4e1d-b4f9-2cf861aec6d9", "ParentModel");

        Set<DMNModelImpl.ModelImportTuple> topmostModels = new HashSet<>();
        DMNModelImpl.ModelImportTuple topmostModelTuple = new DMNModelImpl.ModelImportTuple("parentModel", (DMNModelImpl) dmnModel);
        topmostModels.add(topmostModelTuple);

        DMNResultImplFactory dmnResultFactory = new DMNResultImplFactory();
        DMNContext context = dmnRuntime.newContext();
        context.set("Person name", "Klaus");
        DMNResultImpl result = dmnResultFactory.newDMNResultImpl(importingModel);
        result.setContext(context);

        DMNContext context2 = dmnRuntime.newContext();
        Map<String, Object> parentModel = Map.of("Person name", "Klaus");
        Map<String, Object> childA = Map.of("parentModel", parentModel);
        Map<String, Object> childB = Map.of("parentModel", parentModel);
        context2.set("Person name", "Klaus");
        context2.set("Child A", childA);
        context2.set("Child B", childB);

        DMNRuntimeImpl.populateInputsFromTopmostModel(result, (DMNModelImpl) importingModel, topmostModels);
        assertThat(result.getContext()).usingRecursiveComparison().isEqualTo(context2);
    }

    @Test
    void testPopulateContextWithInheritedData() {
        List<Resource> resources = Arrays.asList(
                ResourceFactory.newClassPathResource("valid_models/DMNv1_6/ImportingNestedInputData.dmn"),
                ResourceFactory.newClassPathResource("valid_models/DMNv1_6/Child_A.dmn"),
                ResourceFactory.newClassPathResource("valid_models/DMNv1_6/Child_B.dmn"),
                ResourceFactory.newClassPathResource("valid_models/DMNv1_6/ParentModel.dmn")
        );

        DMNRuntime dmnRuntime =
                DMNRuntimeBuilder.fromDefaults().buildConfiguration().fromResources(resources).getOrElseThrow(RuntimeException::new);
        DMNModel model = dmnRuntime.getModel("http://www.trisotech.com/definitions/_10435dcd-8774-4575-a338" +
                "-49dd554a0928", "ImportingNestedInputData");

        DMNContext context = dmnRuntime.newContext();
        Map<String, Object> parentModel = Map.of("Person name", "Klaus");
        Map<String, Object> childA = Map.of("parentModel", parentModel);
        Map<String, Object> childB = Map.of("parentModel", parentModel);
        context.set("Person name", "Klaus");
        context.set("Child A", childA);
        context.set("Child B", childB);

        DMNContext toPopulate = dmnRuntime.newContext();
        toPopulate.set("Person name", "Klaus");

        Map mappedData = Map.of("Person name", "Klaus");
        String importName = "parentModel";
        String topmostNamespace = "http://www.trisotech.com/definitions/_ae5b3c17-1ac3-4e1d-b4f9-2cf861aec6d9";

        DMNRuntimeImpl.populateContextWithInheritedData(toPopulate, mappedData, importName, topmostNamespace, (DMNModelImpl) model);
        assertThat(toPopulate).usingRecursiveComparison().isEqualTo(context);
    }

}