/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.pmml.models.drools.provider;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.dmg.pmml.DataDictionary;
import org.dmg.pmml.DataField;
import org.dmg.pmml.DerivedField;
import org.dmg.pmml.Field;
import org.dmg.pmml.PMML;
import org.dmg.pmml.TransformationDictionary;
import org.dmg.pmml.scorecard.Scorecard;
import org.drools.compiler.builder.impl.KnowledgeBuilderImpl;
import org.drools.compiler.lang.descr.CompositePackageDescr;
import org.drools.compiler.lang.descr.PackageDescr;
import org.drools.modelcompiler.builder.GeneratedFile;
import org.junit.BeforeClass;
import org.junit.Test;
import org.kie.pmml.api.enums.DATA_TYPE;
import org.kie.pmml.api.enums.PMML_MODEL;
import org.kie.pmml.api.exceptions.KiePMMLException;
import org.kie.pmml.compiler.api.dto.CommonCompilationDTO;
import org.kie.pmml.compiler.api.testutils.TestUtils;
import org.kie.pmml.compiler.commons.mocks.HasClassLoaderMock;
import org.kie.pmml.models.drools.ast.KiePMMLDroolsAST;
import org.kie.pmml.models.drools.ast.KiePMMLDroolsType;
import org.kie.pmml.models.drools.commons.implementations.HasKnowledgeBuilderMock;
import org.kie.pmml.models.drools.commons.model.KiePMMLDroolsModel;
import org.kie.pmml.models.drools.commons.model.KiePMMLDroolsModelWithSources;
import org.kie.pmml.models.drools.dto.DroolsCompilationDTO;
import org.kie.pmml.models.drools.tuples.KiePMMLOriginalTypeGeneratedType;

import static org.assertj.core.api.Assertions.assertThat;
import static org.kie.pmml.commons.Constants.PACKAGE_NAME;
import static org.kie.pmml.commons.utils.KiePMMLModelUtils.getSanitizedClassName;
import static org.kie.pmml.commons.utils.KiePMMLModelUtils.getSanitizedPackageName;
import static org.kie.pmml.compiler.api.CommonTestingUtils.getFieldsFromDataDictionaryAndTransformationDictionaryAndLocalTransformations;

public class DroolsModelProviderTest {

    private static final String SOURCE_1 = "SimpleScorecardWithTransformations.pmml";
    //  Needed to avoid Mockito usage
    private static final Map<String, String> SOURCE_MAP = new HashMap<>();
    private static PMML pmml;
    private static Scorecard scorecard;
    private static DroolsModelProvider<Scorecard, ? extends KiePMMLDroolsModel> droolsModelProvider;

    @BeforeClass
    public static void setup() throws Exception {
        pmml = TestUtils.loadFromFile(SOURCE_1);
        assertThat(pmml).isNotNull();
        scorecard = (Scorecard) pmml.getModels().get(0);
        assertThat(scorecard).isNotNull();
        droolsModelProvider = new DroolsModelProvider<Scorecard, KiePMMLDroolsModel>() {
            @Override
            public KiePMMLDroolsModel getKiePMMLDroolsModel(final DroolsCompilationDTO<Scorecard> compilationDTO) {
                //  Needed to avoid Mockito usage
                return new KiePMMLDroolsModelTest(compilationDTO.getFields(),
                                                  compilationDTO.getTransformationDictionary(),
                                                  compilationDTO.getModel(),
                                                  compilationDTO.getFieldTypeMap());
            }

            @Override
            public KiePMMLDroolsAST getKiePMMLDroolsAST(final List<Field<?>> fields,
                                                        final Scorecard model,
                                                        final Map<String, KiePMMLOriginalTypeGeneratedType> fieldTypeMap,
                                                        final List<KiePMMLDroolsType> types) {
                //  Needed to avoid Mockito usage
                return new KiePMMLDroolsASTTest(fields, model, fieldTypeMap, types);
            }

            @Override
            public Map<String, String> getKiePMMLDroolsModelSourcesMap(final DroolsCompilationDTO<Scorecard> compilationDTO) throws IOException {
                //  Needed to avoid Mockito usage
                return SOURCE_MAP;
            }

            @Override
            public PMML_MODEL getPMMLModelType() {
                return PMML_MODEL.SCORECARD_MODEL;
            }
        };
    }

