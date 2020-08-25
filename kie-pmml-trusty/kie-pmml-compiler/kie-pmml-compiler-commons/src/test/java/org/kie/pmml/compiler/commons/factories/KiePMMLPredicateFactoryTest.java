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
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ConstructorDeclaration;
import com.github.javaparser.ast.expr.AssignExpr;
import com.github.javaparser.ast.expr.NullLiteralExpr;
import com.github.javaparser.ast.stmt.ExplicitConstructorInvocationStmt;
import org.dmg.pmml.CompoundPredicate;
import org.dmg.pmml.DataDictionary;
import org.dmg.pmml.DataField;
import org.dmg.pmml.DataType;
import org.dmg.pmml.False;
import org.dmg.pmml.FieldName;
import org.dmg.pmml.Predicate;
import org.dmg.pmml.SimplePredicate;
import org.dmg.pmml.True;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.kie.pmml.commons.model.enums.BOOLEAN_OPERATOR;
import org.kie.pmml.commons.model.enums.OPERATOR;
import org.kie.pmml.commons.model.predicates.KiePMMLCompoundPredicate;
import org.kie.pmml.commons.model.predicates.KiePMMLFalsePredicate;
import org.kie.pmml.commons.model.predicates.KiePMMLPredicate;
import org.kie.pmml.commons.model.predicates.KiePMMLSimplePredicate;
import org.kie.pmml.commons.model.predicates.KiePMMLTruePredicate;
import org.kie.pmml.compiler.commons.testutils.PMMLModelTestUtils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.kie.pmml.compiler.commons.testutils.CodegenTestUtils.commonValidateCompilation;
import static org.kie.pmml.compiler.commons.testutils.PMMLModelTestUtils.getDataDictionary;
import static org.kie.pmml.compiler.commons.testutils.PMMLModelTestUtils.getRandomSimplePredicateOperator;
import static org.kie.pmml.compiler.commons.testutils.PMMLModelTestUtils.getRandomValue;
import static org.kie.pmml.compiler.commons.utils.JavaParserUtils.getFromFileName;

public class KiePMMLPredicateFactoryTest {


