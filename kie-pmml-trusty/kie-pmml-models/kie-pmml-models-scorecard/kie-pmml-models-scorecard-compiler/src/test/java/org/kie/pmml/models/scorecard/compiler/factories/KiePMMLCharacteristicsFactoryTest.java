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

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.IntStream;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.MethodReferenceExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.NullLiteralExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import org.dmg.pmml.DataDictionary;
import org.dmg.pmml.DerivedField;
import org.dmg.pmml.PMML;
import org.dmg.pmml.Predicate;
import org.dmg.pmml.TransformationDictionary;
import org.dmg.pmml.scorecard.Attribute;
import org.dmg.pmml.scorecard.Characteristic;
import org.dmg.pmml.scorecard.Characteristics;
import org.dmg.pmml.scorecard.ComplexPartialScore;
import org.dmg.pmml.scorecard.Scorecard;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.kie.pmml.api.enums.REASONCODE_ALGORITHM;
import org.kie.pmml.api.exceptions.KiePMMLException;
import org.kie.pmml.commons.utils.KiePMMLModelUtils;
import org.kie.pmml.compiler.commons.mocks.HasClassLoaderMock;
import org.kie.pmml.compiler.commons.utils.CommonCodegenUtils;
import org.kie.pmml.compiler.commons.utils.JavaParserUtils;
import org.kie.pmml.compiler.testutils.TestUtils;
import org.kie.pmml.models.scorecard.model.KiePMMLCharacteristics;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.kie.pmml.commons.Constants.AS_LIST;
import static org.kie.pmml.commons.Constants.EMPTY_LIST;
import static org.kie.pmml.commons.Constants.EVALUATE_PREDICATE;
import static org.kie.pmml.commons.Constants.PACKAGE_CLASS_TEMPLATE;
import static org.kie.pmml.commons.Constants.PREDICATE_FUNCTION;
import static org.kie.pmml.commons.Constants.REASON_CODE;
import static org.kie.pmml.commons.Constants.REASON_CODE_ALGORITHM;
import static org.kie.pmml.commons.Constants.SCORE;
import static org.kie.pmml.compiler.commons.utils.JavaParserUtils.MAIN_CLASS_NOT_FOUND;
import static org.kie.pmml.compiler.commons.utils.ModelUtils.getDerivedFields;
import static org.kie.pmml.models.scorecard.compiler.factories.KiePMMLCharacteristicsFactory.ATTRIBUTE_FUNCTIONS;
import static org.kie.pmml.models.scorecard.compiler.factories.KiePMMLCharacteristicsFactory.COMPLEX_SCORE_FUNCTION;
import static org.kie.pmml.models.scorecard.compiler.factories.KiePMMLCharacteristicsFactory.EVALUATE_ATTRIBUTE;
import static org.kie.pmml.models.scorecard.compiler.factories.KiePMMLCharacteristicsFactory.EVALUATE_CHARACTERISTIC;
import static org.kie.pmml.models.scorecard.compiler.factories.KiePMMLCharacteristicsFactory.EVALUATE_COMPLEX_SCORE;
import static org.kie.pmml.models.scorecard.compiler.factories.KiePMMLCharacteristicsFactory.EVALUATE_COMPLEX_SCORE_FUNCTION;
import static org.kie.pmml.models.scorecard.compiler.factories.KiePMMLCharacteristicsFactory.KIE_PMML_CHARACTERISTICS_TEMPLATE;
import static org.kie.pmml.models.scorecard.compiler.factories.KiePMMLCharacteristicsFactory.KIE_PMML_CHARACTERISTICS_TEMPLATE_JAVA;
import static org.kie.pmml.models.scorecard.compiler.factories.KiePMMLCharacteristicsFactory.KIE_PMML_CHARACTERISTIC_TEMPLATE;
import static org.kie.pmml.models.scorecard.compiler.factories.KiePMMLCharacteristicsFactory.KIE_PMML_CHARACTERISTIC_TEMPLATE_JAVA;

public class KiePMMLCharacteristicsFactoryTest {

