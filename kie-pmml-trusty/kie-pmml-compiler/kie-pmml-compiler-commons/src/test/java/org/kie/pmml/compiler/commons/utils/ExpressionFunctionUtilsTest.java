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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.function.Supplier;

import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.StringLiteralExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.ExpressionStmt;
import com.github.javaparser.ast.stmt.ReturnStmt;
import com.github.javaparser.ast.stmt.Statement;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import org.dmg.pmml.Aggregate;
import org.dmg.pmml.Apply;
import org.dmg.pmml.Constant;
import org.dmg.pmml.DataType;
import org.dmg.pmml.Discretize;
import org.dmg.pmml.Expression;
import org.dmg.pmml.FieldName;
import org.dmg.pmml.FieldRef;
import org.dmg.pmml.Lag;
import org.dmg.pmml.MapValues;
import org.dmg.pmml.NormContinuous;
import org.dmg.pmml.NormDiscrete;
import org.dmg.pmml.TextIndex;
import org.junit.Test;
import org.kie.pmml.commons.exceptions.KiePMMLException;
import org.kie.pmml.commons.model.tuples.KiePMMLNameValue;

import static com.github.javaparser.StaticJavaParser.parseClassOrInterfaceType;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.kie.pmml.compiler.commons.testutils.CodegenTestUtils.commonValidateCompilation;
import static org.kie.pmml.compiler.commons.utils.CommonCodegenUtils.METHOD_NAME_TEMPLATE;
import static org.kie.pmml.compiler.commons.utils.CommonCodegenUtils.getTypedClassOrInterfaceType;

public class ExpressionFunctionUtilsTest {

    static List<Supplier<Expression>> supportedExpressionSupplier;
    static List<Supplier<Expression>> unsupportedExpressionSupplier;

    static {
        supportedExpressionSupplier = new ArrayList<>();
        supportedExpressionSupplier.add(() -> {
            Constant toReturn = new Constant("VALUE");
            toReturn.setDataType(DataType.STRING);
            return toReturn;
        });
        supportedExpressionSupplier.add(() -> new FieldRef(FieldName.create("FIELD_REF")));
        unsupportedExpressionSupplier = new ArrayList<>();
        unsupportedExpressionSupplier.add(Aggregate::new);
        unsupportedExpressionSupplier.add(Apply::new);
        unsupportedExpressionSupplier.add(Discretize::new);
        unsupportedExpressionSupplier.add(Lag::new);
        unsupportedExpressionSupplier.add(MapValues::new);
        unsupportedExpressionSupplier.add(NormContinuous::new);
        unsupportedExpressionSupplier.add(NormDiscrete::new);
        unsupportedExpressionSupplier.add(TextIndex::new);
    }

    @Test(expected = KiePMMLException.class)
    public void getAggregatedExpressionMethodDeclaration() {
        ExpressionFunctionUtils.getAggregatedExpressionMethodDeclaration("", new Aggregate(), Collections.emptyList());
    }

    @Test(expected = KiePMMLException.class)
    public void getApplyExpressionMethodDeclaration() {
        ExpressionFunctionUtils.getApplyExpressionMethodDeclaration("", new Apply(), Collections.emptyList());
    }

    @Test
    public void getConstantExpressionMethodDeclaration() {
        Constant constant = new Constant();
        constant.setDataType(DataType.DOUBLE);
        constant.setValue(34.6);
        int methodArity = new Random().nextInt(20);
        String methodName = String.format(METHOD_NAME_TEMPLATE, constant.getClass().getSimpleName(), methodArity);
        MethodDeclaration retrieved = ExpressionFunctionUtils.getConstantExpressionMethodDeclaration(methodName, constant, Collections.emptyList());
        String expectedVariableDeclaration = String.format("%1$s constantVariable = %2$s;",
                                                           Double.class.getName(),
                                                           constant.getValue());
        commonValidateConstant(retrieved, constant, methodName, Double.class.getName(), expectedVariableDeclaration);
        //
        constant = new Constant();
        constant.setDataType(DataType.STRING);
        constant.setValue("EXPECTED");
        methodArity = new Random().nextInt(20);
        methodName = String.format(METHOD_NAME_TEMPLATE, constant.getClass().getSimpleName(), methodArity);
        retrieved = DerivedFieldFunctionUtils.getConstantMethodDeclaration(constant, methodArity);
        expectedVariableDeclaration = String.format("%1$s constantVariable = \"%2$s\";",
                                                    String.class.getName(),
                                                    constant.getValue());
        commonValidateConstant(retrieved, constant, methodName, String.class.getName(), expectedVariableDeclaration);
    }

