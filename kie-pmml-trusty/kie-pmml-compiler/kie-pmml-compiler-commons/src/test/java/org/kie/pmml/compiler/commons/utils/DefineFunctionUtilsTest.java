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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import org.dmg.pmml.Aggregate;
import org.dmg.pmml.DataType;
import org.dmg.pmml.DefineFunction;
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
import org.dmg.pmml.Visitor;
import org.dmg.pmml.VisitorAction;
import org.junit.Test;
import org.kie.pmml.api.exceptions.KiePMMLException;

import static com.github.javaparser.StaticJavaParser.parseClassOrInterfaceType;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.kie.pmml.compiler.commons.testutils.PMMLModelTestUtils.getParameterFields;
import static org.kie.pmml.compiler.commons.utils.ExpressionFunctionUtils.DEFAULT_PARAMETERTYPE_MAP;
import static org.kie.pmml.compiler.commons.utils.ExpressionFunctionUtilsTest.DOUBLE_CLASS;
import static org.kie.pmml.compiler.commons.utils.ExpressionFunctionUtilsTest.OBJECT_CLASS;
import static org.kie.pmml.compiler.commons.utils.ExpressionFunctionUtilsTest.STRING_CLASS;
import static org.kie.pmml.compiler.commons.utils.ExpressionFunctionUtilsTest.applySupplier;
import static org.kie.pmml.compiler.commons.utils.ExpressionFunctionUtilsTest.constantSupplier;
import static org.kie.pmml.compiler.commons.utils.ExpressionFunctionUtilsTest.fieldRefSupplier;
import static org.kie.pmml.compiler.commons.utils.ExpressionFunctionUtilsTest.supportedExpressionSupplier;
import static org.kie.pmml.compiler.commons.utils.ExpressionFunctionUtilsTest.unsupportedExpressionSupplier;
import static org.kie.pmml.compiler.commons.utils.ModelUtils.getBoxedClassName;

public class DefineFunctionUtilsTest {

    private static final Function<Supplier<Expression>, DefineFunction> defineFunctionCreator = supplier -> {
        Expression expression = supplier.get();
        DefineFunction defineFunction = new DefineFunction();
        defineFunction.setName("DEFINE_FUNCTION_" + expression.getClass().getSimpleName());
        defineFunction.setExpression(expression);
        return defineFunction;
    };

    @Test(expected = KiePMMLException.class)
    public void getDefineFunctionsMethodMapUnsupportedExpression() {
        List<DefineFunction> defineFunctions = unsupportedExpressionSupplier.stream().map(defineFunctionCreator).collect(Collectors.toList());
        DefineFunctionUtils.getDefineFunctionsMethodMap(defineFunctions);
    }

    @Test
    public void getDefineFunctionsMethodMapSupportedExpression() {
        List<DefineFunction> defineFunctions = supportedExpressionSupplier.stream().map(defineFunctionCreator).collect(Collectors.toList());
        Map<String, MethodDeclaration> retrieved = DefineFunctionUtils.getDefineFunctionsMethodMap(defineFunctions);
        assertEquals(defineFunctions.size(), retrieved.size());
    }

    @Test
    public void getDefineFunctionMethodDeclarationUnsupportedExpression() {
        for (Supplier<Expression> supplier : unsupportedExpressionSupplier) {
            DefineFunction defineFunction = defineFunctionCreator.apply(supplier);
            try {
                DefineFunctionUtils.getDefineFunctionMethodDeclaration(defineFunction);
                fail(String.format("Expecting KiePMMLException for %s", defineFunction));
            } catch (Exception e) {
                assertEquals(KiePMMLException.class, e.getClass());
            }
        }
    }

    @Test
    public void getDefineFunctionMethodDeclarationSupportedExpression() {
        for (Supplier<Expression> supplier : supportedExpressionSupplier) {
            DefineFunction defineFunction = defineFunctionCreator.apply(supplier);
            try {
                DefineFunctionUtils.getDefineFunctionMethodDeclaration(defineFunction);
            } catch (Exception e) {
                fail(String.format("Unexpected %s for %s", e, defineFunction.getExpression().getClass()));
            }
        }
    }