    private static final String BASIC_COMPLEX_PARTIAL_SCORE_SOURCE = "BasicComplexPartialScore.pmml";
    private static final String CONTAINER_CLASS_NAME = KiePMMLModelUtils.getGeneratedClassName("Scorecard");
    private static final String PACKAGE_NAME = "packagename";
    private static PMML basicComplexPartialScorePmml;
    private static DataDictionary basicComplexPartialScoreDataDictionary;
    private static TransformationDictionary basicComplexPartialScoreTransformationDictionary;
    private static Scorecard basicComplexPartialScore;
    private static List<DerivedField> basicComplexPartialScoreDerivedFields;
    private static Characteristics basicComplexPartialScoreCharacteristics;
    private static Characteristic basicComplexPartialScoreFirstCharacteristic;
    private ClassOrInterfaceDeclaration characteristicsTemplate;
    private ClassOrInterfaceDeclaration characteristicTemplate;

    @BeforeClass
    public static void setupClass() throws Exception {
        basicComplexPartialScorePmml = TestUtils.loadFromFile(BASIC_COMPLEX_PARTIAL_SCORE_SOURCE);
        basicComplexPartialScoreDataDictionary = basicComplexPartialScorePmml.getDataDictionary();
        basicComplexPartialScoreTransformationDictionary = basicComplexPartialScorePmml.getTransformationDictionary();
        basicComplexPartialScore = ((Scorecard) basicComplexPartialScorePmml.getModels().get(0));
        basicComplexPartialScoreCharacteristics = basicComplexPartialScore.getCharacteristics();
        basicComplexPartialScoreFirstCharacteristic =
                basicComplexPartialScoreCharacteristics.getCharacteristics().get(0);
        basicComplexPartialScoreDerivedFields = getDerivedFields(basicComplexPartialScoreTransformationDictionary, basicComplexPartialScore.getLocalTransformations());

    }

    @Before
    public void setup() {
        final CompilationUnit characteristicsCloneCU =
                JavaParserUtils.getKiePMMLModelCompilationUnit(CONTAINER_CLASS_NAME,
                                                               PACKAGE_NAME,
                                                               KIE_PMML_CHARACTERISTICS_TEMPLATE_JAVA,
                                                               KIE_PMML_CHARACTERISTICS_TEMPLATE);
        characteristicsTemplate = characteristicsCloneCU.getClassByName(CONTAINER_CLASS_NAME)
                .orElseThrow(() -> new KiePMMLException(MAIN_CLASS_NOT_FOUND + ": " + CONTAINER_CLASS_NAME));
        final CompilationUnit characteristicCloneCU =
                JavaParserUtils.getKiePMMLModelCompilationUnit(CONTAINER_CLASS_NAME,
                                                               PACKAGE_NAME,
                                                               KIE_PMML_CHARACTERISTIC_TEMPLATE_JAVA,
                                                               KIE_PMML_CHARACTERISTIC_TEMPLATE);
        characteristicTemplate = characteristicCloneCU.getClassByName(CONTAINER_CLASS_NAME)
                .orElseThrow(() -> new KiePMMLException(MAIN_CLASS_NOT_FOUND + ": " + CONTAINER_CLASS_NAME));
    }

    @Test
    public void getKiePMMLCharacteristics() {
        final KiePMMLCharacteristics retrieved =
                KiePMMLCharacteristicsFactory.getKiePMMLCharacteristics(basicComplexPartialScoreCharacteristics,
                                                                        basicComplexPartialScoreDataDictionary,
                                                                        basicComplexPartialScoreDerivedFields,
                                                                        basicComplexPartialScore.getInitialScore(),
                                                                        basicComplexPartialScore.getReasonCodeAlgorithm(),
                                                                        PACKAGE_NAME,
                                                                        new HasClassLoaderMock());
        assertNotNull(retrieved);
    }

