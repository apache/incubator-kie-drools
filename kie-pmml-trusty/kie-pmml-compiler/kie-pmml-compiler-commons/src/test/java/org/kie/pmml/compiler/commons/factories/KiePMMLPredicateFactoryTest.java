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

package org.kie.pmml.compiler.commons.factories;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ConstructorDeclaration;
import com.github.javaparser.ast.expr.AssignExpr;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.NullLiteralExpr;
import com.github.javaparser.ast.expr.ObjectCreationExpr;
import com.github.javaparser.ast.stmt.ExplicitConstructorInvocationStmt;
import com.github.javaparser.ast.stmt.ExpressionStmt;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import org.dmg.pmml.Array;
import org.dmg.pmml.CompoundPredicate;
import org.dmg.pmml.DataDictionary;
import org.dmg.pmml.DataField;
import org.dmg.pmml.DataType;
import org.dmg.pmml.False;
import org.dmg.pmml.FieldName;
import org.dmg.pmml.Predicate;
import org.dmg.pmml.SimplePredicate;
import org.dmg.pmml.SimpleSetPredicate;
import org.dmg.pmml.True;
import org.dmg.pmml.Visitor;
import org.dmg.pmml.VisitorAction;
import org.junit.BeforeClass;
import org.junit.Test;
import org.kie.pmml.api.exceptions.KiePMMLException;
import org.kie.pmml.api.enums.ARRAY_TYPE;
import org.kie.pmml.api.enums.BOOLEAN_OPERATOR;
import org.kie.pmml.api.enums.IN_NOTIN;
import org.kie.pmml.api.enums.OPERATOR;
import org.kie.pmml.commons.model.predicates.KiePMMLCompoundPredicate;
import org.kie.pmml.commons.model.predicates.KiePMMLFalsePredicate;
import org.kie.pmml.commons.model.predicates.KiePMMLPredicate;
import org.kie.pmml.commons.model.predicates.KiePMMLSimplePredicate;
import org.kie.pmml.commons.model.predicates.KiePMMLSimpleSetPredicate;
import org.kie.pmml.commons.model.predicates.KiePMMLTruePredicate;
import org.kie.pmml.compiler.commons.testutils.PMMLModelTestUtils;

import static com.github.javaparser.StaticJavaParser.parseClassOrInterfaceType;
import static com.github.javaparser.StaticJavaParser.parseExpression;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.kie.pmml.commons.utils.KiePMMLModelUtils.getSanitizedClassName;
import static org.kie.pmml.compiler.commons.factories.KiePMMLPredicateFactory.KIE_PMML_COMPOUND_PREDICATE_TEMPLATE;
import static org.kie.pmml.compiler.commons.factories.KiePMMLPredicateFactory.KIE_PMML_COMPOUND_PREDICATE_TEMPLATE_JAVA;
import static org.kie.pmml.compiler.commons.factories.KiePMMLPredicateFactory.KIE_PMML_SIMPLE_PREDICATE_TEMPLATE;
import static org.kie.pmml.compiler.commons.factories.KiePMMLPredicateFactory.KIE_PMML_SIMPLE_PREDICATE_TEMPLATE_JAVA;
import static org.kie.pmml.compiler.commons.factories.KiePMMLPredicateFactory.KIE_PMML_SIMPLE_SET_PREDICATE_TEMPLATE;
import static org.kie.pmml.compiler.commons.factories.KiePMMLPredicateFactory.KIE_PMML_SIMPLE_SET_PREDICATE_TEMPLATE_JAVA;
import static org.kie.pmml.compiler.commons.factories.KiePMMLPredicateFactory.KIE_PMML_TRUE_PREDICATE_TEMPLATE;
import static org.kie.pmml.compiler.commons.factories.KiePMMLPredicateFactory.KIE_PMML_TRUE_PREDICATE_TEMPLATE_JAVA;
import static org.kie.pmml.compiler.commons.testutils.CodegenTestUtils.commonEvaluateConstructor;
import static org.kie.pmml.compiler.commons.testutils.CodegenTestUtils.commonValidateCompilation;
import static org.kie.pmml.compiler.commons.testutils.PMMLModelTestUtils.getArray;
import static org.kie.pmml.compiler.commons.testutils.PMMLModelTestUtils.getDataDictionary;
import static org.kie.pmml.compiler.commons.testutils.PMMLModelTestUtils.getRandomSimplePredicateOperator;
import static org.kie.pmml.compiler.commons.testutils.PMMLModelTestUtils.getRandomSimpleSetPredicateOperator;
import static org.kie.pmml.compiler.commons.testutils.PMMLModelTestUtils.getRandomValue;
import static org.kie.pmml.compiler.commons.testutils.PMMLModelTestUtils.getStringObjects;
import static org.kie.pmml.compiler.commons.utils.JavaParserUtils.getFromFileName;

public class KiePMMLPredicateFactoryTest {