    @Test
    public void getKiePMMLModelWithKnowledgeBuilder() {
        KnowledgeBuilderImpl knowledgeBuilder = new KnowledgeBuilderImpl();
        final CommonCompilationDTO<Scorecard> compilationDTO =
                CommonCompilationDTO.fromGeneratedPackageNameAndFields(PACKAGE_NAME,
                                                                       pmml,
                                                                       scorecard,
                                                                       new HasKnowledgeBuilderMock(knowledgeBuilder));
        KiePMMLDroolsModel retrieved = droolsModelProvider.getKiePMMLModel(compilationDTO);
        assertThat(retrieved).isNotNull();
        assertThat(retrieved).isInstanceOf(KiePMMLDroolsModelTest.class);
        KiePMMLDroolsModelTest retrievedTest = (KiePMMLDroolsModelTest) retrieved;
        final List<DataField> originalDataFields = pmml.getDataDictionary().getDataFields();
        final List<DataField> retrievedDataFields = retrievedTest.dataDictionary.getDataFields();
        assertThat(retrievedTest.dataDictionary.getDataFields()).hasSameSizeAs(originalDataFields);
        originalDataFields.forEach(dataField -> {
            Optional<DataField> optRet = retrievedDataFields.stream()
                    .filter(retrievedDataField -> dataField.getName().equals(retrievedDataField.getName()))
                    .findFirst();
            assertThat(optRet).isPresent();
            assertThat(optRet.get().getDataType()).isEqualTo(dataField.getDataType());
        });
        assertThat(retrievedTest.transformationDictionary).isEqualTo(pmml.getTransformationDictionary());
        assertThat(retrievedTest.model).isEqualTo(scorecard);
        String expectedPackageName = getSanitizedPackageName(PACKAGE_NAME);
        assertThat(retrievedTest.getKModulePackageName()).isEqualTo(expectedPackageName);
        assertThat(retrievedTest.getName()).isEqualTo(PACKAGE_NAME);
        PackageDescr packageDescr = knowledgeBuilder.getPackageDescrs("packagename").get(0);
        assertThat(packageDescr).isInstanceOf(CompositePackageDescr.class);
    }

    @Test(expected = KiePMMLException.class)
    public void getKiePMMLModelNoKnowledgeBuilder() {
        final CommonCompilationDTO<Scorecard> compilationDTO =
                CommonCompilationDTO.fromGeneratedPackageNameAndFields(PACKAGE_NAME,
                                                                       pmml,
                                                                       scorecard,
                                                                       new HasClassLoaderMock());
        droolsModelProvider.getKiePMMLModel(compilationDTO);
    }

    @Test
    public void getKiePMMLModelWithSourcesWithKnowledgeBuilder() {
        KnowledgeBuilderImpl knowledgeBuilder = new KnowledgeBuilderImpl();
        final CommonCompilationDTO<Scorecard> compilationDTO =
                CommonCompilationDTO.fromGeneratedPackageNameAndFields(PACKAGE_NAME,
                                                                       pmml,
                                                                       scorecard,
                                                                       new HasKnowledgeBuilderMock(knowledgeBuilder));
        KiePMMLDroolsModelWithSources retrieved = droolsModelProvider.getKiePMMLModelWithSources(compilationDTO);
        assertThat(retrieved).isNotNull();
        assertThat(retrieved.getSourcesMap()).isEqualTo(SOURCE_MAP);
        String expectedPackageName = compilationDTO.getPackageName();
        assertThat(retrieved.getKModulePackageName()).isEqualTo(expectedPackageName);
        assertThat(retrieved.getName()).isEqualTo(scorecard.getModelName());
        PackageDescr packageDescr = knowledgeBuilder.getPackageDescrs(expectedPackageName).get(0);
        commonVerifyPackageDescr(packageDescr, expectedPackageName);
        assertThat(retrieved).isNotNull();
        final String rootPath = expectedPackageName + ".";
        commonVerifyRulesSourcesMap(retrieved.getRulesSourcesMap(), packageDescr, rootPath);
    }

