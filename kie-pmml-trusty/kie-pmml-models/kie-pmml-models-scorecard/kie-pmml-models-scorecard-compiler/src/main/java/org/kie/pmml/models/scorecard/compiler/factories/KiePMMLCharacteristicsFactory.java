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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.ConstructorDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.MethodReferenceExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.TypeExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import org.dmg.pmml.DataDictionary;
import org.dmg.pmml.DataType;
import org.dmg.pmml.DerivedField;
import org.dmg.pmml.Predicate;
import org.dmg.pmml.TransformationDictionary;
import org.dmg.pmml.scorecard.Attribute;
import org.dmg.pmml.scorecard.Characteristic;
import org.dmg.pmml.scorecard.Characteristics;
import org.dmg.pmml.scorecard.ComplexPartialScore;
import org.dmg.pmml.scorecard.Scorecard;
import org.kie.pmml.api.enums.REASONCODE_ALGORITHM;
import org.kie.pmml.api.exceptions.KiePMMLException;
import org.kie.pmml.api.exceptions.KiePMMLInternalException;
import org.kie.pmml.commons.model.HasClassLoader;
import org.kie.pmml.commons.utils.KiePMMLModelUtils;
import org.kie.pmml.compiler.commons.factories.KiePMMLPredicateFactory;
import org.kie.pmml.compiler.commons.utils.CommonCodegenUtils;
import org.kie.pmml.compiler.commons.utils.JavaParserUtils;
import org.kie.pmml.models.scorecard.model.KiePMMLCharacteristics;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.github.javaparser.StaticJavaParser.parseClassOrInterfaceType;
import static org.kie.pmml.commons.Constants.AS_LIST;
import static org.kie.pmml.commons.Constants.EMPTY_LIST;
import static org.kie.pmml.commons.Constants.EVALUATE_PREDICATE;
import static org.kie.pmml.commons.Constants.INITIAL_SCORE;
import static org.kie.pmml.commons.Constants.MISSING_BODY_TEMPLATE;
import static org.kie.pmml.commons.Constants.MISSING_DEFAULT_CONSTRUCTOR;
import static org.kie.pmml.commons.Constants.PACKAGE_CLASS_TEMPLATE;
import static org.kie.pmml.commons.Constants.PREDICATE_FUNCTION;
import static org.kie.pmml.commons.Constants.REASON_CODE;
import static org.kie.pmml.commons.Constants.REASON_CODE_ALGORITHM;
import static org.kie.pmml.commons.Constants.SCORE;
import static org.kie.pmml.commons.Constants.STRING_OBJECT_MAP;
import static org.kie.pmml.compiler.commons.utils.ExpressionFunctionUtils.getExpressionMethodDeclarationWithStringObjectMap;
import static org.kie.pmml.compiler.commons.utils.JavaParserUtils.MAIN_CLASS_NOT_FOUND;
import static org.kie.pmml.compiler.commons.utils.KiePMMLModelFactoryUtils.setConstructorSuperNameInvocation;

public class KiePMMLCharacteristicsFactory {

    static final String KIE_PMML_CHARACTERISTICS_TEMPLATE_JAVA = "KiePMMLCharacteristicsTemplate.tmpl";
    static final String KIE_PMML_CHARACTERISTICS_TEMPLATE = "KiePMMLCharacteristicsTemplate";
    static final String KIE_PMML_CHARACTERISTIC_TEMPLATE_JAVA = "KiePMMLCharacteristicTemplate.tmpl";
    static final String KIE_PMML_CHARACTERISTIC_TEMPLATE = "KiePMMLCharacteristicTemplate";
    static final String EVALUATE_CHARACTERISTICS = "evaluateCharacteristics";
    static final String EVALUATE_CHARACTERISTIC = "evaluateCharacteristic";
    static final String CHARACTERISTIC_FUNCTIONS = "characteristicFunctions";
    static final String EVALUATE_ATTRIBUTE = "evaluateAttribute";
    static final String ATTRIBUTE_FUNCTIONS = "attributeFunctions";
    static final String EVALUATE_COMPLEX_SCORE = "evaluateComplexScore";
    static final String EVALUATE_COMPLEX_SCORE_FUNCTION = "evaluateComplexScoreFunction";
    static final String COMPLEX_SCORE_FUNCTION = "ComplexScoreFunction";

    private static final Logger logger = LoggerFactory.getLogger(KiePMMLCharacteristicsFactory.class.getName());

