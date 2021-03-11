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

import org.dmg.pmml.DataDictionary;
import org.dmg.pmml.DataField;
import org.dmg.pmml.DerivedField;
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
import org.kie.pmml.commons.model.HasClassLoader;
import org.kie.pmml.compiler.commons.mocks.HasClassLoaderMock;
import org.kie.pmml.compiler.testutils.TestUtils;
import org.kie.pmml.models.drools.ast.KiePMMLDroolsAST;
import org.kie.pmml.models.drools.ast.KiePMMLDroolsType;
import org.kie.pmml.models.drools.commons.implementations.HasKnowledgeBuilderMock;
import org.kie.pmml.models.drools.commons.model.KiePMMLDroolsModel;
import org.kie.pmml.models.drools.commons.model.KiePMMLDroolsModelWithSources;
import org.kie.pmml.models.drools.tuples.KiePMMLOriginalTypeGeneratedType;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.kie.pmml.commons.utils.KiePMMLModelUtils.getSanitizedClassName;
import static org.kie.pmml.commons.utils.KiePMMLModelUtils.getSanitizedPackageName;

public class DroolsModelProviderTest {

    private static final String SOURCE_1 = "SimpleScorecardWithTransformations.pmml";
    private static final String PACKAGE_NAME = "PACKAGE_NAME";
    //  Needed to avoid Mockito usage
    private static final Map<String, String> SOURCE_MAP = new HashMap<>();
    private static PMML pmml;
    private static Scorecard scorecard;
    private static DroolsModelProvider<Scorecard, ? extends KiePMMLDroolsModel> droolsModelProvider;