    @Test(expected = KiePMMLException.class)
    public void getKiePMMLModelWithSourcesNoKnowledgeBuilder() {
        final CommonCompilationDTO<Scorecard> compilationDTO =
                CommonCompilationDTO.fromGeneratedPackageNameAndFields(PACKAGE_NAME,
                                                                       pmml,
                                                                       scorecard,
                                                                       new HasClassLoaderMock());
        droolsModelProvider.getKiePMMLModelWithSources(compilationDTO);
    }

    @Test
    public void getPackageDescr() {
        KiePMMLDroolsAST kiePMMLDroolsAST = new KiePMMLDroolsAST(Collections.emptyList(), Collections.emptyList());
        PackageDescr retrieved = droolsModelProvider.getPackageDescr(kiePMMLDroolsAST, PACKAGE_NAME);
        commonVerifyPackageDescr(retrieved, PACKAGE_NAME);
    }

    @Test
    public void getKiePMMLDroolsASTCommon() {
        final Map<String, KiePMMLOriginalTypeGeneratedType> fieldTypeMap = new HashMap<>();
        final List<Field<?>> fields =
                getFieldsFromDataDictionaryAndTransformationDictionaryAndLocalTransformations(pmml.getDataDictionary(),
                                                                                              pmml.getTransformationDictionary(),
                                                                                              scorecard.getLocalTransformations());
        KiePMMLDroolsAST retrieved = droolsModelProvider.getKiePMMLDroolsASTCommon(fields,
                                                                                   scorecard,
                                                                                   fieldTypeMap);
        commonVerifyKiePMMLDroolsAST(retrieved, fieldTypeMap);
        commonVerifyFieldTypeMap(fieldTypeMap, pmml.getDataDictionary().getDataFields(),
                                 pmml.getTransformationDictionary().getDerivedFields(),
                                 scorecard.getLocalTransformations().getDerivedFields());
    }

    @Test
    public void getRulesSourceMap() {
        KnowledgeBuilderImpl knowledgeBuilder = new KnowledgeBuilderImpl();
        final CommonCompilationDTO<Scorecard> compilationDTO =
                CommonCompilationDTO.fromGeneratedPackageNameAndFields(PACKAGE_NAME,
                                                                       pmml,
                                                                       scorecard,
                                                                       new HasKnowledgeBuilderMock(knowledgeBuilder));
        droolsModelProvider.getKiePMMLModelWithSources(compilationDTO);
        String expectedPackageName = compilationDTO.getPackageName();
        PackageDescr packageDescr = knowledgeBuilder.getPackageDescrs(expectedPackageName).get(0);
        final Map<String, String> retrieved = droolsModelProvider.getRulesSourceMap(packageDescr);
        assertThat(retrieved).isNotNull();
        final String rootPath = expectedPackageName + ".";
        commonVerifyRulesSourcesMap(retrieved, packageDescr, rootPath);
    }

    @Test
    public void generateRulesFiles() {
        KnowledgeBuilderImpl knowledgeBuilder = new KnowledgeBuilderImpl();
        final CommonCompilationDTO<Scorecard> compilationDTO =
                CommonCompilationDTO.fromGeneratedPackageNameAndFields(PACKAGE_NAME,
                                                                       pmml,
                                                                       scorecard,
                                                                       new HasKnowledgeBuilderMock(knowledgeBuilder));
        droolsModelProvider.getKiePMMLModelWithSources(compilationDTO);
        String expectedPackageName = compilationDTO.getPackageName();
        PackageDescr packageDescr = knowledgeBuilder.getPackageDescrs(expectedPackageName).get(0);
        final List<GeneratedFile> retrieved = droolsModelProvider.generateRulesFiles(packageDescr);
        assertThat(retrieved).isNotNull();
        final String rootPath = expectedPackageName.replace('.', '/') + "/";
        packageDescr.getTypeDeclarations().forEach(typeDeclarationDescr -> {
            String expectedPath = rootPath + typeDeclarationDescr.getTypeName() + ".java";
            assertThat(retrieved.stream().anyMatch(generatedFile -> generatedFile.getPath().equals(expectedPath))).isTrue();
        });
        String pkgUUID = packageDescr.getPreferredPkgUUID().get();
        String expectedRule = rootPath + "Rules" + pkgUUID + ".java";
        assertThat(retrieved.stream().anyMatch(generatedFile -> generatedFile.getPath().equals(expectedRule))).isTrue();
        String expectedDomain = rootPath + "DomainClassesMetadata" + pkgUUID + ".java";
        assertThat(retrieved.stream().anyMatch(generatedFile -> generatedFile.getPath().equals(expectedDomain))).isTrue();
    }