    private static Map<String, DataType> simplePredicateNameType;
    private static List<SimplePredicate> simplePredicates;
    private static Map<String, Array.Type> simpleSetPredicateNameType;
    private static List<SimpleSetPredicate> simpleSetPredicates;
    private static DataDictionary dataDictionary;
    private ConstructorDeclaration constructorDeclaration;
    private ExplicitConstructorInvocationStmt superInvocation;
    private List<AssignExpr> assignExprs;

    @BeforeClass
    public static void setup() {
        simplePredicateNameType = new HashMap<>();
        simplePredicateNameType.put("age", DataType.INTEGER);
        simplePredicateNameType.put("weight", DataType.DOUBLE);
        simplePredicateNameType.put("name", DataType.STRING);
        simplePredicateNameType.put("runner", DataType.BOOLEAN);
        simplePredicates = simplePredicateNameType
                .entrySet()
                .stream()
                .map(entry -> PMMLModelTestUtils.getSimplePredicate(entry.getKey(),
                                                                    getRandomValue(entry.getValue()),
                                                                    getRandomSimplePredicateOperator()))
                .collect(Collectors.toList());
        List<DataField> dataFields = new ArrayList<>();
        simplePredicateNameType.forEach((name, dataType) -> {
            DataField toAdd = new DataField();
            toAdd.setName(FieldName.create(name));
            toAdd.setDataType(dataType);
            dataFields.add(toAdd);
        });
        dataDictionary = getDataDictionary(dataFields);
        simpleSetPredicateNameType = new HashMap<>();
        simpleSetPredicateNameType.put("age", Array.Type.INT);
        simpleSetPredicateNameType.put("weight", Array.Type.REAL);
        simpleSetPredicateNameType.put("name", Array.Type.STRING);
        simpleSetPredicates = simpleSetPredicateNameType
                .entrySet()
                .stream()
                .map(entry -> {
                    List<String> values = getStringObjects(entry.getValue(), 4);
                    return PMMLModelTestUtils.getSimpleSetPredicate(entry.getKey(),
                                                                    entry.getValue(),
                                                                    values,
                                                                    getRandomSimpleSetPredicateOperator());
                })
                .collect(Collectors.toList());
    }

    @Test
    public void getPredicates() {
        List<Predicate> predicates = new ArrayList<>(simplePredicates);
        CompoundPredicate compoundPredicate = new CompoundPredicate();
        compoundPredicate.setBooleanOperator(CompoundPredicate.BooleanOperator.XOR);
        compoundPredicate.getPredicates().addAll(simplePredicates);
        predicates.add(compoundPredicate);
        True truePredicate = new True();
        predicates.add(truePredicate);
        False falsePredicate = new False();
        predicates.add(falsePredicate);
        List<KiePMMLPredicate> retrieved = KiePMMLPredicateFactory
                .getPredicates(predicates, dataDictionary);
        assertEquals(predicates.size(), retrieved.size());
    }

    @Test
    public void getPredicateSimple() {
        simplePredicates.forEach(simplePredicate -> {
            KiePMMLPredicate retrieved = KiePMMLPredicateFactory.getPredicate(simplePredicate, dataDictionary);
            assertTrue(retrieved instanceof KiePMMLSimplePredicate);
        });
    }

    @Test
    public void getPredicateSimpleSet() {
        simpleSetPredicates.forEach(simpleSetPredicate -> {
            KiePMMLPredicate retrieved = KiePMMLPredicateFactory.getPredicate(simpleSetPredicate, dataDictionary);
            assertTrue(retrieved instanceof KiePMMLSimpleSetPredicate);
        });
    }

    @Test
    public void getPredicateCompound() {
        CompoundPredicate compoundPredicate = new CompoundPredicate();
        compoundPredicate.setBooleanOperator(CompoundPredicate.BooleanOperator.XOR);
        compoundPredicate.getPredicates().addAll(simplePredicates);
        compoundPredicate.getPredicates().addAll(simpleSetPredicates);
        KiePMMLPredicate retrieved = KiePMMLPredicateFactory.getPredicate(compoundPredicate, dataDictionary);
        assertTrue(retrieved instanceof KiePMMLCompoundPredicate);
    }

    @Test
    public void getPredicateTrue() {
        True truePredicate = new True();
        KiePMMLPredicate retrieved = KiePMMLPredicateFactory.getPredicate(truePredicate, dataDictionary);
        assertTrue(retrieved instanceof KiePMMLTruePredicate);
    }

    @Test
    public void getPredicateFalse() {
        False falsePredicate = new False();
        KiePMMLPredicate retrieved = KiePMMLPredicateFactory.getPredicate(falsePredicate, dataDictionary);
        assertTrue(retrieved instanceof KiePMMLFalsePredicate);
    }