    @BeforeClass
    public static void setup() throws Exception {
        pmml = TestUtils.loadFromFile(SOURCE_1);
        assertNotNull(pmml);
        scorecard = (Scorecard) pmml.getModels().get(0);
        assertNotNull(scorecard);
        droolsModelProvider = new DroolsModelProvider<Scorecard, KiePMMLDroolsModel>() {
            @Override
            public KiePMMLDroolsModel getKiePMMLDroolsModel(final DataDictionary dataDictionary,
                                                            final TransformationDictionary transformationDictionary,
                                                            final Scorecard model,
                                                            final Map<String, KiePMMLOriginalTypeGeneratedType> fieldTypeMap,
                                                            final String packageName,
                                                            final HasClassLoader hasClassLoader) {
                //  Needed to avoid Mockito usage
                return new KiePMMLDroolsModelTest(dataDictionary, transformationDictionary, model, fieldTypeMap);
            }

            @Override
            public KiePMMLDroolsAST getKiePMMLDroolsAST(final DataDictionary dataDictionary,
                                                        final Scorecard model,
                                                        final Map<String, KiePMMLOriginalTypeGeneratedType> fieldTypeMap,
                                                        final List<KiePMMLDroolsType> types) {
                //  Needed to avoid Mockito usage
                return new KiePMMLDroolsASTTest(dataDictionary, model, fieldTypeMap, types);
            }

            @Override
            public Map<String, String> getKiePMMLDroolsModelSourcesMap(DataDictionary dataDictionary,
                                                                       TransformationDictionary transformationDictionary, Scorecard model, Map fieldTypeMap, String packageName) throws IOException {
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
        KiePMMLDroolsModel retrieved = droolsModelProvider.getKiePMMLModel(PACKAGE_NAME,
                                                                           pmml.getDataDictionary(),
                                                                           pmml.getTransformationDictionary(),
                                                                           scorecard,
                                                                           new HasKnowledgeBuilderMock(knowledgeBuilder));
        assertNotNull(retrieved);
        assertTrue(retrieved instanceof KiePMMLDroolsModelTest);
        KiePMMLDroolsModelTest retrievedTest = (KiePMMLDroolsModelTest) retrieved;
        assertEquals(pmml.getDataDictionary(), retrievedTest.dataDictionary);
        assertEquals(pmml.getTransformationDictionary(), retrievedTest.transformationDictionary);
        assertEquals(scorecard, retrievedTest.model);
        String expectedPackageName = getSanitizedPackageName(PACKAGE_NAME);
        assertEquals(expectedPackageName, retrievedTest.getKModulePackageName());
        assertEquals(PACKAGE_NAME, retrievedTest.getName());
        PackageDescr packageDescr = knowledgeBuilder.getPackageDescrs("packagename").get(0);
        assertTrue(packageDescr instanceof CompositePackageDescr);
    }

    @Test(expected = KiePMMLException.class)
    public void getKiePMMLModelNoKnowledgeBuilder() {
        droolsModelProvider.getKiePMMLModel(PACKAGE_NAME,
                                            pmml.getDataDictionary(),
                                            pmml.getTransformationDictionary(),
                                            scorecard,
                                            new HasClassLoaderMock());
    }

    @Test
    public void getKiePMMLModelWithSourcesWithKnowledgeBuilder() {
        KnowledgeBuilderImpl knowledgeBuilder = new KnowledgeBuilderImpl();
        KiePMMLDroolsModel retrieved = droolsModelProvider.getKiePMMLModelWithSources(PACKAGE_NAME,
                                                                                      pmml.getDataDictionary(),
                                                                                      pmml.getTransformationDictionary(),
                                                                                      scorecard,
                                                                                      new HasKnowledgeBuilderMock(knowledgeBuilder));
        assertNotNull(retrieved);
        assertTrue(retrieved instanceof KiePMMLDroolsModelWithSources);
        KiePMMLDroolsModelWithSources retrievedWithSources = (KiePMMLDroolsModelWithSources) retrieved;
        assertEquals(SOURCE_MAP, retrievedWithSources.getSourcesMap());
        assertEquals(PACKAGE_NAME, retrievedWithSources.getKModulePackageName());
        assertEquals(scorecard.getModelName(), retrievedWithSources.getName());
        PackageDescr packageDescr = knowledgeBuilder.getPackageDescrs(PACKAGE_NAME).get(0);
        commonVerifyPackageDescr(packageDescr, PACKAGE_NAME);
        assertNotNull(retrieved);
        final String rootPath = PACKAGE_NAME + ".";
        commonVerifyRulesSourcesMap(retrievedWithSources.getRulesSourcesMap(), packageDescr, rootPath);
    }

    @Test(expected = KiePMMLException.class)
    public void getKiePMMLModelWithSourcesWithException() {
        KnowledgeBuilderImpl knowledgeBuilder = new KnowledgeBuilderImpl();
        droolsModelProvider.getKiePMMLModelWithSources(PACKAGE_NAME,
                                                       null,
                                                       null,
                                                       null,
                                                       new HasKnowledgeBuilderMock(knowledgeBuilder));
    }

    @Test(expected = KiePMMLException.class)
    public void getKiePMMLModelWithSourcesNoKnowledgeBuilder() {
        droolsModelProvider.getKiePMMLModelWithSources(PACKAGE_NAME,
                                                       pmml.getDataDictionary(),
                                                       pmml.getTransformationDictionary(),
                                                       scorecard,
                                                       new HasClassLoaderMock());
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
        KiePMMLDroolsAST retrieved = droolsModelProvider.getKiePMMLDroolsASTCommon(pmml.getDataDictionary(),
                                                                                   pmml.getTransformationDictionary()
                , scorecard, fieldTypeMap);
        commonVerifyKiePMMLDroolsAST(retrieved, fieldTypeMap);
        commonVerifyFieldTypeMap(fieldTypeMap, pmml.getDataDictionary().getDataFields(),
                                 pmml.getTransformationDictionary().getDerivedFields(),
                                 scorecard.getLocalTransformations().getDerivedFields());
    }

    @Test
    public void addTransformationsDerivedFields() {
        final Map<String, KiePMMLOriginalTypeGeneratedType> fieldTypeMap = new HashMap<>();
        droolsModelProvider.addTransformationsDerivedFields(fieldTypeMap, pmml.getTransformationDictionary(),
                                                            scorecard.getLocalTransformations());
        commonVerifyFieldTypeMap(fieldTypeMap, Collections.emptyList(),
                                 pmml.getTransformationDictionary().getDerivedFields(),
                                 scorecard.getLocalTransformations().getDerivedFields());
    }

    @Test
    public void getRulesSourceMap() {
        KnowledgeBuilderImpl knowledgeBuilder = new KnowledgeBuilderImpl();
        droolsModelProvider.getKiePMMLModelWithSources(PACKAGE_NAME,
                                                       pmml.getDataDictionary(),
                                                       pmml.getTransformationDictionary(),
                                                       scorecard,
                                                       new HasKnowledgeBuilderMock(knowledgeBuilder));
        PackageDescr packageDescr = knowledgeBuilder.getPackageDescrs(PACKAGE_NAME).get(0);
        final Map<String, String> retrieved = droolsModelProvider.getRulesSourceMap(packageDescr);
        assertNotNull(retrieved);
        final String rootPath = PACKAGE_NAME + ".";
        commonVerifyRulesSourcesMap(retrieved, packageDescr, rootPath);
    }

    @Test
    public void generateRulesFiles() {
        KnowledgeBuilderImpl knowledgeBuilder = new KnowledgeBuilderImpl();
        droolsModelProvider.getKiePMMLModelWithSources(PACKAGE_NAME,
                                                       pmml.getDataDictionary(),
                                                       pmml.getTransformationDictionary(),
                                                       scorecard,
                                                       new HasKnowledgeBuilderMock(knowledgeBuilder));
        PackageDescr packageDescr = knowledgeBuilder.getPackageDescrs("PACKAGE_NAME").get(0);
        final List<GeneratedFile> retrieved = droolsModelProvider.generateRulesFiles(packageDescr);
        assertNotNull(retrieved);
        final String rootPath = "PACKAGE_NAME/";
        packageDescr.getTypeDeclarations().forEach(typeDeclarationDescr -> {
            String expectedPath = rootPath + typeDeclarationDescr.getTypeName() + ".java";
            assertTrue(retrieved.stream().anyMatch(generatedFile -> generatedFile.getPath().equals(expectedPath)));
        });
        String pkgUUID = packageDescr.getPreferredPkgUUID().get();
        String expectedRule = rootPath + "Rules" + pkgUUID + ".java";
        assertTrue(retrieved.stream().anyMatch(generatedFile -> generatedFile.getPath().equals(expectedRule)));
        String expectedDomain = rootPath + "DomainClassesMetadata" + pkgUUID + ".java";
        assertTrue(retrieved.stream().anyMatch(generatedFile -> generatedFile.getPath().equals(expectedDomain)));
    }
    
    private void commonVerifyRulesSourcesMap( Map<String, String> toVerify,  PackageDescr packageDescr, String rootPath) {
        packageDescr.getTypeDeclarations().forEach(typeDeclarationDescr -> {
            String expectedPath = rootPath + typeDeclarationDescr.getTypeName();
            assertTrue(toVerify.keySet().stream().anyMatch(className -> className.equals(expectedPath)));
        });
        String pkgUUID = packageDescr.getPreferredPkgUUID().get();
        String expectedRule = rootPath + "Rules" + pkgUUID;
        assertTrue(toVerify.keySet().stream().anyMatch(className -> className.equals(expectedRule)));
        String expectedDomain = rootPath + "DomainClassesMetadata" + pkgUUID;
        assertTrue(toVerify.keySet().stream().anyMatch(className -> className.equals(expectedDomain)));
    }

    private void commonVerifyPackageDescr(PackageDescr toVerify, String expectedPackageName) {
        assertEquals(expectedPackageName, toVerify.getName());
    }

    private void commonVerifyKiePMMLDroolsAST(final KiePMMLDroolsAST toVerify, final Map<String,
            KiePMMLOriginalTypeGeneratedType> fieldTypeMap) {
        assertNotNull(toVerify);
        assertTrue(toVerify instanceof KiePMMLDroolsASTTest);
        KiePMMLDroolsASTTest toVerifyTest = (KiePMMLDroolsASTTest) toVerify;
        assertEquals(pmml.getDataDictionary(), toVerifyTest.dataDictionary);
        assertEquals(scorecard, toVerifyTest.model);
        if (fieldTypeMap != null) {
            assertEquals(fieldTypeMap, toVerifyTest.fieldTypeMap);
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
        assertEquals(expectedEntries, toVerify.size());
        dataFields.forEach(dataField -> commonVerifyTypesList(dataField, toVerify));
        transformationsFields.forEach(derivedField -> commonVerifyTypesList(derivedField, toVerify));
        localTransformationsFields.forEach(derivedField -> commonVerifyTypesList(derivedField, toVerify));
    }

    private void commonVerifyTypesList(DataField toVerify, final List<KiePMMLDroolsType> types) {
        assertTrue(types.stream()
                           .anyMatch(type -> {
                               String expectedName = getSanitizedClassName(toVerify.getName().getValue().toUpperCase());
                               if (!expectedName.equals(type.getName())) {
                                   return false;
                               }
                               String expectedType =
                                       DATA_TYPE.byName(toVerify.getDataType().value()).getMappedClass().getSimpleName();
                               assertEquals(expectedType, type.getType());
                               return true;
                           }));
    }

    private void commonVerifyTypesList(DerivedField toVerify, final List<KiePMMLDroolsType> types) {
        assertTrue(types.stream()
                           .anyMatch(type -> {
                               String expectedName = getSanitizedClassName(toVerify.getName().getValue().toUpperCase());
                               if (!expectedName.equals(type.getName())) {
                                   return false;
                               }
                               String expectedType =
                                       DATA_TYPE.byName(toVerify.getDataType().value()).getMappedClass().getSimpleName();
                               assertEquals(expectedType, type.getType());
                               return true;
                           }));
    }

    private void commonVerifyFieldTypeMap(final Map<String, KiePMMLOriginalTypeGeneratedType> toVerify,
                                          List<DataField> dataFields, List<DerivedField> transformationsFields,
                                          List<DerivedField> localTransformationsFields) {
        int expectedEntries = dataFields.size() + transformationsFields.size() + localTransformationsFields.size();
        assertEquals(expectedEntries, toVerify.size());
        dataFields.forEach(dataField -> commonVerifyFieldTypeMap(dataField, toVerify));
        transformationsFields.forEach(derivedField -> commonVerifyFieldTypeMap(derivedField, toVerify));
        localTransformationsFields.forEach(derivedField -> commonVerifyFieldTypeMap(derivedField, toVerify));
    }

    private void commonVerifyFieldTypeMap(DataField toVerify,
                                          final Map<String, KiePMMLOriginalTypeGeneratedType> fieldTypeMap) {
        assertTrue(fieldTypeMap.entrySet().stream()
                           .anyMatch(entry -> {
                               if (!entry.getKey().equals(toVerify.getName().getValue())) {
                                   return false;
                               }
                               KiePMMLOriginalTypeGeneratedType value = entry.getValue();
                               assertEquals(toVerify.getDataType().value(), value.getOriginalType());
                               String expectedGeneratedType =
                                       getSanitizedClassName(toVerify.getName().getValue().toUpperCase());
                               assertEquals(expectedGeneratedType, value.getGeneratedType());
                               return true;
                           }));
    }

    private void commonVerifyFieldTypeMap(DerivedField toVerify,
                                          final Map<String, KiePMMLOriginalTypeGeneratedType> fieldTypeMap) {
        assertTrue(fieldTypeMap.entrySet().stream()
                           .anyMatch(entry -> {
                               if (!entry.getKey().equals(toVerify.getName().getValue())) {
                                   return false;
                               }
                               KiePMMLOriginalTypeGeneratedType value = entry.getValue();
                               assertEquals(toVerify.getDataType().value(), value.getOriginalType());
                               String expectedGeneratedType =
                                       getSanitizedClassName(toVerify.getName().getValue().toUpperCase());
                               assertEquals(expectedGeneratedType, value.getGeneratedType());
                               return true;
                           }));
    }

    //  Needed to avoid Mockito usage
    private static class KiePMMLDroolsModelTest extends KiePMMLDroolsModel {

        final DataDictionary dataDictionary;
        final TransformationDictionary transformationDictionary;
        final Scorecard model;
        final Map<String, KiePMMLOriginalTypeGeneratedType> fieldTypeMap;

        public KiePMMLDroolsModelTest(DataDictionary dataDictionary,
                                      TransformationDictionary transformationDictionary, Scorecard model, Map<String,
                KiePMMLOriginalTypeGeneratedType> fieldTypeMap) {
            super(PACKAGE_NAME, Collections.emptyList());
            this.dataDictionary = dataDictionary;
            this.transformationDictionary = transformationDictionary;
            this.model = model;
            this.fieldTypeMap = fieldTypeMap;
        }
    }

    //  Needed to avoid Mockito usage
    private static class KiePMMLDroolsASTTest extends KiePMMLDroolsAST {

        final DataDictionary dataDictionary;
        final Scorecard model;
        final Map<String, KiePMMLOriginalTypeGeneratedType> fieldTypeMap;

        public KiePMMLDroolsASTTest(final DataDictionary dataDictionary,
                                    final Scorecard model,
                                    final Map<String, KiePMMLOriginalTypeGeneratedType> fieldTypeMap,
                                    final List<KiePMMLDroolsType> types) {
            super(types, Collections.emptyList());
            this.dataDictionary = dataDictionary;
            this.model = model;
            this.fieldTypeMap = fieldTypeMap;
        }
    }
}