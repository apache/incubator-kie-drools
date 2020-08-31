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
import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.JAXBException;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.ConstructorDeclaration;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.ObjectCreationExpr;
import com.github.javaparser.ast.expr.StringLiteralExpr;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import org.dmg.pmml.Model;
import org.dmg.pmml.mining.MiningModel;
import org.junit.BeforeClass;
import org.junit.Test;
import org.kie.pmml.commons.model.enums.MINING_FUNCTION;
import org.kie.pmml.commons.model.enums.PMML_MODEL;
import org.kie.pmml.models.mining.model.KiePMMLMiningModel;
import org.xml.sax.SAXException;

import static com.github.javaparser.StaticJavaParser.parseClassOrInterfaceType;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.kie.pmml.compiler.commons.testutils.CodegenTestUtils.commonEvaluateConstructor;
import static org.kie.pmml.compiler.commons.utils.JavaParserUtils.getFromFileName;

public class KiePMMLMiningModelFactoryTest extends AbstractKiePMMLFactoryTest {

    private static final String TEMPLATE_SOURCE = "Template.tmpl";
    private static final String TEMPLATE_CLASS_NAME = "Template";

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
                                                                                             KNOWLEDGE_BUILDER);
        assertNotNull(retrieved);
        assertEquals(MINING_MODEL.getAlgorithmName(), retrieved.getAlgorithmName());
        assertEquals(MINING_MODEL.isScorable(), retrieved.isScorable());
        final String expectedTargetField = "categoricalResult";
        assertEquals(expectedTargetField, retrieved.getTargetField());
    }

    @Test
    public void getKiePMMLMiningModelSourcesMap() {
        final String packageName = "packagename";
        final Map<String, String> retrieved = KiePMMLMiningModelFactory.getKiePMMLMiningModelSourcesMap(
                                                                                                       DATA_DICTIONARY,
                                                                                                       TRANSFORMATION_DICTIONARY,
                                                                                                       MINING_MODEL,
                                                                                                       packageName,
                                                                                                       KNOWLEDGE_BUILDER);
        assertNotNull(retrieved);
    }

    @Test
    public void setConstructor() {
        Model model = new MiningModel();
        PMML_MODEL pmmlModel = PMML_MODEL.byName(model.getClass().getSimpleName());
        ConstructorDeclaration constructorDeclaration = MODEL_TEMPLATE.getDefaultConstructor().get();
        String targetField = "TARGET_FIELD";
        MINING_FUNCTION miningFunction = MINING_FUNCTION.CLASSIFICATION;
        String generatedClassName = "GENERATEDCLASSNAME";
        String segmentationClass = "SEGMENTATIONCLASS";
        KiePMMLMiningModelFactory.setConstructor(generatedClassName,
                                                 constructorDeclaration,
                                                 targetField,
                                                 miningFunction,
                                                 pmmlModel.name(),
                                                 segmentationClass);
        Map<Integer, Expression> superInvocationExpressionsMap = new HashMap<>();
        superInvocationExpressionsMap.put(0, new NameExpr(String.format("\"%s\"", pmmlModel.name())));
        Map<String, Expression> assignExpressionMap = new HashMap<>();
        assignExpressionMap.put("targetField", new StringLiteralExpr(targetField));
        assignExpressionMap.put("miningFunction", new NameExpr(miningFunction.getClass().getName() + "." + miningFunction.name()));
        assignExpressionMap.put("pmmlMODEL", new NameExpr(pmmlModel.getClass().getName() + "." + pmmlModel.name()));
        ClassOrInterfaceType kiePMMLSegmentationClass = parseClassOrInterfaceType(segmentationClass);
        ObjectCreationExpr objectCreationExpr = new ObjectCreationExpr();
        objectCreationExpr.setType(kiePMMLSegmentationClass);
        assignExpressionMap.put("segmentation", objectCreationExpr);
        assertTrue(commonEvaluateConstructor(constructorDeclaration, generatedClassName, superInvocationExpressionsMap, assignExpressionMap));
    }
}