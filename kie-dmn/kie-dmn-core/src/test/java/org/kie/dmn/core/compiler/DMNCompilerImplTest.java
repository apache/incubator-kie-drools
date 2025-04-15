/* Licensed to the Apache Software Foundation (ASF) under one
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
package org.kie.dmn.core.compiler;

import java.util.ArrayList;
import org.drools.io.ClassPathResource;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.kie.api.io.Resource;
import org.kie.dmn.api.core.DMNMessageType;
import org.kie.dmn.api.core.DMNModel;

import org.kie.dmn.core.impl.DMNModelImpl;
import org.kie.dmn.model.api.*;
import org.kie.dmn.model.v1_5.*;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.verification.VerificationMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

class DMNCompilerImplTest {

    public static final Logger LOG = LoggerFactory.getLogger(DMNCompilerImplTest.class);

    private static DMNCompilerImpl dMNCompiler;
    private static final String NAMESPACE = "http://www.montera.com.au/spec/DMN/local-hrefs";
    private static Definitions parent;

    @BeforeAll
    static void setup() {
        String modelName = "LocalHrefs";
        parent = new TDefinitions();
        parent.setName(modelName);
        parent.setNamespace(NAMESPACE);

        dMNCompiler = new DMNCompilerImpl();
    }

    @Test
    void getId() {
        String localPart = "reference";
        DMNElementReference elementReference = new TDMNElementReference();
        elementReference.setHref(String.format("%s#%s", NAMESPACE, localPart));
        elementReference.setParent(parent);
        String retrieved = DMNCompilerImpl.getId(elementReference);
        assertThat(retrieved).isNotNull().isEqualTo(localPart);

        String expected = String.format("%s#%s", "http://a-different-namespace", localPart);
        elementReference.setHref(expected);
        retrieved = DMNCompilerImpl.getId(elementReference);
        assertThat(retrieved).isNotNull().isEqualTo(expected);
    }

    @Test
    void getRootElement() {
        String localPart = "reference";
        DMNElementReference elementReference = new TDMNElementReference();
        String href = String.format("%s#%s", NAMESPACE, localPart);
        elementReference.setHref(href);
        elementReference.setParent(parent);
        Definitions retrieved = DMNCompilerImpl.getRootElement(elementReference);
        assertThat(retrieved).isNotNull().isEqualTo(parent);

        InformationRequirement informationRequirement = new TInformationRequirement();
        elementReference.setParent(informationRequirement);
        assertThatExceptionOfType(RuntimeException.class).isThrownBy(
                () -> DMNCompilerImpl.getRootElement(elementReference)).withMessageContaining
                ("Failed to get Definitions parent for org.kie.dmn.model.v1_5");
        informationRequirement.setParent(parent);
        retrieved = DMNCompilerImpl.getRootElement(elementReference);
        assertThat(retrieved).isNotNull().isEqualTo(parent);
    }

    @Test
    void compile() {
        List<DMNModel> dmnModels = new ArrayList<>();
        String nameSpace = "http://www.trisotech.com/dmn/definitions/_f27bb64b-6fc7-4e1f-9848-11ba35e0df44";
        Resource resource = new ClassPathResource( "valid_models/DMNv1_5/Imported_Model_Unamed.dmn",
                this.getClass());
        DMNModel importedModel = dMNCompiler.compile( resource, dmnModels);
        assertThat(importedModel).isNotNull();
        assertThat(importedModel.getNamespace()).isNotNull().isEqualTo(nameSpace);
        assertThat(importedModel.getMessages()).isEmpty();
    }

    @Test
    void compileWithUnknownTypeModelImports() {
        List<DMNModel> dmnModels = new ArrayList<>();
        String nameSpace = "http://www.trisotech.com/dmn/definitions/_f27bb64b-6fc7-4e1f-9848-11ba35e0df44";
        String modelName = "Imported Model";
        String importType = String.valueOf(DMNImportsUtil.ImportType.UNKNOWN);
        Resource resource = new ClassPathResource( "valid_models/DMNv1_5/Imported_Model_Unamed.dmn",
                this.getClass());
        DMNModel model = dMNCompiler.compile( resource, dmnModels);
        assertThat(model).isNotNull();
        Definitions dmnDefn = model.getDefinitions();
        addImport(dmnDefn, importType, nameSpace, modelName);
        dmnModels.add(model);
        model = dMNCompiler.compile(dmnDefn, resource, dmnModels);
        assertThat(model).isNotNull();
        assertThat(model.getName()).isNotNull().isEqualTo(modelName);
        assertThat(model.getMessages()).hasSize(1);
        assertThat(model.getMessages().get(0).getText()).isEqualTo("DMN: Import type unknown: 'UNKNOWN'. (Invalid FEEL syntax on the referenced expression) ");

    }

    @Test
    void compileWithImportingDmnModel() {
        List<DMNModel> dmnModels = new ArrayList<>();
        Resource resource = new ClassPathResource( "valid_models/DMNv1_5/Imported_Model_Unamed.dmn",
                this.getClass());
        DMNModel importedModel = dMNCompiler.compile( resource, dmnModels);
        assertThat(importedModel).isNotNull();
        dmnModels.add(importedModel);

        //imported model - Importing_Named_Model.dmn
        String nameSpace = "http://www.trisotech.com/dmn/definitions/_f79aa7a4-f9a3-410a-ac95-bea496edabgc";
        resource = new ClassPathResource( "valid_models/DMNv1_5/Importing_Named_Model.dmn",
                this.getClass());

        DMNModel importingModel = dMNCompiler.compile(resource, dmnModels);
        assertThat(importingModel).isNotNull();
        assertThat(importingModel.getNamespace()).isNotNull().isEqualTo(nameSpace);
        assertThat(importingModel.getMessages()).isEmpty();
    }

    @Test
    void compileImportingModelWithoutImportedModel()  {
        List<DMNModel> dmnModels = new ArrayList<>();
        String modelName = "Importing named Model";
        Resource resource = new ClassPathResource( "valid_models/DMNv1_5/Importing_Named_Model.dmn",
                this.getClass());
        DMNModel model = dMNCompiler.compile( resource, dmnModels);
        assertThat(model).isNotNull();
        assertThat(model.getName()).isNotNull().isEqualTo(modelName);

        Definitions dmnDefn = model.getDefinitions();
        dmnModels.add(model);
        model = dMNCompiler.compile(dmnDefn, resource, dmnModels);
        assertThat(model).isNotNull();
        assertThat(model.getName()).isNotNull().isEqualTo(modelName);
        assertThat(model.getMessages()).hasSize(5);
        assertThat(model.getMessages().get(0).getMessageType()).isEqualTo(DMNMessageType.IMPORT_NOT_FOUND);

    }

    @Test
    void compileWithInvalidModel() {
        List<DMNModel> dmnModels = new ArrayList<>();
        Resource resource = new ClassPathResource( "invalid_models/DMNv1_5/DMN-Invalid.dmn",
                this.getClass());
        DMNModel importedModel = dMNCompiler.compile( resource, dmnModels);
        assertThat(importedModel).isNotNull();
        assertThat(importedModel.getMessages()).isNotEmpty();
        assertThat(importedModel.getMessages()).hasSize(1);
        assertThat(importedModel.getMessages().get(0).getMessageType()).isEqualTo(DMNMessageType.ERR_COMPILING_FEEL);
    }

    @Test
    void iterateImportsWithDMNImport() {
        commonIterateImport(DMNImportsUtil.ImportType.DMN, null, times(1), never(), never());
    }

    @Test
    void iterateImportsWithPMMLImport() {
        DMNModelImpl mockedModel = mock(DMNModelImpl.class);
        commonIterateImport(DMNImportsUtil.ImportType.PMML, mockedModel, never(), times(1), never());
        verify(mockedModel, times(1)).setImportAliasForNS(anyString(), anyString(), anyString());
    }

    @Test
    void iterateImportsWithUnknownImport() {
        commonIterateImport(DMNImportsUtil.ImportType.UNKNOWN, null, never(), never(), times(1));
    }

    private void commonIterateImport(DMNImportsUtil.ImportType importType, DMNModelImpl model, VerificationMode dmnInvocation, VerificationMode pmmlInvocation, VerificationMode logErrorInvocation) {
        Definitions dmnDefs = new TDefinitions();
        addImport(dmnDefs, "importType", "nameSpace", "modelName");
        try (MockedStatic<DMNImportsUtil> mockDMNImportsUtil = Mockito.mockStatic(DMNImportsUtil.class)) {
            mockDMNImportsUtil.when(() -> DMNImportsUtil.logErrorMessage(Mockito.any(), Mockito.any())).thenAnswer(invocation -> {
                LOG.debug("Mocked static void logErrorMessage !");
                return null;
            });
            mockDMNImportsUtil.when(() -> DMNImportsUtil.whichImportType(Mockito.any())).thenAnswer(invocation -> {
                LOG.debug("Mocked static void whichImportType !");
                return importType;
            });
            dMNCompiler.iterateImports(dmnDefs, null, model, null );
            mockDMNImportsUtil.verify(() -> DMNImportsUtil.resolveDMNImportType(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any()), dmnInvocation);
            mockDMNImportsUtil.verify(() -> DMNImportsUtil.whichImportType(Mockito.any()), times(1));
            mockDMNImportsUtil.verify(() -> DMNImportsUtil.resolvePMMLImportType(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any()), pmmlInvocation);
            mockDMNImportsUtil.verify(() -> DMNImportsUtil.logErrorMessage(Mockito.any(), Mockito.any()), logErrorInvocation);
        }
    }

    private void addImport(Definitions dmnDefs, String importType, String nameSpace, String modelName) {
        dmnDefs.setName(modelName);
        Import toAdd = new TImport();
        toAdd.setNamespace(nameSpace);
        toAdd.setName(modelName);
        toAdd.setImportType(importType);
        toAdd.setParent(dmnDefs);
        toAdd.setLocationURI(importType);
        dmnDefs.getImport().add(toAdd);
    }

}