    @Test(expected = KiePMMLException.class)
    public void getDefineFunctionMethodDeclarationWithoutExpression() {
        DefineFunctionUtils.getDefineFunctionMethodDeclaration(new DefineFunction());
    }

    @Test(expected = IllegalArgumentException.class)
    public void getExpressionMethodDeclarationUnknownExpression() {
        Expression expression = new Expression() {
            @Override
            public VisitorAction accept(Visitor visitor) {
                return null;
            }
        };
        DefineFunctionUtils.getExpressionMethodDeclaration("", expression, DataType.STRING, Collections.emptyList());
    }

    @Test
    public void getExpressionMethodDeclarationUnsupportedExpression() {
        for (Supplier<Expression> supplier : unsupportedExpressionSupplier) {
            Expression expression = supplier.get();
            try {
                DefineFunctionUtils.getExpressionMethodDeclaration("", expression, DataType.STRING, Collections.emptyList());
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
                DefineFunctionUtils.getExpressionMethodDeclaration("METHOD_NAME", expression, DataType.STRING, Collections.emptyList());
            } catch (Exception e) {
                fail(String.format("Unexpected %s for %s", e, expression.getClass()));
            }
        }
    }

    @Test(expected = KiePMMLException.class)
    public void getAggregatedMethodDeclaration() {
        DefineFunctionUtils.getAggregatedMethodDeclaration("", new Aggregate(), OBJECT_CLASS, DEFAULT_PARAMETERTYPE_MAP);
    }

    @Test
    public void getApplyMethodDeclaration() {
        String methodName = "METHOD_NAME";
        MethodDeclaration retrieved = DefineFunctionUtils.getApplyMethodDeclaration(methodName, applySupplier.get(), OBJECT_CLASS, DEFAULT_PARAMETERTYPE_MAP);
        String expected = String.format("java.lang.Object %s(java.util.List<org.kie.pmml.commons.model.tuples.KiePMMLNameValue> param1) {\n" +
                                                "    java.lang.Object variableapplyVariableConstant1 = 34.6;\n" +
                                                "    java.util.Optional<org.kie.pmml.commons.model.tuples.KiePMMLNameValue> kiePMMLNameValue = param1.stream().filter((org.kie.pmml.commons.model.tuples.KiePMMLNameValue lmbdParam) -> java.util.Objects.equals(\"FIELD_REF\", lmbdParam.getName())).findFirst();\n" +
                                                "    java.lang.Object variableapplyVariableFieldRef2 = (java.lang.Object) kiePMMLNameValue.map(org.kie.pmml.commons.model.tuples.KiePMMLNameValue::getValue).orElse(null);\n" +
                                                "    java.lang.Object applyVariable = this.FUNCTION_NAME(param1, variableapplyVariableConstant1, variableapplyVariableFieldRef2);\n" +
                                                "    return applyVariable;\n" +
                                                "}", methodName);
        assertEquals(expected, retrieved.toString());
    }

    @Test
    public void getConstantMethodDeclaration() {
        String methodName = "METHOD_NAME";
        MethodDeclaration retrieved = DefineFunctionUtils.getConstantMethodDeclaration(methodName, constantSupplier.get(), DOUBLE_CLASS, DEFAULT_PARAMETERTYPE_MAP);
        String expected = String.format("java.lang.Double %s(java.util.List<org.kie.pmml.commons.model.tuples.KiePMMLNameValue> param1) {\n" +
                                                "    java.lang.Double constantVariable = 34.6;\n" +
                                                "    return constantVariable;\n" +
                                                "}", methodName);
        assertEquals(expected, retrieved.toString());
    }

    @Test(expected = KiePMMLException.class)
    public void getDiscretizeMethodDeclaration() {
        DefineFunctionUtils.getDiscretizeMethodDeclaration("", new Discretize(), OBJECT_CLASS, DEFAULT_PARAMETERTYPE_MAP);
    }

