/**
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
package org.kie.dmn.core.compiler;


import org.drools.io.ClassPathResource;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.kie.api.io.Resource;
import org.kie.dmn.api.core.DMNCompiler;
import org.kie.dmn.api.core.DMNModel;

import org.kie.dmn.model.api.*;
import org.kie.dmn.model.v1_5.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.*;
import java.util.*;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatExceptionOfType;

@ExtendWith(MockitoExtension.class)
class DMNCompilerImplTest {

    private static DMNCompiler dMNCompiler;

    private static List<DRGElementCompiler> drgCompilers = new ArrayList<>();
    {
        drgCompilers.add( new InputDataCompiler() );
        drgCompilers.add( new BusinessKnowledgeModelCompiler() );
        drgCompilers.add( new DecisionCompiler() );
        drgCompilers.add( new DecisionServiceCompiler() );
        drgCompilers.add( new KnowledgeSourceCompiler() ); // keep last as it's a void compiler
    }

    private static final String nameSpace = "http://www.montera.com.au/spec/DMN/local-hrefs";
    private static Definitions parent;

    @BeforeAll
    static void setup() throws IOException {
        String modelName = "LocalHrefs";
        parent = new TDefinitions();
        parent.setName(modelName);
        parent.setNamespace(nameSpace);

        dMNCompiler = new DMNCompilerImpl();
    }

    @Test
    void getId() {
        String localPart = "reference";
        DMNElementReference elementReference = new TDMNElementReference();
        elementReference.setHref(String.format("%s#%s", nameSpace, localPart));
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
        String href = String.format("%s#%s", nameSpace, localPart);
        elementReference.setHref(href);
        elementReference.setParent(parent);
        Definitions retrieved = DMNCompilerImpl.getRootElement(elementReference);
        assertThat(retrieved).isNotNull().isEqualTo(parent);

        InformationRequirement informationRequirement = new TInformationRequirement();
        elementReference.setParent(informationRequirement);
        assertThatExceptionOfType(RuntimeException.class).isThrownBy(() -> DMNCompilerImpl.getRootElement(elementReference));

        informationRequirement.setParent(parent);
        retrieved = DMNCompilerImpl.getRootElement(elementReference);
        assertThat(retrieved).isNotNull().isEqualTo(parent);
    }

    @Test
    void compileTest()  {
        List<DMNModel> dmnModels = new ArrayList<>();
        Resource resource = new ClassPathResource( "valid_models/DMNv1_5/Sample.dmn",
                this.getClass());
        DMNModel model = dMNCompiler.compile( resource, dmnModels);
        assertThat(model).isNotNull();
    }

    @Test
    void compileTestWithInvalidModelImports() {
        List<DMNModel> dmnModels = new ArrayList<>();
        String nameSpace = "https://kie.apache.org/dmn/_857FE424-BEDA-4772-AB8E-2F4CDDB864AB";
        String modelName = "loan_pre_qualification";
        String importType = String.valueOf(ImportDMNResolverUtil.ImportType.DMN);
        Resource resource = new ClassPathResource( "valid_models/DMNv1_5/Sample.dmn",
                this.getClass());
        DMNModel model = dMNCompiler.compile( resource, dmnModels);
        assertThat(model).isNotNull();
        Definitions dmnDefn = model.getDefinitions();
        addImport(dmnDefn, importType, nameSpace, modelName);
        dmnModels.add(model);
        model = dMNCompiler.compile(dmnDefn, resource, dmnModels);
        assertThat(model).isNotNull();

    }

    @Test
    void compileTestWithDmnModelImports() {
        List<DMNModel> dmnModels = new ArrayList<>();
        String nameSpace = "https://kie.apache.org/dmn/_857FE424-BEDA-4772-AB8E-2F4CDDB864AB";
        String modelName = "loan_pre_qualification";
        String URI_DMN = "http://www.omg.org/spec/DMN/20180521/MODEL/";
        Resource resource = new ClassPathResource( "valid_models/DMNv1_5/Sample.dmn",
                this.getClass());
        DMNModel model = dMNCompiler.compile( resource, dmnModels);
        assertThat(model).isNotNull();
        Definitions dmnDefn = model.getDefinitions();
        addImport(dmnDefn, URI_DMN, nameSpace, modelName);

        dmnModels.add(model);
        model = dMNCompiler.compile(dmnDefn, resource, dmnModels);
        assertThat(model).isNotNull();
    }

    @Test
    void compileTestWithPmmlModelImports()  {
        List<DMNModel> dmnModels = new ArrayList<>();
        String nameSpace = "https://github.com/kiegroup/drools/kie-dmn/_A4BCA8B8-CF08-433F-93B2-A2598F19ECFF";
        String modelName = "Traffic Violation";
        String location_pmml = "http://www.dmg.org/PMML-4_3";
        //String URI_DMN = "http://www.omg.org/spec/DMN/20180521/MODEL/";
        Resource resource = new ClassPathResource( "valid_models/DMNv1_x/Traffic Violation.dmn",
                this.getClass());
        DMNModel model = dMNCompiler.compile( resource, dmnModels);
        assertThat(model).isNotNull();
        Definitions dmnDefs = model.getDefinitions();
        addImport(dmnDefs, location_pmml, nameSpace, modelName);

        dmnModels.add(model);
        model = dMNCompiler.compile(dmnDefs, resource, dmnModels);
        assertThat(model).isNotNull();
    }

    private void addImport(Definitions dmnDefs, String importType, String nameSpace, String modelName) {
        dmnDefs.setName(modelName);
        Import import1 = new TImport();
        import1.setNamespace(nameSpace);
        import1.setName(modelName);
        import1.setImportType(importType);
        import1.setParent(dmnDefs);
        import1.setLocationURI(importType);
        dmnDefs.getImport().add(import1);
    }

}