    private KiePMMLCharacteristicsFactory() {
        // Avoid instantiation
    }

    public static KiePMMLCharacteristics getKiePMMLCharacteristics(final Characteristics characteristics,
                                                                   final DataDictionary dataDictionary,
                                                                   final List<DerivedField> derivedFields,
                                                                   final Number initialScore,
                                                                   final Scorecard.ReasonCodeAlgorithm reasonCodeAlgorithm,
                                                                   final String packageName,
                                                                   final HasClassLoader hasClassLoader) {
        logger.trace("getKiePMMLCharacteristics {} {}", packageName, characteristics);
        String className = KiePMMLModelUtils.getGeneratedClassName("Characteristics");
        String fullClassName = packageName + "." + className;
        final Map<String, String> sourcesMap = getKiePMMLCharacteristicsSourcesMap(characteristics, dataDictionary,
                                                                                   derivedFields,
                                                                                   initialScore,
                                                                                   reasonCodeAlgorithm,
                                                                                   className, packageName);
        try {
            Class<?> kiePMMLCharacteristicsClass = hasClassLoader.compileAndLoadClass(sourcesMap, fullClassName);
            return (KiePMMLCharacteristics) kiePMMLCharacteristicsClass.newInstance();
        } catch (Exception e) {
            throw new KiePMMLException(e);
        }
    }

    public static Map<String, String> getKiePMMLCharacteristicsSourcesMap(final Characteristics characteristics,
                                                                          final DataDictionary dataDictionary,
                                                                          final List<DerivedField> derivedFields,
                                                                          final Number initialScore,
                                                                          final Scorecard.ReasonCodeAlgorithm reasonCodeAlgorithm,
                                                                          final String containerClassName,
                                                                          final String packageName) {
        logger.trace("getKiePMMLCharacteristicsSourcesMap {} {}", characteristics, packageName);
        final CompilationUnit characteristicsCloneCU =
                JavaParserUtils.getKiePMMLModelCompilationUnit(containerClassName,
                                                               packageName,
                                                               KIE_PMML_CHARACTERISTICS_TEMPLATE_JAVA,
                                                               KIE_PMML_CHARACTERISTICS_TEMPLATE);
        final ClassOrInterfaceDeclaration characteristicsTemplate =
                characteristicsCloneCU.getClassByName(containerClassName)
                        .orElseThrow(() -> new KiePMMLException(MAIN_CLASS_NOT_FOUND + ": " + containerClassName));
        final CompilationUnit characteristicCloneCU = JavaParserUtils.getKiePMMLModelCompilationUnit(containerClassName,
                                                                                                     packageName,
                                                                                                     KIE_PMML_CHARACTERISTIC_TEMPLATE_JAVA, KIE_PMML_CHARACTERISTIC_TEMPLATE);
        final ClassOrInterfaceDeclaration characteristicTemplate =
                characteristicCloneCU.getClassByName(containerClassName)
                        .orElseThrow(() -> new KiePMMLException(MAIN_CLASS_NOT_FOUND + ": " + containerClassName));
        final AtomicInteger atomicInteger = new AtomicInteger(0);
        final NodeList<Expression> evaluateCharacteristicsReferences = new NodeList<>();
        for (Characteristic characteristic : characteristics) {
            String characteristicName = containerClassName + "Characteristic_" + atomicInteger.addAndGet(1);
            addCharacteristic(characteristicsTemplate, characteristicTemplate, dataDictionary, derivedFields,
                              reasonCodeAlgorithm, characteristic,
                              containerClassName, characteristicName);
            MethodReferenceExpr toAdd = new MethodReferenceExpr();
            toAdd.setScope(new NameExpr(containerClassName));
            String identifier = EVALUATE_CHARACTERISTIC + characteristicName;
            toAdd.setIdentifier(identifier);
            evaluateCharacteristicsReferences.add(toAdd);
        }

        final MethodDeclaration characteristicsMethodDeclaration =
                characteristicsTemplate.getMethodsByName(EVALUATE_CHARACTERISTICS).get(0);
        final BlockStmt body = characteristicsMethodDeclaration.getBody()
                .orElseThrow(() -> new KiePMMLInternalException(String.format(MISSING_BODY_TEMPLATE,
                                                                              characteristicsMethodDeclaration)));
        // Set initialScore
        if (initialScore != null) {
            NameExpr initialScoreExpression = new NameExpr(initialScore.toString());
            CommonCodegenUtils.setVariableDeclaratorValue(body, INITIAL_SCORE, initialScoreExpression);
        }
        // Set characteristics
        final MethodCallExpr valuesInit = new MethodCallExpr();
        if (evaluateCharacteristicsReferences.isEmpty()) {
            valuesInit.setScope(new TypeExpr(parseClassOrInterfaceType(Collections.class.getName())));
            valuesInit.setName(EMPTY_LIST);
        } else {
            valuesInit.setScope(new TypeExpr(parseClassOrInterfaceType(Arrays.class.getName())));
            valuesInit.setName(AS_LIST);
            valuesInit.setArguments(evaluateCharacteristicsReferences);
        }
        CommonCodegenUtils.setVariableDeclaratorValue(body, CHARACTERISTIC_FUNCTIONS, valuesInit);
        String fullCharacteristicsName = String.format(PACKAGE_CLASS_TEMPLATE, packageName, containerClassName);

        //
        final ConstructorDeclaration constructorDeclaration = characteristicsTemplate.getDefaultConstructor()
                .orElseThrow(() -> new KiePMMLException(String.format(MISSING_DEFAULT_CONSTRUCTOR,
                                                                      characteristicsTemplate)));
        setConstructorSuperNameInvocation(containerClassName, constructorDeclaration, containerClassName);
        final Map<String, String> toReturn = new HashMap<>();
        toReturn.put(fullCharacteristicsName, characteristicsCloneCU.toString());
        return toReturn;
    }

