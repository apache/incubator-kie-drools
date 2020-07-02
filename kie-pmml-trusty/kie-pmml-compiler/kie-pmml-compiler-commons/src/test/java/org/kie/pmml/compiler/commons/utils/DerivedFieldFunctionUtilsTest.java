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

package org.kie.pmml.compiler.commons.utils;

import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.StringLiteralExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.ReturnStmt;
import com.github.javaparser.ast.stmt.Statement;
import org.dmg.pmml.Aggregate;
import org.dmg.pmml.Apply;
import org.dmg.pmml.Constant;
import org.dmg.pmml.DataType;
import org.dmg.pmml.DerivedField;
import org.dmg.pmml.Discretize;
import org.dmg.pmml.Expression;
import org.dmg.pmml.FieldName;
import org.dmg.pmml.FieldRef;
import org.dmg.pmml.Lag;
import org.dmg.pmml.MapValues;
import org.dmg.pmml.NormContinuous;
import org.dmg.pmml.NormDiscrete;
import org.dmg.pmml.TextIndex;
import org.dmg.pmml.Visitor;
import org.dmg.pmml.VisitorAction;
import org.junit.Test;
import org.kie.pmml.commons.exceptions.KiePMMLException;
import org.kie.pmml.commons.model.tuples.KiePMMLNameValue;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.kie.pmml.compiler.commons.testutils.CodegenTestUtils.commonValidateCompilation;
import static org.kie.pmml.compiler.commons.utils.CommonCodegenUtils.METHOD_NAME_TEMPLATE;
import static org.kie.pmml.compiler.commons.utils.ExpressionFunctionUtilsTest.supportedExpressionSupplier;
import static org.kie.pmml.compiler.commons.utils.ExpressionFunctionUtilsTest.unsupportedExpressionSupplier;

public class DerivedFieldFunctionUtilsTest {

    private static final Function<Supplier<Expression>, DerivedField> derivedFieldCreator = supplier -> {
        Expression expression = supplier.get();
        DerivedField defineFunction = new DerivedField();
        defineFunction.setName(FieldName.create("DERIVED_FIELD_" + expression.getClass().getSimpleName()));
        defineFunction.setExpression(expression);
        return defineFunction;
    };

    @Test(expected = KiePMMLException.class)
    public void getDerivedFieldsMethodMapUnsupportedExpression() {
        List<DerivedField> derivedFields = unsupportedExpressionSupplier.stream().map(derivedFieldCreator).collect(Collectors.toList());
        AtomicInteger arityCounter = new AtomicInteger();
        DerivedFieldFunctionUtils.getDerivedFieldsMethodMap(derivedFields, arityCounter);
    }

    @Test
    public void getDerivedFieldsMethodMapSupportedExpression() {
        List<DerivedField> derivedFields = supportedExpressionSupplier.stream().map(derivedFieldCreator).collect(Collectors.toList());
        AtomicInteger arityCounter = new AtomicInteger();
        Map<String, MethodDeclaration> retrieved = DerivedFieldFunctionUtils.getDerivedFieldsMethodMap(derivedFields, arityCounter);
        assertEquals(derivedFields.size(), retrieved.size());
    }

    @Test
    public void getDerivedFieldMethodDeclarationUnsupportedExpression() {
        for (Supplier<Expression> supplier : unsupportedExpressionSupplier) {
            DerivedField derivedField = derivedFieldCreator.apply(supplier);
            try {
                DerivedFieldFunctionUtils.getDerivedFieldMethodDeclaration(derivedField, new AtomicInteger());
                fail(String.format("Expecting KiePMMLException for %s", derivedField));
            } catch (Exception e) {
                assertTrue(e instanceof KiePMMLException);
            }
        }
    }

    @Test
    public void getDerivedFieldMethodDeclarationSupportedExpression() {
        for (Supplier<Expression> supplier : supportedExpressionSupplier) {
            DerivedField derivedField = derivedFieldCreator.apply(supplier);
            try {
                DerivedFieldFunctionUtils.getDerivedFieldMethodDeclaration(derivedField, new AtomicInteger());
            } catch (Exception e) {
                fail(String.format("Unexpected %s for %s", e, derivedField.getExpression().getClass()));
            }
        }
    }

    @Test(expected = KiePMMLException.class)
    public void getDerivedFieldMethodDeclarationWithoutExpression() {
        DerivedFieldFunctionUtils.getDerivedFieldMethodDeclaration(new DerivedField(), new AtomicInteger());
    }

    @Test(expected = IllegalArgumentException.class)
    public void getExpressionMethodDeclarationUnknownExpression() {
        Expression expression = new Expression() {
            @Override
            public VisitorAction accept(Visitor visitor) {
                return null;
            }
        };
        DerivedFieldFunctionUtils.getExpressionMethodDeclaration(expression, new AtomicInteger());
    }

    @Test
    public void getExpressionMethodDeclarationUnsupportedExpression() {
        for (Supplier<Expression> supplier : unsupportedExpressionSupplier) {
            Expression expression = supplier.get();
            try {
                DerivedFieldFunctionUtils.getExpressionMethodDeclaration(expression, new AtomicInteger());
                fail(String.format("Expecting KiePMMLException for %s", expression.getClass()));
            } catch (Exception e) {
                assertTrue(e instanceof KiePMMLException);
            }
        }
    }

    @Test
    public void getExpressionMethodDeclarationSupportedExpression() {
        for (Supplier<Expression> supplier : supportedExpressionSupplier) {
            Expression expression = supplier.get();
            try {
                DerivedFieldFunctionUtils.getExpressionMethodDeclaration(expression, new AtomicInteger());
            } catch (Exception e) {
                fail(String.format("Unexpected %s for %s", e, expression.getClass()));
            }
        }
    }

