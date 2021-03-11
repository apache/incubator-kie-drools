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
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.ExpressionStmt;
import com.github.javaparser.ast.stmt.ReturnStmt;
import com.github.javaparser.ast.stmt.Statement;
import org.dmg.pmml.Aggregate;
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
import org.kie.pmml.api.exceptions.KiePMMLException;
import org.kie.pmml.commons.model.tuples.KiePMMLNameValue;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.kie.pmml.compiler.commons.utils.CommonCodegenUtils.METHOD_NAME_TEMPLATE;
import static org.kie.pmml.compiler.commons.utils.ExpressionFunctionUtils.DEFAULT_PARAMETERTYPE_MAP;
import static org.kie.pmml.compiler.commons.utils.ExpressionFunctionUtilsTest.DOUBLE_CLASS;
import static org.kie.pmml.compiler.commons.utils.ExpressionFunctionUtilsTest.OBJECT_CLASS;
import static org.kie.pmml.compiler.commons.utils.ExpressionFunctionUtilsTest.STRING_CLASS;
import static org.kie.pmml.compiler.commons.utils.ExpressionFunctionUtilsTest.applySupplier;
import static org.kie.pmml.compiler.commons.utils.ExpressionFunctionUtilsTest.constantSupplier;
import static org.kie.pmml.compiler.commons.utils.ExpressionFunctionUtilsTest.fieldRefSupplier;
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
        List<DerivedField> derivedFields =
                unsupportedExpressionSupplier.stream().map(derivedFieldCreator).collect(Collectors.toList());
        AtomicInteger arityCounter = new AtomicInteger();
        DerivedFieldFunctionUtils.getDerivedFieldsMethodMap(derivedFields, arityCounter);
    }

    @Test
    public void getDerivedFieldsMethodMapSupportedExpression() {
        List<DerivedField> derivedFields =
                supportedExpressionSupplier.stream().map(derivedFieldCreator).collect(Collectors.toList());
        AtomicInteger arityCounter = new AtomicInteger();
        Map<String, MethodDeclaration> retrieved = DerivedFieldFunctionUtils.getDerivedFieldsMethodMap(derivedFields,
                                                                                                       arityCounter);
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
                assertEquals(KiePMMLException.class, e.getClass());
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
        DerivedFieldFunctionUtils.getExpressionMethodDeclaration(expression, DataType.STRING, new AtomicInteger());
    }

    @Test
    public void getExpressionMethodDeclarationUnsupportedExpression() {
        for (Supplier<Expression> supplier : unsupportedExpressionSupplier) {
            Expression expression = supplier.get();
            try {
                DerivedFieldFunctionUtils.getExpressionMethodDeclaration(expression, DataType.STRING,
                                                                         new AtomicInteger());
                fail(String.format("Expecting KiePMMLException for %s", expression.getClass()));
            } catch (Exception e) {
                assertEquals(KiePMMLException.class, e.getClass());
            }
        }
    }

    @Test
    public void getExpressionMethodDeclarationSupportedExpression() {
        for (Supplier<Expression> supplier : supportedExpressionSupplier) {
            Expression expression = supplier.get();
            try {
                DerivedFieldFunctionUtils.getExpressionMethodDeclaration(expression, DataType.STRING,
                                                                         new AtomicInteger());
            } catch (Exception e) {
                fail(String.format("Unexpected %s for %s", e, expression.getClass()));
            }
        }
    }

    @Test(expected = KiePMMLException.class)
    public void getAggregatedMethodDeclaration() {
        DerivedFieldFunctionUtils.getAggregatedMethodDeclaration(new Aggregate(), OBJECT_CLASS, 3, DEFAULT_PARAMETERTYPE_MAP);
    }

    @Test
    public void getApplyMethodDeclaration() {
        MethodDeclaration retrieved = DerivedFieldFunctionUtils.getApplyMethodDeclaration(applySupplier.get(),
                                                                                          OBJECT_CLASS, 3,
                                                                                          DEFAULT_PARAMETERTYPE_MAP);
        String expected = "java.lang.Object Apply3(java.util.List<org.kie.pmml.commons.model.tuples.KiePMMLNameValue>" +
                " param1) {\n" +
                "    java.lang.Object variableapplyVariableConstant1 = 34.6;\n" +
                "    java.util.Optional<org.kie.pmml.commons.model.tuples.KiePMMLNameValue> kiePMMLNameValue = param1" +
                ".stream().filter((org.kie.pmml.commons.model.tuples.KiePMMLNameValue lmbdParam) -> java.util.Objects" +
                ".equals(\"FIELD_REF\", lmbdParam.getName())).findFirst();\n" +
                "    java.lang.Object variableapplyVariableFieldRef2 = (java.lang.Object) kiePMMLNameValue.map(org" +
                ".kie.pmml.commons.model.tuples.KiePMMLNameValue::getValue).orElse(null);\n" +
                "    java.lang.Object applyVariable = this.FUNCTION_NAME(param1, variableapplyVariableConstant1, " +
                "variableapplyVariableFieldRef2);\n" +
                "    return applyVariable;\n" +
                "}";
        assertEquals(expected, retrieved.toString());
    }

    @Test
    public void getConstantMethodDeclaration() {
        Constant constant = constantSupplier.get();
        int methodArity = new Random().nextInt(20);
        MethodDeclaration retrieved = DerivedFieldFunctionUtils.getConstantMethodDeclaration(constant, DOUBLE_CLASS,
                                                                                             methodArity,
                                                                                             DEFAULT_PARAMETERTYPE_MAP);
        String expectedVariable = String.format("%s constantVariable = %s;", Double.class.getName(),
                                                constant.getValue());
        commonValidateConstant(retrieved, constant, methodArity, Double.class.getName(), expectedVariable);
        //
        constant = new Constant();
        constant.setDataType(DataType.STRING);
        constant.setValue("EXPECTED");
        methodArity = new Random().nextInt(20);
        expectedVariable = String.format("%s constantVariable = \"%s\";", String.class.getName(), constant.getValue());
        retrieved = DerivedFieldFunctionUtils.getConstantMethodDeclaration(constant, STRING_CLASS, methodArity, DEFAULT_PARAMETERTYPE_MAP);
        commonValidateConstant(retrieved, constant, methodArity, String.class.getName(), expectedVariable);
    }

    @Test(expected = KiePMMLException.class)
    public void getDiscretizeMethodDeclaration() {
        DerivedFieldFunctionUtils.getDiscretizeMethodDeclaration(new Discretize(), OBJECT_CLASS, 3, DEFAULT_PARAMETERTYPE_MAP);
    }

    @Test
    public void getFieldRefMethodDeclaration() {
        int methodArity = new Random().nextInt(20);
        FieldRef fieldRef = fieldRefSupplier.get();
        MethodDeclaration retrieved = DerivedFieldFunctionUtils.getFieldRefMethodDeclaration(fieldRef, STRING_CLASS,
                                                                                             methodArity,
                                                                                             DEFAULT_PARAMETERTYPE_MAP);
        String expectedVariable = String.format("%1$s fieldRefVariable = (%1$s) kiePMMLNameValue.map(%2$s::getValue)" +
                                                        ".orElse(%3$s);",
                                                String.class.getName(),
                                                KiePMMLNameValue.class.getName(),
                                                fieldRef.getMapMissingTo());
        commonValidateFieldRef(retrieved, fieldRef, methodArity, expectedVariable, String.class.getName());
        //
        fieldRef.setMapMissingTo("MAP_MISSING_TO");
        retrieved = DerivedFieldFunctionUtils.getFieldRefMethodDeclaration(fieldRef, STRING_CLASS, methodArity, DEFAULT_PARAMETERTYPE_MAP);
        expectedVariable = String.format("%1$s fieldRefVariable = (%1$s) kiePMMLNameValue.map(%2$s::getValue).orElse" +
                                                 "(\"%3$s\");",
                                         String.class.getName(),
                                         KiePMMLNameValue.class.getName(),
                                         fieldRef.getMapMissingTo());
        commonValidateFieldRef(retrieved, fieldRef, methodArity, expectedVariable, String.class.getName());
    }

    @Test(expected = KiePMMLException.class)
    public void getLagMethodDeclaration() {
        DerivedFieldFunctionUtils.getLagMethodDeclaration(new Lag(), OBJECT_CLASS, 3, DEFAULT_PARAMETERTYPE_MAP);
    }

    @Test(expected = KiePMMLException.class)
    public void getMapValuesMethodDeclaration() {
        DerivedFieldFunctionUtils.getMapValuesMethodDeclaration(new MapValues(), OBJECT_CLASS, 3, DEFAULT_PARAMETERTYPE_MAP);
    }

    @Test(expected = KiePMMLException.class)
    public void getNormContinuousMethodDeclaration() {
        DerivedFieldFunctionUtils.getNormContinuousMethodDeclaration(new NormContinuous(), OBJECT_CLASS, 3, DEFAULT_PARAMETERTYPE_MAP);
    }

    @Test(expected = KiePMMLException.class)
    public void getNormDiscreteMethodDeclaration() {
        DerivedFieldFunctionUtils.getNormDiscreteMethodDeclaration(new NormDiscrete(), OBJECT_CLASS, 3, DEFAULT_PARAMETERTYPE_MAP);
    }

    @Test(expected = KiePMMLException.class)
    public void getTextIndexMethodDeclaration() {
        DerivedFieldFunctionUtils.getTextIndexMethodDeclaration(new TextIndex(), OBJECT_CLASS, 3, DEFAULT_PARAMETERTYPE_MAP);
    }

    private void commonValidateConstant(MethodDeclaration retrieved, Constant constant, int methodArity,
                                        String expectedClass, String expectedVariableDeclaration) {
        commonValidateMethodDeclaration(retrieved, constant, methodArity);
        assertEquals(expectedClass, retrieved.getType().asString());
        BlockStmt body = retrieved.getBody().orElseThrow(() -> new RuntimeException("Expecting BlockBody"));
        NodeList<Statement> statements = body.getStatements();
        assertEquals(2, statements.size());
        assertTrue(statements.get(0) instanceof ExpressionStmt);
        assertEquals(expectedVariableDeclaration, statements.get(0).toString());
        assertTrue(statements.get(1) instanceof ReturnStmt);
        ReturnStmt returnStmt = (ReturnStmt) statements.get(1);
        assertEquals("return constantVariable;", returnStmt.toString());
    }

    private void commonValidateFieldRef(MethodDeclaration retrieved, FieldRef fieldRef, int methodArity,
                                        String expectedVariableDeclaration, String expectedReturnType) {
        commonValidateMethodDeclaration(retrieved, fieldRef, methodArity);
        assertEquals(expectedReturnType, retrieved.getType().asString());
        BlockStmt body = retrieved.getBody().orElseThrow(() -> new RuntimeException("Expecting BlockBody"));
        NodeList<Statement> statements = body.getStatements();
        assertEquals(3, statements.size());
        assertTrue(statements.get(1) instanceof ExpressionStmt);
        assertEquals(expectedVariableDeclaration, statements.get(1).toString());
        assertTrue(statements.get(2) instanceof ReturnStmt);
        ReturnStmt returnStmt = (ReturnStmt) statements.get(2);
        assertEquals("return fieldRefVariable;", returnStmt.toString());
    }

    private void commonValidateMethodDeclaration(MethodDeclaration toValidate, Expression expression, int methodArity) {
        assertNotNull(toValidate);
        String expectedMethodName = String.format(METHOD_NAME_TEMPLATE, expression.getClass().getSimpleName(),
                                                  methodArity);
        assertEquals(toValidate.getName().asString(), expectedMethodName);
    }
}