    static void addCharacteristic(final ClassOrInterfaceDeclaration characteristicsTemplate,
                                  final ClassOrInterfaceDeclaration characteristicTemplate,
                                  final DataDictionary dataDictionary,
                                  final List<DerivedField> derivedFields,
                                  final Scorecard.ReasonCodeAlgorithm reasonCodeAlgorithm,
                                  final Characteristic characteristic,
                                  final String containerClassName, final String characteristicName) {

        final AtomicInteger atomicInteger = new AtomicInteger(0);
        final NodeList<Expression> evaluateAttributesReferences = new NodeList<>();
        for (Attribute attribute : characteristic.getAttributes()) {
            String attributeName = characteristicName + "_" + atomicInteger.addAndGet(1);
            addAttribute(characteristicsTemplate, characteristicTemplate, dataDictionary,
                         derivedFields,
                         attribute,
                         containerClassName, attributeName);
            MethodReferenceExpr toAdd = new MethodReferenceExpr();
            toAdd.setScope(new NameExpr(containerClassName));
            String identifier = EVALUATE_ATTRIBUTE + attributeName;
            toAdd.setIdentifier(identifier);
            evaluateAttributesReferences.add(toAdd);
        }
        final MethodDeclaration evaluateCharacteristicMethodDeclaration =
                getEvaluateCharacteristicMethodDeclaration(characteristicTemplate, characteristicName,
                                                           characteristic.getBaselineScore(),
                                                           characteristic.getReasonCode(),
                                                           reasonCodeAlgorithm,
                                                           evaluateAttributesReferences);
        CommonCodegenUtils.addMethodDeclarationToClass(characteristicsTemplate,
                                                       evaluateCharacteristicMethodDeclaration);
    }