    @Test
    public void getFieldRefMethodDeclaration() {
        String methodName = "METHOD_NAME";
        FieldRef fieldRef = fieldRefSupplier.get();
        ParameterField parameterField = new ParameterField(FieldName.create("FIELD_REF"));
        LinkedHashMap<String, ClassOrInterfaceType> modifiedParametersMap = new LinkedHashMap<>(DEFAULT_PARAMETERTYPE_MAP);
        modifiedParametersMap.put(parameterField.getName().toString(), parseClassOrInterfaceType(getBoxedClassName(parameterField)));
        MethodDeclaration retrieved = DefineFunctionUtils.getFieldRefMethodDeclaration(methodName, fieldRef, STRING_CLASS, modifiedParametersMap);
        String expected = String.format("java.lang.String %s(java.util.List<org.kie.pmml.commons.model.tuples.KiePMMLNameValue> param1, java.lang.Object FIELD_REF) {\n" +
                                                "    java.lang.String fieldRefVariable = FIELD_REF != null ? (java.lang.String) org.kie.pmml.commons.utils.ConverterTypeUtil.convert(java.lang.String.class, FIELD_REF) " +
                                                ": (java.lang.String) null;\n" +
                                                "    return fieldRefVariable;\n" +
                                                "}", methodName);
        assertEquals(expected, retrieved.toString());
        String mapMissingTo = "MAP_MISSING_TO";
        fieldRef.setMapMissingTo(mapMissingTo);
        retrieved = DefineFunctionUtils.getFieldRefMethodDeclaration(methodName, fieldRef, STRING_CLASS, modifiedParametersMap);
        expected = String.format("java.lang.String %s(java.util.List<org.kie.pmml.commons.model.tuples.KiePMMLNameValue> param1, java.lang.Object FIELD_REF) {\n" +
                                                "    java.lang.String fieldRefVariable = FIELD_REF != null ? (java.lang.String) org.kie.pmml.commons.utils.ConverterTypeUtil.convert(java.lang.String.class, FIELD_REF) " +
                                         ": (java.lang.String) \"MAP_MISSING_TO\";\n" +
                                                "    return fieldRefVariable;\n" +
                                                "}", methodName);
        assertEquals(expected, retrieved.toString());
    }

    @Test(expected = KiePMMLException.class)
    public void getLagMethodDeclaration() {
        DefineFunctionUtils.getLagMethodDeclaration("", new Lag(), OBJECT_CLASS, DEFAULT_PARAMETERTYPE_MAP);
    }

    @Test(expected = KiePMMLException.class)
    public void getMapValuesMethodDeclaration() {
        DefineFunctionUtils.getMapValuesMethodDeclaration("", new MapValues(), OBJECT_CLASS, DEFAULT_PARAMETERTYPE_MAP);
    }

    @Test(expected = KiePMMLException.class)
    public void getNormContinuousMethodDeclaration() {
        DefineFunctionUtils.getNormContinuousMethodDeclaration("", new NormContinuous(), OBJECT_CLASS, DEFAULT_PARAMETERTYPE_MAP);
    }

    @Test(expected = KiePMMLException.class)
    public void getNormDiscreteMethodDeclaration() {
        DefineFunctionUtils.getNormDiscreteMethodDeclaration("", new NormDiscrete(), OBJECT_CLASS, DEFAULT_PARAMETERTYPE_MAP);
    }

    @Test(expected = KiePMMLException.class)
    public void getTextIndexMethodDeclaration() {
        DefineFunctionUtils.getTextIndexMethodDeclaration("", new TextIndex(), OBJECT_CLASS, DEFAULT_PARAMETERTYPE_MAP);
    }

    @Test
    public void getClassOrInterfaceTypes() {
        List<ParameterField> parameterFields = getParameterFields();
        Map<String, ClassOrInterfaceType> retrieved = DefineFunctionUtils.getNameClassOrInterfaceTypeMap(parameterFields);
        assertEquals(parameterFields.size(), retrieved.size());
        for (ParameterField parameter : parameterFields) {
            assertTrue(retrieved.containsKey(parameter.getName().toString()));
            commonVerifyParameterClassOrInterfaceType(retrieved.get(parameter.getName().toString()), parameter);
        }
    }


    private void commonVerifyParameterClassOrInterfaceType(ClassOrInterfaceType toVerify, ParameterField parameterField) {
        String expectedClass = ModelUtils.getBoxedClassName(parameterField);
        assertEquals(expectedClass, toVerify.toString());
    }

}