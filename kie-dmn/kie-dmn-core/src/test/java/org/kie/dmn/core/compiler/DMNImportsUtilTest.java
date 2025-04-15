/*
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

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.net.URL;
import java.nio.file.Files;
import java.util.*;
import java.util.function.Function;

import javax.xml.namespace.QName;

import java.util.stream.Stream;
import org.drools.io.ClassPathResource;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.kie.api.io.Resource;
import org.kie.dmn.api.core.DMNCompiler;
import org.kie.dmn.api.core.DMNModel;
import org.kie.dmn.api.core.ast.BusinessKnowledgeModelNode;
import org.kie.dmn.core.ast.BusinessKnowledgeModelNodeImpl;
import org.kie.dmn.core.impl.DMNModelImpl;
import org.kie.dmn.core.pmml.EfestoPMMLUtils;
import org.kie.dmn.feel.util.Either;
import org.kie.dmn.model.api.BusinessKnowledgeModel;
import org.kie.dmn.model.api.Context;
import org.kie.dmn.model.api.ContextEntry;
import org.kie.dmn.model.api.Definitions;
import org.kie.dmn.model.api.Expression;
import org.kie.dmn.model.api.FunctionDefinition;
import org.kie.dmn.model.api.FunctionKind;
import org.kie.dmn.model.api.Import;
import org.kie.dmn.model.api.InformationItem;
import org.kie.dmn.model.api.LiteralExpression;
import org.kie.dmn.model.v1_1.TImport;
import org.kie.dmn.model.v1_5.TBusinessKnowledgeModel;
import org.kie.dmn.model.v1_5.TConditional;
import org.kie.dmn.model.v1_5.TContext;
import org.kie.dmn.model.v1_5.TContextEntry;
import org.kie.dmn.model.v1_5.TFunctionDefinition;
import org.kie.dmn.model.v1_5.TInformationItem;
import org.kie.dmn.model.v1_5.TLiteralExpression;
import org.kie.efesto.common.core.storage.ContextStorage;
import org.kie.pmml.api.identifiers.LocalComponentIdPmml;

import static org.assertj.core.api.Assertions.assertThat;
import static org.kie.dmn.core.util.DMNTestUtil.getRelativeResolver;
import static org.mockito.Mockito.mock;

class DMNImportsUtilTest {

    private static final String dmnPmmlModelName = "TestRegressionDMN";
    private static final String dmnPmmlFileName = "KiePMMLRegression";

    private static final String dmnPmmlNameSpace =  "https://kiegroup.org/dmn/_51A1FD67-8A67-4332-9889-B718BE8B7456";
    private static final String dmnPmmlFullFileName = String.format("%s.dmn", dmnPmmlFileName);
    private static final String dmnPmmlFullPathFileName = String.format("valid_models/DMNv1_x/pmml/%s", dmnPmmlFullFileName);

    private static final String pmmlImportedName = "TestRegression";
    protected static final String pmmlModelName =  "LinReg";
    protected static final String pmmlFileName = "test_regression";
    protected static final String pmmlFullFileName = String.format("%s.pmml", pmmlFileName);
    protected static final String pmmlFullPathFileName = String.format("valid_models/DMNv1_x/pmml/%s", pmmlFullFileName);

    private static DMNCompiler dMNCompiler;

    @BeforeAll
    static void setup() {
        dMNCompiler = new DMNCompilerImpl();
    }

    @BeforeEach
    public  void init() {
        ContextStorage.reset();
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
    void resolvePMMLImportTypeWithRelativeResolver() throws IOException {
        DMNModelImpl model = getDMNModelWithUnresolvedPMMLImport();
        Import anImport =getImportForUnresolvedPMMLImport();
        Function<String, Reader> relativeResolver = getRelativeResolverForPMMLImport();
        DMNCompilerConfigurationImpl dmnCompilerConfig = (DMNCompilerConfigurationImpl)((DMNCompilerImpl)dMNCompiler).getDmnCompilerConfig();
        assertThat(model.getPmmlImportInfo()).isEmpty();
        DMNImportsUtil.resolvePMMLImportType(model, getDMNDefinitionsForDMNWithPMMLImport(), anImport, relativeResolver, dmnCompilerConfig);
        assertThat(model.getPmmlImportInfo()).hasSize(1).containsOnlyKeys(anImport.getName());
    }

    @Test
    void resolvePMMLImportTypeWithoutRelativeResolver() throws IOException {
        DMNModelImpl model = getDMNModelWithUnresolvedPMMLImport();
        Import anImport = getImportForUnresolvedPMMLImport();
        DMNCompilerConfigurationImpl dmnCompilerConfig = (DMNCompilerConfigurationImpl)((DMNCompilerImpl)dMNCompiler).getDmnCompilerConfig();
        assertThat(model.getPmmlImportInfo()).isEmpty();
        DMNImportsUtil.resolvePMMLImportType(model, getDMNDefinitionsForDMNWithPMMLImport(), anImport, null, dmnCompilerConfig);
        assertThat(model.getPmmlImportInfo()).hasSize(1).containsOnlyKeys(pmmlImportedName);
    }


    @Test
    void resolvePMMLImportTypeFromRelativeResolver() throws IOException {
        DMNModelImpl model = getDMNModelWithUnresolvedPMMLImport();
        Import anImport = getImportForUnresolvedPMMLImport();
        Function<String, Reader> relativeResolver = getRelativeResolverForPMMLImport();
        DMNCompilerConfigurationImpl dmnCompilerConfig = (DMNCompilerConfigurationImpl)((DMNCompilerImpl)dMNCompiler).getDmnCompilerConfig();
        assertThat(model.getPmmlImportInfo()).isEmpty();
        DMNImportsUtil.resolvePMMLImportTypeFromRelativeResolver(model, getDMNDefinitionsForDMNWithPMMLImport(), anImport, relativeResolver, dmnCompilerConfig);
        assertThat(model.getPmmlImportInfo()).hasSize(1).containsOnlyKeys(anImport.getName());
    }

    @Test
    void resolvePMMLImportTypeFromModelLocalUriId() throws IOException {
        String pmmlFilePath = "../pmml/test_tree_new.pmml";
        URL resource = DMNImportsUtilTest.class.getResource(pmmlFilePath);
        assertThat(resource).isNotNull();
        File pmmlFile = new File(resource.getFile());
        assertThat(pmmlFile).isNotNull().exists();

        String pmmlFileContent = Files.readString(pmmlFile.toPath());
        LocalComponentIdPmml modelLocalUriId = EfestoPMMLUtils.compilePMML(pmmlFileContent, pmmlFile.getName(), "SampleMineNew" , Thread.currentThread().getContextClassLoader());

        DMNModelImpl model = getDMNModelWithUnresolvedPMMLImport();
        Import anImport =getImportForUnresolvedPMMLImport();
        DMNCompilerConfigurationImpl dmnCompilerConfig = (DMNCompilerConfigurationImpl)((DMNCompilerImpl)dMNCompiler).getDmnCompilerConfig();
        assertThat(model.getPmmlImportInfo()).isEmpty();
        DMNImportsUtil.resolvePMMLImportTypeFromModelLocalUriId(model, anImport, modelLocalUriId, dmnCompilerConfig);
        assertThat(model.getPmmlImportInfo()).hasSize(1).containsOnlyKeys(anImport.getName());
    }

    @ParameterizedTest
    @MethodSource("getExpectedResult")
    void getPMMLModelFromDefinitions(Map.Entry<String, String> expectedResult) throws IOException {
        Definitions defs = getDMNDefinitionsForDMNWithPMMLImport();
        assertThat(DMNImportsUtil.getPMMLModelName(defs, expectedResult.getKey())).isEqualTo(expectedResult.getValue());
    }

    @ParameterizedTest
    @MethodSource("getExpectedResult")
    void getPMMLModelFromFunctionDef(Map.Entry<String, String> expectedResult) {
        FunctionDefinition functionDefinition = getFunctionDefinitionForPMMLImport();
        assertThat(DMNImportsUtil.getPMMLModelName(functionDefinition, expectedResult.getKey())).isEqualTo(expectedResult.getValue());
    }

    @Test
    void getPMMLModelFromFunctionDefNameFails() {
        FunctionDefinition functionDefinition = getFunctionDefinitionForPMMLImport();
        assertThat(DMNImportsUtil.getPMMLModelName(functionDefinition, "NOT_PMML_IMPORTED_NAME")).isNull();
    }

    @Test
    void getModelNameSuccess() {
        ContextEntry contextEntry = getPmmlModelNameContextEntry("model");
        Context context = getContext(contextEntry);
        assertThat(DMNImportsUtil.getModelName(context)).isEqualTo(pmmlModelName);
    }

    @Test
    void getModelNameFails() {
        ContextEntry contextEntry = getPmmlModelNameContextEntry("not-model");
        Context context = getContext(contextEntry);
        assertThat(DMNImportsUtil.getModelName(context)).isNull();
        //
        contextEntry = getContextEntry("not-model", new TConditional());
        context = getContext(contextEntry);
        assertThat(DMNImportsUtil.getModelName(context)).isNull();
    }

    @Test
    void referToPMMLImportedName() {
        ContextEntry contextEntry = getPmmlModelNameContextEntry("document");
        Context context = getContext(contextEntry);
        assertThat(DMNImportsUtil.referToPMMLImportedName(context, pmmlModelName)).isTrue();
        //
        assertThat(DMNImportsUtil.referToPMMLImportedName(context, "NOT_MODEL_NAME")).isFalse();
        //
        contextEntry = getPmmlModelNameContextEntry("not-document");
        context = getContext(contextEntry);
        assertThat(DMNImportsUtil.referToPMMLImportedName(context, pmmlModelName)).isFalse();
    }

    @Test
    void isDocumentContextEntry() {
        ContextEntry contextEntry = getPmmlModelNameContextEntry("document");
        assertThat(DMNImportsUtil.isDocumentContextEntry(contextEntry, pmmlModelName)).isTrue();
        //
        assertThat(DMNImportsUtil.isDocumentContextEntry(contextEntry, "NOT_MODEL_NAME")).isFalse();
        //
        contextEntry = getPmmlModelNameContextEntry("not-document");
        assertThat(DMNImportsUtil.isDocumentContextEntry(contextEntry, pmmlModelName)).isFalse();
    }

    @Test
    void isModelContextEntry() {
        ContextEntry contextEntry = getContextEntry("model", new TLiteralExpression());
        assertThat(DMNImportsUtil.isModelContextEntry(contextEntry)).isTrue();
        contextEntry = getContextEntry("not-model", new TLiteralExpression());
        assertThat(DMNImportsUtil.isModelContextEntry(contextEntry)).isFalse();
    }

    @Test
    void hasVariableName() {
        ContextEntry contextEntry = getContextEntry("variable");
        assertThat(DMNImportsUtil.hasVariableName(contextEntry, "variable")).isTrue();
        assertThat(DMNImportsUtil.hasVariableName(contextEntry, "not-variable")).isFalse();
    }

    private static Definitions getDMNDefinitionsForDMNWithPMMLImport() throws IOException {
        Resource resource = new ClassPathResource( dmnPmmlFullPathFileName,
                                                   DMNImportsUtilTest.class);
        return ((DMNCompilerImpl)dMNCompiler).getMarshaller().unmarshal(resource.getReader());
    }

    private static DMNModelImpl getDMNModelWithUnresolvedPMMLImport() {
        List<DMNModel> dmnModels = new ArrayList<>();
        Resource dmnResource = new ClassPathResource( "valid_models/DMNv1_5/Sample.dmn",
                                                      DMNImportsUtilTest.class);
        DMNModel simpleModel = dMNCompiler.compile( dmnResource, dmnModels);
        assertThat(simpleModel).isNotNull();


        String pmmlFilePath = "../pmml/test_tree_new.pmml";
        URL resource = DMNImportsUtilTest.class.getResource(pmmlFilePath);
        assertThat(resource).isNotNull();

        DMNModelImpl toReturn = new DMNModelImpl(simpleModel.getDefinitions(), dmnResource);
        assertThat(toReturn.getPmmlImportInfo()).isEmpty();
        // encapsulatedLogic
        FunctionDefinition encapsulatedLogic = getFunctionDefinitionForPMMLImport();
        // businessKnowledgeModel
        BusinessKnowledgeModel businessKnowledgeModel = new TBusinessKnowledgeModel();
        businessKnowledgeModel.setEncapsulatedLogic(encapsulatedLogic);
        businessKnowledgeModel.setName("BKM");
        businessKnowledgeModel.setParent(simpleModel.getDefinitions());
        // BusinessKnowledgeModelNode
        BusinessKnowledgeModelNode businessKnowledgeModelNode = new BusinessKnowledgeModelNodeImpl(businessKnowledgeModel);
        toReturn.addBusinessKnowledgeModel(businessKnowledgeModelNode);
        return toReturn;
    }

    private static FunctionDefinition getFunctionDefinitionForPMMLImport() {
        // document context entry
        ContextEntry documentContextEntry = getPmmlImportedNameContextEntry("document");
        // model context entry
        ContextEntry modelContextEntry = getPmmlModelNameContextEntry("model");
        // context
        Context context = getContext(documentContextEntry, modelContextEntry);
        FunctionDefinition toReturn = new TFunctionDefinition();
        toReturn.setKind(FunctionKind.PMML);
        toReturn.addChildren(context);
        return toReturn;
    }

    private static Import getImportForUnresolvedPMMLImport() {
        String pmmlFilePath = "../pmml/test_tree_new.pmml";
        URL resource = DMNImportsUtilTest.class.getResource(pmmlFilePath);
        assertThat(resource).isNotNull();
        File pmmlFile = new File(resource.getFile());
        Import anImport = new org.kie.dmn.model.v1_5.TImport();
        anImport.setLocationURI(pmmlFile.getAbsolutePath());
        anImport.setImportType("pmml");
        anImport.setName(pmmlImportedName);
        return anImport;
    }

    private static Function<String, Reader> getRelativeResolverForPMMLImport() throws IOException {
        String pmmlFilePath = "../pmml/test_tree_new.pmml";
        URL resource = DMNImportsUtilTest.class.getResource(pmmlFilePath);
        assertThat(resource).isNotNull();
        File pmmlFile = new File(resource.getFile());
        String pmmlFileContent = Files.readString(pmmlFile.toPath());
        return getRelativeResolver(pmmlFile.getAbsolutePath(), pmmlFileContent);
    }

    private static Import makeImport(final String namespace, final String name, final String modelName) {
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

    private static Stream<Map.Entry<String, String>> getExpectedResult() {
        Map<String, String> toReturn = new HashMap<>();
        toReturn.put(pmmlImportedName, pmmlModelName);
        toReturn.put("NOT_IMPORTED_NAME", null);
        return toReturn.entrySet().stream();
    }

    private static Context getContext(ContextEntry ... contextEntries) {
        Context toReturn = new TContext();
        for (ContextEntry contextEntry : contextEntries) {
            toReturn.getContextEntry().add(contextEntry);
        }
        return toReturn;
    }

    private static ContextEntry getPmmlImportedNameContextEntry(String variableName) {
        ContextEntry toReturn = getContextEntry(variableName);
        toReturn.setExpression(getPmmlImportedNameLiteralExpression());
        return toReturn;
    }

    private static ContextEntry getPmmlModelNameContextEntry(String variableName) {
        ContextEntry toReturn = getContextEntry(variableName);
        toReturn.setExpression(getPmmlModelNameLiteralExpression());
        return toReturn;
    }

    private static ContextEntry getContextEntry(String variableName, Expression expression) {
        ContextEntry toReturn = getContextEntry(variableName);
        toReturn.setExpression(expression);
        return toReturn;
    }

    private static ContextEntry getContextEntry(String variableName) {
        InformationItem variable = getInformationItem(variableName);
        ContextEntry toReturn = new TContextEntry();
        toReturn.setVariable(variable);
        return toReturn;
    }

    private static InformationItem getInformationItem(String name) {
        InformationItem toReturn = new TInformationItem();
        toReturn.setName(name);
        return toReturn;
    }

    private static LiteralExpression getPmmlModelNameLiteralExpression() {
        LiteralExpression toReturn = new TLiteralExpression();
        toReturn.setText(String.format("\"%s\"", pmmlModelName));
        return toReturn;
    }

    private static LiteralExpression getPmmlImportedNameLiteralExpression() {
        LiteralExpression toReturn = new TLiteralExpression();
        toReturn.setText(String.format("\"%s\"", pmmlImportedName));
        return toReturn;
    }

}