    @Test(expected = KiePMMLException.class)
    public void getPredicateUnknown() {
        Predicate unknownPredicate = new Predicate() {

            @Override
            public VisitorAction accept(Visitor visitor) {
                return null;
            }
        };
        KiePMMLPredicateFactory.getPredicate(unknownPredicate, dataDictionary);
    }

    @Test
    public void getKiePMMLSimplePredicate() {
        simplePredicates.forEach(simplePredicate -> {
            KiePMMLSimplePredicate retrieved = KiePMMLPredicateFactory.getKiePMMLSimplePredicate(simplePredicate,
                                                                                                 simplePredicateNameType.get(simplePredicate.getField().getValue()));
            commonVerifySimplePredicate(retrieved, null);
        });
    }

    @Test
    public void getKiePMMLSimpleSetPredicate() {
        simpleSetPredicates.forEach(simpleSetPredicate -> {
            KiePMMLSimpleSetPredicate retrieved =
                    KiePMMLPredicateFactory.getKiePMMLSimpleSetPredicate(simpleSetPredicate);
            commonVerifySimpleSetPredicate(retrieved, null);
        });
    }

    @Test
    public void getKiePMMLCompoundPredicate() {
        CompoundPredicate compoundPredicate = new CompoundPredicate();
        compoundPredicate.setBooleanOperator(CompoundPredicate.BooleanOperator.XOR);
        compoundPredicate.getPredicates().addAll(simplePredicates);

        KiePMMLCompoundPredicate retrieved = KiePMMLPredicateFactory.getKiePMMLCompoundPredicate(compoundPredicate,
                                                                                                 dataDictionary);
        assertNotNull(retrieved);
        assertEquals(BOOLEAN_OPERATOR.XOR, retrieved.getBooleanOperator());
        assertEquals(simplePredicates.size(), retrieved.getKiePMMLPredicates().size());
        retrieved.getKiePMMLPredicates().forEach(simplePredicate -> commonVerifySimplePredicate((KiePMMLSimplePredicate) simplePredicate, retrieved.getId()));
    }

    @Test
    public void getKiePMMLTruePredicate() {
        KiePMMLTruePredicate retrieved = KiePMMLPredicateFactory.getKiePMMLTruePredicate();
        assertNotNull(retrieved);
        KiePMMLTruePredicate expected = KiePMMLTruePredicate.builder(Collections.emptyList()).build();
        assertEquals(expected.getName(), retrieved.getName());
        assertNotEquals(expected.getId(), retrieved.getId());
    }

    @Test
    public void getKiePMMLFalsePredicate() {
        KiePMMLFalsePredicate retrieved = KiePMMLPredicateFactory.getKiePMMLFalsePredicate();
        assertNotNull(retrieved);
        KiePMMLFalsePredicate expected = KiePMMLFalsePredicate.builder(Collections.emptyList()).build();
        assertEquals(expected.getName(), retrieved.getName());
        assertNotEquals(expected.getId(), retrieved.getId());
    }

    @Test
    public void getPredicateSourcesMap() {
        String packageName = "PACKAGENAME";
        String predicateName = "PREDICATENAME";
        KiePMMLPredicate kiePMMLPredicate = KiePMMLSimplePredicate
                .builder(predicateName, Collections.emptyList(), OPERATOR.GREATER_OR_EQUAL)
                .withValue(24)
                .build();
        Map<String, String> retrieved = KiePMMLPredicateFactory.getPredicateSourcesMap(kiePMMLPredicate, packageName);
        commonVerifySourceMap(retrieved, packageName, getSanitizedClassName(kiePMMLPredicate.getId()), 1);
        kiePMMLPredicate = KiePMMLCompoundPredicate.builder(Collections.emptyList(), BOOLEAN_OPERATOR.OR).build();
        retrieved = KiePMMLPredicateFactory.getPredicateSourcesMap(kiePMMLPredicate, packageName);
        commonVerifySourceMap(retrieved, packageName, getSanitizedClassName(kiePMMLPredicate.getId()), 1);
        kiePMMLPredicate = KiePMMLTruePredicate.builder(Collections.emptyList()).build();
        retrieved = KiePMMLPredicateFactory.getPredicateSourcesMap(kiePMMLPredicate, packageName);
        commonVerifySourceMap(retrieved, packageName, getSanitizedClassName(kiePMMLPredicate.getId()), 1);
        kiePMMLPredicate = KiePMMLFalsePredicate.builder(Collections.emptyList()).build();
        retrieved = KiePMMLPredicateFactory.getPredicateSourcesMap(kiePMMLPredicate, packageName);
        commonVerifySourceMap(retrieved, packageName, getSanitizedClassName(kiePMMLPredicate.getId()), 1);
    }

