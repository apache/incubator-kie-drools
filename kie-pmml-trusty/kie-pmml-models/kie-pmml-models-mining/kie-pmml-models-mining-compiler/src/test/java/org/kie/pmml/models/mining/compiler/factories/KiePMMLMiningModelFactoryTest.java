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

package org.kie.pmml.models.mining.compiler.factories;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.xml.bind.JAXBException;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.ConstructorDeclaration;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.NullLiteralExpr;
import com.github.javaparser.ast.expr.ObjectCreationExpr;
import com.github.javaparser.ast.expr.StringLiteralExpr;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import org.apache.commons.lang3.RandomStringUtils;
import org.dmg.pmml.DataDictionary;
import org.dmg.pmml.MiningFunction;
import org.dmg.pmml.Model;
import org.dmg.pmml.TransformationDictionary;
import org.dmg.pmml.mining.MiningModel;
import org.dmg.pmml.mining.Segmentation;
import org.junit.BeforeClass;
import org.junit.Test;
import org.kie.pmml.api.enums.MINING_FUNCTION;
import org.kie.pmml.api.enums.PMML_MODEL;
import org.kie.pmml.commons.model.KiePMMLModel;
import org.kie.pmml.models.mining.compiler.HasKnowledgeBuilderMock;
import org.kie.pmml.models.mining.model.KiePMMLMiningModel;
import org.xml.sax.SAXException;

import static com.github.javaparser.StaticJavaParser.parseClassOrInterfaceType;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.kie.pmml.commons.Constants.PACKAGE_CLASS_TEMPLATE;
import static org.kie.pmml.commons.utils.KiePMMLModelUtils.getSanitizedClassName;
import static org.kie.pmml.commons.utils.KiePMMLModelUtils.getSanitizedPackageName;
import static org.kie.pmml.compiler.commons.testutils.CodegenTestUtils.commonEvaluateConstructor;
import static org.kie.pmml.compiler.commons.utils.JavaParserUtils.getFromFileName;
import static org.kie.pmml.models.mining.compiler.factories.KiePMMLMiningModelFactory.SEGMENTATIONNAME_TEMPLATE;

public class KiePMMLMiningModelFactoryTest extends AbstractKiePMMLFactoryTest {

    private static final String TEMPLATE_SOURCE = "KiePMMLMiningModelTemplate.tmpl";
    private static final String TEMPLATE_CLASS_NAME = "KiePMMLMiningModelTemplate";
    private static final String PACKAGE_NAME = "PACKAGE_NAME";

    private static CompilationUnit COMPILATION_UNIT;
    private static ClassOrInterfaceDeclaration MODEL_TEMPLATE;

    @BeforeClass
    public static void setup() throws IOException, JAXBException, SAXException {
        innerSetup();
        COMPILATION_UNIT = getFromFileName(TEMPLATE_SOURCE);
        MODEL_TEMPLATE = COMPILATION_UNIT.getClassByName(TEMPLATE_CLASS_NAME).get();
    }

    @Test
    public void getKiePMMLMiningModel() {
        final KiePMMLMiningModel retrieved = KiePMMLMiningModelFactory.getKiePMMLMiningModel(DATA_DICTIONARY,
                                                                                             TRANSFORMATION_DICTIONARY,
                                                                                             MINING_MODEL,
                                                                                             PACKAGE_NAME,
                                                                                             new HasKnowledgeBuilderMock(KNOWLEDGE_BUILDER));
        assertNotNull(retrieved);
        assertEquals(MINING_MODEL.getAlgorithmName(), retrieved.getAlgorithmName());
        assertEquals(MINING_MODEL.isScorable(), retrieved.isScorable());
        final String expectedTargetField = "categoricalResult";
        assertEquals(expectedTargetField, retrieved.getTargetField());
    }

    @Test
    public void getKiePMMLMiningModelSourcesMap() {
        final String packageName = "packagename";
        final List<KiePMMLModel> nestedModels = new ArrayList<>();
        final Map<String, String> retrieved = KiePMMLMiningModelFactory.getKiePMMLMiningModelSourcesMap(DATA_DICTIONARY,
                                                                                                        TRANSFORMATION_DICTIONARY,
                                                                                                        MINING_MODEL,
                                                                                                        packageName,
                                                                                                        new HasKnowledgeBuilderMock(KNOWLEDGE_BUILDER),
                                                                                                        nestedModels);
        assertNotNull(retrieved);
        int expectedNestedModels = MINING_MODEL.getSegmentation().getSegments().size();
        assertEquals(expectedNestedModels, nestedModels.size());
    }