    private void commonVerifyRulesSourcesMap(Map<String, String> toVerify, PackageDescr packageDescr, String rootPath) {
        packageDescr.getTypeDeclarations().forEach(typeDeclarationDescr -> {
            String expectedPath = rootPath + typeDeclarationDescr.getTypeName();
            assertThat(toVerify.keySet().stream().anyMatch(className -> className.equals(expectedPath))).isTrue();
        });
        String pkgUUID = packageDescr.getPreferredPkgUUID().get();
        String expectedRule = rootPath + "Rules" + pkgUUID;
        assertThat(toVerify.keySet().stream().anyMatch(className -> className.equals(expectedRule))).isTrue();
        String expectedDomain = rootPath + "DomainClassesMetadata" + pkgUUID;
        assertThat(toVerify.keySet().stream().anyMatch(className -> className.equals(expectedDomain))).isTrue();
    }

    private void commonVerifyPackageDescr(PackageDescr toVerify, String expectedPackageName) {
        assertThat(toVerify.getName()).isEqualTo(expectedPackageName);
    }

    private void commonVerifyKiePMMLDroolsAST(final KiePMMLDroolsAST toVerify, final Map<String,
            KiePMMLOriginalTypeGeneratedType> fieldTypeMap) {
        assertThat(toVerify).isNotNull();
        assertThat(toVerify).isInstanceOf(KiePMMLDroolsASTTest.class);
        KiePMMLDroolsASTTest toVerifyTest = (KiePMMLDroolsASTTest) toVerify;

        final List<DataField> originalDataFields = pmml.getDataDictionary().getDataFields();
        final List<DataField> retrievedDataFields = toVerifyTest.dataDictionary.getDataFields();
        assertThat(toVerifyTest.dataDictionary.getDataFields()).hasSameSizeAs(originalDataFields);
        originalDataFields.forEach(dataField -> {
            Optional<DataField> optRet = retrievedDataFields.stream()
                    .filter(retrievedDataField -> dataField.getName().equals(retrievedDataField.getName()))
                    .findFirst();
            assertThat(optRet).isPresent();
            assertThat(optRet.get().getDataType()).isEqualTo(dataField.getDataType());
        });
        assertThat(toVerifyTest.model).isEqualTo(scorecard);
        if (fieldTypeMap != null) {
            assertThat(toVerifyTest.fieldTypeMap).isEqualTo(fieldTypeMap);
        }
        commonVerifyTypesList(toVerify.getTypes(),
                              pmml.getDataDictionary().getDataFields(),
                              pmml.getTransformationDictionary().getDerivedFields(),
                              scorecard.getLocalTransformations().getDerivedFields());
    }

    private void commonVerifyTypesList(final List<KiePMMLDroolsType> toVerify, List<DataField> dataFields,
                                       List<DerivedField> transformationsFields,
                                       List<DerivedField> localTransformationsFields) {
        int expectedEntries = dataFields.size() + transformationsFields.size() + localTransformationsFields.size();
        assertThat(toVerify).hasSize(expectedEntries);
        dataFields.forEach(dataField -> commonVerifyTypesList(dataField, toVerify));
        transformationsFields.forEach(derivedField -> commonVerifyTypesList(derivedField, toVerify));
        localTransformationsFields.forEach(derivedField -> commonVerifyTypesList(derivedField, toVerify));
    }