    @Test
    public void getKiePMMLSimplePredicateSourcesMap() {
        String predicateName = "PREDICATENAME";
        KiePMMLSimplePredicate kiePMMLSimplePredicate = KiePMMLSimplePredicate
                .builder(predicateName, Collections.emptyList(), OPERATOR.GREATER_OR_EQUAL)
                .withValue(24)
                .build();
        String packageName = "PACKAGENAME";
        final Map<String, String> retrieved =
                KiePMMLPredicateFactory.getKiePMMLSimplePredicateSourcesMap(kiePMMLSimplePredicate, packageName);
        commonVerifySourceMap(retrieved, packageName, getSanitizedClassName(kiePMMLSimplePredicate.getId()), 1);
    }

    @Test
    public void getKiePMMLSimpleSetPredicateSourcesMap() {
        String predicateName = "PREDICATENAME";
        ARRAY_TYPE arrayType = ARRAY_TYPE.STRING;
        IN_NOTIN inNotIn = IN_NOTIN.IN;
        List<Object> values = getObjects(arrayType, 3);
        KiePMMLSimpleSetPredicate kiePMMLSimpleSetPredicate = KiePMMLSimpleSetPredicate
                .builder(predicateName,
                         Collections.emptyList(),
                         arrayType,
                         inNotIn)
                .withValues(values)
                .build();
        String packageName = "PACKAGENAME";
        final Map<String, String> retrieved =
                KiePMMLPredicateFactory.getKiePMMLSimpleSetPredicateSourcesMap(kiePMMLSimpleSetPredicate, packageName);
        commonVerifySourceMap(retrieved, packageName, getSanitizedClassName(kiePMMLSimpleSetPredicate.getId()), 1);
    }

    @Test
    public void getKiePMMLCompoundPredicateSourcesMap() {
        KiePMMLSimplePredicate kiePMMLSimplePredicateInt = KiePMMLSimplePredicate
                .builder("SIMPLEPREDICATEINTNAME", Collections.emptyList(), OPERATOR.GREATER_OR_EQUAL)
                .withValue(24)
                .build();
        KiePMMLSimplePredicate kiePMMLSimplePredicateString = KiePMMLSimplePredicate
                .builder("SIMPLEPREDICATESTRINGNAME", Collections.emptyList(), OPERATOR.GREATER_OR_EQUAL)
                .withValue("FOR")
                .build();
        ARRAY_TYPE arrayType = ARRAY_TYPE.STRING;
        IN_NOTIN inNotIn = IN_NOTIN.IN;
        List<Object> values = getObjects(arrayType, 3);
        KiePMMLSimpleSetPredicate kiePMMLSimpleSetPredicate = KiePMMLSimpleSetPredicate
                .builder("SIMPLESETPREDICATE",
                         Collections.emptyList(),
                         arrayType,
                         inNotIn)
                .withValues(values)
                .build();
        List<KiePMMLPredicate> kiePMMLPredicates = Arrays.asList(kiePMMLSimplePredicateInt,
                                                                 kiePMMLSimplePredicateString,
                                                                 kiePMMLSimpleSetPredicate);
        KiePMMLCompoundPredicate kiePMMLCompoundPredicate = KiePMMLCompoundPredicate.builder(Collections.emptyList(),
                                                                                             BOOLEAN_OPERATOR.OR)
                .withKiePMMLPredicates(kiePMMLPredicates)
                .build();
        String packageName = "PACKAGENAME";
        final Map<String, String> retrieved =
                KiePMMLPredicateFactory.getKiePMMLCompoundPredicateSourcesMap(kiePMMLCompoundPredicate, packageName);
        commonVerifySourceMap(retrieved, packageName, getSanitizedClassName(kiePMMLCompoundPredicate.getId()), 4);
    }

    @Test(expected = IllegalArgumentException.class)
    public void getKiePMMLCompoundPredicateUnsupportedSourcesMap() {
        KiePMMLSimplePredicate kiePMMLSimplePredicateInt = KiePMMLSimplePredicate
                .builder("SIMPLEPREDICATEINTNAME", Collections.emptyList(), OPERATOR.GREATER_OR_EQUAL)
                .withValue(24)
                .build();
        KiePMMLSimplePredicate kiePMMLSimplePredicateString = KiePMMLSimplePredicate
                .builder("SIMPLEPREDICATESTRINGNAME", Collections.emptyList(), OPERATOR.GREATER_OR_EQUAL)
                .withValue("FOR")
                .build();
        ARRAY_TYPE arrayType = ARRAY_TYPE.STRING;
        IN_NOTIN inNotIn = IN_NOTIN.IN;
        List<Object> values = getObjects(arrayType, 3);
        KiePMMLSimpleSetPredicate kiePMMLSimpleSetPredicate = KiePMMLSimpleSetPredicate
                .builder("SIMPLESETPREDICATE",
                         Collections.emptyList(),
                         arrayType,
                         inNotIn)
                .withValues(values)
                .build();
        List<KiePMMLPredicate> kiePMMLPredicates = Arrays.asList(kiePMMLSimplePredicateInt,
                                                                 kiePMMLSimplePredicateString,
                                                                 kiePMMLSimpleSetPredicate);
        KiePMMLCompoundPredicate kiePMMLCompoundPredicate = KiePMMLCompoundPredicate.builder(Collections.emptyList(),
                                                                                             BOOLEAN_OPERATOR.SURROGATE)
                .withKiePMMLPredicates(kiePMMLPredicates)
                .build();
        KiePMMLPredicateFactory.getKiePMMLCompoundPredicateSourcesMap(kiePMMLCompoundPredicate, "PACKAGENAME");
    }