    private static final String TEMPLATE_SOURCE = "Template.tmpl";
    private static final String TEMPLATE_CLASS_NAME = "Template";
    private static Map<String, DataType> simplePredicateNameType;
    private static List<SimplePredicate> simplePredicates;
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
    }

    @Before
    public void init() {
        CompilationUnit compilationUnit = getFromFileName(TEMPLATE_SOURCE);
        constructorDeclaration = compilationUnit.getClassByName(TEMPLATE_CLASS_NAME)
                .orElseThrow(() -> new RuntimeException("Failed to retrieve ClassOrInterfaceDeclaration " + TEMPLATE_CLASS_NAME + "  from " + TEMPLATE_SOURCE))
                .getDefaultConstructor()
                .orElseThrow(() -> new RuntimeException("Failed to retrieve default constructor from " + TEMPLATE_SOURCE));
        assertNotNull(constructorDeclaration);
        assertEquals(TEMPLATE_CLASS_NAME, constructorDeclaration.getName().asString());
        assertTrue(compilationUnit.getClassByName(TEMPLATE_CLASS_NAME).isPresent());
        superInvocation = constructorDeclaration.getBody().getStatements()
                .stream()
                .filter(statement -> statement instanceof ExplicitConstructorInvocationStmt)
                .map(statement -> (ExplicitConstructorInvocationStmt) statement)
                .findFirst()
                .orElseThrow(() ->new RuntimeException("Failed to retrieve super invocation from " + TEMPLATE_SOURCE));
        assertEquals("modelName", superInvocation.getArgument(0).asNameExpr().getNameAsString());
        assignExprs = constructorDeclaration.getBody().findAll(AssignExpr.class);
        assertNotNull(assignExprs);
        assertFalse(assignExprs.isEmpty());
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
    public void getPredicate() {
        simplePredicates.forEach(simplePredicate -> {
            KiePMMLPredicate retrieved = KiePMMLPredicateFactory.getPredicate(simplePredicate, dataDictionary);
            assertTrue(retrieved instanceof KiePMMLSimplePredicate);
        });
        CompoundPredicate compoundPredicate = new CompoundPredicate();
        compoundPredicate.setBooleanOperator(CompoundPredicate.BooleanOperator.XOR);
        compoundPredicate.getPredicates().addAll(simplePredicates);
        KiePMMLPredicate retrieved = KiePMMLPredicateFactory.getPredicate(compoundPredicate, dataDictionary);
        assertTrue(retrieved instanceof KiePMMLCompoundPredicate);
        True truePredicate = new True();
        retrieved = KiePMMLPredicateFactory.getPredicate(truePredicate, dataDictionary);
        assertTrue(retrieved instanceof KiePMMLTruePredicate);
        False falsePredicate = new False();
        retrieved = KiePMMLPredicateFactory.getPredicate(falsePredicate, dataDictionary);
        assertTrue(retrieved instanceof KiePMMLFalsePredicate);
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
    public void getKiePMMLCompoundPredicate() {
        CompoundPredicate compoundPredicate = new CompoundPredicate();
        compoundPredicate.setBooleanOperator(CompoundPredicate.BooleanOperator.XOR);
        compoundPredicate.getPredicates().addAll(simplePredicates);

        KiePMMLCompoundPredicate retrieved = KiePMMLPredicateFactory.getKiePMMLCompoundPredicate(compoundPredicate, dataDictionary);
        assertNotNull(retrieved);
        assertEquals(BOOLEAN_OPERATOR.XOR, retrieved.getBooleanOperator());
        assertEquals(simplePredicates.size(), retrieved.getKiePMMLPredicates().size());
        retrieved.getKiePMMLPredicates().forEach(simplePredicate -> commonVerifySimplePredicate((KiePMMLSimplePredicate)simplePredicate, retrieved.getId()));
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
        Map<String, String> retrieved =  KiePMMLPredicateFactory.getPredicateSourcesMap(kiePMMLPredicate, packageName);
        commonVerifySourceMap(retrieved, packageName, kiePMMLPredicate.getName());
        kiePMMLPredicate = KiePMMLCompoundPredicate.builder(Collections.emptyList(), BOOLEAN_OPERATOR.OR).build();
        retrieved =  KiePMMLPredicateFactory.getPredicateSourcesMap(kiePMMLPredicate, packageName);
        commonVerifySourceMap(retrieved, packageName, kiePMMLPredicate.getName());
        kiePMMLPredicate = KiePMMLTruePredicate.builder(Collections.emptyList()).build();
        retrieved =  KiePMMLPredicateFactory.getPredicateSourcesMap(kiePMMLPredicate, packageName);
        commonVerifySourceMap(retrieved, packageName, kiePMMLPredicate.getName());
        kiePMMLPredicate = KiePMMLFalsePredicate.builder(Collections.emptyList()).build();
        retrieved =  KiePMMLPredicateFactory.getPredicateSourcesMap(kiePMMLPredicate, packageName);
        commonVerifySourceMap(retrieved, packageName, kiePMMLPredicate.getName());
    }

    @Test
    public void getKiePMMLSimplePredicateSourcesMap() {
        String predicateName = "PREDICATENAME";
        KiePMMLSimplePredicate kiePMMLSimplePredicate = KiePMMLSimplePredicate
                .builder(predicateName, Collections.emptyList(), OPERATOR.GREATER_OR_EQUAL)
                .withValue(24)
                .build();
        String packageName = "PACKAGENAME";
        final Map<String, String> retrieved =  KiePMMLPredicateFactory.getKiePMMLSimplePredicateSourcesMap(kiePMMLSimplePredicate, packageName);
        commonVerifySourceMap(retrieved, packageName, kiePMMLSimplePredicate.getName());
    }

    @Test
    public void getKiePMMLCompoundPredicateSourcesMap() {
        KiePMMLCompoundPredicate kiePMMLCompoundPredicate = KiePMMLCompoundPredicate.builder(Collections.emptyList(), BOOLEAN_OPERATOR.OR).build();
        String packageName = "PACKAGENAME";
        final Map<String, String> retrieved =  KiePMMLPredicateFactory.getKiePMMLCompoundPredicateSourcesMap(kiePMMLCompoundPredicate, packageName);
        commonVerifySourceMap(retrieved, packageName, kiePMMLCompoundPredicate.getName());
    }

    @Test
    public void getKiePMMLTruePredicateSourcesMap() {
        final KiePMMLTruePredicate kiePMMLTruePredicate = KiePMMLTruePredicate.builder(Collections.emptyList()).build();
        String packageName = "PACKAGENAME";
        final Map<String, String> retrieved =  KiePMMLPredicateFactory.getKiePMMLTruePredicateSourcesMap(kiePMMLTruePredicate, packageName);
        commonVerifySourceMap(retrieved, packageName, kiePMMLTruePredicate.getName());
    }

    @Test
    public void getKiePMMLFalsePredicateSourcesMap() {
        final KiePMMLFalsePredicate kiePMMLFalsePredicate = KiePMMLFalsePredicate.builder(Collections.emptyList()).build();
        String packageName = "PACKAGENAME";
        final Map<String, String> retrieved =  KiePMMLPredicateFactory.getKiePMMLFalsePredicateSourcesMap(kiePMMLFalsePredicate, packageName);
        commonVerifySourceMap(retrieved, packageName, kiePMMLFalsePredicate.getName());
    }

    @Test
    public void setSimplePredicateConstructor() {
        AssignExpr valueAssignExpr =  assignExprs.stream().filter(assignExpr -> assignExpr.getTarget().asNameExpr().getNameAsString().equals("value"))
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
        commonVerifyConstructor(generatedClassName, predicateName);
        String expected = operator.getClass().getCanonicalName() + "." + operator.name();
        assertEquals(expected, superInvocation.getArgument(2).asNameExpr().getNameAsString());
        assertEquals("24", valueAssignExpr.getValue().asStringLiteralExpr().asString());

        operator = OPERATOR.IS_MISSING;
        value = "VALUE";
        KiePMMLPredicateFactory.setSimplePredicateConstructor(generatedClassName,
                                                                predicateName,
                                                                constructorDeclaration,
                                                                operator, value);
        commonVerifyConstructor(generatedClassName, predicateName);
        expected = operator.getClass().getCanonicalName() + "." + operator.name();
        assertEquals(expected, superInvocation.getArgument(2).asNameExpr().getNameAsString());
        assertEquals("VALUE", valueAssignExpr.getValue().asStringLiteralExpr().asString());
    }

    @Test
    public void setCompoundPredicateConstructor() {
        String generatedClassName = "GENERATEDCLASSNAME";
        String predicateName = "PREDICATENAME";
        BOOLEAN_OPERATOR booleanOperator = BOOLEAN_OPERATOR.OR;
        KiePMMLPredicateFactory.setCompoundPredicateConstructor(generatedClassName,
                                                                 predicateName,
                                                                 constructorDeclaration,
                                                                booleanOperator);
        commonVerifyConstructor(generatedClassName, predicateName);
        String expected = booleanOperator.getClass().getCanonicalName() + "." + booleanOperator.name();
        assertEquals(expected, superInvocation.getArgument(2).asNameExpr().getNameAsString());
        booleanOperator = BOOLEAN_OPERATOR.AND;
        KiePMMLPredicateFactory.setCompoundPredicateConstructor(generatedClassName,
                                                                predicateName,
                                                                constructorDeclaration,
                                                                booleanOperator);
        commonVerifyConstructor(generatedClassName, predicateName);
        expected = booleanOperator.getClass().getCanonicalName() + "." + booleanOperator.name();
        assertEquals(expected, superInvocation.getArgument(2).asNameExpr().getNameAsString());
    }

    @Test
    public void setTrueFalsePredicateConstructor() {
        String generatedClassName = "GENERATEDCLASSNAME";
        String predicateName = "PREDICATENAME";
        KiePMMLPredicateFactory.setTrueFalsePredicateConstructor(generatedClassName,
                                                                 predicateName,
                                                                 constructorDeclaration);
        commonVerifyConstructor(generatedClassName, predicateName);
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

    private void commonVerifySourceMap(final Map<String, String> toVerify, String packageName, String predicateName) {
        assertNotNull(toVerify);
        assertEquals(1, toVerify.size());
        String expectedKey = String.format("%s.%s", packageName, predicateName);
        assertTrue(toVerify.containsKey(expectedKey));
        commonValidateCompilation(toVerify);

    }

    private void commonVerifyConstructor(String generatedClassName, String predicateName) {
        assertEquals(generatedClassName, constructorDeclaration.getName().asString());
        assertEquals(String.format("\"%s\"", predicateName), superInvocation.getArgument(0).asNameExpr().getNameAsString());

    }
}