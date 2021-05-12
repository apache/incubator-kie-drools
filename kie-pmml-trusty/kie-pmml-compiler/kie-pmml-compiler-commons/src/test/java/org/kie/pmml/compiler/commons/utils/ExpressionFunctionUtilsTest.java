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
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
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
import org.dmg.pmml.ParameterField;
import org.dmg.pmml.TextIndex;
import org.junit.Test;
import org.kie.pmml.api.exceptions.KiePMMLException;
import org.kie.pmml.api.utils.ConverterTypeUtil;
import org.kie.pmml.commons.model.tuples.KiePMMLNameValue;

import static com.github.javaparser.StaticJavaParser.parseClassOrInterfaceType;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.kie.pmml.compiler.commons.testutils.CodegenTestUtils.commonValidateCompilation;
import static org.kie.pmml.compiler.commons.testutils.PMMLModelTestUtils.getParameterFields;
import static org.kie.pmml.compiler.commons.utils.CommonCodegenUtils.METHOD_NAME_TEMPLATE;
import static org.kie.pmml.compiler.commons.utils.ExpressionFunctionUtils.CONSTANT_VALUE;
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
    public void getKiePMMLNameValueExpressionMethodDeclarationUnsupportedExpression() {
        final AtomicInteger arityCounter = new AtomicInteger();
        unsupportedExpressionSupplier.forEach(supplier -> {
            Expression expression = supplier.get();
            String methodName = String.format(METHOD_NAME_TEMPLATE, expression.getClass().getSimpleName(),
                                              arityCounter);
            try {
                ExpressionFunctionUtils.getKiePMMLNameValueExpressionMethodDeclaration(expression, DataType.STRING,
                                                                                       methodName);
                fail("Expecting KiePMMLException for " + expression);
            } catch (Exception e) {
                assertTrue(e instanceof KiePMMLException);
            }
        });
    }

    @Test
    public void getKiePMMLNameValueExpressionMethodDeclarationSupportedExpression() {
        final AtomicInteger arityCounter = new AtomicInteger();
        supportedExpressionSupplier.forEach(supplier -> {
            Expression expression = supplier.get();
            String methodName = String.format(METHOD_NAME_TEMPLATE, expression.getClass().getSimpleName(),
                                              arityCounter);
            assertNotNull(ExpressionFunctionUtils.getKiePMMLNameValueExpressionMethodDeclaration(expression, DataType.STRING,
                                                                                   methodName));
        });
    }

    @Test
    public void getVariableParametersExpressionMethodUnsupportedDeclaration() {
        final AtomicInteger arityCounter = new AtomicInteger();
        final List<ParameterField> parameterFields = new LinkedList<>();
        ParameterField toAdd = new ParameterField();
        toAdd.setDataType(DataType.DOUBLE);
        toAdd.setName(FieldName.create("otherParameter"));
        parameterFields.add(toAdd);
        unsupportedExpressionSupplier.forEach(supplier -> {
            Expression expression = supplier.get();
            String methodName = String.format(METHOD_NAME_TEMPLATE, expression.getClass().getSimpleName(),
                                              arityCounter);
            try {
                ExpressionFunctionUtils.getVariableParametersExpressionMethodDeclaration(methodName, expression,
                                                                                         DataType.STRING,
                                                                                         parameterFields);
                fail("Expecting KiePMMLException for " + expression);
            } catch (Exception e) {
                assertTrue(e instanceof KiePMMLException);
            }
        });
    }

    @Test
    public void getVariableParametersExpressionMethodSupportedDeclaration() {
        final AtomicInteger arityCounter = new AtomicInteger();
        final List<ParameterField> parameterFields = new LinkedList<>();
        ParameterField toAdd = new ParameterField();
        toAdd.setDataType(DataType.DOUBLE);
        toAdd.setName(FieldName.create("otherParameter"));
        parameterFields.add(toAdd);
        supportedExpressionSupplier.forEach(supplier -> {
            Expression expression = supplier.get();
            String methodName = String.format(METHOD_NAME_TEMPLATE, expression.getClass().getSimpleName(),
                                              arityCounter);
            assertNotNull(ExpressionFunctionUtils.getVariableParametersExpressionMethodDeclaration(methodName, expression,
                                                                                     DataType.STRING, parameterFields));
        });
    }

    @Test
    public void getApplyExpressionMethodDeclaration() {
        Apply apply = applySupplier.get();
        int methodArity = new Random().nextInt(20);
        String methodName = String.format(METHOD_NAME_TEMPLATE, apply.getClass().getSimpleName(), methodArity);
        MethodDeclaration retrieved = ExpressionFunctionUtils.getApplyExpressionMethodDeclaration(methodName, apply,
                                                                                                  OBJECT_CLASS,
                                                                                                  DEFAULT_PARAMETERTYPE_MAP);
        String expected = String.format("java.lang.Object %s(java.util.List<org.kie.pmml.commons.model.tuples" +
                                                ".KiePMMLNameValue> param1) {\n" +
                                                "    java.lang.Object variableapplyVariableConstant1 = 34.6;\n" +
                                                "    java.util.Optional<org.kie.pmml.commons.model.tuples" +
                                                ".KiePMMLNameValue> kiePMMLNameValue = param1.stream().filter(" +
                                                "(lmbdParam) -> java" +
                                                ".util.Objects.equals(\"FIELD_REF\", lmbdParam.getName())).findFirst" +
                                                "();\n" +
                                                "    java.lang.Object variableapplyVariableFieldRef2 = (java.lang" +
                                                ".Object) kiePMMLNameValue.map(org.kie.pmml.commons.model.tuples" +
                                                ".KiePMMLNameValue::getValue).orElse(null);\n" +
                                                "    java.lang.Object applyVariable = this.FUNCTION_NAME(param1, " +
                                                "variableapplyVariableConstant1, variableapplyVariableFieldRef2);\n" +
                                                "    return applyVariable;\n" +
                                                "}", methodName);
        expected = expected.replace("\n", System.lineSeparator());
        assertEquals(expected, retrieved.toString());
        //
        ParameterField parameterField = new ParameterField(FieldName.create("FIELD_REF"));
        LinkedHashMap<String, ClassOrInterfaceType> modifiedParametersMap =
                new LinkedHashMap<>(DEFAULT_PARAMETERTYPE_MAP);
        modifiedParametersMap.put(parameterField.getName().toString(),
                                  parseClassOrInterfaceType(getBoxedClassName(parameterField)));
        retrieved = ExpressionFunctionUtils.getApplyExpressionMethodDeclaration(methodName, apply,
                                                                                OBJECT_CLASS,
                                                                                modifiedParametersMap);
        expected = String.format("java.lang.Object %s(java.util.List<org.kie.pmml.commons.model.tuples" +
                                         ".KiePMMLNameValue> " +
                                         "param1, java.lang.Object FIELD_REF) {\n" +
                                         "    java.lang.Object variableapplyVariableConstant1 = 34.6;\n" +
                                         "    java.lang.Object variableapplyVariableFieldRef2 = FIELD_REF != null ? " +
                                         "(java.lang.Object) org.kie" +
                                         ".pmml.api.utils.ConverterTypeUtil.convert(java.lang.Object.class, " +
                                         "FIELD_REF) : (java.lang" +
                                         ".Object) null;\n" +
                                         "    java.lang.Object applyVariable = this.FUNCTION_NAME(param1, " +
                                         "variableapplyVariableConstant1, " +
                                         "variableapplyVariableFieldRef2);\n" +
                                         "    return applyVariable;\n" +
                                         "}", methodName);
        expected = expected.replace("\n", System.lineSeparator());
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

    @Test
    public void getUnsupportedExpressionBlockStmt() {
        final AtomicInteger arityCounter = new AtomicInteger();
        final ClassOrInterfaceType returnedType = parseClassOrInterfaceType(Object.class.getName());
        unsupportedExpressionSupplier.forEach(supplier -> {
            Expression expression = supplier.get();
            String methodName = String.format(METHOD_NAME_TEMPLATE, expression.getClass().getSimpleName(),
                                              arityCounter);
            try {
                ExpressionFunctionUtils.getExpressionBlockStmt(methodName, expression, returnedType,
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
            assertNotNull(ExpressionFunctionUtils.getExpressionBlockStmt(methodName, expression, returnedType, new LinkedHashMap<>()));
        });
    }

    @Test
    public void getApplyExpressionBlockStmt() {
        Apply apply = applySupplier.get();
        String variableName = "VARIABLE_NAME";
        BlockStmt retrieved = ExpressionFunctionUtils.getApplyExpressionBlockStmt(variableName, apply,
                                                                                  parseClassOrInterfaceType(Object
                                                                                                                    .class.getName()),
                                                                                  DEFAULT_PARAMETERTYPE_MAP);
        String expected = String.format("{\n" +
                                                "    java.lang.Object variable%1$sConstant1 = 34.6;\n" +
                                                "    java.util.Optional<org.kie.pmml.commons.model.tuples" +
                                                ".KiePMMLNameValue> kiePMMLNameValue = param1" +
                                                ".stream().filter((lmbdParam) -> java.util.Objects" +
                                                ".equals(\"FIELD_REF\", lmbdParam.getName())).findFirst();\n" +
                                                "    java.lang.Object variable%1$sFieldRef2 = (java.lang.Object) " +
                                                "kiePMMLNameValue.map(org" +
                                                ".kie.pmml.commons.model.tuples.KiePMMLNameValue::getValue).orElse" +
                                                "(null);\n" +
                                                "    java.lang.Object %1$s = this.FUNCTION_NAME(param1, " +
                                                "variable%1$sConstant1, " +
                                                "variable%1$sFieldRef2);\n" +
                                                "}", variableName);
        expected = expected.replace("\n", System.lineSeparator());
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
                                                                                  parseClassOrInterfaceType(Object
                                                                                                                    .class.getName()),
                                                                                  DEFAULT_PARAMETERTYPE_MAP);
        String expected = String.format("{\n" +
                                                "    java.lang.Object variable%1$sConstant1 = \"STRING_VALUE\";\n" +
                                                "    java.lang.Object variablevariable%1$sApply2Constant1 = 34.6;\n" +
                                                "    java.util.Optional<org.kie.pmml.commons.model.tuples" +
                                                ".KiePMMLNameValue> kiePMMLNameValue = param1" +
                                                ".stream().filter((lmbdParam) -> java.util.Objects" +
                                                ".equals(\"FIELD_REF\", lmbdParam.getName())).findFirst();\n" +
                                                "    java.lang.Object variablevariable%1$sApply2FieldRef2 = (java" +
                                                ".lang.Object) " +
                                                "kiePMMLNameValue.map(org.kie.pmml.commons.model.tuples" +
                                                ".KiePMMLNameValue::getValue).orElse(null);\n" +
                                                "    java.lang.Object variable%1$sApply2 = this.FUNCTION_NAME(param1," +
                                                " " +
                                                "variablevariableVARIABLE_NAMEApply2Constant1, " +
                                                "variablevariable%1$sApply2FieldRef2);\n" +
                                                "    java.lang.Object %1$s = this.EXTERNAL_FUNCTION_NAME(param1, " +
                                                "variable%1$sConstant1, variable%1$sApply2);\n" +
                                                "}", variableName);
        expected = expected.replace("\n", System.lineSeparator());
        assertEquals(expected, retrieved.toString());
    }

    @Test
    public void getConstantExpressionBlockStmt() {
        String variableName = "VARIABLE_NAME";
        Constant constant = constantSupplier.get();
        ClassOrInterfaceType returnedType = parseClassOrInterfaceType(Double.class.getName());
        BlockStmt retrieved = ExpressionFunctionUtils.getConstantExpressionBlockStmt(variableName,
                                                                                     constant,
                                                                                     returnedType);
        String expected = String.format("{\n" +
                                                "    %1$s %2$s = %3$s;\n" +
                                                "}",
                                        Double.class.getName(),
                                        variableName,
                                        constant.getValue());
        expected = expected.replace("\n", System.lineSeparator());
        assertEquals(expected, retrieved.toString());
        constant.setDataType(DataType.STRING);
        constant.setValue("STRING_VALUE");
        returnedType = parseClassOrInterfaceType(String.class.getName());
        retrieved = ExpressionFunctionUtils.getConstantExpressionBlockStmt(variableName, constant, returnedType);
        expected = String.format("{\n" +
                                         "    %1$s %2$s = \"%3$s\";\n" +
                                         "}",
                                 String.class.getName(),
                                 variableName,
                                 constant.getValue());
        expected = expected.replace("\n", System.lineSeparator());
        assertEquals(expected, retrieved.toString());
    }

    @Test
    public void getFieldRefExpressionFromKiePMMLNameValuesBlockStmt() {
        String variableName = "VARIABLE_NAME";
        FieldRef fieldRef = fieldRefSupplier.get();
        String classType = Object.class.getName();
        final ClassOrInterfaceType classOrInterfaceType = parseClassOrInterfaceType(classType);
        BlockStmt retrieved = ExpressionFunctionUtils.getFieldRefExpressionFromKiePMMLNameValuesBlockStmt(variableName,
                                                                                                          fieldRef,
                                                                                                          classOrInterfaceType);
        String expected = String.format("{\n" +
                "    java.util.Optional<org.kie.pmml.commons.model.tuples.KiePMMLNameValue> kiePMMLNameValue = param1" +
                ".stream().filter((lmbdParam) -> java.util.Objects" +
                ".equals(\"FIELD_REF\", lmbdParam.getName())).findFirst();\n" +
                "    %1$s %2$s = (java.lang.Object) kiePMMLNameValue.map(org.kie.pmml.commons" +
                ".model.tuples.KiePMMLNameValue::getValue).orElse(null);\n" +
                "}", classType, variableName);
        expected = expected.replace("\n", System.lineSeparator());
        assertEquals(expected, retrieved.toString());
        String mapMissingTo = "MAP_MISSING_TO";
        fieldRef.setMapMissingTo(mapMissingTo);
        retrieved = ExpressionFunctionUtils.getFieldRefExpressionFromKiePMMLNameValuesBlockStmt(variableName,
                                                                                                fieldRef,
                                                                                                classOrInterfaceType);
        String mapMissingQuoted = String.format("\"%s\"", mapMissingTo);
        expected = String.format("{\n" +
                "    java.util.Optional<org.kie.pmml.commons.model.tuples.KiePMMLNameValue> kiePMMLNameValue = param1" +
                ".stream().filter((lmbdParam) -> java.util.Objects" +
                ".equals(\"FIELD_REF\", lmbdParam.getName())).findFirst();\n" +
                "    %1$s %2$s = (java.lang.Object) kiePMMLNameValue.map(org.kie.pmml.commons" +
                ".model.tuples.KiePMMLNameValue::getValue).orElse(%3$s);\n" +
                "}", classType, variableName, mapMissingQuoted);
        expected = expected.replace("\n", System.lineSeparator());
        assertEquals(expected, retrieved.toString());
    }

    @Test
    public void getFieldRefExpressionFromInputValueBlockStmt() {
        String variableName = "VARIABLE_NAME";
        FieldRef fieldRef = fieldRefSupplier.get();
        String classType = Object.class.getName();
        final ClassOrInterfaceType classOrInterfaceType = parseClassOrInterfaceType(classType);
        BlockStmt retrieved = ExpressionFunctionUtils.getFieldRefExpressionFromInputValueBlockStmt(variableName,
                                                                                                   fieldRef,
                                                                                                   classOrInterfaceType);
        String expected = String.format("{\n" +
                "    %1$s %2$s = FIELD_REF != null ? (%1$s) org.kie.pmml.api.utils" +
                ".ConverterTypeUtil.convert(%1$s.class, FIELD_REF) : (%1$s) null;\n" +
                "}", classType, variableName);
        expected = expected.replace("\n", System.lineSeparator());
        assertEquals(expected, retrieved.toString());
        String mapMissingTo = "MAP_MISSING_TO";
        fieldRef.setMapMissingTo(mapMissingTo);
        retrieved = ExpressionFunctionUtils.getFieldRefExpressionFromInputValueBlockStmt(variableName,
                                                                                         fieldRef,
                                                                                         classOrInterfaceType);
        String mapMissingQuoted = String.format("\"%s\"", mapMissingTo);
        expected = String.format("{\n" +
                "    %1$s %2$s = FIELD_REF != null ? (%1$s) " +
                "org.kie.pmml.api.utils.ConverterTypeUtil.convert(%1$s.class, FIELD_REF) : (%1$s) %3$s;\n" +
                "}", classType, variableName, mapMissingQuoted);
        expected = expected.replace("\n", System.lineSeparator());
        assertEquals(expected, retrieved.toString());
    }

    @Test
    public void getExpressionMethodDeclaration() {
        String methodName = "METHOD_NAME";
        String variableName = "VARIABLE_NAME";
        String classType = Object.class.getName();
        final ClassOrInterfaceType classOrInterfaceType = parseClassOrInterfaceType(classType);
        MethodDeclaration retrieved = ExpressionFunctionUtils.getExpressionMethodDeclaration(methodName,
                                                                                             variableName,
                                                                                             new BlockStmt(),
                                                                                             classOrInterfaceType,
                                                                                             DEFAULT_PARAMETERTYPE_MAP);
        assertNotNull(retrieved);
        String expected = String.format("%1$s %2$s(java.util.List<org.kie.pmml.commons.model.tuples" +
                ".KiePMMLNameValue> param1) {\n" +
                "    return %3$s;\n" +
                "}", classType, methodName, variableName);
        expected = expected.replace("\n", System.lineSeparator());
        assertEquals(expected, retrieved.toString());
    }


    @Test
    public void converterTypeUtilFieldAccessorExpr() {
        assertEquals(ConverterTypeUtil.class.getName(), CONVERTER_TYPE_UTIL_FIELD_ACCESSOR_EXPR.toString());
    }

    @Test
    public void getClassOrInterfaceTypes() {
        List<ParameterField> parameterFields = getParameterFields();
        Map<String, ClassOrInterfaceType> retrieved =
                ExpressionFunctionUtils.getNameClassOrInterfaceTypeMap(parameterFields);
        assertEquals(parameterFields.size(), retrieved.size());
        for (ParameterField parameter : parameterFields) {
            assertTrue(retrieved.containsKey(parameter.getName().toString()));
            commonVerifyParameterClassOrInterfaceType(retrieved.get(parameter.getName().toString()), parameter);
        }
    }

    private void commonVerifyParameterClassOrInterfaceType(ClassOrInterfaceType toVerify,
                                                           ParameterField parameterField) {
        String expectedClass = ModelUtils.getBoxedClassName(parameterField);
        assertEquals(expectedClass, toVerify.toString());
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