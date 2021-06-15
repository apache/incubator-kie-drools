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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;

import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.MethodDeclaration;
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
import org.kie.pmml.api.exceptions.KiePMMLException;
import org.kie.pmml.api.utils.ConverterTypeUtil;

import static com.github.javaparser.StaticJavaParser.parseClassOrInterfaceType;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.kie.pmml.compiler.commons.testutils.CodegenTestUtils.commonValidateCompilation;
import static org.kie.pmml.compiler.commons.utils.CommonCodegenUtils.METHOD_NAME_TEMPLATE;
import static org.kie.pmml.compiler.commons.utils.ExpressionFunctionUtils.CONSTANT_VALUE;
import static org.kie.pmml.compiler.commons.utils.ExpressionFunctionUtils.CONVERTER_TYPE_UTIL_FIELD_ACCESSOR_EXPR;
import static org.kie.pmml.compiler.commons.utils.ExpressionFunctionUtils.DEFAULT_PARAMETERTYPE_MAP;

public class ExpressionFunctionUtilsTest {

    final static List<Supplier<Expression>> supportedExpressionSupplier;
    final static List<Supplier<Expression>> unsupportedExpressionSupplier;
    final static Supplier<Constant> constantSupplier = () -> {
        Constant toReturn = new Constant();
        toReturn.setDataType(DataType.DOUBLE);
        toReturn.setValue(34.6);
        return toReturn;
    };
    final static Supplier<FieldRef> fieldRefSupplier = () -> new FieldRef(FieldName.create("FIELD_REF"));
    final static Supplier<Apply> applySupplier = () -> {
        Constant constant = constantSupplier.get();
        FieldRef fieldRef = fieldRefSupplier.get();
        String functionName = "FUNCTION_NAME";
        Apply toReturn = new Apply();
        toReturn.addExpressions(constant, fieldRef);
        toReturn.setFunction(functionName);
        return toReturn;
    };
    final static ClassOrInterfaceType DOUBLE_CLASS = parseClassOrInterfaceType(Double.class.getName());
    final static ClassOrInterfaceType STRING_CLASS = parseClassOrInterfaceType(String.class.getName());

    static {
        supportedExpressionSupplier = new ArrayList<>();
        supportedExpressionSupplier.add(applySupplier::get);
        supportedExpressionSupplier.add(constantSupplier::get);
        supportedExpressionSupplier.add(fieldRefSupplier::get);
        unsupportedExpressionSupplier = new ArrayList<>();
        unsupportedExpressionSupplier.add(Aggregate::new);
        unsupportedExpressionSupplier.add(Discretize::new);
        unsupportedExpressionSupplier.add(Lag::new);
        unsupportedExpressionSupplier.add(MapValues::new);
        unsupportedExpressionSupplier.add(NormContinuous::new);
        unsupportedExpressionSupplier.add(NormDiscrete::new);
        unsupportedExpressionSupplier.add(TextIndex::new);
    }

    @Test
    public void getConstantExpressionMethodDeclaration() {
        Constant constant = constantSupplier.get();
        int methodArity = new Random().nextInt(20);
        String methodName = String.format(METHOD_NAME_TEMPLATE, constant.getClass().getSimpleName(), methodArity);

        MethodDeclaration retrieved = ExpressionFunctionUtils.getConstantExpressionMethodDeclaration(methodName,
                                                                                                     constant,
                                                                                                     DOUBLE_CLASS,
                                                                                                     DEFAULT_PARAMETERTYPE_MAP);
        String expectedVariableDeclaration = String.format("%s %s = %s;",
                                                           Double.class.getName(),
                                                           CONSTANT_VALUE,
                                                           constant.getValue());
        commonValidateConstant(retrieved, constant, methodName, Double.class.getName(), expectedVariableDeclaration);
        //
        constant = new Constant();
        constant.setDataType(DataType.STRING);
        constant.setValue("EXPECTED");
        methodArity = new Random().nextInt(20);
        methodName = String.format(METHOD_NAME_TEMPLATE, constant.getClass().getSimpleName(), methodArity);
        retrieved = ExpressionFunctionUtils.getConstantExpressionMethodDeclaration(methodName, constant, STRING_CLASS
                , DEFAULT_PARAMETERTYPE_MAP);
        expectedVariableDeclaration = String.format("%s %s = \"%s\";",
                                                    String.class.getName(),
                                                    CONSTANT_VALUE,
                                                    constant.getValue());
        commonValidateConstant(retrieved, constant, methodName, String.class.getName(), expectedVariableDeclaration);
    }

