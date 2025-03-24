/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.kie.dmn.core.compiler;

import java.util.*;
import java.util.function.Function;

import javax.xml.namespace.QName;

import org.drools.io.ClassPathResource;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.kie.api.io.Resource;
import org.kie.dmn.api.core.DMNCompiler;
import org.kie.dmn.api.core.DMNModel;
import org.kie.dmn.core.impl.DMNModelImpl;
import org.kie.dmn.feel.util.Either;
import org.kie.dmn.model.api.Definitions;
import org.kie.dmn.model.api.Import;
import org.kie.dmn.model.v1_1.TImport;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

class DMNImportsUtilTest {

    private static DMNCompiler dMNCompiler;

    @BeforeAll
    static void setup() {
        dMNCompiler = new DMNCompilerImpl();
    }

    @Test
    void nSonly() {
        final Import i = makeImport("ns1", null, null);
        final List<QName> available = Arrays.asList(new QName("ns1", "m1"),
                                                    new QName("ns2", "m2"),
                                                    new QName("ns3", "m3"));
        final Either<String, QName> result = DMNImportsUtil.resolveImportDMN(i, available, Function.identity());
        assertThat(result.isRight()).isTrue();
        assertThat(result.getOrElse(null)).isEqualTo(new QName("ns1", "m1"));
    }

    @Test
    void nSandModelName() {
        final Import i = makeImport("ns1", null, "m1");
        final List<QName> available = Arrays.asList(new QName("ns1", "m1"),
                                                    new QName("ns2", "m2"),
                                                    new QName("ns3", "m3"));
        final Either<String, QName> result = DMNImportsUtil.resolveImportDMN(i, available, Function.identity());
        assertThat(result.isRight()).isTrue();
        assertThat(result.getOrElse(null)).isEqualTo(new QName("ns1", "m1"));
    }

    @Test
    void nSandModelNameWithAlias() {
        final Import i = makeImport("ns1", "aliased", "m1");
        final List<QName> available = Arrays.asList(new QName("ns1", "m1"),
                                                    new QName("ns2", "m2"),
                                                    new QName("ns3", "m3"));
        final Either<String, QName> result = DMNImportsUtil.resolveImportDMN(i, available, Function.identity());
        assertThat(result.isRight()).isTrue();
        assertThat(result.getOrElse(null)).isEqualTo(new QName("ns1", "m1"));
    }

    @Test
    void nSnoModelNameWithAlias() {
        final Import i = makeImport("ns1", "mymodel", null);
        final List<QName> available = Arrays.asList(new QName("ns1", "m1"),
                                                    new QName("ns2", "m2"),
                                                    new QName("ns3", "m3"));
        final Either<String, QName> result = DMNImportsUtil.resolveImportDMN(i, available, Function.identity());
        assertThat(result.isRight()).isTrue();
        assertThat(result.getOrElse(null)).isEqualTo(new QName("ns1", "m1"));
    }

    @Test
    void nSandUnexistentModelName() {
        final Import i = makeImport("ns1", null, "boh");
        final List<QName> available = Arrays.asList(new QName("ns1", "m1"),
                                                    new QName("ns2", "m2"),
                                                    new QName("ns3", "m3"));
        final Either<String, QName> result = DMNImportsUtil.resolveImportDMN(i, available, Function.identity());
        assertThat(result.isLeft()).isTrue();
    }

    @Test
    void nSnoModelNameDefaultWithAlias2() {
        final Import i = makeImport("ns1", "boh", null);
        final List<QName> available = Arrays.asList(new QName("ns1", "m1"),
                                                    new QName("ns2", "m2"),
                                                    new QName("ns3", "m3"));
        final Either<String, QName> result = DMNImportsUtil.resolveImportDMN(i, available, Function.identity());
        assertThat(result.isRight()).isTrue();
        assertThat(result.getOrElse(null)).isEqualTo(new QName("ns1", "m1"));
    }