    @Test
    public void getKiePMMLMiningModelSourcesMapCompiled() {
        final List<KiePMMLModel> nestedModels = new ArrayList<>();
        final HasKnowledgeBuilderMock hasKnowledgeBuilderMock = new HasKnowledgeBuilderMock(KNOWLEDGE_BUILDER);
        final String segmentationName = String.format(SEGMENTATIONNAME_TEMPLATE, MINING_MODEL.getModelName());
        final List<String> expectedGeneratedClasses =
                MINING_MODEL.getSegmentation().getSegments().stream().map(segment -> {
            String modelName = segment.getModel().getModelName();
            String sanitizedPackageName = getSanitizedPackageName(PACKAGE_NAME + "."
                                                                          + segmentationName + "."
                                                                          + segment.getId() + "."
                                                                          + modelName);
            String sanitizedClassName = getSanitizedClassName(modelName);
            return String.format(PACKAGE_CLASS_TEMPLATE, sanitizedPackageName, sanitizedClassName);
        }).collect(Collectors.toList());
        expectedGeneratedClasses.forEach(expectedGeneratedClass -> {
            try {
                hasKnowledgeBuilderMock.getClassLoader().loadClass(expectedGeneratedClass);
                fail("Expecting class not found: " + expectedGeneratedClass);
            } catch (Exception e) {
                assertTrue(e instanceof ClassNotFoundException);
            }
        });
        final Map<String, String> retrieved =
                KiePMMLMiningModelFactory.getKiePMMLMiningModelSourcesMapCompiled(DATA_DICTIONARY,
                                                                                                                TRANSFORMATION_DICTIONARY,
                                                                                                                MINING_MODEL,
                                                                                                                PACKAGE_NAME,
                                                                                                                hasKnowledgeBuilderMock,
                                                                                                                nestedModels);
        assertNotNull(retrieved);
        int expectedNestedModels = MINING_MODEL.getSegmentation().getSegments().size();
        assertEquals(expectedNestedModels, nestedModels.size());
        expectedGeneratedClasses.forEach(expectedGeneratedClass -> {
            try {
                hasKnowledgeBuilderMock.getClassLoader().loadClass(expectedGeneratedClass);
            } catch (Exception e) {
                fail("Expecting class to be loaded, but got: " + e.getClass().getName() + " -> " + e.getMessage());
                e.printStackTrace();
            }
        });
    }

    @Test
    public void setConstructor() {
        MiningModel model = new MiningModel();
        model.setModelName(RandomStringUtils.random(6, true, false));
        model.setMiningFunction(MiningFunction.CLASSIFICATION);
        PMML_MODEL pmmlModel = PMML_MODEL.byName(model.getClass().getSimpleName());
        final ClassOrInterfaceDeclaration modelTemplate = MODEL_TEMPLATE.clone();
        MINING_FUNCTION miningFunction = MINING_FUNCTION.byName(model.getMiningFunction().value());
        String segmentationClass = "SEGMENTATIONCLASS";
        KiePMMLMiningModelFactory.setConstructor(model,
                                                 new DataDictionary(),
                                                 new TransformationDictionary(),
                                                 modelTemplate,
                                                 segmentationClass);
        Map<Integer, Expression> superInvocationExpressionsMap = new HashMap<>();
        superInvocationExpressionsMap.put(0, new NameExpr(String.format("\"%s\"", model.getModelName())));
        Map<String, Expression> assignExpressionMap = new HashMap<>();
        assignExpressionMap.put("targetField", new NullLiteralExpr());
        assignExpressionMap.put("miningFunction",
                                new NameExpr(miningFunction.getClass().getName() + "." + miningFunction.name()));
        assignExpressionMap.put("pmmlMODEL", new NameExpr(pmmlModel.getClass().getName() + "." + pmmlModel.name()));
        ClassOrInterfaceType kiePMMLSegmentationClass = parseClassOrInterfaceType(segmentationClass);
        ObjectCreationExpr objectCreationExpr = new ObjectCreationExpr();
        objectCreationExpr.setType(kiePMMLSegmentationClass);
        assignExpressionMap.put("segmentation", objectCreationExpr);
        ConstructorDeclaration constructorDeclaration = modelTemplate.getDefaultConstructor().get();
        assertTrue(commonEvaluateConstructor(constructorDeclaration, getSanitizedClassName(model.getModelName()),
                                             superInvocationExpressionsMap, assignExpressionMap));
    }
}