    @Test
    public void getUnsupportedExpressionBlockStmt() {
        final AtomicInteger arityCounter = new AtomicInteger();
        final ClassOrInterfaceType returnedType = parseClassOrInterfaceType(Object.class.getName());
        unsupportedExpressionSupplier.forEach(supplier -> {
            Expression expression = supplier.get();
            String methodName = String.format(METHOD_NAME_TEMPLATE, expression.getClass().getSimpleName(),
                                              arityCounter);
            try {
                ExpressionFunctionUtils.getExpressionBlockStmtWithKiePMMLValues(methodName, expression, returnedType,
                                                                                new LinkedHashMap<>());
                fail("Expecting KiePMMLException for " + expression);
            } catch (Exception e) {
                assertTrue(e instanceof KiePMMLException);
            }
        });
    }

    @Test
    public void getSupportedExpressionBlockStmt() {
        final AtomicInteger arityCounter = new AtomicInteger();
        final ClassOrInterfaceType returnedType = parseClassOrInterfaceType(Object.class.getName());
        supportedExpressionSupplier.forEach(supplier -> {
            Expression expression = supplier.get();
            String methodName = String.format(METHOD_NAME_TEMPLATE, expression.getClass().getSimpleName(),
                                              arityCounter);
            assertNotNull(ExpressionFunctionUtils.getExpressionBlockStmtWithKiePMMLValues(methodName, expression, returnedType, new LinkedHashMap<>()));
        });
    }

    @Test
    public void getApplyExpressionBlockStmt() {
        Apply apply = applySupplier.get();
        String variableName = "VARIABLE_NAME";
        BlockStmt retrieved = ExpressionFunctionUtils.getApplyExpressionBlockStmtWithKiePMMLValues(variableName, apply,
                                                                                                   parseClassOrInterfaceType(Object
                                                                                                                                     .class.getName()),
                                                                                                   DEFAULT_PARAMETERTYPE_MAP);
        BlockStmt expected = JavaParserUtils.parseBlock(String.format("{" +
                                                "    java.lang.Object variable%1$sConstant1 = 34.6;" +
                                                "    java.util.Optional<org.kie.pmml.commons.model.tuples" +
                                                ".KiePMMLNameValue> kiePMMLNameValue = param1" +
                                                ".stream().filter((lmbdParam) -> java.util.Objects" +
                                                ".equals(\"FIELD_REF\", lmbdParam.getName())).findFirst();" +
                                                "    java.lang.Object variable%1$sFieldRef2 = (java.lang.Object) " +
                                                "kiePMMLNameValue.map(org" +
                                                ".kie.pmml.commons.model.tuples.KiePMMLNameValue::getValue).orElse" +
                                                "(null);" +
                                                "    java.lang.Object %1$s = this.FUNCTION_NAME(param1, " +
                                                "variable%1$sConstant1, " +
                                                "variable%1$sFieldRef2);" +
                                                "}", variableName));
        JavaParserUtils.equalsNode(expected, retrieved);
    }