    @Test(expected = KiePMMLException.class)
    public void getDiscretizeExpressionMethodDeclaration() {
        ExpressionFunctionUtils.getDiscretizeExpressionMethodDeclaration("", new Discretize(), Collections.emptyList());
    }

    @Test
    public void getFieldRefExpressionMethodDeclaration() {
        int methodArity = new Random().nextInt(20);
        String fieldName = "FIELD_NAME";
        FieldRef fieldRef = new FieldRef(FieldName.create(fieldName));
        String methodName = String.format(METHOD_NAME_TEMPLATE, fieldRef.getClass().getSimpleName(), methodArity);
        MethodDeclaration retrieved = ExpressionFunctionUtils.getFieldRefExpressionMethodDeclaration(methodName, fieldRef, Collections.singletonList(getTypedClassOrInterfaceType(List.class.getName(), Collections.singletonList(KiePMMLNameValue.class.getName()))));
        String expected = String.format("java.lang.Object fieldRefVariable = kiePMMLNameValue.map(%1$s::getValue).orElse(%2$s);",
                                        KiePMMLNameValue.class.getName(),
                                        fieldRef.getMapMissingTo());
        commonValidateFieldRefMethod(retrieved, fieldRef, methodName, expected);
        //
        fieldRef.setMapMissingTo("MAP_MISSING_TO");
        methodName = String.format(METHOD_NAME_TEMPLATE, fieldRef.getClass().getSimpleName(), methodArity);
        retrieved = ExpressionFunctionUtils.getFieldRefExpressionMethodDeclaration(methodName, fieldRef, Collections.singletonList(getTypedClassOrInterfaceType(List.class.getName(), Collections.singletonList(KiePMMLNameValue.class.getName()))));
        expected = String.format("java.lang.Object fieldRefVariable = kiePMMLNameValue.map(%1$s::getValue).orElse(\"%2$s\");",
                                 KiePMMLNameValue.class.getName(),
                                 fieldRef.getMapMissingTo());
        commonValidateFieldRefMethod(retrieved, fieldRef, methodName, expected);
    }

    @Test(expected = KiePMMLException.class)
    public void getLagExpressionMethodDeclaration() {
        ExpressionFunctionUtils.getLagExpressionMethodDeclaration("", new Lag(), Collections.emptyList());
    }

    @Test(expected = KiePMMLException.class)
    public void getMapValuesExpressionMethodDeclaration() {
        ExpressionFunctionUtils.getMapValuesExpressionMethodDeclaration("", new MapValues(), Collections.emptyList());
    }

    @Test(expected = KiePMMLException.class)
    public void getNormContinuousExpressionMethodDeclaration() {
        ExpressionFunctionUtils.getNormContinuousExpressionMethodDeclaration("", new NormContinuous(), Collections.emptyList());
    }

    @Test(expected = KiePMMLException.class)
    public void getNormDiscreteExpressionMethodDeclaration() {
        ExpressionFunctionUtils.getNormDiscreteExpressionMethodDeclaration("", new NormDiscrete(), Collections.emptyList());
    }

    @Test(expected = KiePMMLException.class)
    public void getTextIndexExpressionMethodDeclaration() {
        ExpressionFunctionUtils.getTextIndexExpressionMethodDeclaration("", new TextIndex(), Collections.emptyList());
    }

