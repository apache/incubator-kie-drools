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

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.StringLiteralExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.ReturnStmt;
import com.github.javaparser.ast.stmt.Statement;
import org.dmg.pmml.Constant;
import org.dmg.pmml.DataType;
import org.dmg.pmml.DerivedField;
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
        Constant constant = new Constant();
        constant.setDataType(DataType.DOUBLE);
        constant.setValue(34.6);
        DerivedField derivedFieldConstant = new DerivedField();
        derivedFieldConstant.setName(FieldName.create("derivedFieldConstant"));
        derivedFieldConstant.setExpression(constant);
        String fieldName = "FIELD_NAME";
        FieldRef fieldRef = new FieldRef(FieldName.create(fieldName));
        DerivedField derivedFieldFieldRef = new DerivedField();
        derivedFieldFieldRef.setName(FieldName.create("derivedFieldFieldRef"));
        derivedFieldFieldRef.setExpression(fieldRef);
        final List<DerivedField> derivedFields = Arrays.asList(derivedFieldConstant, derivedFieldFieldRef);
        AtomicInteger arityCounter = new AtomicInteger();
        final Map<String, MethodDeclaration> retrieved = DerivedFieldFunctionUtils.getDerivedFieldsMethodMap(derivedFields, arityCounter);
        assertEquals(derivedFields.size(), retrieved.size());
        commonValidateConstant(retrieved.get(derivedFieldConstant.getName().toString()), constant, 1, double.class.getName());
        String expected = String.format("return kiePMMLNameValue.map(%1$s::getValue).orElse(%2$s);",
                                        KiePMMLNameValue.class.getName(),
                                        fieldRef.getMapMissingTo());
        commonValidateFieldRef(retrieved.get(derivedFieldFieldRef.getName().toString()), fieldRef, 2, expected);
    }

    @Test
    public void getDerivedFieldMethodDeclaration() {
        Constant constant = new Constant();
        constant.setDataType(DataType.DOUBLE);
        constant.setValue(34.6);
        DerivedField derivedField = new DerivedField();
        derivedField.setExpression(constant);
        AtomicInteger arityCounter = new AtomicInteger();
        MethodDeclaration retrieved = DerivedFieldFunctionUtils.getDerivedFieldMethodDeclaration(derivedField, arityCounter);
        commonValidateConstant(retrieved, constant, arityCounter.get(), double.class.getName());
        //
        String fieldName = "FIELD_NAME";
        FieldRef fieldRef = new FieldRef(FieldName.create(fieldName));
        derivedField.setExpression(fieldRef);
        retrieved = DerivedFieldFunctionUtils.getDerivedFieldMethodDeclaration(derivedField, arityCounter);
        String expected = String.format("return kiePMMLNameValue.map(%1$s::getValue).orElse(%2$s);",
                                        KiePMMLNameValue.class.getName(),
                                        fieldRef.getMapMissingTo());
        commonValidateFieldRef(retrieved, fieldRef, arityCounter.get(), expected);
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