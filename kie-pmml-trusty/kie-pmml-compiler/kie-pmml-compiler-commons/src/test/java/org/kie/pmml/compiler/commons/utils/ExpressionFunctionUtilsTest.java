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
import org.dmg.pmml.ParameterField;
import org.dmg.pmml.TextIndex;
import org.junit.Test;
import org.kie.pmml.api.exceptions.KiePMMLException;
import org.kie.pmml.commons.model.tuples.KiePMMLNameValue;

import static com.github.javaparser.StaticJavaParser.parseClassOrInterfaceType;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.kie.pmml.compiler.commons.testutils.CodegenTestUtils.commonValidateCompilation;
import static org.kie.pmml.compiler.commons.utils.CommonCodegenUtils.METHOD_NAME_TEMPLATE;
import static org.kie.pmml.compiler.commons.utils.ExpressionFunctionUtils.CONVERTER_TYPE_UTIL_FIELD_ACCESSOR_EXPR;
import static org.kie.pmml.compiler.commons.utils.ExpressionFunctionUtils.DEFAULT_PARAMETERTYPE_MAP;
import static org.kie.pmml.compiler.commons.utils.ModelUtils.getBoxedClassName;

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
    final static ClassOrInterfaceType OBJECT_CLASS = parseClassOrInterfaceType(Object.class.getName());
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
    public void converterTypeUtilFieldAccessorExpr() {
        assertEquals("org.kie.pmml.commons.utils.ConverterTypeUtil", CONVERTER_TYPE_UTIL_FIELD_ACCESSOR_EXPR.toString());
    }

    @Test(expected = KiePMMLException.class)
    public void getAggregatedExpressionMethodDeclaration() {
        ExpressionFunctionUtils.getAggregatedExpressionMethodDeclaration("", new Aggregate(), OBJECT_CLASS,
                                                                         DEFAULT_PARAMETERTYPE_MAP);
    }

    @Test
    public void getApplyExpressionMethodDeclaration() {
        Apply apply = applySupplier.get();
        int methodArity = new Random().nextInt(20);
        String methodName = String.format(METHOD_NAME_TEMPLATE, apply.getClass().getSimpleName(), methodArity);
        MethodDeclaration retrieved = ExpressionFunctionUtils.getApplyExpressionMethodDeclaration(methodName, apply,
                                                                                                  OBJECT_CLASS,
                                                                                                  DEFAULT_PARAMETERTYPE_MAP);
        String expected = String.format("java.lang.Object %s(java.util.List<org.kie.pmml.commons.model.tuples.KiePMMLNameValue> param1) {\n" +
                                                "    java.lang.Object variableapplyVariableConstant1 = 34.6;\n" +
                                                "    java.util.Optional<org.kie.pmml.commons.model.tuples" +
                                                ".KiePMMLNameValue> kiePMMLNameValue = param1.stream().filter((org" +
                                                ".kie.pmml.commons.model.tuples.KiePMMLNameValue lmbdParam) -> java" +
                                                ".util.Objects.equals(\"FIELD_REF\", lmbdParam.getName())).findFirst" +
                                                "();\n" +
                                                "    java.lang.Object variableapplyVariableFieldRef2 = (java.lang" +
                                                ".Object) kiePMMLNameValue.map(org.kie.pmml.commons.model.tuples" +
                                                ".KiePMMLNameValue::getValue).orElse(null);\n" +
                                                "    java.lang.Object applyVariable = this.FUNCTION_NAME(param1, " +
                                                "variableapplyVariableConstant1, variableapplyVariableFieldRef2);\n" +
                                                "    return applyVariable;\n" +
                                                "}", methodName);
        assertEquals(expected, retrieved.toString());
        //
        ParameterField parameterField = new ParameterField(FieldName.create("FIELD_REF"));
        LinkedHashMap<String, ClassOrInterfaceType> modifiedParametersMap = new LinkedHashMap<>(DEFAULT_PARAMETERTYPE_MAP);
        modifiedParametersMap.put(parameterField.getName().toString(), parseClassOrInterfaceType(getBoxedClassName(parameterField)));
        retrieved = ExpressionFunctionUtils.getApplyExpressionMethodDeclaration(methodName, apply,
                                                                                                  OBJECT_CLASS,
                                                                                modifiedParametersMap);
        expected = String.format("java.lang.Object %s(java.util.List<org.kie.pmml.commons.model.tuples.KiePMMLNameValue> " +
                "param1, java.lang.Object FIELD_REF) {\n" +
                "    java.lang.Object variableapplyVariableConstant1 = 34.6;\n" +
                "    java.lang.Object variableapplyVariableFieldRef2 = FIELD_REF != null ? (java.lang.Object) org.kie" +
                ".pmml.commons.utils.ConverterTypeUtil.convert(java.lang.Object.class, FIELD_REF) : (java.lang" +
                ".Object) null;\n" +
                "    java.lang.Object applyVariable = this.FUNCTION_NAME(param1, variableapplyVariableConstant1, " +
                "variableapplyVariableFieldRef2);\n" +
                "    return applyVariable;\n" +
                "}", methodName);
        assertEquals(expected, retrieved.toString());
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
        retrieved = ExpressionFunctionUtils.getConstantExpressionMethodDeclaration(methodName, constant, STRING_CLASS,
                                                                                   DEFAULT_PARAMETERTYPE_MAP);
        expectedVariableDeclaration = String.format("%1$s constantVariable = \"%2$s\";",
                                                    String.class.getName(),
                                                    constant.getValue());
        commonValidateConstant(retrieved, constant, methodName, String.class.getName(), expectedVariableDeclaration);
    }

    @Test(expected = KiePMMLException.class)
    public void getDiscretizeExpressionMethodDeclaration() {
        ExpressionFunctionUtils.getDiscretizeExpressionMethodDeclaration("", new Discretize(), OBJECT_CLASS,
                                                                         DEFAULT_PARAMETERTYPE_MAP);
    }

    @Test
    public void getFieldRefExpressionMethodDeclaration() {
        int methodArity = new Random().nextInt(20);
        FieldRef fieldRef = fieldRefSupplier.get();
        String methodName = String.format(METHOD_NAME_TEMPLATE, fieldRef.getClass().getSimpleName(), methodArity);
        MethodDeclaration retrieved = ExpressionFunctionUtils.getFieldRefExpressionMethodDeclaration(methodName,
                                                                                                     fieldRef,
                                                                                                     STRING_CLASS,
                                                                                                     DEFAULT_PARAMETERTYPE_MAP);
        String expected = String.format("java.lang.String fieldRefVariable = (java.lang.String) kiePMMLNameValue.map" +
                                                "(%1$s::getValue).orElse(%2$s);",
                                        KiePMMLNameValue.class.getName(),
                                        fieldRef.getMapMissingTo());
        commonValidateFieldRefMethod(retrieved, methodName, expected, String.class.getName());
        //
        fieldRef.setMapMissingTo("MAP_MISSING_TO");
        methodName = String.format(METHOD_NAME_TEMPLATE, fieldRef.getClass().getSimpleName(), methodArity);
        retrieved = ExpressionFunctionUtils.getFieldRefExpressionMethodDeclaration(methodName, fieldRef, STRING_CLASS,
                                                                                   DEFAULT_PARAMETERTYPE_MAP);
        expected = String.format("java.lang.String fieldRefVariable = (java.lang.String) kiePMMLNameValue.map" +
                                         "(%1$s::getValue).orElse(\"%2$s\");",
                                 KiePMMLNameValue.class.getName(),
                                 fieldRef.getMapMissingTo());
        commonValidateFieldRefMethod(retrieved, methodName, expected, String.class.getName());
    }

    @Test(expected = KiePMMLException.class)
    public void getLagExpressionMethodDeclaration() {
        ExpressionFunctionUtils.getLagExpressionMethodDeclaration("methodName", new Lag(), OBJECT_CLASS,
                                                                  DEFAULT_PARAMETERTYPE_MAP);
    }

    @Test(expected = KiePMMLException.class)
    public void getMapValuesExpressionMethodDeclaration() {
        ExpressionFunctionUtils.getMapValuesExpressionMethodDeclaration("methodName", new MapValues(), OBJECT_CLASS,
                                                                        DEFAULT_PARAMETERTYPE_MAP);
    }

    @Test(expected = KiePMMLException.class)
    public void getNormContinuousExpressionMethodDeclaration() {
        ExpressionFunctionUtils.getNormContinuousExpressionMethodDeclaration("methodName", new NormContinuous(),
                                                                             OBJECT_CLASS, DEFAULT_PARAMETERTYPE_MAP);
    }

    @Test(expected = KiePMMLException.class)
    public void getNormDiscreteExpressionMethodDeclaration() {
        ExpressionFunctionUtils.getNormDiscreteExpressionMethodDeclaration("methodName", new NormDiscrete(),
                                                                           OBJECT_CLASS, DEFAULT_PARAMETERTYPE_MAP);
    }

    @Test(expected = KiePMMLException.class)
    public void getTextIndexExpressionMethodDeclaration() {
        ExpressionFunctionUtils.getTextIndexExpressionMethodDeclaration("methodName", new TextIndex(), OBJECT_CLASS,
                                                                        DEFAULT_PARAMETERTYPE_MAP);
    }

    @Test
    public void getExpressionBlockStmtUnsupportedExpression() {
        for (Supplier<Expression> supplier : unsupportedExpressionSupplier) {
            Expression expression = supplier.get();
            try {
                ExpressionFunctionUtils.getExpressionBlockStmt("variableName", expression,
                                                               parseClassOrInterfaceType(Object.class.getName()),
                                                               DEFAULT_PARAMETERTYPE_MAP);
                fail(String.format("Expecting KiePMMLException for %s", expression.getClass()));
            } catch (Exception e) {
                assertEquals(KiePMMLException.class, e.getClass());
            }
        }
    }

    @Test
    public void getExpressionBlockStmtSupportedExpression() {
        for (Supplier<Expression> supplier : supportedExpressionSupplier) {
            Expression expression = supplier.get();
            try {
                ExpressionFunctionUtils.getExpressionBlockStmt("variableName", expression,
                                                               parseClassOrInterfaceType(Object.class.getName()),
                                                               DEFAULT_PARAMETERTYPE_MAP);
            } catch (Exception e) {
                fail(String.format("Unexpected %s for %s", e, expression.getClass()));
            }
        }
    }

    @Test(expected = KiePMMLException.class)
    public void getAggregatedExpressionBlockStmt() {
        ExpressionFunctionUtils.getAggregatedExpressionBlockStmt("variableName", new Aggregate(),
                                                                 parseClassOrInterfaceType(Object.class.getName()),
                                                                 DEFAULT_PARAMETERTYPE_MAP);
    }

    @Test
    public void getApplyExpressionBlockStmt() {
        Apply apply = applySupplier.get();
        String variableName = "VARIABLE_NAME";
        BlockStmt retrieved = ExpressionFunctionUtils.getApplyExpressionBlockStmt(variableName, apply,
                                                                                  parseClassOrInterfaceType(Object.class.getName()), DEFAULT_PARAMETERTYPE_MAP);
        String expected = "{\n" +
                "    java.lang.Object variableVARIABLE_NAMEConstant1 = 34.6;\n" +
                "    java.util.Optional<org.kie.pmml.commons.model.tuples.KiePMMLNameValue> kiePMMLNameValue = param1" +
                ".stream().filter((org.kie.pmml.commons.model.tuples.KiePMMLNameValue lmbdParam) -> java.util.Objects" +
                ".equals(\"FIELD_REF\", lmbdParam.getName())).findFirst();\n" +
                "    java.lang.Object variableVARIABLE_NAMEFieldRef2 = (java.lang.Object) kiePMMLNameValue.map(org" +
                ".kie.pmml.commons.model.tuples.KiePMMLNameValue::getValue).orElse(null);\n" +
                "    java.lang.Object VARIABLE_NAME = this.FUNCTION_NAME(param1, variableVARIABLE_NAMEConstant1, " +
                "variableVARIABLE_NAMEFieldRef2);\n" +
                "}";
        assertEquals(expected, retrieved.toString());
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
        BlockStmt retrieved = ExpressionFunctionUtils.getApplyExpressionBlockStmt(variableName, apply,
                                                                                  parseClassOrInterfaceType(Object.class.getName()), DEFAULT_PARAMETERTYPE_MAP);
        String expected = "{\n" +
                "    java.lang.Object variableVARIABLE_NAMEConstant1 = \"STRING_VALUE\";\n" +
                "    java.lang.Object variablevariableVARIABLE_NAMEApply2Constant1 = 34.6;\n" +
                "    java.util.Optional<org.kie.pmml.commons.model.tuples.KiePMMLNameValue> kiePMMLNameValue = param1" +
                ".stream().filter((org.kie.pmml.commons.model.tuples.KiePMMLNameValue lmbdParam) -> java.util.Objects" +
                ".equals(\"FIELD_REF\", lmbdParam.getName())).findFirst();\n" +
                "    java.lang.Object variablevariableVARIABLE_NAMEApply2FieldRef2 = (java.lang.Object) " +
                "kiePMMLNameValue.map(org.kie.pmml.commons.model.tuples.KiePMMLNameValue::getValue).orElse(null);\n" +
                "    java.lang.Object variableVARIABLE_NAMEApply2 = this.FUNCTION_NAME(param1, " +
                "variablevariableVARIABLE_NAMEApply2Constant1, variablevariableVARIABLE_NAMEApply2FieldRef2);\n" +
                "    java.lang.Object VARIABLE_NAME = this.EXTERNAL_FUNCTION_NAME(param1, " +
                "variableVARIABLE_NAMEConstant1, variableVARIABLE_NAMEApply2);\n" +
                "}";
        assertEquals(expected, retrieved.toString());
    }

    @Test
    public void getConstantExpressionBlockStmt() {
        String variableName = "VARIABLE_NAME";
        Constant constant = constantSupplier.get();
        ClassOrInterfaceType returnedType = parseClassOrInterfaceType(Double.class.getName());
        BlockStmt retrieved = ExpressionFunctionUtils.getConstantExpressionBlockStmt(variableName, constant,
                                                                                     returnedType,
                                                                                     DEFAULT_PARAMETERTYPE_MAP);
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
        retrieved = ExpressionFunctionUtils.getConstantExpressionBlockStmt(variableName, constant, returnedType,
                                                                           DEFAULT_PARAMETERTYPE_MAP);
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
        ExpressionFunctionUtils.getDiscretizeExpressionBlockStmt("variableName", new Discretize(),
                                                                 parseClassOrInterfaceType(Object.class.getName()),
                                                                 DEFAULT_PARAMETERTYPE_MAP);
    }

    @Test
    public void getFieldRefExpressionFromCommonDataBlockStmt() {
        String variableName = "VARIABLE_NAME";
        FieldRef fieldRef = fieldRefSupplier.get();
        BlockStmt retrieved = ExpressionFunctionUtils.getFieldRefExpressionFromCommonDataBlockStmt(variableName, fieldRef,
                                                                                                   parseClassOrInterfaceType(Object.class.getName()), DEFAULT_PARAMETERTYPE_MAP);
        String expected = "{\n" +
                "    java.util.Optional<org.kie.pmml.commons.model.tuples.KiePMMLNameValue> kiePMMLNameValue = param1" +
                ".stream().filter((org.kie.pmml.commons.model.tuples.KiePMMLNameValue lmbdParam) -> java.util.Objects" +
                ".equals(\"FIELD_REF\", lmbdParam.getName())).findFirst();\n" +
                "    java.lang.Object VARIABLE_NAME = (java.lang.Object) kiePMMLNameValue.map(org.kie.pmml.commons" +
                ".model.tuples.KiePMMLNameValue::getValue).orElse(null);\n" +
                "}";
        assertEquals(expected, retrieved.toString());
        String mapMissingTo = "MAP_MISSING_TO";
        fieldRef.setMapMissingTo(mapMissingTo);
        retrieved = ExpressionFunctionUtils.getFieldRefExpressionFromCommonDataBlockStmt(variableName, fieldRef,
                                                                                         parseClassOrInterfaceType(Object.class.getName()), DEFAULT_PARAMETERTYPE_MAP);
        expected = "{\n" +
                "    java.util.Optional<org.kie.pmml.commons.model.tuples.KiePMMLNameValue> kiePMMLNameValue = param1" +
                ".stream().filter((org.kie.pmml.commons.model.tuples.KiePMMLNameValue lmbdParam) -> java.util.Objects" +
                ".equals(\"FIELD_REF\", lmbdParam.getName())).findFirst();\n" +
                "    java.lang.Object VARIABLE_NAME = (java.lang.Object) kiePMMLNameValue.map(org.kie.pmml.commons" +
                ".model.tuples.KiePMMLNameValue::getValue).orElse(\"MAP_MISSING_TO\");\n" +
                "}";
        assertEquals(expected, retrieved.toString());
    }

    @Test
    public void getFieldRefExpressionFromDefineFunctionBlockStmt() {
        String variableName = "VARIABLE_NAME";
        FieldRef fieldRef = fieldRefSupplier.get();
        ParameterField parameterField = new ParameterField(FieldName.create("FIELD_REF"));
        LinkedHashMap<String, ClassOrInterfaceType> modifiedParametersMap = new LinkedHashMap<>(DEFAULT_PARAMETERTYPE_MAP);
        modifiedParametersMap.put(parameterField.getName().toString(), parseClassOrInterfaceType(getBoxedClassName(parameterField)));
        BlockStmt retrieved = ExpressionFunctionUtils.getFieldRefExpressionFromDefineFunctionBlockStmt(variableName, fieldRef,
                                                                                                   parseClassOrInterfaceType(Object.class.getName()), modifiedParametersMap);
        String expected = "{\n" +
                "    java.lang.Object VARIABLE_NAME = FIELD_REF != null ? (java.lang.Object) org.kie.pmml.commons.utils.ConverterTypeUtil.convert(java.lang.Object.class, FIELD_REF) : (java.lang.Object) null;\n" +
                "}";
        assertEquals(expected, retrieved.toString());
        String mapMissingTo = "MAP_MISSING_TO";
        fieldRef.setMapMissingTo(mapMissingTo);
        retrieved = ExpressionFunctionUtils.getFieldRefExpressionFromDefineFunctionBlockStmt(variableName, fieldRef,
                                                                                         parseClassOrInterfaceType(Object.class.getName()), modifiedParametersMap);
        expected = "{\n" +
                "    java.lang.Object VARIABLE_NAME = FIELD_REF != null ? (java.lang.Object) org.kie.pmml.commons.utils.ConverterTypeUtil.convert(java.lang.Object.class, FIELD_REF) : (java.lang.Object) \"MAP_MISSING_TO\";\n" +
                "}";
        assertEquals(expected, retrieved.toString());
    }

    @Test(expected = KiePMMLException.class)
    public void getLagExpressionBlockStmt() {
        ExpressionFunctionUtils.getLagExpressionBlockStmt("variableName", new Lag(),
                                                          parseClassOrInterfaceType(Object.class.getName()),
                                                          DEFAULT_PARAMETERTYPE_MAP);
    }

    @Test(expected = KiePMMLException.class)
    public void getMapValuesExpressionBlockStmt() {
        ExpressionFunctionUtils.getMapValuesExpressionBlockStmt("variableName", new MapValues(),
                                                                parseClassOrInterfaceType(Object.class.getName()),
                                                                DEFAULT_PARAMETERTYPE_MAP);
    }

    @Test(expected = KiePMMLException.class)
    public void getNormContinuousExpressionBlockStmtn() {
        ExpressionFunctionUtils.getNormContinuousExpressionBlockStmt("variableName", new NormContinuous(),
                                                                     parseClassOrInterfaceType(Object.class.getName()), DEFAULT_PARAMETERTYPE_MAP);
    }

    @Test(expected = KiePMMLException.class)
    public void getNormDiscreteExpressionBlockStmt() {
        ExpressionFunctionUtils.getNormDiscreteExpressionBlockStmt("variableName", new NormDiscrete(),
                                                                   parseClassOrInterfaceType(Object.class.getName()),
                                                                   DEFAULT_PARAMETERTYPE_MAP);
    }

    @Test(expected = KiePMMLException.class)
    public void getTextIndexExpressionBlockStmt() {
        ExpressionFunctionUtils.getTextIndexExpressionBlockStmt("variableName", new TextIndex(),
                                                                parseClassOrInterfaceType(Object.class.getName()),
                                                                DEFAULT_PARAMETERTYPE_MAP);
    }

    @Test
    public void getExpressionMethodDeclaration() {
        String methodName = "METHOD_NAME";
        String variableName = "VARIABLE_NAME";
        MethodDeclaration retrieved = ExpressionFunctionUtils.getExpressionMethodDeclaration(methodName,
                                                                                             variableName,
                                                                                             new BlockStmt(),
                                                                                             parseClassOrInterfaceType(Object.class.getName()),
                                                                                             DEFAULT_PARAMETERTYPE_MAP);
        assertNotNull(retrieved);
        String expected = "java.lang.Object METHOD_NAME(java.util.List<org.kie.pmml.commons.model.tuples" +
                ".KiePMMLNameValue> param1) {\n" +
                "    return VARIABLE_NAME;\n" +
                "}";
        assertEquals(expected, retrieved.toString());
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
        assertEquals("return constantVariable;", retrievedString);
        commonValidateCompilation(retrieved);
    }

    private void commonValidateFieldRefMethod(MethodDeclaration retrieved, String expectedMethodName,
                                              String expectedVariableAssignment, String expectedType) {
        commonValidateMethodDeclaration(retrieved, expectedMethodName);
        assertEquals(expectedType, retrieved.getType().asString());
        BlockStmt body = retrieved.getBody().orElseThrow(() -> new RuntimeException("Expecting BlockBody"));
        NodeList<Statement> statements = body.getStatements();
        assertEquals(3, statements.size());
        assertTrue(statements.get(1) instanceof ExpressionStmt);
        assertEquals(expectedVariableAssignment, statements.get(1).toString());
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