    static MethodDeclaration getEvaluateCharacteristicMethodDeclaration(final ClassOrInterfaceDeclaration characteristicTemplate,
                                                                        final String characteristicName,
                                                                        final Object scoreParam,
                                                                        final String reasonCode,
                                                                        final Scorecard.ReasonCodeAlgorithm reasonCodeAlgorithm,
                                                                        final NodeList<Expression> evaluateAttributesReferences) {
        final MethodDeclaration toReturn =
                characteristicTemplate.getMethodsByName(EVALUATE_CHARACTERISTIC).get(0).clone();
        toReturn.setName(EVALUATE_CHARACTERISTIC + characteristicName);
        final BlockStmt body = toReturn.getBody()
                .orElseThrow(() -> new KiePMMLInternalException(String.format(MISSING_BODY_TEMPLATE, toReturn)));
        // Set score
        if (scoreParam != null) {
            String scoreParamExpr = scoreParam instanceof String ? String.format("\"%s\"", scoreParam) :
                    scoreParam.toString();
            NameExpr scoreExpression = new NameExpr(scoreParamExpr);
            CommonCodegenUtils.setVariableDeclaratorValue(body, SCORE, scoreExpression);
        }
        // Set reasonCode
        if (reasonCode != null) {
            String reasonCodeParamExpr = String.format("\"%s\"", reasonCode);
            NameExpr reasonCodeExpression = new NameExpr(reasonCodeParamExpr);
            CommonCodegenUtils.setVariableDeclaratorValue(body, REASON_CODE, reasonCodeExpression);
        }
        // Set reasonCodeAlgorithm
        if (reasonCodeAlgorithm != null) {
            REASONCODE_ALGORITHM reasCdAl = REASONCODE_ALGORITHM.byName(reasonCodeAlgorithm.value());
            String reasonCodeAlgorithmParamExpr = String.format("%s.%s", reasCdAl.getClass().getName(),
                                                                reasCdAl.name());
            NameExpr reasonCodeAlgorithmExpression = new NameExpr(reasonCodeAlgorithmParamExpr);
            CommonCodegenUtils.setVariableDeclaratorValue(body, REASON_CODE_ALGORITHM, reasonCodeAlgorithmExpression);
        }
        // Set attributes
        final MethodCallExpr valuesInit = new MethodCallExpr();
        if (evaluateAttributesReferences.isEmpty()) {
            valuesInit.setScope(new TypeExpr(parseClassOrInterfaceType(Collections.class.getName())));
            valuesInit.setName(EMPTY_LIST);
        } else {
            valuesInit.setScope(new TypeExpr(parseClassOrInterfaceType(Arrays.class.getName())));
            valuesInit.setName(AS_LIST);
            valuesInit.setArguments(evaluateAttributesReferences);
        }
        CommonCodegenUtils.setVariableDeclaratorValue(body, ATTRIBUTE_FUNCTIONS, valuesInit);
        return toReturn;
    }

    static void addAttribute(final ClassOrInterfaceDeclaration characteristicsTemplate,
                             final ClassOrInterfaceDeclaration characteristicTemplate,
                             final DataDictionary dataDictionary,
                             final List<DerivedField> derivedFields,
                             final Attribute attribute,
                             final String containerClassName, final String attributeName) {
        // Add predicate
        addPredicate(characteristicsTemplate, characteristicTemplate, dataDictionary, derivedFields,
                     attribute.getPredicate(),
                     containerClassName, attributeName);
        // Add complex score
        final ComplexPartialScore complexPartialScore = attribute.getComplexPartialScore();
        final boolean hasComplexScore = complexPartialScore != null;
        if (hasComplexScore) {
            addEvaluateComplexScoreMethod(characteristicsTemplate, characteristicTemplate, dataDictionary,
                                          complexPartialScore,
                                          containerClassName, attributeName);
        }
        // Add method declaration
        final MethodDeclaration attributeMethodDeclaration =
                getEvaluateAttributeMethodDeclaration(characteristicTemplate, containerClassName, attributeName,
                                                      attribute.getPartialScore(), attribute.getReasonCode(),
                                                      hasComplexScore);
        CommonCodegenUtils.addMethodDeclarationToClass(characteristicsTemplate, attributeMethodDeclaration);
    }

    static MethodDeclaration getEvaluateAttributeMethodDeclaration(final ClassOrInterfaceDeclaration characteristicTemplate, final String containerClassName, final String attributeName, final Object scoreParam, final String reasonCode, final boolean hasComplexScore) {
        final MethodDeclaration toReturn =
                characteristicTemplate.getMethodsByName(EVALUATE_ATTRIBUTE).get(0).clone();
        toReturn.setName(EVALUATE_ATTRIBUTE + attributeName);
        final BlockStmt body = toReturn.getBody()
                .orElseThrow(() -> new KiePMMLInternalException(String.format(MISSING_BODY_TEMPLATE, toReturn)));
        final MethodReferenceExpr predicateReference = new MethodReferenceExpr();
        predicateReference.setScope(new NameExpr(containerClassName));
        String identifier = EVALUATE_PREDICATE + attributeName;
        predicateReference.setIdentifier(identifier);
        CommonCodegenUtils.setVariableDeclaratorValue(body, PREDICATE_FUNCTION, predicateReference);

        if (hasComplexScore) {
            final MethodReferenceExpr evaluateComplexScoreReference = new MethodReferenceExpr();
            evaluateComplexScoreReference.setScope(new NameExpr(containerClassName));
            identifier = EVALUATE_COMPLEX_SCORE + attributeName;
            evaluateComplexScoreReference.setIdentifier(identifier);
            CommonCodegenUtils.setVariableDeclaratorValue(body, EVALUATE_COMPLEX_SCORE_FUNCTION,
                                                          evaluateComplexScoreReference);
        }
        // Set score
        if (scoreParam != null) {
            String scoreParamExpr = scoreParam instanceof String ? String.format("\"%s\"", scoreParam) :
                    scoreParam.toString();
            NameExpr scoreExpression = new NameExpr(scoreParamExpr);
            CommonCodegenUtils.setVariableDeclaratorValue(body, SCORE, scoreExpression);
        }
        // Set reasonCode
        if (reasonCode != null) {
            String reasonCodeParamExpr = String.format("\"%s\"", reasonCode);
            NameExpr reasonCodeExpression = new NameExpr(reasonCodeParamExpr);
            CommonCodegenUtils.setVariableDeclaratorValue(body, REASON_CODE, reasonCodeExpression);
        }
        return toReturn;
    }