    private void commonVerifyTypesList(Field<?> toVerify, final List<KiePMMLDroolsType> types) {
        assertThat(types.stream()
                           .anyMatch(type -> {
                               String expectedName = getSanitizedClassName(toVerify.getName().getValue());
                               if (!type.getName().startsWith(expectedName)) {
                                   return false;
                               }
                               String expectedType =
                                       DATA_TYPE.byName(toVerify.getDataType().value()).getMappedClass().getSimpleName();
                               assertThat(type.getType()).isEqualTo(expectedType);
                               return true;
                           })).isTrue();
    }

    private void commonVerifyFieldTypeMap(final Map<String, KiePMMLOriginalTypeGeneratedType> toVerify,
                                          List<DataField> dataFields, List<DerivedField> transformationsFields,
                                          List<DerivedField> localTransformationsFields) {
        int expectedEntries = dataFields.size() + transformationsFields.size() + localTransformationsFields.size();
        assertThat(toVerify).hasSize(expectedEntries);
        dataFields.forEach(dataField -> commonVerifyFieldTypeMap(dataField, toVerify));
        transformationsFields.forEach(derivedField -> commonVerifyFieldTypeMap(derivedField, toVerify));
        localTransformationsFields.forEach(derivedField -> commonVerifyFieldTypeMap(derivedField, toVerify));
    }

    private void commonVerifyFieldTypeMap(Field<?> toVerify,
                                          final Map<String, KiePMMLOriginalTypeGeneratedType> fieldTypeMap) {
        assertThat(fieldTypeMap.entrySet().stream()
                           .anyMatch(entry -> {
                               if (!entry.getKey().equals(toVerify.getName().getValue())) {
                                   return false;
                               }
                               KiePMMLOriginalTypeGeneratedType value = entry.getValue();
                               assertThat(value.getOriginalType()).isEqualTo(toVerify.getDataType().value());
                               String expectedGeneratedType =
                                       getSanitizedClassName(toVerify.getName().getValue());
                               assertThat(value.getGeneratedType()).startsWith(expectedGeneratedType);
                               return true;
                           })).isTrue();
    }

    //  Needed to avoid Mockito usage
    private static class KiePMMLDroolsModelTest extends KiePMMLDroolsModel {

        final DataDictionary dataDictionary;
        final TransformationDictionary transformationDictionary;
        final Scorecard model;
        final Map<String, KiePMMLOriginalTypeGeneratedType> fieldTypeMap;

        public KiePMMLDroolsModelTest(final List<Field<?>> fields,
                                      TransformationDictionary transformationDictionary, Scorecard model, Map<String,
                KiePMMLOriginalTypeGeneratedType> fieldTypeMap) {
            super(PACKAGE_NAME, Collections.emptyList());
            this.dataDictionary = new DataDictionary();
            fields.stream().filter(DataField.class::isInstance).map(DataField.class::cast).forEach(dataDictionary::addDataFields);
            this.transformationDictionary = transformationDictionary;
            this.model = model;
            this.fieldTypeMap = fieldTypeMap;
            this.kModulePackageName = getSanitizedPackageName(PACKAGE_NAME);
        }

   }

    //  Needed to avoid Mockito usage
    private static class KiePMMLDroolsASTTest extends KiePMMLDroolsAST {

        final DataDictionary dataDictionary;
        final Scorecard model;
        final Map<String, KiePMMLOriginalTypeGeneratedType> fieldTypeMap;

        public KiePMMLDroolsASTTest(final List<Field<?>> fields,
                                    final Scorecard model,
                                    final Map<String, KiePMMLOriginalTypeGeneratedType> fieldTypeMap,
                                    final List<KiePMMLDroolsType> types) {
            super(types, Collections.emptyList());
            this.dataDictionary = new DataDictionary();
            fields.stream().filter(DataField.class::isInstance).map(DataField.class::cast).forEach(dataDictionary::addDataFields);
            this.model = model;
            this.fieldTypeMap = fieldTypeMap;
        }
    }
}