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
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import org.dmg.pmml.Aggregate;
import org.dmg.pmml.Apply;
import org.dmg.pmml.Constant;
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
import org.kie.pmml.commons.exceptions.KiePMMLException;
import org.kie.pmml.commons.model.enums.DATA_TYPE;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.kie.pmml.compiler.commons.testutils.PMMLModelTestUtils.getParameterField;

public class DefineFunctionUtilsTest {

    private static Map<String, String> expectedEventuallyBoxedClassName;
    private static List<Supplier<Expression>> supportedExpressionSupplier;
    private static List<Supplier<Expression>> unsupportedExpressionSupplier;
    private static final Function<Supplier<Expression>, DefineFunction> defineFunctionCreator = supplier -> {
        Expression expression = supplier.get();
        DefineFunction defineFunction = new DefineFunction();
        defineFunction.setName("DEFINE_FUNCTION_" + expression.getClass().getSimpleName());
        defineFunction.setExpression(expression);
        return defineFunction;
    };

    static {
        expectedEventuallyBoxedClassName = new HashMap<>();
        expectedEventuallyBoxedClassName.put("string", String.class.getName());
        expectedEventuallyBoxedClassName.put("integer", Integer.class.getName());
        expectedEventuallyBoxedClassName.put("float", Float.class.getName());
        expectedEventuallyBoxedClassName.put("double", Double.class.getName());
        expectedEventuallyBoxedClassName.put("boolean", Boolean.class.getName());
        expectedEventuallyBoxedClassName.put("date", Date.class.getName());
        expectedEventuallyBoxedClassName.put("time", Date.class.getName());
        expectedEventuallyBoxedClassName.put("dateTime", Date.class.getName());
        expectedEventuallyBoxedClassName.put("dateDaysSince[0]", Long.class.getName());
        expectedEventuallyBoxedClassName.put("dateDaysSince[1960]", Long.class.getName());
        expectedEventuallyBoxedClassName.put("dateDaysSince[1970]", Long.class.getName());
        expectedEventuallyBoxedClassName.put("dateDaysSince[1980]", Long.class.getName());
        expectedEventuallyBoxedClassName.put("timeSeconds", Long.class.getName());
        expectedEventuallyBoxedClassName.put("dateTimeSecondsSince[0]", Long.class.getName());
        expectedEventuallyBoxedClassName.put("dateTimeSecondsSince[1960]", Long.class.getName());
        expectedEventuallyBoxedClassName.put("dateTimeSecondsSince[1970]", Long.class.getName());
        expectedEventuallyBoxedClassName.put("dateTimeSecondsSince[1980]", Long.class.getName());
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
                assertTrue(e instanceof KiePMMLException);
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
        DefineFunctionUtils.getExpressionMethodDeclaration("", expression, Collections.emptyList());
    }

    @Test
    public void getExpressionMethodDeclarationUnsupportedExpression() {
        for (Supplier<Expression> supplier : unsupportedExpressionSupplier) {
            Expression expression = supplier.get();
            try {
                DefineFunctionUtils.getExpressionMethodDeclaration("", expression, Collections.emptyList());
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
                DefineFunctionUtils.getExpressionMethodDeclaration("METHOD_NAME", expression, Collections.emptyList());
            } catch (Exception e) {
                fail(String.format("Unexpected %s for %s", e, expression.getClass()));
            }
        }
    }

    @Test(expected = KiePMMLException.class)
    public void getAggregatedMethodDeclaration() {
        DefineFunctionUtils.getAggregatedMethodDeclaration("", new Aggregate(), Collections.emptyList());
    }

    @Test(expected = KiePMMLException.class)
    public void getApplyMethodDeclaration() {
        DefineFunctionUtils.getApplyMethodDeclaration("", new Apply(), Collections.emptyList());
    }

    @Test
    public void getConstantMethodDeclaration() {
    }

    @Test(expected = KiePMMLException.class)
    public void getDiscretizeMethodDeclaration() {
        DefineFunctionUtils.getDiscretizeMethodDeclaration("", new Discretize(), Collections.emptyList());
    }

    @Test
    public void getFieldRefMethodDeclaration() {
    }

    @Test(expected = KiePMMLException.class)
    public void getLagMethodDeclaration() {
        DefineFunctionUtils.getLagMethodDeclaration("", new Lag(), Collections.emptyList());
    }

    @Test(expected = KiePMMLException.class)
    public void getMapValuesMethodDeclaration() {
        DefineFunctionUtils.getMapValuesMethodDeclaration("", new MapValues(), Collections.emptyList());
    }

    @Test(expected = KiePMMLException.class)
    public void getNormContinuousMethodDeclaration() {
        DefineFunctionUtils.getNormContinuousMethodDeclaration("", new NormContinuous(), Collections.emptyList());
    }

    @Test(expected = KiePMMLException.class)
    public void getNormDiscreteMethodDeclaration() {
        DefineFunctionUtils.getNormDiscreteMethodDeclaration("", new NormDiscrete(), Collections.emptyList());
    }

    @Test(expected = KiePMMLException.class)
    public void getTextIndexMethodDeclaration() {
        DefineFunctionUtils.getTextIndexMethodDeclaration("", new TextIndex(), Collections.emptyList());
    }

    @Test
    public void getClassOrInterfaceTypes() {
        List<ParameterField> parameterFields = getParameterFields();
        List<ClassOrInterfaceType> retrieved = DefineFunctionUtils.getClassOrInterfaceTypes(parameterFields);
        assertEquals(parameterFields.size(), retrieved.size());
        for (int i = 0; i < parameterFields.size(); i++) {
            commonVerifyParameterClassOrInterfaceType(retrieved.get(i), parameterFields.get(i));
        }
    }

    @Test
    public void getEventuallyBoxedClassName() {
        List<ParameterField> parameterFields = getParameterFields();
        parameterFields.forEach(parameterField -> {
            String retrieved = DefineFunctionUtils.getEventuallyBoxedClassName(parameterField);
            commonVerifyEventuallyBoxedClassName(retrieved, parameterField);
        });
    }

    private void commonVerifyParameterClassOrInterfaceType(ClassOrInterfaceType toVerify, ParameterField parameterField) {
        commonVerifyEventuallyBoxedClassName(toVerify.toString(), parameterField);
    }

    private void commonVerifyEventuallyBoxedClassName(String toVerify, ParameterField parameterField) {
        assertEquals(expectedEventuallyBoxedClassName.get(parameterField.getDataType().value()), toVerify);
    }

    private List<ParameterField> getParameterFields() {
        DATA_TYPE[] dataTypes = DATA_TYPE.values();
        List<ParameterField> toReturn = new ArrayList<>();
        for (int i = 0; i < dataTypes.length; i++) {
            DataType dataType = DataType.fromValue(dataTypes[i].getName());
            ParameterField toAdd = getParameterField(dataType.value().toUpperCase(), dataType);
            toReturn.add(toAdd);
        }
        return toReturn;
    }
}