    @Test
    public void getApplyExpressionNestedBlockStmt() {
        Apply nestedApply = applySupplier.get();

        Constant constant = new Constant();
        constant.setDataType(DataType.STRING);
        constant.setValue("STRING_VALUE");
        String functionName = "EXTERNAL_FUNCTION_NAME";
        Apply apply = new Apply();
        apply.addExpressions(constant, nestedApply);
        apply.setFunction(functionName);
        String variableName = "VARIABLE_NAME";
        BlockStmt retrieved = ExpressionFunctionUtils.getApplyExpressionBlockStmtWithKiePMMLValues(variableName, apply,
                                                                                                   parseClassOrInterfaceType(Object
                                                                                                                                     .class.getName()),
                                                                                                   DEFAULT_PARAMETERTYPE_MAP);
        BlockStmt expected = JavaParserUtils.parseBlock(String.format("{" +
                                                "    java.lang.Object variable%1$sConstant1 = \"STRING_VALUE\";" +
                                                "    java.lang.Object variablevariable%1$sApply2Constant1 = 34.6;" +
                                                "    java.util.Optional<org.kie.pmml.commons.model.tuples" +
                                                ".KiePMMLNameValue> kiePMMLNameValue = param1" +
                                                ".stream().filter((lmbdParam) -> java.util.Objects" +
                                                ".equals(\"FIELD_REF\", lmbdParam.getName())).findFirst();" +
                                                "    java.lang.Object variablevariable%1$sApply2FieldRef2 = (java" +
                                                ".lang.Object) " +
                                                "kiePMMLNameValue.map(org.kie.pmml.commons.model.tuples" +
                                                ".KiePMMLNameValue::getValue).orElse(null);" +
                                                "    java.lang.Object variable%1$sApply2 = this.FUNCTION_NAME(param1," +
                                                " " +
                                                "variablevariableVARIABLE_NAMEApply2Constant1, " +
                                                "variablevariable%1$sApply2FieldRef2);" +
                                                "    java.lang.Object %1$s = this.EXTERNAL_FUNCTION_NAME(param1, " +
                                                "variable%1$sConstant1, variable%1$sApply2);" +
                                                "}", variableName));
        JavaParserUtils.equalsNode(expected, retrieved);
    }

    @Test
    public void getConstantExpressionBlockStmt() {
        String variableName = "VARIABLE_NAME";
        Constant constant = constantSupplier.get();
        ClassOrInterfaceType returnedType = parseClassOrInterfaceType(Double.class.getName());
        BlockStmt retrieved = ExpressionFunctionUtils.getConstantExpressionBlockStmt(variableName,
                                                                                     constant,
                                                                                     returnedType);
        BlockStmt expected = JavaParserUtils.parseBlock(String.format("{" +
                                                "    %1$s %2$s = %3$s;" +
                                                "}",
                                        Double.class.getName(),
                                        variableName,
                                        constant.getValue()));
        JavaParserUtils.equalsNode(expected, retrieved);
        constant.setDataType(DataType.STRING);
        constant.setValue("STRING_VALUE");
        returnedType = parseClassOrInterfaceType(String.class.getName());
        retrieved = ExpressionFunctionUtils.getConstantExpressionBlockStmt(variableName, constant, returnedType);
        expected = JavaParserUtils.parseBlock(String.format("{" +
                                         "    %1$s %2$s = \"%3$s\";" +
                                         "}",
                                 String.class.getName(),
                                 variableName,
                                 constant.getValue()));
        JavaParserUtils.equalsNode(expected, retrieved);
    }

    @Test
    public void getFieldRefExpressionFromKiePMMLNameValuesBlockStmt() {
        String variableName = "VARIABLE_NAME";
        FieldRef fieldRef = fieldRefSupplier.get();
        String classType = Object.class.getName();
        final ClassOrInterfaceType classOrInterfaceType = parseClassOrInterfaceType(classType);
        BlockStmt retrieved = ExpressionFunctionUtils.getFieldRefExpressionBlockStmtWithKiePMMLValues(variableName,
                                                                                                      fieldRef,
                                                                                                      classOrInterfaceType);
        BlockStmt expected = JavaParserUtils.parseBlock(String.format("{" +
                                                "    java.util.Optional<org.kie.pmml.commons.model.tuples.KiePMMLNameValue> kiePMMLNameValue = param1" +
                                                ".stream().filter((lmbdParam) -> java.util.Objects" +
                                                ".equals(\"FIELD_REF\", lmbdParam.getName())).findFirst();" +
                                                "    %1$s %2$s = (java.lang.Object) kiePMMLNameValue.map(org.kie.pmml.commons" +
                                                ".model.tuples.KiePMMLNameValue::getValue).orElse(null);" +
                                                "}", classType, variableName));
        JavaParserUtils.equalsNode(expected, retrieved);
        String mapMissingTo = "MAP_MISSING_TO";
        fieldRef.setMapMissingTo(mapMissingTo);
        retrieved = ExpressionFunctionUtils.getFieldRefExpressionBlockStmtWithKiePMMLValues(variableName,
                                                                                            fieldRef,
                                                                                            classOrInterfaceType);
        String mapMissingQuoted = String.format("\"%s\"", mapMissingTo);
        expected = JavaParserUtils.parseBlock(String.format("{" +
                                         "    java.util.Optional<org.kie.pmml.commons.model.tuples.KiePMMLNameValue> kiePMMLNameValue = param1" +
                                         ".stream().filter((lmbdParam) -> java.util.Objects" +
                                         ".equals(\"FIELD_REF\", lmbdParam.getName())).findFirst();" +
                                         "    %1$s %2$s = (java.lang.Object) kiePMMLNameValue.map(org.kie.pmml.commons" +
                                         ".model.tuples.KiePMMLNameValue::getValue).orElse(%3$s);" +
                                         "}", classType, variableName, mapMissingQuoted));
        JavaParserUtils.equalsNode(expected, retrieved);
    }