    @Test
    public void getKiePMMLCharacteristicsSourcesMap() {
        final Map<String, String> retrieved =
                KiePMMLCharacteristicsFactory.getKiePMMLCharacteristicsSourcesMap(basicComplexPartialScoreCharacteristics,
                                                                                  basicComplexPartialScoreDataDictionary,
                                                                                  basicComplexPartialScoreDerivedFields,
                                                                                  basicComplexPartialScore.getInitialScore(),
                                                                                  basicComplexPartialScore.getReasonCodeAlgorithm(),
                                                                                  CONTAINER_CLASS_NAME,
                                                                                  PACKAGE_NAME);
        assertNotNull(retrieved);
        assertEquals(1, retrieved.size());
        String expected = String.format(PACKAGE_CLASS_TEMPLATE, PACKAGE_NAME, CONTAINER_CLASS_NAME);
        assertTrue(retrieved.containsKey(expected));
    }

    @Test
    public void addCharacteristic() {
        final String characteristicName = "CharacteristicName";
        String expected = EVALUATE_CHARACTERISTIC + characteristicName;
        assertTrue(characteristicsTemplate.getMethodsByName(expected).isEmpty());
        for (int i = 0; i < basicComplexPartialScoreFirstCharacteristic.getAttributes().size(); i++) {
            String attributeName = characteristicName + "_" + (i + 1);
            expected = EVALUATE_ATTRIBUTE + attributeName;
            assertTrue(characteristicsTemplate.getMethodsByName(expected).isEmpty());
            expected = EVALUATE_PREDICATE + attributeName;
            assertTrue(characteristicsTemplate.getMethodsByName(expected).isEmpty());
        }
        KiePMMLCharacteristicsFactory.addCharacteristic(characteristicsTemplate,
                                                        characteristicTemplate,
                                                        basicComplexPartialScoreDataDictionary,
                                                        basicComplexPartialScoreDerivedFields,
                                                        basicComplexPartialScore.getReasonCodeAlgorithm(),
                                                        basicComplexPartialScoreFirstCharacteristic,
                                                        CONTAINER_CLASS_NAME,
                                                        characteristicName);
        assertEquals(1, characteristicsTemplate.getMethodsByName(expected).size());
        for (int i = 0; i < basicComplexPartialScoreFirstCharacteristic.getAttributes().size(); i++) {
            String attributeName = characteristicName + "_" + (i + 1);
            expected = EVALUATE_ATTRIBUTE + attributeName;
            assertEquals(1, characteristicsTemplate.getMethodsByName(expected).size());
            expected = EVALUATE_PREDICATE + attributeName;
            assertEquals(1, characteristicsTemplate.getMethodsByName(expected).size());
        }
    }

    @Test
    public void getEvaluateCharacteristicMethodDeclaration() {
        final String characteristicName = "CharacteristicName";
        Object scoreParam = null;
        String reasonCode = null;
        Scorecard.ReasonCodeAlgorithm reasonCodeAlgorithm = Scorecard.ReasonCodeAlgorithm.POINTS_ABOVE;
        NodeList<Expression> evaluateAttributesReferences = new NodeList<>();
        MethodDeclaration retrieved =
                KiePMMLCharacteristicsFactory.getEvaluateCharacteristicMethodDeclaration(characteristicTemplate,
                                                                                         characteristicName,
                                                                                         scoreParam,
                                                                                         reasonCode,
                                                                                         reasonCodeAlgorithm,
                                                                                         evaluateAttributesReferences);
        commonVerifyGetEvaluateCharacteristicMethodDeclaration(retrieved,
                                                               characteristicName,
                                                               scoreParam,
                                                               reasonCode,
                                                               reasonCodeAlgorithm,
                                                               evaluateAttributesReferences);
        //
        scoreParam = "ScorePar";
        reasonCode = "reasonCode";
        retrieved =
                KiePMMLCharacteristicsFactory.getEvaluateCharacteristicMethodDeclaration(characteristicTemplate,
                                                                                         characteristicName,
                                                                                         scoreParam,
                                                                                         reasonCode,
                                                                                         reasonCodeAlgorithm,
                                                                                         evaluateAttributesReferences);
        commonVerifyGetEvaluateCharacteristicMethodDeclaration(retrieved,
                                                               characteristicName,
                                                               scoreParam,
                                                               reasonCode,
                                                               reasonCodeAlgorithm,
                                                               evaluateAttributesReferences);
        //
        scoreParam = 23432.34;
        reasonCodeAlgorithm = Scorecard.ReasonCodeAlgorithm.POINTS_BELOW;
        retrieved =
                KiePMMLCharacteristicsFactory.getEvaluateCharacteristicMethodDeclaration(characteristicTemplate,
                                                                                         characteristicName,
                                                                                         scoreParam,
                                                                                         reasonCode,
                                                                                         reasonCodeAlgorithm,
                                                                                         evaluateAttributesReferences);
        commonVerifyGetEvaluateCharacteristicMethodDeclaration(retrieved,
                                                               characteristicName,
                                                               scoreParam,
                                                               reasonCode,
                                                               reasonCodeAlgorithm,
                                                               evaluateAttributesReferences);

        //
        IntStream.range(0, 3).forEach(i -> {
            MethodReferenceExpr toAdd = new MethodReferenceExpr();
            toAdd.setScope(new NameExpr(characteristicName));
            toAdd.setIdentifier("method_" + i);
            evaluateAttributesReferences.add(new MethodReferenceExpr());
        });
        retrieved =
                KiePMMLCharacteristicsFactory.getEvaluateCharacteristicMethodDeclaration(characteristicTemplate,
                                                                                         characteristicName,
                                                                                         scoreParam,
                                                                                         reasonCode,
                                                                                         reasonCodeAlgorithm,
                                                                                         evaluateAttributesReferences);
        commonVerifyGetEvaluateCharacteristicMethodDeclaration(retrieved,
                                                               characteristicName,
                                                               scoreParam,
                                                               reasonCode,
                                                               reasonCodeAlgorithm,
                                                               evaluateAttributesReferences);
    }