    @Test(expected = KiePMMLException.class)
    public void getAggregatedExpressionBlockStmt() {
        ExpressionFunctionUtils.getAggregatedExpressionBlockStmt(new Aggregate(), Collections.emptyList());
    }

    @Test(expected = KiePMMLException.class)
    public void getApplyExpressionBlockStmt() {
        ExpressionFunctionUtils.getApplyExpressionBlockStmt(new Apply(), Collections.emptyList());
    }

    @Test
    public void getConstantExpressionBlockStmt() {
        String variableName = "VARIABLE_NAME";
        Constant constant = new Constant();
        constant.setDataType(DataType.DOUBLE);
        constant.setValue(34.6);
        ClassOrInterfaceType returnedType = parseClassOrInterfaceType(Double.class.getName());
        BlockStmt retrieved = ExpressionFunctionUtils.getConstantExpressionBlockStmt(variableName, constant, returnedType, Collections.emptyList());
        String expected = String.format("{\n" +
                                                "    %1$s %2$s = %3$s;\n" +
                                                "}",
                                        Double.class.getName(),
                                        variableName,
                                        constant.getValue());
        assertEquals(expected, retrieved.toString());
        constant.setDataType(DataType.STRING);
        constant.setValue("STRING_VALUE");
        returnedType = parseClassOrInterfaceType(String.class.getName());
        retrieved = ExpressionFunctionUtils.getConstantExpressionBlockStmt(variableName, constant, returnedType, Collections.emptyList());
        expected = String.format("{\n" +
                                                "    %1$s %2$s = \"%3$s\";\n" +
                                                "}",
                                 String.class.getName(),
                                        variableName,
                                        constant.getValue());
        assertEquals(expected, retrieved.toString());
    }

    @Test(expected = KiePMMLException.class)
    public void getDiscretizeExpressionBlockStmt() {
        ExpressionFunctionUtils.getDiscretizeExpressionBlockStmt(new Discretize(), Collections.emptyList());
    }

    @Test
    public void getFieldRefExpressionBlockStmt() {
        String variableName = "VARIABLE_NAME";
        String fieldName = "FIELD_NAME";
        FieldRef fieldRef = new FieldRef(FieldName.create(fieldName));
        BlockStmt retrieved = ExpressionFunctionUtils.getFieldRefExpressionBlockStmt(variableName, fieldRef, Collections.emptyList());
        String expected = String.format("{\n" +
                                                "    java.util.Optional<org.kie.pmml.commons.model.tuples.KiePMMLNameValue> kiePMMLNameValue = param1.stream().filter((org.kie.pmml.commons.model.tuples.KiePMMLNameValue lmbdParam) -> java.util.Objects.equals(\"%1$s\", lmbdParam.getName())).findFirst();\n" +
                                                "    java.lang.Object %2$s = kiePMMLNameValue.map(org.kie.pmml.commons.model.tuples.KiePMMLNameValue::getValue).orElse(null);\n" +
                                                "}",
                                        fieldName,
                                        variableName);
        assertEquals(expected, retrieved.toString());
        String mapMissingTo = "MAP_MISSING_TO";
        fieldRef.setMapMissingTo(mapMissingTo);
        retrieved = ExpressionFunctionUtils.getFieldRefExpressionBlockStmt(variableName, fieldRef, Collections.emptyList());
        expected = String.format("{\n" +
                                         "    java.util.Optional<org.kie.pmml.commons.model.tuples.KiePMMLNameValue> kiePMMLNameValue = param1.stream().filter((org.kie.pmml.commons.model.tuples.KiePMMLNameValue lmbdParam) -> java.util.Objects.equals(\"%1$s\", lmbdParam.getName())).findFirst();\n" +
                                         "    java.lang.Object %2$s = kiePMMLNameValue.map(org.kie.pmml.commons.model.tuples.KiePMMLNameValue::getValue).orElse(\"%3$s\");\n" +
                                         "}",
                                 fieldName,
                                 variableName,
                                 mapMissingTo);
        assertEquals(expected, retrieved.toString());
    }

