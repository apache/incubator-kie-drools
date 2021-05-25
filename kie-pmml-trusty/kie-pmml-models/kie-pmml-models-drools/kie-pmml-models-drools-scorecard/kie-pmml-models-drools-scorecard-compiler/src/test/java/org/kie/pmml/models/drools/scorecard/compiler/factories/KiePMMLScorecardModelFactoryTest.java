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

package org.kie.pmml.models.drools.scorecard.compiler.factories;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.ConstructorDeclaration;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.StringLiteralExpr;
import org.dmg.pmml.DataDictionary;
import org.dmg.pmml.PMML;
import org.dmg.pmml.scorecard.Scorecard;
import org.junit.BeforeClass;
import org.junit.Test;
import org.kie.pmml.api.enums.MINING_FUNCTION;
import org.kie.pmml.api.enums.PMML_MODEL;
import org.kie.pmml.compiler.commons.mocks.HasClassLoaderMock;
import org.kie.pmml.compiler.testutils.TestUtils;
import org.kie.pmml.models.drools.ast.KiePMMLDroolsAST;
import org.kie.pmml.models.drools.scorecard.model.KiePMMLScorecardModel;
import org.kie.pmml.models.drools.tuples.KiePMMLOriginalTypeGeneratedType;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.kie.pmml.commons.utils.KiePMMLModelUtils.getSanitizedClassName;
import static org.kie.pmml.compiler.commons.testutils.CodegenTestUtils.commonEvaluateConstructor;
import static org.kie.pmml.compiler.commons.utils.JavaParserUtils.getFromFileName;
import static org.kie.pmml.models.drools.scorecard.compiler.factories.KiePMMLScorecardModelFactory.KIE_PMML_SCORECARD_MODEL_TEMPLATE;
import static org.kie.pmml.models.drools.scorecard.compiler.factories.KiePMMLScorecardModelFactory.KIE_PMML_SCORECARD_MODEL_TEMPLATE_JAVA;
import static org.kie.pmml.models.drools.utils.KiePMMLASTTestUtils.getFieldTypeMap;

public class KiePMMLScorecardModelFactoryTest {

    private static final String SOURCE_1 = "ScorecardSample.pmml";
    private static final String TARGET_FIELD = "overallScore";
    private static final String PACKAGE_NAME = "packagename";
    private static PMML pmml;
    private static Scorecard scorecardModel;
    private static ClassOrInterfaceDeclaration classOrInterfaceDeclaration;

    @BeforeClass
    public static void setUp() throws Exception {
        pmml = TestUtils.loadFromFile(SOURCE_1);
        assertNotNull(pmml);
        assertEquals(1, pmml.getModels().size());
        assertTrue(pmml.getModels().get(0) instanceof Scorecard);
        scorecardModel = (Scorecard) pmml.getModels().get(0);
        assertNotNull(scorecardModel);
        CompilationUnit templateCU = getFromFileName(KIE_PMML_SCORECARD_MODEL_TEMPLATE_JAVA);
        classOrInterfaceDeclaration = templateCU
                .getClassByName(KIE_PMML_SCORECARD_MODEL_TEMPLATE).get();
    }

    @Test
    public void getKiePMMLScorecardModel() throws Exception {
        final Map<String, KiePMMLOriginalTypeGeneratedType> fieldTypeMap = getFieldTypeMap(pmml.getDataDictionary(),
                                                                                           pmml.getTransformationDictionary(),
                                                                                           scorecardModel.getLocalTransformations());
        KiePMMLScorecardModel retrieved = KiePMMLScorecardModelFactory.getKiePMMLScorecardModel(pmml.getDataDictionary(),
                                                                                                pmml.getTransformationDictionary(),
                                                                                                scorecardModel,
                                                                                                fieldTypeMap,
                                                                                                PACKAGE_NAME,
                                                                                                new HasClassLoaderMock());
        assertNotNull(retrieved);
        assertEquals(scorecardModel.getModelName(), retrieved.getName());
        assertEquals(TARGET_FIELD, retrieved.getTargetField());
    }

    @Test
    public void getKiePMMLScorecardModelSourcesMap()  {
        final Map<String, KiePMMLOriginalTypeGeneratedType> fieldTypeMap = getFieldTypeMap(pmml.getDataDictionary(),
                                                                                           pmml.getTransformationDictionary(),
                                                                                           scorecardModel.getLocalTransformations());
        Map<String, String> retrieved = KiePMMLScorecardModelFactory.getKiePMMLScorecardModelSourcesMap(pmml.getDataDictionary(),
                                                                                                        pmml.getTransformationDictionary(),
                                                                                                        scorecardModel,
                                                                                                        fieldTypeMap,
                                                                                                        PACKAGE_NAME);
        assertNotNull(retrieved);
        assertEquals(1, retrieved.size());
    }


    @Test
    public void getKiePMMLDroolsAST() {
        final DataDictionary dataDictionary = pmml.getDataDictionary();
        final Map<String, KiePMMLOriginalTypeGeneratedType> fieldTypeMap = getFieldTypeMap(pmml.getDataDictionary(),
                                                                                           pmml.getTransformationDictionary(),
                                                                                           scorecardModel.getLocalTransformations());
        KiePMMLDroolsAST retrieved = KiePMMLScorecardModelFactory.getKiePMMLDroolsAST(dataDictionary, scorecardModel, fieldTypeMap, Collections.emptyList());
        assertNotNull(retrieved);
    }

    @Test
    public void setConstructor() {
        final String targetField = "overallScore";
        final ClassOrInterfaceDeclaration modelTemplate = classOrInterfaceDeclaration.clone();
        KiePMMLScorecardModelFactory.setConstructor(scorecardModel,
                                                    pmml.getDataDictionary(),
                                                    pmml.getTransformationDictionary(),
                                                    modelTemplate);
        Map<Integer, Expression> superInvocationExpressionsMap = new HashMap<>();
        superInvocationExpressionsMap.put(0, new NameExpr(String.format("\"%s\"", scorecardModel.getModelName())));
        MINING_FUNCTION miningFunction = MINING_FUNCTION.byName(scorecardModel.getMiningFunction().value());
        PMML_MODEL pmmlModel = PMML_MODEL.byName(scorecardModel.getClass().getSimpleName());
        Map<String, Expression> assignExpressionMap = new HashMap<>();
        assignExpressionMap.put("targetField", new StringLiteralExpr(targetField));
        assignExpressionMap.put("miningFunction",
                                new NameExpr(miningFunction.getClass().getName() + "." + miningFunction.name()));
        assignExpressionMap.put("pmmlMODEL", new NameExpr(pmmlModel.getClass().getName() + "." + pmmlModel.name()));
        ConstructorDeclaration constructorDeclaration = modelTemplate.getDefaultConstructor().get();
        assertTrue(commonEvaluateConstructor(constructorDeclaration, getSanitizedClassName(scorecardModel.getModelName()), superInvocationExpressionsMap, assignExpressionMap));
    }
}