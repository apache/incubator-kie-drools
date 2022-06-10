/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
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

package org.kie.pmml.models.scorecard.compiler.factories;

import java.util.Map;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.ConstructorDeclaration;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.ExplicitConstructorInvocationStmt;
import com.github.javaparser.ast.stmt.Statement;
import org.dmg.pmml.DataDictionary;
import org.dmg.pmml.PMML;
import org.dmg.pmml.TransformationDictionary;
import org.dmg.pmml.scorecard.Scorecard;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.kie.memorycompiler.KieMemoryCompiler;
import org.kie.pmml.api.enums.REASONCODE_ALGORITHM;
import org.kie.pmml.api.exceptions.KiePMMLException;
import org.kie.pmml.api.exceptions.KiePMMLInternalException;
import org.kie.pmml.commons.utils.KiePMMLModelUtils;
import org.kie.pmml.compiler.api.dto.CommonCompilationDTO;
import org.kie.pmml.compiler.api.testutils.TestUtils;
import org.kie.pmml.compiler.commons.mocks.HasClassLoaderMock;
import org.kie.pmml.compiler.commons.utils.CommonCodegenUtils;
import org.kie.pmml.compiler.commons.utils.JavaParserUtils;
import org.kie.pmml.models.scorecard.compiler.ScorecardCompilationDTO;
import org.kie.pmml.models.scorecard.model.KiePMMLScorecardModel;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;
import static org.kie.pmml.commons.Constants.MISSING_CONSTRUCTOR_IN_BODY;
import static org.kie.pmml.commons.Constants.MISSING_DEFAULT_CONSTRUCTOR;
import static org.kie.pmml.commons.utils.KiePMMLModelUtils.getSanitizedClassName;
import static org.kie.pmml.compiler.commons.utils.JavaParserUtils.MAIN_CLASS_NOT_FOUND;
import static org.kie.pmml.models.scorecard.compiler.factories.KiePMMLScorecardModelFactory.KIE_PMML_SCORECARD_MODEL_TEMPLATE;
import static org.kie.pmml.models.scorecard.compiler.factories.KiePMMLScorecardModelFactory.KIE_PMML_SCORECARD_MODEL_TEMPLATE_JAVA;

public class KiePMMLScorecardModelFactoryTest {

    private static final String BASIC_COMPLEX_PARTIAL_SCORE_SOURCE = "BasicComplexPartialScore.pmml";
    private static final String PACKAGE_NAME = "packagename";
    private static final String CONTAINER_CLASS_NAME = KiePMMLModelUtils.getGeneratedClassName("Scorecard");
    private static final CompilationUnit scorecardCloneCU =
            JavaParserUtils.getKiePMMLModelCompilationUnit(CONTAINER_CLASS_NAME,
                                                           PACKAGE_NAME,
                                                           KIE_PMML_SCORECARD_MODEL_TEMPLATE_JAVA,
                                                           KIE_PMML_SCORECARD_MODEL_TEMPLATE);
    private static ClassOrInterfaceDeclaration scorecardTemplateOriginal;
    private static PMML basicComplexPartialScorePmml;
    private static DataDictionary basicComplexPartialScoreDataDictionary;
    private static TransformationDictionary basicComplexPartialScoreTransformationDictionary;
    private static Scorecard basicComplexPartialScore;
    private ClassOrInterfaceDeclaration scorecardTemplate;

    @BeforeClass
    public static void setupClass() throws Exception {
        basicComplexPartialScorePmml = TestUtils.loadFromFile(BASIC_COMPLEX_PARTIAL_SCORE_SOURCE);
        basicComplexPartialScoreDataDictionary = basicComplexPartialScorePmml.getDataDictionary();
        basicComplexPartialScoreTransformationDictionary = basicComplexPartialScorePmml.getTransformationDictionary();
        basicComplexPartialScore = ((Scorecard) basicComplexPartialScorePmml.getModels().get(0));
        scorecardTemplateOriginal = scorecardCloneCU.getClassByName(CONTAINER_CLASS_NAME)
                .orElseThrow(() -> new KiePMMLException(MAIN_CLASS_NOT_FOUND + ": " + CONTAINER_CLASS_NAME));
    }