    @Test
    public void addAttribute() {
        String attributeName = "CharacteristicName_1";
        final Attribute attribute = basicComplexPartialScoreFirstCharacteristic.getAttributes().get(0);
        String expectedMethod = EVALUATE_ATTRIBUTE + attributeName;
        assertTrue(characteristicsTemplate.getMethodsByName(expectedMethod).isEmpty());
        KiePMMLCharacteristicsFactory.addAttribute(characteristicsTemplate,
                                                   characteristicTemplate,
                                                   basicComplexPartialScoreDataDictionary,
                                                   basicComplexPartialScoreDerivedFields,
                                                   attribute,
                                                   CONTAINER_CLASS_NAME,
                                                   attributeName);
        assertEquals(1, characteristicsTemplate.getMethodsByName(expectedMethod).size());
    }

    @Test
    public void getEvaluateAttributeMethodDeclaration() {
        String attributeName = "CharacteristicName_1";
        boolean hasComplexScore = false;
        Object scoreParam = 243.4533;
        String reasonCode = null;
        // no complex score
        MethodDeclaration retrieved =
                KiePMMLCharacteristicsFactory.getEvaluateAttributeMethodDeclaration(characteristicTemplate,
                                                                                    CONTAINER_CLASS_NAME, attributeName,
                                                                                    scoreParam,
                                                                                    reasonCode,
                                                                                    hasComplexScore);
        commonVerifyGetEvaluateAttributeMethodDeclaration(retrieved, attributeName, scoreParam, reasonCode, hasComplexScore);
        // complex score
        commonVerifyGetEvaluateAttributeMethodDeclaration(retrieved, attributeName, scoreParam, reasonCode, hasComplexScore);
        scoreParam = null;
        reasonCode = "reasonCode";
        hasComplexScore = true;
        retrieved = KiePMMLCharacteristicsFactory.getEvaluateAttributeMethodDeclaration(characteristicTemplate,
                                                                                        CONTAINER_CLASS_NAME,
                                                                                        attributeName,
                                                                                        scoreParam,
                                                                                        reasonCode,
                                                                                        hasComplexScore);
        commonVerifyGetEvaluateAttributeMethodDeclaration(retrieved, attributeName, scoreParam, reasonCode, hasComplexScore);
    }

