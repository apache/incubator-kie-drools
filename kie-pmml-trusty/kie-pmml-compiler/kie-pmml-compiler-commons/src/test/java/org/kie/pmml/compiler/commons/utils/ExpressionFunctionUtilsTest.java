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

import java.util.Collections;
import java.util.List;
import java.util.Random;

import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.StringLiteralExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.ReturnStmt;
import com.github.javaparser.ast.stmt.Statement;
import org.dmg.pmml.Constant;
import org.dmg.pmml.DataType;
import org.dmg.pmml.Expression;
import org.dmg.pmml.FieldName;
import org.dmg.pmml.FieldRef;
import org.junit.Test;
import org.kie.pmml.commons.model.tuples.KiePMMLNameValue;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.kie.pmml.compiler.commons.testutils.CodegenTestUtils.commonValidateCompilation;
import static org.kie.pmml.compiler.commons.utils.CommonCodegenUtils.METHOD_NAME_TEMPLATE;
import static org.kie.pmml.compiler.commons.utils.CommonCodegenUtils.getTypedClassOrInterfaceType;

public class ExpressionFunctionUtilsTest {

    @Test
    public void getConstantExpressionMethodDeclaration() {
        Constant constant = new Constant();
        constant.setDataType(DataType.DOUBLE);
        constant.setValue(34.6);
        int methodArity = new Random().nextInt(20);
        String methodName = String.format(METHOD_NAME_TEMPLATE, constant.getClass().getSimpleName(), methodArity);
        MethodDeclaration retrieved = ExpressionFunctionUtils.getConstantExpressionMethodDeclaration(methodName, constant, Collections.emptyList());
        commonValidateConstant(retrieved, constant, methodName, double.class.getName());
        //
        constant = new Constant();
        constant.setDataType(DataType.STRING);
        constant.setValue("EXPECTED");
        methodArity = new Random().nextInt(20);
        methodName = String.format(METHOD_NAME_TEMPLATE, constant.getClass().getSimpleName(), methodArity);
        retrieved = DerivedFieldFunctionUtils.getConstantMethodDeclaration(constant, methodArity);
        commonValidateConstant(retrieved, constant, methodName, String.class.getName());
    }

    @Test
    public void getFieldRefExpressionMethodDeclaration() {
        int methodArity = new Random().nextInt(20);
        String fieldName = "FIELD_NAME";
        FieldRef fieldRef = new FieldRef(FieldName.create(fieldName));
        String methodName = String.format(METHOD_NAME_TEMPLATE, fieldRef.getClass().getSimpleName(), methodArity);
        MethodDeclaration retrieved = ExpressionFunctionUtils.getFieldRefExpressionMethodDeclaration(methodName, fieldRef, Collections.singletonList(getTypedClassOrInterfaceType(List.class.getName(), Collections.singletonList(KiePMMLNameValue.class.getName()))));
        String expected = String.format("return kiePMMLNameValue.map(%1$s::getValue).orElse(%2$s);",
                                        KiePMMLNameValue.class.getName(),
                                        fieldRef.getMapMissingTo());
        commonValidateFieldRef(retrieved, fieldRef, methodName, expected);
        //
        fieldRef.setMapMissingTo("MAP_MISSING_TO");
        methodName = String.format(METHOD_NAME_TEMPLATE, fieldRef.getClass().getSimpleName(), methodArity);
        retrieved = ExpressionFunctionUtils.getFieldRefExpressionMethodDeclaration(methodName, fieldRef, Collections.singletonList(getTypedClassOrInterfaceType(List.class.getName(), Collections.singletonList(KiePMMLNameValue.class.getName()))));
        expected = String.format("return kiePMMLNameValue.map(%1$s::getValue).orElse(\"%2$s\");",
                                 KiePMMLNameValue.class.getName(),
                                 fieldRef.getMapMissingTo());
        commonValidateFieldRef(retrieved, fieldRef, methodName, expected);
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

    private void commonValidateConstant(MethodDeclaration retrieved, Constant constant, String expectedMethodName, String expectedClass) {
        commonValidateMethodDeclaration(retrieved, expectedMethodName);
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

    private void commonValidateFieldRef(MethodDeclaration retrieved, FieldRef fieldRef, String expectedMethodName, String expected) {
        commonValidateMethodDeclaration(retrieved, expectedMethodName);
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

    private void commonValidateMethodDeclaration(MethodDeclaration toValidate, String expectedMethodName) {
        assertNotNull(toValidate);
        assertEquals(toValidate.getName().asString(), expectedMethodName);
    }
}