    @Before
    public void setup() {
        scorecardTemplate = scorecardTemplateOriginal.clone();
    }

    @Test
    public void getKiePMMLScorecardModel() {
        final CommonCompilationDTO<Scorecard> source =
                CommonCompilationDTO.fromGeneratedPackageNameAndFields(PACKAGE_NAME,
                                                                       basicComplexPartialScorePmml,
                                                                       basicComplexPartialScore,
                                                                       new HasClassLoaderMock());
        KiePMMLScorecardModel retrieved =
                KiePMMLScorecardModelFactory.getKiePMMLScorecardModel(ScorecardCompilationDTO.fromCompilationDTO(source));
        assertThat(retrieved).isNotNull();
    }

    @Test
    public void getKiePMMLScorecardModelSourcesMap() {
        final CommonCompilationDTO<Scorecard> source =
                CommonCompilationDTO.fromGeneratedPackageNameAndFields(PACKAGE_NAME,
                                                                       basicComplexPartialScorePmml,
                                                                       basicComplexPartialScore,
                                                                       new HasClassLoaderMock());
        ScorecardCompilationDTO compilationDTO = ScorecardCompilationDTO.fromCompilationDTO(source);
        final Map<String, String> retrieved =
                KiePMMLScorecardModelFactory.getKiePMMLScorecardModelSourcesMap(compilationDTO);
        assertThat(retrieved).isNotNull();
        assertThat(retrieved).hasSize(2);
        assertThat(retrieved).containsKey(compilationDTO.getPackageCanonicalClassName());
        assertThat(retrieved).containsKey(compilationDTO.getPackageCanonicalCharacteristicsClassName());
        try {
            KieMemoryCompiler.compile(retrieved, Thread.currentThread().getContextClassLoader());
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void setConstructor() {
        String fullCharacteristicsClassName = PACKAGE_NAME + ".fullCharacteristicsClassName";
        final CommonCompilationDTO<Scorecard> source =
                CommonCompilationDTO.fromGeneratedPackageNameAndFields(PACKAGE_NAME,
                                                                       basicComplexPartialScorePmml,
                                                                       basicComplexPartialScore,
                                                                       new HasClassLoaderMock());
        KiePMMLScorecardModelFactory.setConstructor(ScorecardCompilationDTO.fromCompilationDTO(source),
                                                    scorecardTemplate,
                                                    fullCharacteristicsClassName);
        final ConstructorDeclaration constructorDeclaration =
                scorecardTemplate.getDefaultConstructor().orElseThrow(() -> new KiePMMLInternalException(String.format(MISSING_DEFAULT_CONSTRUCTOR, scorecardTemplate.getName())));
        final BlockStmt body = constructorDeclaration.getBody();
        final ExplicitConstructorInvocationStmt retrieved =
                CommonCodegenUtils.getExplicitConstructorInvocationStmt(body)
                        .orElseThrow(() -> new KiePMMLException(String.format(MISSING_CONSTRUCTOR_IN_BODY, body)));
        Statement expected = JavaParserUtils
                .parseStatement(String.format("super(\"%1$s\", Collections.emptyList()" +
                                                      ", new %2$s" +
                                                      "(), %3$s, %4$s, %5$s, %6$s);\n",
                                              getSanitizedClassName(basicComplexPartialScore.getModelName()),
                                              fullCharacteristicsClassName,
                                              basicComplexPartialScore.getInitialScore(),
                                              basicComplexPartialScore.isUseReasonCodes(),
                                              REASONCODE_ALGORITHM.class.getName() + "." + REASONCODE_ALGORITHM.byName(basicComplexPartialScore.getReasonCodeAlgorithm().value()),
                                              basicComplexPartialScore.getBaselineScore()
                ));
        assertThat(JavaParserUtils.equalsNode(expected, retrieved)).isTrue();
    }
}