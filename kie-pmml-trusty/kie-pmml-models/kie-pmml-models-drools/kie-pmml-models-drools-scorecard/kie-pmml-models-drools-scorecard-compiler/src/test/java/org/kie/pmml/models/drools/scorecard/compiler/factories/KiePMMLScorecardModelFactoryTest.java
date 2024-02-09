/**
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
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.kie.pmml.api.enums.MINING_FUNCTION;
import org.kie.pmml.api.enums.PMML_MODEL;
import org.kie.pmml.compiler.api.dto.CommonCompilationDTO;
import org.kie.pmml.compiler.api.testutils.TestUtils;
import org.kie.pmml.compiler.commons.mocks.PMMLCompilationContextMock;
import org.kie.pmml.models.drools.ast.KiePMMLDroolsAST;
import org.kie.pmml.models.drools.dto.DroolsCompilationDTO;
import org.kie.pmml.models.drools.tuples.KiePMMLOriginalTypeGeneratedType;

import static org.assertj.core.api.Assertions.assertThat;
import static org.kie.pmml.commons.Constants.PACKAGE_NAME;
import static org.kie.pmml.commons.utils.KiePMMLModelUtils.getSanitizedClassName;
import static org.kie.pmml.compiler.api.CommonTestingUtils.getFieldsFromDataDictionary;
import static org.kie.pmml.compiler.commons.testutils.CodegenTestUtils.commonEvaluateConstructor;
import static org.kie.pmml.compiler.commons.utils.JavaParserUtils.getFromFileName;
import static org.kie.pmml.models.drools.scorecard.compiler.factories.KiePMMLScorecardModelFactory.KIE_PMML_SCORECARD_MODEL_TEMPLATE;
import static org.kie.pmml.models.drools.scorecard.compiler.factories.KiePMMLScorecardModelFactory.KIE_PMML_SCORECARD_MODEL_TEMPLATE_JAVA;
import static org.kie.pmml.models.drools.utils.KiePMMLASTTestUtils.getFieldTypeMap;

public class KiePMMLScorecardModelFactoryTest {

    private static final String SOURCE_1 = "ScorecardSample.pmml";
    private static final String TARGET_FIELD = "overallScore";
    private static PMML pmml;
    private static Scorecard scorecardModel;
    private static ClassOrInterfaceDeclaration classOrInterfaceDeclaration;

    @BeforeAll
    public static void setUp() throws Exception {
        pmml = TestUtils.loadFromFile(SOURCE_1);
        assertThat(pmml).isNotNull();
        assertThat(pmml.getModels()).hasSize(1);
        assertThat(pmml.getModels().get(0)).isInstanceOf(Scorecard.class);
        scorecardModel = (Scorecard) pmml.getModels().get(0);
        assertThat(scorecardModel).isNotNull();
        CompilationUnit templateCU = getFromFileName(KIE_PMML_SCORECARD_MODEL_TEMPLATE_JAVA);
        classOrInterfaceDeclaration = templateCU
                .getClassByName(KIE_PMML_SCORECARD_MODEL_TEMPLATE).get();
    }

    @Test
    void getKiePMMLScorecardModelSourcesMap() {
        final DataDictionary dataDictionary = pmml.getDataDictionary();
        final Map<String, KiePMMLOriginalTypeGeneratedType> fieldTypeMap = getFieldTypeMap(dataDictionary,
                pmml.getTransformationDictionary(),
                scorecardModel.getLocalTransformations());
        final CommonCompilationDTO<Scorecard> compilationDTO =
                CommonCompilationDTO.fromGeneratedPackageNameAndFields(PACKAGE_NAME,
                                                                       pmml,
                                                                       scorecardModel,
                                                                       new PMMLCompilationContextMock(), "FILENAME");
        final DroolsCompilationDTO<Scorecard> droolsCompilationDTO =
                DroolsCompilationDTO.fromCompilationDTO(compilationDTO,
                        fieldTypeMap);
        Map<String, String> retrieved =
                KiePMMLScorecardModelFactory.getKiePMMLScorecardModelSourcesMap(droolsCompilationDTO);
        assertThat(retrieved).isNotNull();
        assertThat(retrieved).hasSize(1);
    }

    @Test
    void getKiePMMLDroolsAST() {
        final DataDictionary dataDictionary = pmml.getDataDictionary();
        final Map<String, KiePMMLOriginalTypeGeneratedType> fieldTypeMap = getFieldTypeMap(dataDictionary,
                pmml.getTransformationDictionary(),
                scorecardModel.getLocalTransformations());
        KiePMMLDroolsAST retrieved =
                KiePMMLScorecardModelFactory.getKiePMMLDroolsAST(getFieldsFromDataDictionary(dataDictionary),
                        scorecardModel, fieldTypeMap, Collections.emptyList());
        assertThat(retrieved).isNotNull();
    }

    @Test
    void setConstructor() {
        final String targetField = "overallScore";
        final ClassOrInterfaceDeclaration modelTemplate = classOrInterfaceDeclaration.clone();
        final CommonCompilationDTO<Scorecard> compilationDTO =
                CommonCompilationDTO.fromGeneratedPackageNameAndFields(PACKAGE_NAME,
                                                                       pmml,
                                                                       scorecardModel,
                                                                       new PMMLCompilationContextMock(), "FILENAME");
        final DroolsCompilationDTO<Scorecard> droolsCompilationDTO =
                DroolsCompilationDTO.fromCompilationDTO(compilationDTO,
                        new HashMap<>());
        KiePMMLScorecardModelFactory.setConstructor(droolsCompilationDTO,
                modelTemplate);
        Map<Integer, Expression> superInvocationExpressionsMap = new HashMap<>();
        superInvocationExpressionsMap.put(0, new NameExpr(String.format("\"%s\"", "FILENAME")));
        superInvocationExpressionsMap.put(1, new NameExpr(String.format("\"%s\"", scorecardModel.getModelName())));
        MINING_FUNCTION miningFunction = MINING_FUNCTION.byName(scorecardModel.getMiningFunction().value());
        PMML_MODEL pmmlModel = PMML_MODEL.byName(scorecardModel.getClass().getSimpleName());
        Map<String, Expression> assignExpressionMap = new HashMap<>();
        assignExpressionMap.put("targetField", new StringLiteralExpr(targetField));
        assignExpressionMap.put("miningFunction",
                new NameExpr(miningFunction.getClass().getName() + "." + miningFunction.name()));
        assignExpressionMap.put("pmmlMODEL", new NameExpr(pmmlModel.getClass().getName() + "." + pmmlModel.name()));
        ConstructorDeclaration constructorDeclaration = modelTemplate.getDefaultConstructor().get();
        assertThat(commonEvaluateConstructor(constructorDeclaration,
                getSanitizedClassName(scorecardModel.getModelName()),
                superInvocationExpressionsMap, assignExpressionMap)).isTrue();
    }
}