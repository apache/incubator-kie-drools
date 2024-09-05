/**
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

package org.kie.dmn.core.compiler;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;

import org.junit.jupiter.api.Test;
import org.kie.dmn.api.core.DMNModel;
import org.kie.dmn.api.core.DMNRuntime;
import org.kie.dmn.backend.marshalling.v1x.DMNMarshallerFactory;
import org.kie.dmn.core.impl.DMNModelImpl;
import org.kie.dmn.core.util.DMNRuntimeUtil;
import org.kie.dmn.model.api.Definitions;
import org.kie.dmn.model.api.NamedElement;

import static org.assertj.core.api.Assertions.assertThat;
import static org.kie.dmn.core.compiler.UnnamedImportUtils.addIfNotPresent;
import static org.kie.dmn.core.compiler.UnnamedImportUtils.isInUnnamedImport;

class UnnamedImportUtilsTest {

    @Test
    void isInUnnamedImportTrueWithHrefNamespace() {
        commonIsInUnnamedImportTrue("valid_models/DMNv1_5/Importing_EmptyNamed_Model_With_Href_Namespace.dmn",
                                    "valid_models/DMNv1_5/Imported_Model_Unamed.dmn");
    }

    @Test
    void isInUnnamedImportTrueWithoutHrefNamespace() {
        commonIsInUnnamedImportTrue("valid_models/DMNv1_5/Importing_EmptyNamed_Model_Without_Href_Namespace.dmn",
                                    "valid_models/DMNv1_5/Imported_Model_Unamed.dmn");
    }

    @Test
    void isInUnnamedImportFalse() {
        final DMNRuntime runtime = DMNRuntimeUtil.createRuntimeWithAdditionalResources("valid_models/DMNv1_5/Importing_Named_Model.dmn",
                                                                                       this.getClass(),
                                                                                       "valid_models/DMNv1_5/Imported_Model_Unamed.dmn");

        final DMNModel importedModel = runtime.getModel("http://www.trisotech.com/dmn/definitions/_f27bb64b-6fc7-4e1f-9848-11ba35e0df44",
                                                        "Imported Model");
        assertThat(importedModel).isNotNull();
        final DMNModelImpl importingModel = (DMNModelImpl)runtime.getModel("http://www.trisotech.com/dmn/definitions/_f79aa7a4-f9a3-410a-ac95-bea496edabgc",
                                                                           "Importing named Model");
        assertThat(importingModel).isNotNull();
        assertThat(importedModel.getDecisions()).noneMatch(node -> isInUnnamedImport(node, importingModel));
        assertThat(importedModel.getBusinessKnowledgeModels()).noneMatch(node -> isInUnnamedImport(node, importingModel));
        assertThat(importedModel.getDecisionServices()).noneMatch(node -> isInUnnamedImport(node, importingModel));
        assertThat(importedModel.getInputs()).noneMatch(node -> isInUnnamedImport(node, importingModel));
        assertThat(importedModel.getItemDefinitions()).noneMatch(node -> isInUnnamedImport(node, importingModel));
    }

    @Test
    void addIfNotPresentTrue() throws IOException {
        URL importedModelFileResource = Thread.currentThread().getContextClassLoader().getResource(
                "valid_models/DMNv1_5/Imported_Model_Unamed.dmn");
        assertThat(importedModelFileResource).isNotNull();
        try (InputStream is = importedModelFileResource.openStream()) {
            String xml = new String(is.readAllBytes(), StandardCharsets.UTF_8);
            Definitions definitions = DMNMarshallerFactory.newDefaultMarshaller().unmarshal(xml);
            assertThat(definitions.getDecisionService()).allMatch(definition -> added(definition));
            assertThat(definitions.getBusinessContextElement()).allMatch(definition -> added(definition));
            assertThat(definitions.getDrgElement()).allMatch(definition -> added(definition));
            assertThat(definitions.getImport()).allMatch(definition -> added(definition));
            assertThat(definitions.getItemDefinition()).allMatch(definition -> added(definition));
        }
    }

    @Test
    void addIfNotPresentFalseWithHrefNamespace() throws IOException {
        commonAddIfNotPresentFalse("valid_models/DMNv1_5/Importing_EmptyNamed_Model_With_Href_Namespace.dmn",
                                    "valid_models/DMNv1_5/Imported_Model_Unamed.dmn");
    }

    @Test
    void addIfNotPresentFalseWithoutHrefNamespace() throws IOException {
        commonAddIfNotPresentFalse("valid_models/DMNv1_5/Importing_EmptyNamed_Model_Without_Href_Namespace.dmn",
                                    "valid_models/DMNv1_5/Imported_Model_Unamed.dmn");
    }

    private void commonIsInUnnamedImportTrue(String importingModelRef, String importedModelRef) {
        final DMNRuntime runtime = DMNRuntimeUtil.createRuntimeWithAdditionalResources(importingModelRef,
                                                                                       this.getClass(),
                                                                                       importedModelRef);

        final DMNModel importedModel = runtime.getModel("http://www.trisotech.com/dmn/definitions/_f27bb64b-6fc7-4e1f" +
                                                                "-9848-11ba35e0df44",
                                                        "Imported Model");
        assertThat(importedModel).isNotNull();
        final DMNModelImpl importingModel = (DMNModelImpl) runtime.getModel("http://www.trisotech" +
                                                                                    ".com/dmn/definitions/_f79aa7a4" +
                                                                                    "-f9a3-410a-ac95-bea496edabgc",
                                                                            "Importing empty-named Model");
        assertThat(importingModel).isNotNull();
        assertThat(importedModel.getDecisions()).allMatch(node -> isInUnnamedImport(node, importingModel));
        assertThat(importedModel.getBusinessKnowledgeModels()).allMatch(node -> isInUnnamedImport(node, importingModel));
        assertThat(importedModel.getDecisionServices()).allMatch(node -> isInUnnamedImport(node, importingModel));
        assertThat(importedModel.getInputs()).allMatch(node -> isInUnnamedImport(node, importingModel));
        assertThat(importedModel.getItemDefinitions()).allMatch(node -> isInUnnamedImport(node, importingModel));
    }

    private void commonAddIfNotPresentFalse(String importingModelRef, String importedModelRef) throws IOException {
        final DMNRuntime runtime = DMNRuntimeUtil.createRuntimeWithAdditionalResources(importingModelRef,
                                                                                       this.getClass(),
                                                                                       importedModelRef);
        final DMNModelImpl importingModel = (DMNModelImpl)runtime.getModel("http://www.trisotech.com/dmn/definitions/_f79aa7a4-f9a3-410a-ac95-bea496edabgc",
                                                                           "Importing empty-named Model");
        assertThat(importingModel).isNotNull();

        Definitions importingDefinitions = importingModel.getDefinitions();
        URL importedModelFileResource = Thread.currentThread().getContextClassLoader().getResource(
                "valid_models/DMNv1_5/Imported_Model_Unamed.dmn");
        assertThat(importedModelFileResource).isNotNull();
        try (InputStream is = importedModelFileResource.openStream()) {
            String importedXml = new String(is.readAllBytes(), StandardCharsets.UTF_8);
            Definitions importedDefinitions = DMNMarshallerFactory.newDefaultMarshaller().unmarshal(importedXml);
            assertThat(importedDefinitions.getDecisionService()).noneMatch(definition -> added(importingDefinitions.getDecisionService(), definition));
            assertThat(importedDefinitions.getBusinessContextElement()).noneMatch(definition -> added(importingDefinitions.getBusinessContextElement(), definition));
            assertThat(importedDefinitions.getDrgElement()).noneMatch(definition -> added(importingDefinitions.getDrgElement(), definition));
            assertThat(importedDefinitions.getImport()).noneMatch(definition -> added(importingDefinitions.getImport(), definition));
            assertThat(importedDefinitions.getItemDefinition()).noneMatch(definition -> added(importingDefinitions.getItemDefinition(), definition));
        }
    }

    private  <T extends NamedElement> boolean added(T source) {
        return added(new ArrayList<>(), source);
    }

    private  <T extends NamedElement> boolean added(Collection<T> target, T source) {
        addIfNotPresent(target, source);
        return target.contains(source);
    }

}