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

package org.kie.dmn.core.compiler;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.kie.dmn.api.core.DMNModel;
import org.kie.dmn.api.core.DMNRuntime;
import org.kie.dmn.backend.marshalling.v1x.DMNMarshallerFactory;
import org.kie.dmn.core.impl.DMNModelImpl;
import org.kie.dmn.core.util.DMNRuntimeUtil;
import org.kie.dmn.model.api.*;
import org.kie.dmn.model.v1_5.TImport;
import org.kie.dmn.model.v1_5.TInputData;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.kie.dmn.core.compiler.UnnamedImportUtils.addIfNotPresent;
import static org.kie.dmn.core.compiler.UnnamedImportUtils.isInUnnamedImport;
import static org.kie.dmn.core.compiler.UnnamedImportUtils.checkIfNotPresent;

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
            assertThat(definitions.getDecisionService()).allMatch(source -> added(source, DecisionService.class));
            assertThat(definitions.getBusinessContextElement()).allMatch(source -> added(source, BusinessContextElement.class));
            assertThat(definitions.getDrgElement()).allMatch(source -> added(source, DRGElement.class));
            assertThat(definitions.getImport()).allMatch(source -> added(source, Import.class));
            assertThat(definitions.getItemDefinition()).allMatch(source -> added(source, ItemDefinition.class));
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

    @Test
    void checkIfNotPresentWithMatchingName() {
        TInputData targetElement = new TInputData();
        targetElement.setName("modelName");
        TInputData sourceElement = new TInputData();
        sourceElement.setName("modelName");
        boolean result = UnnamedImportUtils.checkIfNotPresent(List.of(targetElement), sourceElement, TInputData.class);
        assertThat(result).isFalse();
    }

    @Test
    void checkIfNotPresentWithNonMatchingName() {
        TInputData targetElement = new TInputData();
        targetElement.setName("targetName");
        TInputData sourceElement = new TInputData();
        sourceElement.setName("sourceName");
        boolean result = UnnamedImportUtils.checkIfNotPresent(List.of(targetElement), sourceElement, TInputData.class);
        assertThat(result).isTrue();
    }

    @Test
    void checkIfNotPresentWithNamedImport() {
        TImport unnamedImport = new TImport();
        unnamedImport.setName("targetName");
        TImport source = new TImport();
        source.setName("sourceName");
        boolean result = UnnamedImportUtils.checkIfNotPresent(List.of(unnamedImport), source, TImport.class);
        assertThat(result).isTrue();
    }

    @Test
    void checkIfNotPresentWithEmptyNamedImport() {
        TImport unnamedImport = new TImport();
        unnamedImport.setName("");
        TImport source = new TImport();
        source.setName("");
        boolean result = UnnamedImportUtils.checkIfNotPresent(List.of(unnamedImport), source, TImport.class);
        assertThat(result).isTrue();
    }

    @Test
    void checkIfNotPresentWithDifferentTargetClass() {
        TInputData targetElement = new TInputData();
        targetElement.setName("modelName");
        TImport source = new TImport();
        source.setName("modelName");
        assertThatThrownBy(() -> UnnamedImportUtils.checkIfNotPresent(List.of(targetElement), source, DecisionService.class))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("type mismatch");
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
            assertThat(importedDefinitions.getDecisionService()).noneMatch(definition -> added(importingDefinitions.getDecisionService(), definition, DecisionService.class));
            assertThat(importedDefinitions.getBusinessContextElement()).noneMatch(definition -> added(importingDefinitions.getBusinessContextElement(), definition, BusinessContextElement.class));
            assertThat(importedDefinitions.getDrgElement()).noneMatch(definition -> added(importingDefinitions.getDrgElement(), definition, DRGElement.class));
            assertThat(importedDefinitions.getImport()).noneMatch(definition -> added(importingDefinitions.getImport(), definition, Import.class));
            assertThat(importedDefinitions.getItemDefinition()).noneMatch(definition -> added(importingDefinitions.getItemDefinition(), definition, ItemDefinition.class));
        }
    }

    private  <T extends NamedElement> boolean added(T source, Class expectedClass) {
        return added(new ArrayList<>(), source, expectedClass);
    }

    private  <T extends NamedElement> boolean added(Collection<T> target, T source, Class expectedClass) {
        addIfNotPresent(target, source, expectedClass);
        return target.contains(source);
    }

    private  <T extends NamedElement> boolean checkIfNotPresent(Collection<T> target, T source, Class expectedClass) {
        return UnnamedImportUtils.checkIfNotPresent(target, source, expectedClass);
    }

}