    @Test
    public void getFieldRefExpressionFromInputValueBlockStmt() {
        String variableName = "VARIABLE_NAME";
        FieldRef fieldRef = fieldRefSupplier.get();
        String classType = Object.class.getName();
        final ClassOrInterfaceType classOrInterfaceType = parseClassOrInterfaceType(classType);
        BlockStmt retrieved = ExpressionFunctionUtils.getFieldRefExpressionBlockStmtWithInputValue(variableName,
                                                                                                   fieldRef,
                                                                                                   classOrInterfaceType);
        BlockStmt expected = JavaParserUtils.parseBlock(String.format("{" +
                                                "    %1$s %2$s = FIELD_REF != null ? (%1$s) org.kie.pmml.api.utils" +
                                                ".ConverterTypeUtil.convert(%1$s.class, FIELD_REF) : (%1$s) null;" +
                                                "}", classType, variableName));
        JavaParserUtils.equalsNode(expected, retrieved);
        String mapMissingTo = "MAP_MISSING_TO";
        fieldRef.setMapMissingTo(mapMissingTo);
        retrieved = ExpressionFunctionUtils.getFieldRefExpressionBlockStmtWithInputValue(variableName,
                                                                                         fieldRef,
                                                                                         classOrInterfaceType);
        String mapMissingQuoted = String.format("\"%s\"", mapMissingTo);
        expected = JavaParserUtils.parseBlock(String.format("{" +
                                         "    %1$s %2$s = FIELD_REF != null ? (%1$s) " +
                                         "org.kie.pmml.api.utils.ConverterTypeUtil.convert(%1$s.class, FIELD_REF) : (%1$s) %3$s;" +
                                         "}", classType, variableName, mapMissingQuoted));
        JavaParserUtils.equalsNode(expected, retrieved);
    }

    @Test
    public void getExpressionMethodDeclaration() {
        String methodName = "METHOD_NAME";
        String variableName = "VARIABLE_NAME";
        String classType = Object.class.getName();
        final ClassOrInterfaceType classOrInterfaceType = parseClassOrInterfaceType(classType);
        MethodDeclaration retrieved = ExpressionFunctionUtils.getExpressionMethodDeclarationWithKiePMMLValues(methodName,
                                                                                                              variableName,
                                                                                                              new BlockStmt(),
                                                                                                              classOrInterfaceType,
                                                                                                              DEFAULT_PARAMETERTYPE_MAP);
        assertNotNull(retrieved);
        MethodDeclaration expected = JavaParserUtils.parseMethod(String.format("%1$s %2$s(java.util.List<org.kie.pmml.commons.model.tuples" +
                                                ".KiePMMLNameValue> param1) {" +
                                                "    return %3$s;" +
                                                "}", classType, methodName, variableName));
        JavaParserUtils.equalsNode(expected, retrieved);
    }


    @Test
    public void converterTypeUtilFieldAccessorExpr() {
        assertEquals(ConverterTypeUtil.class.getName(), CONVERTER_TYPE_UTIL_FIELD_ACCESSOR_EXPR.toString());
    }

    private void commonValidateConstant(MethodDeclaration retrieved, Constant constant, String expectedMethodName,
                                        String expectedClass, String variableDeclaration) {
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
        String expected = String.format("return %s;", CONSTANT_VALUE);
        assertEquals(expected, retrievedString);
        commonValidateCompilation(retrieved);
    }

    private void commonValidateMethodDeclaration(MethodDeclaration toValidate, String expectedMethodName) {
        assertNotNull(toValidate);
        assertEquals(toValidate.getName().asString(), expectedMethodName);
    }
}