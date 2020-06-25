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

import java.util.Random;

import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.NameExpr;
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

public class DerivedFieldFunctionUtilsTest {

    @Test
    public void getDerivedFieldsMethodMap() {
    }

    @Test
    public void getDerivedFieldMethodDeclaration() {
    }

    @Test
    public void getExpressionMethodDeclaration() {
    }

    @Test
    public void getAggregatefMethodDeclaration() {
    }

    @Test
    public void getApplyMethodDeclaration() {
    }

    @Test
    public void getConstantMethodDeclaration() {
        Constant constant = new Constant();
        constant.setDataType(DataType.DOUBLE);
        constant.setValue(34.6);
        int methodArity = new Random().nextInt(20);
        MethodDeclaration retrieved = DerivedFieldFunctionUtils.getConstantMethodDeclaration(constant, methodArity);
        commonValidateMethodDeclaration(retrieved, constant, methodArity);
        assertEquals(double.class.getName(), retrieved.getType().asString());
        BlockStmt body = retrieved.getBody().orElseThrow(() -> new RuntimeException("Expecting BlockBody"));
        NodeList<Statement> statements = body.getStatements();
        assertEquals(1, statements.size());
        assertTrue(statements.get(0) instanceof ReturnStmt);
        ReturnStmt returnStmt = (ReturnStmt) statements.get(0);
        com.github.javaparser.ast.expr.Expression expression = returnStmt.getExpression().orElseThrow(() -> new RuntimeException("Expecting Expression"));
        assertTrue(expression instanceof NameExpr);
        assertEquals(constant.getValue().toString(), ((NameExpr) expression).getNameAsString());
        //
        constant = new Constant();
        constant.setDataType(DataType.STRING);
        constant.setValue("EXPECTED");
        methodArity = new Random().nextInt(20);
        retrieved = DerivedFieldFunctionUtils.getConstantMethodDeclaration(constant, methodArity);
        commonValidateMethodDeclaration(retrieved, constant, methodArity);
        assertEquals(String.class.getName(), retrieved.getType().asString());
        body = retrieved.getBody().orElseThrow(() -> new RuntimeException("Expecting BlockBody"));
        statements = body.getStatements();
        assertEquals(1, statements.size());
        assertTrue(statements.get(0) instanceof ReturnStmt);
        returnStmt = (ReturnStmt) statements.get(0);
        expression = returnStmt.getExpression().orElseThrow(() -> new RuntimeException("Expecting Expression"));
        assertTrue(expression instanceof NameExpr);
        assertEquals(constant.getValue().toString(), ((NameExpr) expression).getNameAsString());
    }

    @Test
    public void getDiscretizeMethodDeclaration() {
    }

    @Test
    public void getFieldRefMethodDeclaration() {
        int methodArity = new Random().nextInt(20);
        String fieldName = "FIELD_NAME";
        FieldRef fieldRef = new FieldRef(FieldName.create(fieldName));
        MethodDeclaration retrieved = DerivedFieldFunctionUtils.getFieldRefMethodDeclaration(fieldRef, methodArity);
        commonValidateMethodDeclaration(retrieved, fieldRef, methodArity);
        assertEquals(Object.class.getName(), retrieved.getType().asString());
        BlockStmt body = retrieved.getBody().orElseThrow(() -> new RuntimeException("Expecting BlockBody"));
        NodeList<Statement> statements = body.getStatements();
        assertEquals(2, statements.size());
        assertTrue(statements.get(1) instanceof ReturnStmt);
        ReturnStmt returnStmt = (ReturnStmt) statements.get(1);
        String expected = String.format("return kiePMMLNameValue.map(%1$s::getValue).orElse(%2$s);",
                                         KiePMMLNameValue.class.getName(),
                                         fieldRef.getMapMissingTo());
        String retrievedString = returnStmt.toString();
        assertEquals(expected, retrievedString);
        commonValidateCompilation(retrieved);
        //
        fieldRef.setMapMissingTo("MAP_MISSING_TO");
        retrieved = DerivedFieldFunctionUtils.getFieldRefMethodDeclaration(fieldRef, methodArity);
        commonValidateMethodDeclaration(retrieved, fieldRef, methodArity);
        assertEquals(Object.class.getName(), retrieved.getType().asString());
        body = retrieved.getBody().orElseThrow(() -> new RuntimeException("Expecting BlockBody"));
        statements = body.getStatements();
        assertEquals(2, statements.size());
        assertTrue(statements.get(1) instanceof ReturnStmt);
        returnStmt = (ReturnStmt) statements.get(1);
        expected = String.format("return kiePMMLNameValue.map(%1$s::getValue).orElse(\"%2$s\");",
                                        KiePMMLNameValue.class.getName(),
                                        fieldRef.getMapMissingTo());
        retrievedString = returnStmt.toString();
        assertEquals(expected, retrievedString);
        commonValidateCompilation(retrieved);
    }

    @Test
    public void getLagMethodDeclaration() {
    }

    @Test
    public void getMapValuesMethodDeclaration() {
    }

    @Test
    public void getNormContinuousMethodDeclaration() {
    }

    @Test
    public void getNormDiscreteMethodDeclaration() {
    }

    @Test
    public void getTextIndexMethodDeclaration() {
    }

    private void commonValidateMethodDeclaration(MethodDeclaration toValidate, Expression expression, int methodArity) {
        assertNotNull(toValidate);
        String expectedMethodName = String.format(METHOD_NAME_TEMPLATE, expression.getClass().getSimpleName(), methodArity);
        assertEquals(toValidate.getName().asString(), expectedMethodName);
    }
}