    @Test
    public void getKiePMMLTruePredicateSourcesMap() {
        final KiePMMLTruePredicate kiePMMLTruePredicate = KiePMMLTruePredicate.builder(Collections.emptyList()).build();
        String packageName = "PACKAGENAME";
        final Map<String, String> retrieved =
                KiePMMLPredicateFactory.getKiePMMLTruePredicateSourcesMap(kiePMMLTruePredicate, packageName);
        commonVerifySourceMap(retrieved, packageName, getSanitizedClassName(kiePMMLTruePredicate.getId()), 1);
    }

    @Test
    public void getKiePMMLFalsePredicateSourcesMap() {
        final KiePMMLFalsePredicate kiePMMLFalsePredicate =
                KiePMMLFalsePredicate.builder(Collections.emptyList()).build();
        String packageName = "PACKAGENAME";
        final Map<String, String> retrieved =
                KiePMMLPredicateFactory.getKiePMMLFalsePredicateSourcesMap(kiePMMLFalsePredicate, packageName);
        commonVerifySourceMap(retrieved, packageName, getSanitizedClassName(kiePMMLFalsePredicate.getId()), 1);
    }

    @Test
    public void setSimplePredicateConstructorGreaterOrEqual() {
        init(KIE_PMML_SIMPLE_PREDICATE_TEMPLATE_JAVA, KIE_PMML_SIMPLE_PREDICATE_TEMPLATE);
        AssignExpr valueAssignExpr =
                assignExprs.stream().filter(assignExpr -> assignExpr.getTarget().asNameExpr().getNameAsString().equals("value"))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Missing \"value\" assign variable"));
        assertTrue(valueAssignExpr.getValue() instanceof NullLiteralExpr);
        String generatedClassName = "GENERATEDCLASSNAME";
        String predicateName = "PREDICATENAME";
        OPERATOR operator = OPERATOR.GREATER_OR_EQUAL;
        Object value = 24;
        KiePMMLPredicateFactory.setSimplePredicateConstructor(generatedClassName,
                                                              predicateName,
                                                              constructorDeclaration,
                                                              operator, value);
        Map<Integer, Expression> superInvocationExpressionsMap = new HashMap<>();
        superInvocationExpressionsMap.put(0, new NameExpr(String.format("\"%s\"", predicateName)));
        superInvocationExpressionsMap.put(2,
                                          new NameExpr(operator.getClass().getCanonicalName() + "." + operator.name()));
        Map<String, Expression> assignExpressionMap = new HashMap<>();
        assignExpressionMap.put("value", valueAssignExpr.getValue().asNameExpr());
        assertTrue(commonEvaluateConstructor(constructorDeclaration, generatedClassName, superInvocationExpressionsMap,
                                  assignExpressionMap));
        assertEquals("24", valueAssignExpr.getValue().asNameExpr().toString());
    }

    @Test(expected = IllegalArgumentException.class)
    public void setSimplePredicateConstructorIsMissing() {
        init(KIE_PMML_SIMPLE_PREDICATE_TEMPLATE_JAVA, KIE_PMML_SIMPLE_PREDICATE_TEMPLATE);
        AssignExpr valueAssignExpr =
                assignExprs.stream().filter(assignExpr -> assignExpr.getTarget().asNameExpr().getNameAsString().equals("value"))
                        .findFirst()
                        .orElseThrow(() -> new RuntimeException("Missing \"value\" assign variable"));
        assertTrue(valueAssignExpr.getValue() instanceof NullLiteralExpr);
        String generatedClassName = "GENERATEDCLASSNAME";
        String predicateName = "PREDICATENAME";
        OPERATOR operator = OPERATOR.IS_MISSING;
        Object value = "VALUE";
        KiePMMLPredicateFactory.setSimplePredicateConstructor(generatedClassName,
                                                              predicateName,
                                                              constructorDeclaration,
                                                              operator, value);
    }

    @Test
    public void setSimpleSetPredicateConstructor() {
        init(KIE_PMML_SIMPLE_SET_PREDICATE_TEMPLATE_JAVA, KIE_PMML_SIMPLE_SET_PREDICATE_TEMPLATE);
        AssignExpr valueAssignExpr =
                assignExprs.stream().filter(assignExpr -> assignExpr.getTarget().asNameExpr().getNameAsString().equals("values"))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Missing \"values\" assign variable"));
        assertTrue(valueAssignExpr.getValue() instanceof NullLiteralExpr);
        String generatedClassName = "GENERATEDCLASSNAME";
        String predicateName = "PREDICATENAME";
        ARRAY_TYPE arrayType = ARRAY_TYPE.REAL;
        IN_NOTIN inNotIn = IN_NOTIN.IN;
        List<Object> values = getObjects(arrayType, 3);
        KiePMMLPredicateFactory.setSimpleSetPredicateConstructor(generatedClassName,
                                                                 predicateName,
                                                                 constructorDeclaration,
                                                                 arrayType,
                                                                 inNotIn,
                                                                 values);
        Map<Integer, Expression> superInvocationExpressionsMap = new HashMap<>();
        superInvocationExpressionsMap.put(0, new NameExpr(String.format("\"%s\"", predicateName)));
        superInvocationExpressionsMap.put(2,
                                          new NameExpr(arrayType.getClass().getCanonicalName() + "." + arrayType.name()));
        superInvocationExpressionsMap.put(3,
                                          new NameExpr(inNotIn.getClass().getCanonicalName() + "." + inNotIn.name()));
        Map<String, Expression> assignExpressionMap = new HashMap<>();
        ClassOrInterfaceType kiePMMLSegmentClass = parseClassOrInterfaceType(ArrayList.class.getName());
        ObjectCreationExpr objectCreationExpr = new ObjectCreationExpr();
        objectCreationExpr.setType(kiePMMLSegmentClass);
        assignExpressionMap.put("values", objectCreationExpr);
        assertTrue(commonEvaluateConstructor(constructorDeclaration, generatedClassName, superInvocationExpressionsMap,
                                  assignExpressionMap));
        List<MethodCallExpr> methodCallExprs = constructorDeclaration.getBody()
                .getStatements().stream().filter(statement -> statement instanceof ExpressionStmt)
                .map(statement -> (ExpressionStmt) statement)
                .filter(expressionStmt -> expressionStmt.getExpression() instanceof MethodCallExpr)
                .map(expressionStmt -> (MethodCallExpr) expressionStmt.getExpression())
                .filter(methodCallExpr -> methodCallExpr.getScope().isPresent() &&
                        methodCallExpr.getScope().get().asNameExpr().getName().asString().equals("values") &&
                        methodCallExpr.getName().asString().equals("add"))
                .collect(Collectors.toList());
        assertNotNull(methodCallExprs);
        assertEquals(values.size(), methodCallExprs.size());
        values.forEach(o -> {
            Expression expected = new NameExpr(o.toString());
            assertTrue(methodCallExprs.stream()
                               .anyMatch(methodCallExpr -> methodCallExpr.getArguments().size() == 1 &&
                                       methodCallExpr.getArguments().get(0).equals(expected)));
        });
    }

    @Test
    public void setCompoundPredicateConstructorOr() {
        init(KIE_PMML_COMPOUND_PREDICATE_TEMPLATE_JAVA, KIE_PMML_COMPOUND_PREDICATE_TEMPLATE);
        String generatedClassName = "GENERATEDCLASSNAME";
        String predicateName = "PREDICATENAME";
        BOOLEAN_OPERATOR booleanOperator = BOOLEAN_OPERATOR.OR;
        Set<String> predicatesClasses = new HashSet<>(Arrays.asList("PREDICATE_A", "PREDICATE_B"));
        KiePMMLPredicateFactory.setCompoundPredicateConstructor(generatedClassName,
                                                                predicateName,
                                                                constructorDeclaration,
                                                                booleanOperator,
                                                                predicatesClasses);
        Map<Integer, Expression> superInvocationExpressionsMap = new HashMap<>();
        superInvocationExpressionsMap.put(0, new NameExpr(String.format("\"%s\"", predicateName)));
        superInvocationExpressionsMap.put(2,
                                          new NameExpr(booleanOperator.getClass().getCanonicalName() + "." + booleanOperator.name()));
        Map<String, Expression> assignExpressionMap = new HashMap<>();
        Expression expression = parseExpression("(aBoolean, aBoolean2) -> aBoolean != null ? aBoolean || aBoolean2 : " +
                                                        "aBoolean2");
        assignExpressionMap.put("operatorFunction", expression);
        ClassOrInterfaceType kiePMMLSegmentClass = parseClassOrInterfaceType(ArrayList.class.getName());
        ObjectCreationExpr objectCreationExpr = new ObjectCreationExpr();
        objectCreationExpr.setType(kiePMMLSegmentClass);
        assignExpressionMap.put("kiePMMLPredicates", objectCreationExpr);
        assertTrue(commonEvaluateConstructor(constructorDeclaration, generatedClassName, superInvocationExpressionsMap,
                                  assignExpressionMap));
        List<MethodCallExpr> methodCallExprs = constructorDeclaration.getBody()
                .getStatements().stream().filter(statement -> statement instanceof ExpressionStmt)
                .map(statement -> (ExpressionStmt) statement)
                .filter(expressionStmt -> expressionStmt.getExpression() instanceof MethodCallExpr)
                .map(expressionStmt -> (MethodCallExpr) expressionStmt.getExpression())
                .filter(methodCallExpr -> methodCallExpr.getScope().isPresent() &&
                        methodCallExpr.getScope().get().asNameExpr().getName().asString().equals("kiePMMLPredicates") &&
                        methodCallExpr.getName().asString().equals("add"))
                .collect(Collectors.toList());
        assertNotNull(methodCallExprs);
        assertEquals(predicatesClasses.size(), methodCallExprs.size());
        predicatesClasses.forEach(predicateClass -> {
            ClassOrInterfaceType kiePMMLPredicateClass = parseClassOrInterfaceType(predicateClass);
            ObjectCreationExpr expected = new ObjectCreationExpr();
            expected.setType(kiePMMLPredicateClass);
            assertTrue(methodCallExprs.stream()
                               .anyMatch(methodCallExpr -> methodCallExpr.getArguments().size() == 1 &&
                                       methodCallExpr.getArguments().get(0).equals(expected)));
        });
    }

    @Test
    public void setCompoundPredicateConstructorAnd() {
        init(KIE_PMML_COMPOUND_PREDICATE_TEMPLATE_JAVA, KIE_PMML_COMPOUND_PREDICATE_TEMPLATE);
        String generatedClassName = "GENERATEDCLASSNAME";
        String predicateName = "PREDICATENAME";
        Set<String> predicatesClasses = new HashSet<>(Arrays.asList("PREDICATE_A", "PREDICATE_B"));
        BOOLEAN_OPERATOR booleanOperator = BOOLEAN_OPERATOR.AND;
        KiePMMLPredicateFactory.setCompoundPredicateConstructor(generatedClassName,
                                                                predicateName,
                                                                constructorDeclaration,
                                                                booleanOperator,
                                                                predicatesClasses);
        Map<Integer, Expression> superInvocationExpressionsMap = new HashMap<>();
        superInvocationExpressionsMap.put(0, new NameExpr(String.format("\"%s\"", predicateName)));
        superInvocationExpressionsMap.put(2,
                                          new NameExpr(booleanOperator.getClass().getCanonicalName() + "." + booleanOperator.name()));
        Map<String, Expression> assignExpressionMap = new HashMap<>();
        Expression expression = parseExpression("(aBoolean, aBoolean2) -> aBoolean != null ? aBoolean && aBoolean2 : aBoolean2");
        assignExpressionMap.put("operatorFunction", expression);
        ClassOrInterfaceType kiePMMLSegmentClass = parseClassOrInterfaceType(ArrayList.class.getName());
        ObjectCreationExpr objectCreationExpr = new ObjectCreationExpr();
        objectCreationExpr.setType(kiePMMLSegmentClass);
        assignExpressionMap.put("kiePMMLPredicates", objectCreationExpr);
        assertTrue(commonEvaluateConstructor(constructorDeclaration, generatedClassName, superInvocationExpressionsMap,
                                  assignExpressionMap));
    }

    @Test(expected = IllegalArgumentException.class)
    public void setCompoundPredicateUnsupportedConstructor() {
        String generatedClassName = "GENERATEDCLASSNAME";
        String predicateName = "PREDICATENAME";
        BOOLEAN_OPERATOR booleanOperator = BOOLEAN_OPERATOR.SURROGATE;
        Set<String> predicatesClasses = new HashSet<>(Arrays.asList("PREDICATE_A", "PREDICATE_B"));
        KiePMMLPredicateFactory.setCompoundPredicateConstructor(generatedClassName,
                                                                predicateName,
                                                                constructorDeclaration,
                                                                booleanOperator,
                                                                predicatesClasses);
    }

    @Test
    public void setTrueFalsePredicateConstructor() {
        init(KIE_PMML_TRUE_PREDICATE_TEMPLATE_JAVA, KIE_PMML_TRUE_PREDICATE_TEMPLATE);
        String generatedClassName = "GENERATEDCLASSNAME";
        String predicateName = "PREDICATENAME";
        KiePMMLPredicateFactory.setTrueFalsePredicateConstructor(generatedClassName,
                                                                 predicateName,
                                                                 constructorDeclaration);
        Map<Integer, Expression> superInvocationExpressionsMap = new HashMap<>();
        superInvocationExpressionsMap.put(0, new NameExpr(String.format("\"%s\"", predicateName)));
        Map<String, Expression> assignExpressionMap = new HashMap<>();
        assertTrue(commonEvaluateConstructor(constructorDeclaration, generatedClassName, superInvocationExpressionsMap,
                                  assignExpressionMap));
    }

    @Test
    public void getObjectsFromArray() {
        List<String> values = Arrays.asList("32", "11", "43");
        Array array = getArray(Array.Type.INT, values);
        List<Object> retrieved = KiePMMLPredicateFactory.getObjectsFromArray(array);
        assertEquals(values.size(), retrieved.size());
        for (int i = 0; i < values.size(); i++) {
            Object obj = retrieved.get(i);
            assertTrue(obj instanceof Integer);
            Integer expected = Integer.valueOf(values.get(i));
            assertEquals(expected, obj);
        }
        values = Arrays.asList("just", "11", "fun");
        array = getArray(Array.Type.STRING, values);
        retrieved = KiePMMLPredicateFactory.getObjectsFromArray(array);
        assertEquals(values.size(), retrieved.size());
        for (int i = 0; i < values.size(); i++) {
            Object obj = retrieved.get(i);
            assertTrue(obj instanceof String);
            assertEquals(values.get(i), obj);
        }
        values = Arrays.asList("23.11", "11", "123.123");
        array = getArray(Array.Type.REAL, values);
        retrieved = KiePMMLPredicateFactory.getObjectsFromArray(array);
        assertEquals(values.size(), retrieved.size());
        for (int i = 0; i < values.size(); i++) {
            Object obj = retrieved.get(i);
            assertTrue(obj instanceof Double);
            Double expected = Double.valueOf(values.get(i));
            assertEquals(expected, obj);
        }
    }

    private void init(String templateSource, String templateClassName) {
        CompilationUnit compilationUnit = getFromFileName(templateSource);
        constructorDeclaration = compilationUnit.getClassByName(templateClassName)
                .orElseThrow(() -> new RuntimeException("Failed to retrieve ClassOrInterfaceDeclaration " + templateClassName + "  from " + templateSource))
                .getDefaultConstructor()
                .orElseThrow(() -> new RuntimeException("Failed to retrieve default constructor from " + templateSource));
        assertNotNull(constructorDeclaration);
        assertEquals(templateClassName, constructorDeclaration.getName().asString());
        assertTrue(compilationUnit.getClassByName(templateClassName).isPresent());
        superInvocation = constructorDeclaration.getBody().getStatements()
                .stream()
                .filter(statement -> statement instanceof ExplicitConstructorInvocationStmt)
                .map(statement -> (ExplicitConstructorInvocationStmt) statement)
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Failed to retrieve super invocation from " + templateSource));
        assertEquals("name", superInvocation.getArgument(0).asNameExpr().getNameAsString());
        assignExprs = constructorDeclaration.getBody().findAll(AssignExpr.class);
        assertNotNull(assignExprs);
    }

    private void commonVerifySimplePredicate(final KiePMMLSimplePredicate toVerify, final String parentId) {
        Optional<SimplePredicate> simplePredicate = simplePredicates
                .stream()
                .filter(predicate -> predicate.getField().getValue().equals(toVerify.getName()))
                .findFirst();
        if (!simplePredicate.isPresent()) {
            fail();
        } else {
            simplePredicate.ifPresent(predicate -> {
                OPERATOR expected = OPERATOR.byName(predicate.getOperator().value());
                assertEquals(expected, toVerify.getOperator());
                assertEquals(parentId, toVerify.getParentId());
            });
        }
    }

    private void commonVerifySimpleSetPredicate(final KiePMMLSimpleSetPredicate toVerify, final String parentId) {
        Optional<SimpleSetPredicate> simpleSetPredicate = simpleSetPredicates
                .stream()
                .filter(predicate -> predicate.getField().getValue().equals(toVerify.getName()))
                .findFirst();
        if (!simpleSetPredicate.isPresent()) {
            fail();
        } else {
            simpleSetPredicate.ifPresent(predicate -> {
                ARRAY_TYPE arrayTypeExpected = ARRAY_TYPE.byName(predicate.getArray().getType().value());
                assertEquals(arrayTypeExpected, toVerify.getArrayType());
                IN_NOTIN inNotInExpected = IN_NOTIN.byName(predicate.getBooleanOperator().value());
                assertEquals(inNotInExpected, toVerify.getInNotIn());
                assertEquals(parentId, toVerify.getParentId());
            });
        }
    }

    private void commonVerifySourceMap(final Map<String, String> toVerify, String packageName, String predicateName,
                                       int expectedSize) {
        assertNotNull(toVerify);
        assertEquals(expectedSize, toVerify.size());
        String expectedKey = String.format("%s.%s", packageName, predicateName);
        assertTrue(toVerify.containsKey(expectedKey));
        commonValidateCompilation(toVerify);
    }

    private List<Object> getObjects(ARRAY_TYPE arrayType, int size) {
        return IntStream.range(0, size).mapToObj(index -> {
            switch (arrayType) {
                case INT:
                    return new Random().nextInt(40);
                case REAL:
                    return new Random().nextDouble();
                case STRING:
                    return UUID.randomUUID().toString();
                default:
                    return null;
            }
        })
                .collect(Collectors.toList());
    }
}