    @Test(expected = KiePMMLException.class)
    public void getLagExpressionBlockStmt() {
        ExpressionFunctionUtils.getLagExpressionBlockStmt(new Lag(), Collections.emptyList());
    }

    @Test(expected = KiePMMLException.class)
    public void getMapValuesExpressionBlockStmt() {
        ExpressionFunctionUtils.getMapValuesExpressionBlockStmt(new MapValues(), Collections.emptyList());
    }

    @Test(expected = KiePMMLException.class)
    public void getNormContinuousExpressionBlockStmtn() {
        ExpressionFunctionUtils.getNormContinuousExpressionBlockStmtn(new NormContinuous(), Collections.emptyList());
    }

    @Test(expected = KiePMMLException.class)
    public void getNormDiscreteExpressionBlockStmt() {
        ExpressionFunctionUtils.getNormDiscreteExpressionBlockStmt(new NormDiscrete(), Collections.emptyList());
    }

    @Test(expected = KiePMMLException.class)
    public void getTextIndexExpressionBlockStmt() {
        ExpressionFunctionUtils.getTextIndexExpressionBlockStmt(new TextIndex(), Collections.emptyList());
    }

    @Test
    public void getExpressionMethodDeclaration() {
        String methodName = "METHOD_NAME";
        MethodDeclaration retrieved = ExpressionFunctionUtils.getExpressionMethodDeclaration(methodName, Collections.singletonList(getTypedClassOrInterfaceType(List.class.getName(), Collections.singletonList(KiePMMLNameValue.class.getName()))));
        assertNotNull(retrieved);
        String expected = String.format("empty %s(java.util.List<org.kie.pmml.commons.model.tuples.KiePMMLNameValue> param1) {\n" +
                                                "}", methodName);
        assertEquals(expected, retrieved.toString());
    }

    private void commonValidateConstant(MethodDeclaration retrieved, Constant constant, String expectedMethodName, String expectedClass, String variableDeclaration) {
        commonValidateMethodDeclaration(retrieved, expectedMethodName);
        assertEquals(expectedClass, retrieved.getType().asString());
        BlockStmt body = retrieved.getBody().orElseThrow(() -> new RuntimeException("Expecting BlockBody"));
        NodeList<Statement> statements = body.getStatements();
        assertEquals(2, statements.size());
        assertTrue(statements.get(0) instanceof ExpressionStmt);
        assertEquals(variableDeclaration, statements.get(0).toString());
        assertTrue(statements.get(1) instanceof ReturnStmt);
        ReturnStmt returnStmt = (ReturnStmt) statements.get(1);
        String retrievedString = returnStmt.toString();
        assertEquals("return constantVariable;", retrievedString);
        commonValidateCompilation(retrieved);
    }

    private void commonValidateFieldRefMethod(MethodDeclaration retrieved, FieldRef fieldRef, String expectedMethodName, String expected) {
        commonValidateMethodDeclaration(retrieved, expectedMethodName);
        assertEquals(Object.class.getName(), retrieved.getType().asString());
        BlockStmt body = retrieved.getBody().orElseThrow(() -> new RuntimeException("Expecting BlockBody"));
        NodeList<Statement> statements = body.getStatements();
        assertEquals(3, statements.size());
        assertTrue(statements.get(1) instanceof ExpressionStmt);
        assertEquals(expected, statements.get(1).toString());
        assertTrue(statements.get(2) instanceof ReturnStmt);
        ReturnStmt returnStmt = (ReturnStmt) statements.get(2);
        String retrievedString = returnStmt.toString();
        assertEquals("return fieldRefVariable;", retrievedString);
        commonValidateCompilation(retrieved);
    }

    private void commonValidateMethodDeclaration(MethodDeclaration toValidate, String expectedMethodName) {
        assertNotNull(toValidate);
        assertEquals(toValidate.getName().asString(), expectedMethodName);
    }
}