    static void addEvaluateComplexScoreMethod(final ClassOrInterfaceDeclaration characteristicsTemplate,
                                              final ClassOrInterfaceDeclaration characteristicTemplate,
                                              final DataDictionary dataDictionary,
                                              final ComplexPartialScore complexPartialScore,
                                              final String containerClassName, final String attributeName) {
        final org.dmg.pmml.Expression expression = complexPartialScore.getExpression();
        if (expression == null) {
            throw new IllegalArgumentException("Missing required Expression in ComplexPartialScore " + complexPartialScore);
        }
        String expressionMethodName = COMPLEX_SCORE_FUNCTION + attributeName;
        MethodDeclaration retrieved = getExpressionMethodDeclarationWithStringObjectMap(expression, DataType.DOUBLE,
                                                                                        expressionMethodName);
        retrieved.setModifiers(Modifier.Keyword.PRIVATE, Modifier.Keyword.STATIC);
        CommonCodegenUtils.addMethodDeclarationToClass(characteristicsTemplate, retrieved);
        final MethodDeclaration evaluateComplexScoreMethod =
                characteristicTemplate.getMethodsByName(EVALUATE_COMPLEX_SCORE).get(0).clone();
        evaluateComplexScoreMethod.setName(EVALUATE_COMPLEX_SCORE + attributeName);
        final BlockStmt body =
                evaluateComplexScoreMethod.getBody()
                        .orElseThrow(() -> new KiePMMLException(String.format(MISSING_BODY_TEMPLATE,
                                                                              evaluateComplexScoreMethod)));
        final MethodCallExpr methodReferenceExpr = new MethodCallExpr();
        methodReferenceExpr.setScope(new NameExpr(containerClassName));
        methodReferenceExpr.setName(expressionMethodName);
        methodReferenceExpr.setArguments(NodeList.nodeList(new NameExpr(STRING_OBJECT_MAP)));
        CommonCodegenUtils.setVariableDeclaratorValue(body, "toReturn", methodReferenceExpr);
        CommonCodegenUtils.addMethodDeclarationToClass(characteristicsTemplate, evaluateComplexScoreMethod);
    }

    static void addPredicate(final ClassOrInterfaceDeclaration characteristicsTemplate,
                             final ClassOrInterfaceDeclaration characteristicTemplate,
                             final DataDictionary dataDictionary,
                             final List<DerivedField> derivedFields,
                             final Predicate predicate,
                             final String containerClassName,
                             final String attributeName) {
        final List<MethodDeclaration> compoundPredicateMethods = new ArrayList<>();
        final MethodDeclaration evaluatePredicateMethod =
                characteristicTemplate.getMethodsByName(EVALUATE_PREDICATE).get(0).clone();
        evaluatePredicateMethod.setName(EVALUATE_PREDICATE + attributeName);
        final BlockStmt evaluatePredicateBody = KiePMMLPredicateFactory.getPredicateBody(predicate,
                                                                                         dataDictionary,
                                                                                         derivedFields,
                                                                                         compoundPredicateMethods,
                                                                                         containerClassName,
                                                                                         attributeName,
                                                                                         new AtomicInteger());
        evaluatePredicateMethod.setBody(evaluatePredicateBody);
        compoundPredicateMethods.add(evaluatePredicateMethod);
        CommonCodegenUtils.addMethodDeclarationsToClass(characteristicsTemplate, compoundPredicateMethods);
    }
}
