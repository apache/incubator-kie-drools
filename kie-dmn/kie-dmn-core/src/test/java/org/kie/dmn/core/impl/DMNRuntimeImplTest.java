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
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.kie.api.io.Resource;
import org.kie.dmn.api.core.DMNContext;
import org.kie.dmn.api.core.DMNMessage;
import org.kie.dmn.api.core.DMNModel;
import org.kie.dmn.api.core.DMNResult;
import org.kie.dmn.api.core.DMNRuntime;
import org.kie.dmn.api.core.ast.InputDataNode;
import org.kie.dmn.core.api.DMNFactory;
import org.kie.dmn.core.internal.utils.DMNRuntimeBuilder;
import org.kie.dmn.core.util.DMNRuntimeUtil;
import org.kie.internal.io.ResourceFactory;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

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
        Optional<Set<DMNModelImpl.ModelImportTuple>> topmostModel = DMNRuntimeImpl.getTopmostModel((DMNModelImpl) model);
        topmostModel.ifPresent(set ->
                assertThat(set)
                        .extracting(DMNModelImpl.ModelImportTuple::getImportName)
                        .containsOnly("parentModel")
        );
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
        DMNResultImplFactory dmnResultFactory = new DMNResultImplFactory();
        DMNContext context = dmnRuntime.newContext();
        context.set("Person name", "Klaus");
        DMNResultImpl result = dmnResultFactory.newDMNResultImpl(model);
        result.setContext(context);
        DMNRuntimeImpl.populateResultContextWithTopmostParentsValues(result, (DMNModelImpl) model);

        DMNContext context2 = dmnRuntime.newContext();
        context2.set("Person", Map.of("name", "Klaus"));

        Map<String, Object> parentModel = Map.of("Person", Map.of("name", "Klaus"));
        Map<String, Object> childA = Map.of("parentModel", parentModel);
        Map<String, Object> childB = Map.of("parentModel", parentModel);
        context2.set("Child A", childA);
        context2.set("Child B", childB);

        assertThat(result.getContext()).isEqualTo(context2);

    }
}