    @Test
    public void addEvaluateComplexScoreMethod() {
        String attributeName = "CharacteristicName_1";
        final Attribute attribute = basicComplexPartialScoreFirstCharacteristic.getAttributes().get(0);
        final ComplexPartialScore complexPartialScore = attribute.getComplexPartialScore();
        String expectedMethod = EVALUATE_COMPLEX_SCORE + attributeName;
        assertTrue(characteristicsTemplate.getMethodsByName(expectedMethod).isEmpty());
        expectedMethod = COMPLEX_SCORE_FUNCTION + attributeName;
        assertTrue(characteristicsTemplate.getMethodsByName(expectedMethod).isEmpty());

        KiePMMLCharacteristicsFactory.addEvaluateComplexScoreMethod(characteristicsTemplate,
                                                                    characteristicTemplate,
                                                                    basicComplexPartialScoreDataDictionary,
                                                                    complexPartialScore,
                                                                    CONTAINER_CLASS_NAME,
                                                                    attributeName);
        expectedMethod = EVALUATE_COMPLEX_SCORE + attributeName;
        assertEquals(1, characteristicsTemplate.getMethodsByName(expectedMethod).size());
        expectedMethod = COMPLEX_SCORE_FUNCTION + attributeName;
        assertEquals(1, characteristicsTemplate.getMethodsByName(expectedMethod).size());
    }

    @Test
    public void addPredicate() {
        String attributeName = "CharacteristicName_1";
        final Attribute attribute = basicComplexPartialScoreFirstCharacteristic.getAttributes().get(0);
        final Predicate predicate = attribute.getPredicate();
        String expectedMethod = EVALUATE_PREDICATE + attributeName;
        assertTrue(characteristicsTemplate.getMethodsByName(expectedMethod).isEmpty());
        KiePMMLCharacteristicsFactory.addPredicate(characteristicsTemplate,
                                                   characteristicTemplate,
                                                   basicComplexPartialScoreDataDictionary,
                                                   basicComplexPartialScoreDerivedFields,
                                                   predicate,
                                                   CONTAINER_CLASS_NAME,
                                                   attributeName);
        assertEquals(1, characteristicsTemplate.getMethodsByName(expectedMethod).size());
    }

    private void commonVerifyGetEvaluateCharacteristicMethodDeclaration(final MethodDeclaration retrieved,
                                                                        final String characteristicName,
                                                                        final Object scoreParam,
                                                                        final String reasonCode,
                                                                        final Scorecard.ReasonCodeAlgorithm reasonCodeAlgorithm,
                                                                        final NodeList<Expression> evaluateAttributesReferences) {
        assertNotNull(retrieved);
        String expected = EVALUATE_CHARACTERISTIC + characteristicName;
        assertEquals(expected, retrieved.getName().asString());
        assertEquals("Number", retrieved.getType().asString());
        assertTrue(retrieved.getBody().isPresent());
        BlockStmt bodyRetrieved = retrieved.getBody().get();
        // score
        Optional<Expression> retrievedExpression = CommonCodegenUtils.getVariableInitializer(bodyRetrieved,
                                                                                             SCORE);
        assertTrue(retrievedExpression.isPresent());
        if (scoreParam != null) {
            NameExpr nameExpr = retrievedExpression.get().asNameExpr();
            expected = scoreParam instanceof String ? String.format("\"%s\"", scoreParam) : scoreParam.toString();
            assertEquals(expected, nameExpr.toString());
        } else {
            assertTrue(retrievedExpression.get() instanceof NullLiteralExpr);
        }
        // reason code
        retrievedExpression = CommonCodegenUtils.getVariableInitializer(bodyRetrieved,
                                                                        REASON_CODE);
        assertTrue(retrievedExpression.isPresent());
        if (reasonCode != null) {
            NameExpr nameExpr = retrievedExpression.get().asNameExpr();
            expected = String.format("\"%s\"", reasonCode);
            assertEquals(expected, nameExpr.toString());
        } else {
            assertTrue(retrievedExpression.get() instanceof NullLiteralExpr);
        }
        // reasonCodeAlgorithm
        retrievedExpression = CommonCodegenUtils.getVariableInitializer(bodyRetrieved,
                                                                        REASON_CODE_ALGORITHM);
        assertTrue(retrievedExpression.isPresent());
        if (reasonCodeAlgorithm != null) {
            NameExpr nameExpr = retrievedExpression.get().asNameExpr();
            expected = String.format("%s.%s", REASONCODE_ALGORITHM.class.getName(), reasonCodeAlgorithm.name());
            assertEquals(expected, nameExpr.toString());
        } else {
            assertTrue(retrievedExpression.get() instanceof NullLiteralExpr);
        }
        // attributeFunctions
        retrievedExpression = CommonCodegenUtils.getVariableInitializer(bodyRetrieved,
                                                                        ATTRIBUTE_FUNCTIONS);
        assertTrue(retrievedExpression.isPresent());
        assertTrue(retrievedExpression.get() instanceof MethodCallExpr);
        MethodCallExpr valuesInit = retrievedExpression.get().asMethodCallExpr();
        if (evaluateAttributesReferences.isEmpty()) {
            expected = Collections.class.getName();
            assertEquals(expected, valuesInit.getScope().get().asTypeExpr().toString());
            assertEquals(EMPTY_LIST, valuesInit.getName().asString());
        } else {
            expected = Arrays.class.getName();
            assertEquals(expected, valuesInit.getScope().get().asTypeExpr().toString());
            assertEquals(AS_LIST, valuesInit.getName().asString());
            assertEquals(evaluateAttributesReferences, valuesInit.getArguments());
        }
    }