    @Test(expected = KiePMMLException.class)
    public void getAggregatedMethodDeclaration() {
        DerivedFieldFunctionUtils.getAggregatedMethodDeclaration(new Aggregate(), 3);
    }

    @Test(expected = KiePMMLException.class)
    public void getApplyMethodDeclaration() {
        DerivedFieldFunctionUtils.getApplyMethodDeclaration(new Apply(), 3);
    }

    @Test
    public void getConstantMethodDeclaration() {
        Constant constant = new Constant();
        constant.setDataType(DataType.DOUBLE);
        constant.setValue(34.6);
        int methodArity = new Random().nextInt(20);
        MethodDeclaration retrieved = DerivedFieldFunctionUtils.getConstantMethodDeclaration(constant, methodArity);
        commonValidateConstant(retrieved, constant, methodArity, double.class.getName());
        //
        constant = new Constant();
        constant.setDataType(DataType.STRING);
        constant.setValue("EXPECTED");
        methodArity = new Random().nextInt(20);
        retrieved = DerivedFieldFunctionUtils.getConstantMethodDeclaration(constant, methodArity);
        commonValidateConstant(retrieved, constant, methodArity, String.class.getName());
    }

    @Test(expected = KiePMMLException.class)
    public void getDiscretizeMethodDeclaration() {
        DerivedFieldFunctionUtils.getDiscretizeMethodDeclaration(new Discretize(), 3);
    }

    @Test
    public void getFieldRefMethodDeclaration() {
        int methodArity = new Random().nextInt(20);
        String fieldName = "FIELD_NAME";
        FieldRef fieldRef = new FieldRef(FieldName.create(fieldName));
        MethodDeclaration retrieved = DerivedFieldFunctionUtils.getFieldRefMethodDeclaration(fieldRef, methodArity);
        String expected = String.format("return kiePMMLNameValue.map(%1$s::getValue).orElse(%2$s);",
                                        KiePMMLNameValue.class.getName(),
                                        fieldRef.getMapMissingTo());
        commonValidateFieldRef(retrieved, fieldRef, methodArity, expected);
        //
        fieldRef.setMapMissingTo("MAP_MISSING_TO");
        retrieved = DerivedFieldFunctionUtils.getFieldRefMethodDeclaration(fieldRef, methodArity);
        expected = String.format("return kiePMMLNameValue.map(%1$s::getValue).orElse(\"%2$s\");",
                                 KiePMMLNameValue.class.getName(),
                                 fieldRef.getMapMissingTo());
        commonValidateFieldRef(retrieved, fieldRef, methodArity, expected);
    }

    @Test(expected = KiePMMLException.class)
    public void getLagMethodDeclaration() {
        DerivedFieldFunctionUtils.getLagMethodDeclaration(new Lag(), 3);
    }

    @Test(expected = KiePMMLException.class)
    public void getMapValuesMethodDeclaration() {
        DerivedFieldFunctionUtils.getMapValuesMethodDeclaration(new MapValues(), 3);
    }

    @Test(expected = KiePMMLException.class)
    public void getNormContinuousMethodDeclaration() {
        DerivedFieldFunctionUtils.getNormContinuousMethodDeclaration(new NormContinuous(), 3);
    }

    @Test(expected = KiePMMLException.class)
    public void getNormDiscreteMethodDeclaration() {
        DerivedFieldFunctionUtils.getNormDiscreteMethodDeclaration( new NormDiscrete(), 3);
    }

    @Test(expected = KiePMMLException.class)
    public void getTextIndexMethodDeclaration() {
        DerivedFieldFunctionUtils.getTextIndexMethodDeclaration(new TextIndex(), 3);
    }

    private void commonValidateConstant(MethodDeclaration retrieved, Constant constant, int methodArity, String expectedClass) {
        commonValidateMethodDeclaration(retrieved, constant, methodArity);
        assertEquals(expectedClass, retrieved.getType().asString());
        BlockStmt body = retrieved.getBody().orElseThrow(() -> new RuntimeException("Expecting BlockBody"));
        NodeList<Statement> statements = body.getStatements();
        assertEquals(1, statements.size());
        assertTrue(statements.get(0) instanceof ReturnStmt);
        ReturnStmt returnStmt = (ReturnStmt) statements.get(0);
        com.github.javaparser.ast.expr.Expression expression = returnStmt.getExpression().orElseThrow(() -> new RuntimeException("Expecting Expression"));
        assertTrue(expression instanceof StringLiteralExpr);
        assertEquals(constant.getValue().toString(), ((StringLiteralExpr) expression).asString());
    }

    private void commonValidateFieldRef(MethodDeclaration retrieved, FieldRef fieldRef, int methodArity, String expected) {
        commonValidateMethodDeclaration(retrieved, fieldRef, methodArity);
        assertEquals(Object.class.getName(), retrieved.getType().asString());
        BlockStmt body = retrieved.getBody().orElseThrow(() -> new RuntimeException("Expecting BlockBody"));
        NodeList<Statement> statements = body.getStatements();
        assertEquals(2, statements.size());
        assertTrue(statements.get(1) instanceof ReturnStmt);
        ReturnStmt returnStmt = (ReturnStmt) statements.get(1);
        String retrievedString = returnStmt.toString();
        assertEquals(expected, retrievedString);
        commonValidateCompilation(retrieved);
    }

    private void commonValidateMethodDeclaration(MethodDeclaration toValidate, Expression expression, int methodArity) {
        assertNotNull(toValidate);
        String expectedMethodName = String.format(METHOD_NAME_TEMPLATE, expression.getClass().getSimpleName(), methodArity);
        assertEquals(toValidate.getName().asString(), expectedMethodName);
    }
}