    @Test
    void locateInNS() {
        final Import i = makeImport("nsA", null, "m1");
        final List<QName> available = Arrays.asList(new QName("nsA", "m1"),
                                                    new QName("nsA", "m2"),
                                                    new QName("nsB", "m3"));
        final Either<String, QName> result = DMNImportsUtil.resolveImportDMN(i, available, Function.identity());
        assertThat(result.isRight()).isTrue();
        assertThat(result.getOrElse(null)).isEqualTo(new QName("nsA", "m1"));
    }

    @Test
    void locateInNSnoModelNameWithAlias() {
        final Import i = makeImport("nsA", "m1", null);
        final List<QName> available = Arrays.asList(new QName("nsA", "m1"),
                                                    new QName("nsA", "m2"),
                                                    new QName("nsB", "m3"));
        final Either<String, QName> result = DMNImportsUtil.resolveImportDMN(i, available, Function.identity());
        assertThat(result.isLeft()).isTrue();
    }

    @Test
    void locateInNSAliased() {
        final Import i = makeImport("nsA", "aliased", "m1");
        final List<QName> available = Arrays.asList(new QName("nsA", "m1"),
                                                    new QName("nsA", "m2"),
                                                    new QName("nsB", "m3"));
        final Either<String, QName> result = DMNImportsUtil.resolveImportDMN(i, available, Function.identity());
        assertThat(result.isRight()).isTrue();
        assertThat(result.getOrElse(null)).isEqualTo(new QName("nsA", "m1"));
    }

    @Test
    void locateInNSunexistent() {
        final Import i = makeImport("nsA", null, "boh");
        final List<QName> available = Arrays.asList(new QName("nsA", "m1"),
                                                    new QName("nsA", "m2"),
                                                    new QName("nsB", "m3"));
        final Either<String, QName> result = DMNImportsUtil.resolveImportDMN(i, available, Function.identity());
        assertThat(result.isLeft()).isTrue();
    }

    @Test
    void locateInNSnoModelNameWithAlias2() {
        final Import i = makeImport("nsA", "boh", null);
        final List<QName> available = Arrays.asList(new QName("nsA", "m1"),
                                                    new QName("nsA", "m2"),
                                                    new QName("nsB", "m3"));
        final Either<String, QName> result = DMNImportsUtil.resolveImportDMN(i, available, Function.identity());
        assertThat(result.isLeft()).isTrue();
    }

    @Test
    void locateInNSAliasedBadScenario() {
        final Import i = makeImport("nsA", "aliased", "mA");
        final List<QName> available = Arrays.asList(new QName("nsA", "mA"),
                                                    new QName("nsA", "mA"),
                                                    new QName("nsB", "m3"));
        final Either<String, QName> result = DMNImportsUtil.resolveImportDMN(i, available, Function.identity());
        assertThat(result.isLeft()).isTrue();
    }

    @Test
    void emptyDMNCollection() {
        final Import i = makeImport("nsA", "aliased", "mA");
        final Either<String, QName> result = DMNImportsUtil.resolveImportDMN(i, Collections.emptyList(), Function.identity());
        assertThat(result.isLeft()).isTrue();
    }

    private Import makeImport(final String namespace, final String name, final String modelName) {
        final Import i = new TImport();
        i.setNamespace(namespace);
        final Map<QName, String> addAttributes = new HashMap<>();
        if (name != null) {
            addAttributes.put(TImport.NAME_QNAME, name);
        }
        if (modelName != null) {
            addAttributes.put(TImport.MODELNAME_QNAME, modelName);
        }
        i.setAdditionalAttributes(addAttributes);
        final Definitions definitions = mock(Definitions.class);
        definitions.setNamespace("ParentDMNNamespace");
        definitions.setName("ParentDMN");
        i.setParent(definitions);
        return i;
    }