    private void commonVerifyGetEvaluateAttributeMethodDeclaration(final MethodDeclaration retrieved,
                                                                   final String attributeName,
                                                                   final Object scoreParam,
                                                                   final String reasonCode,
                                                                   final boolean hasComplexScore) {
        assertNotNull(retrieved);
        String expected = EVALUATE_ATTRIBUTE + attributeName;
        assertEquals(expected, retrieved.getName().asString());
        assertEquals("Number", retrieved.getType().asString());
        assertTrue(retrieved.getBody().isPresent());
        BlockStmt bodyRetrieved = retrieved.getBody().get();
        // predicate function
        Optional<Expression> retrievedExpression = CommonCodegenUtils.getVariableInitializer(bodyRetrieved,
                                                                                             PREDICATE_FUNCTION);
        assertTrue(retrievedExpression.isPresent());
        MethodReferenceExpr reference = retrievedExpression.get().asMethodReferenceExpr();
        assertEquals(CONTAINER_CLASS_NAME, reference.getScope().toString());
        expected = EVALUATE_PREDICATE + attributeName;
        assertEquals(expected, reference.getIdentifier());
        // complexScore
        if (hasComplexScore) {
            retrievedExpression = CommonCodegenUtils.getVariableInitializer(bodyRetrieved,
                                                                            EVALUATE_COMPLEX_SCORE_FUNCTION);
            assertTrue(retrievedExpression.isPresent());
            reference = retrievedExpression.get().asMethodReferenceExpr();
            assertEquals(CONTAINER_CLASS_NAME, reference.getScope().toString());
            expected = EVALUATE_COMPLEX_SCORE + attributeName;
            assertEquals(expected, reference.getIdentifier());
        }

        // score
        retrievedExpression = CommonCodegenUtils.getVariableInitializer(bodyRetrieved, SCORE);
        assertTrue(retrievedExpression.isPresent());
        if (scoreParam != null) {
            NameExpr nameExpr = retrievedExpression.get().asNameExpr();
            expected = scoreParam instanceof String ? String.format("\"%s\"", scoreParam) : scoreParam.toString();
            assertEquals(expected, nameExpr.toString());
        } else {
            assertTrue(retrievedExpression.get() instanceof NullLiteralExpr);
        }
        // reason code
        retrievedExpression = CommonCodegenUtils.getVariableInitializer(bodyRetrieved,
                                                                        REASON_CODE);
        assertTrue(retrievedExpression.isPresent());
        if (reasonCode != null) {
            NameExpr nameExpr = retrievedExpression.get().asNameExpr();
            expected = String.format("\"%s\"", reasonCode);
            assertEquals(expected, nameExpr.toString());
        } else {
            assertTrue(retrievedExpression.get() instanceof NullLiteralExpr);
        }
    }
}