    @Test
    void checkLocatedDMNModel() {
        List<DMNModel> toMerge = new ArrayList<>();
        List<DMNModel> dmnModels = new ArrayList<>();
        String nameSpace = "http://www.trisotech.com/dmn/definitions/_f79aa7a4-f9a3-410a-ac95-bea496edabgc";
        Resource resource = new ClassPathResource( "valid_models/DMNv1_5/Importing_Named_Model.dmn",
                this.getClass());
        DMNModel importingModel = dMNCompiler.compile(resource, dmnModels);
        assertThat(importingModel).isNotNull();
        assertThat(importingModel.getNamespace()).isNotNull().isEqualTo(nameSpace);

        Import input = importingModel.getDefinitions().getImport().get(0);
        DMNModelImpl model = new DMNModelImpl(importingModel.getDefinitions(), resource);
        DMNModel located = new DMNModelImpl(importingModel.getDefinitions(), resource);
        DMNImportsUtil.checkLocatedDMNModel(input, located, model, toMerge);
        assertThat(importingModel).isNotNull();
        assertThat(importingModel.getNamespace()).isNotNull().isEqualTo(nameSpace);
        assertThat(toMerge).isEmpty();
    }

    @Test
    void resolveDMNImportType()  {
        List<DMNModel> toMerge = new ArrayList<>();
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

        Import input = importingModel.getDefinitions().getImport().get(0);
        DMNModelImpl model = new DMNModelImpl(importingModel.getDefinitions(), resource);
        DMNImportsUtil.resolveDMNImportType(input, dmnModels, model, toMerge);
        assertThat(model.getMessages()).isEmpty();
        assertThat(model.getImportAliasesForNS().entrySet().stream().findFirst())
                .isPresent().get().extracting(Map.Entry::getValue)
                .extracting(QName::getLocalPart).isNotNull().isEqualTo("Imported Model");

    }



    @Test
    void checkLocatedDMNModelWithAliasNull() {
        String namespace="http://www.trisotech.com/dmn/definitions/_f79aa7a4-f9a3-410a-ac95-bea496edabgc";
        List<DMNModel> toMerge = new ArrayList<>();
        List<DMNModel> dmnModels = new ArrayList<>();
        Resource resource = new ClassPathResource( "valid_models/DMNv1_5/Importing_EmptyNamed_Model_Without_Href_Namespace.dmn",
                this.getClass());
        DMNModel emptyNamedModel = dMNCompiler.compile( resource, dmnModels);
        assertThat(emptyNamedModel).isNotNull();
        dmnModels.add(emptyNamedModel);

        Import input = emptyNamedModel.getDefinitions().getImport().get(0);
        DMNModelImpl model = new DMNModelImpl(emptyNamedModel.getDefinitions(), resource);
        DMNModel located = new DMNModelImpl(emptyNamedModel.getDefinitions(), resource);
        DMNImportsUtil.checkLocatedDMNModel(input, located, model, toMerge);
        assertThat(emptyNamedModel).isNotNull();
        assertThat(toMerge).isNotEmpty();
        assertThat(toMerge.size()).isEqualTo(1);
        assertThat(toMerge.get(0).getNamespace()).isNotNull().isEqualTo(namespace);
    }

    @Test
    void resolvePMMLImportType() {
        List<DMNModel> dmnModels = new ArrayList<>();
        Resource dmnResource = new ClassPathResource( "../pmml/KiePMMLNewTree.dmn",
                this.getClass());
        DMNModel importingModel = dMNCompiler.compile( dmnResource, dmnModels);
        assertThat(importingModel).isNotNull();


        Import input = importingModel.getDefinitions().getImport().get(0);
        DMNModelImpl model = new DMNModelImpl(importingModel.getDefinitions(), dmnResource);

        Resource relativeResource = new ClassPathResource( "../pmml/test_tree_new.pmml",
                this.getClass());
        assertThat(model.getPmmlImportInfo()).isEmpty();
        DMNCompilerConfigurationImpl dmnCompilerConfig = (DMNCompilerConfigurationImpl)((DMNCompilerImpl)dMNCompiler).getDmnCompilerConfig();
        DMNImportsUtil.resolvePMMLImportType(model, input, relativeResource, dmnCompilerConfig);
        assertThat(model.getPmmlImportInfo()).hasSize(1).containsOnlyKeys("test_tree");
    }

}
