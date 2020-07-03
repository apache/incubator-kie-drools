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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import org.dmg.pmml.Aggregate;
import org.dmg.pmml.Apply;
import org.dmg.pmml.Constant;
import org.dmg.pmml.DataType;
import org.dmg.pmml.DefineFunction;
import org.dmg.pmml.Discretize;
import org.dmg.pmml.Expression;
import org.dmg.pmml.FieldRef;
import org.dmg.pmml.Lag;
import org.dmg.pmml.MapValues;
import org.dmg.pmml.NormContinuous;
import org.dmg.pmml.NormDiscrete;
import org.dmg.pmml.ParameterField;
import org.dmg.pmml.TextIndex;
import org.kie.pmml.commons.exceptions.KiePMMLException;
import org.kie.pmml.commons.model.tuples.KiePMMLNameValue;

import static com.github.javaparser.StaticJavaParser.parseClassOrInterfaceType;
import static org.kie.pmml.compiler.commons.utils.CommonCodegenUtils.getTypedClassOrInterfaceType;
import static org.kie.pmml.compiler.commons.utils.ExpressionFunctionUtils.getApplyExpressionMethodDeclaration;
import static org.kie.pmml.compiler.commons.utils.ExpressionFunctionUtils.getConstantExpressionMethodDeclaration;
import static org.kie.pmml.compiler.commons.utils.ExpressionFunctionUtils.getFieldRefExpressionMethodDeclaration;
import static org.kie.pmml.compiler.commons.utils.ModelUtils.getBoxedClassName;

/**
 * Class meant to provide <i>helper</i> methods to retrieve <code>Function</code> code-generators
 * out of <code>DefineFunction</code>s
 */
public class DefineFunctionUtils {

    private DefineFunctionUtils() {
        // Avoid instantiation
    }

    static Map<String, MethodDeclaration> getDefineFunctionsMethodMap(final List<DefineFunction> defineFunctions) {
        Map<String, MethodDeclaration> toReturn = new HashMap<>();
        defineFunctions.forEach(defineFunction ->
                                        toReturn.put(defineFunction.getName(),
                                                     getDefineFunctionMethodDeclaration(defineFunction)));
        return toReturn;
    }

    static MethodDeclaration getDefineFunctionMethodDeclaration(final DefineFunction defineFunction) {
        final Expression expression = defineFunction.getExpression();
        if (expression != null) {
            return getExpressionMethodDeclaration(defineFunction.getName(), expression, defineFunction.getDataType(),
                                                  defineFunction.getParameterFields());
        } else {
            throw new KiePMMLException("Define Function without Expression are not supported, yet");
        }
    }

    /**
     * @param methodName
     * @param expression
     * @param dataType
     * @param parameterFields
     * @return
     */
    static MethodDeclaration getExpressionMethodDeclaration(final String methodName, final Expression expression,
                                                            final DataType dataType,
                                                            final List<ParameterField> parameterFields) {
        final ClassOrInterfaceType returnedType = parseClassOrInterfaceType(getBoxedClassName(dataType));
        final List<ClassOrInterfaceType> parametersClassOrInterfaces = getClassOrInterfaceTypes(parameterFields);
        parametersClassOrInterfaces.add(0, getTypedClassOrInterfaceType(List.class.getName(),
                                                                        Collections.singletonList(KiePMMLNameValue.class.getName())));
        if (expression instanceof Aggregate) {
            return getAggregatedMethodDeclaration(methodName, (Aggregate) expression, returnedType,
                                                  parametersClassOrInterfaces);
        } else if (expression instanceof Apply) {
            return getApplyMethodDeclaration(methodName, (Apply) expression, returnedType, parametersClassOrInterfaces);
        } else if (expression instanceof Constant) {
            return getConstantMethodDeclaration(methodName, (Constant) expression, returnedType,
                                                parametersClassOrInterfaces);
        } else if (expression instanceof Discretize) {
            return getDiscretizeMethodDeclaration(methodName, (Discretize) expression, returnedType,
                                                  parametersClassOrInterfaces);
        } else if (expression instanceof FieldRef) {
            return getFieldRefMethodDeclaration(methodName, (FieldRef) expression, returnedType,
                                                parametersClassOrInterfaces);
        } else if (expression instanceof Lag) {
            return getLagMethodDeclaration(methodName, (Lag) expression, returnedType, parametersClassOrInterfaces);
        } else if (expression instanceof MapValues) {
            return getMapValuesMethodDeclaration(methodName, (MapValues) expression, returnedType,
                                                 parametersClassOrInterfaces);
        } else if (expression instanceof NormContinuous) {
            return getNormContinuousMethodDeclaration(methodName, (NormContinuous) expression, returnedType,
                                                      parametersClassOrInterfaces);
        } else if (expression instanceof NormDiscrete) {
            return getNormDiscreteMethodDeclaration(methodName, (NormDiscrete) expression, returnedType,
                                                    parametersClassOrInterfaces);
        } else if (expression instanceof TextIndex) {
            return getTextIndexMethodDeclaration(methodName, (TextIndex) expression, returnedType,
                                                 parametersClassOrInterfaces);
        } else {
            throw new IllegalArgumentException(String.format("Expression %s not managed", expression.getClass()));
        }
    }

    /**
     * @param methodName
     * @param aggregate
     * @param returnedType
     * @param parameterFields
     * @return
     */
    static MethodDeclaration getAggregatedMethodDeclaration(final String methodName, final Aggregate aggregate,
                                                            final ClassOrInterfaceType returnedType,
                                                            final List<ClassOrInterfaceType> parameterFields) {
        throw new KiePMMLException("Aggregate not managed, yet");
    }

    /**
     * For each <code>Expression</code> generates the code to retrieve the value, and then invoke the specified
     * <b>function</b>
     * with the retrieved values.
     * e.g.
     * <pre>
     *    Object FUNCTION(List<KiePMMLNameValue> param1)  {
     *      Object variableVARIABLE_NAMEConstant1 = 34.6;
     *      Optional<KiePMMLNameValue> kiePMMLNameValue = param1.stream().filter((KiePMMLNameValue lmbdParam) -> Objects.equals("FIELD_NAME", lmbdParam.getName())).findFirst();
     *      Object variableVARIABLE_NAMEFieldRef2 = kiePMMLNameValue.map(KiePMMLNameValue::getValue).orElse(null);
     *      Object VARIABLE_NAME = this.FUNCTION_NAME(variableVARIABLE_NAMEConstant1, variableVARIABLE_NAMEFieldRef2);
     *    }
     * </pre>
     * @param methodName
     * @param apply
     * @param returnedType
     * @param parameterFields
     * @return
     */
    static MethodDeclaration getApplyMethodDeclaration(final String methodName, final Apply apply,
                                                       final ClassOrInterfaceType returnedType,
                                                       final List<ClassOrInterfaceType> parameterFields) {
        return getApplyExpressionMethodDeclaration(methodName, apply, returnedType, parameterFields);
    }

    /**
     * Return
     * <pre>
     *     (<i>returnedType</i>) constant(<i>methodArity</i>))(List<KiePMMLNameValue> param1) {
     *     return (<i>constant_value</i>);
     * }
     * </pre>
     * e.g.
     * <pre>
     *     double constant10(java.util.List<org.kie.pmml.commons.model.tuples.KiePMMLNameValue> param1) {
     *     return 34.6;
     * }
     * </pre>
     * @param methodName
     * @param constant
     * @param returnedType
     * @param parameterFields
     * @return
     */
    static MethodDeclaration getConstantMethodDeclaration(final String methodName, final Constant constant,
                                                          final ClassOrInterfaceType returnedType,
                                                          final List<ClassOrInterfaceType> parameterFields) {
        return getConstantExpressionMethodDeclaration(methodName, constant, returnedType, parameterFields);
    }

    /**
     * @param methodName
     * @param discretize
     * @param returnedType
     * @param parameterFields
     * @return
     */
    static MethodDeclaration getDiscretizeMethodDeclaration(final String methodName, final Discretize discretize,
                                                            final ClassOrInterfaceType returnedType,
                                                            final List<ClassOrInterfaceType> parameterFields) {
        throw new KiePMMLException("Discretize not managed, yet");
    }

    /**
     * Returns
     * <pre>
     * (<i>returnedType</i>) FieldRef(<i>methodArity</i>)(java.util.List<KiePMMLNameValue> param1) {
     *      Optional<KiePMMLNameValue> kiePMMLNameValue = param1.stream().filter((KiePMMLNameValue lmbdParam) ->
     *          Objects.equals(<i>(FieldRef_name)</i>, lmbdParam.getName())).findFirst();
     *      return kiePMMLNameValue.map(KiePMMLNameValue::getValue).orElse(<i>(FieldRef_mapMissingTo)</i>);
     * }
     * </pre>
     * @param methodName
     * @param fieldRef
     * @param returnedType
     * @param parameterFields
     * @return
     */
    static MethodDeclaration getFieldRefMethodDeclaration(final String methodName, final FieldRef fieldRef,
                                                          final ClassOrInterfaceType returnedType,
                                                          final List<ClassOrInterfaceType> parameterFields) {
        return getFieldRefExpressionMethodDeclaration(methodName, fieldRef, returnedType, parameterFields);
    }

    /**
     * @param methodName
     * @param lag
     * @param returnedType
     * @param parameterFields
     * @return
     */
    static MethodDeclaration getLagMethodDeclaration(final String methodName, final Lag lag,
                                                     final ClassOrInterfaceType returnedType,
                                                     final List<ClassOrInterfaceType> parameterFields) {
        throw new KiePMMLException("Lag not managed, yet");
    }

    /**
     * @param methodName
     * @param mapValues
     * @param returnedType
     * @param parameterFields
     * @return
     */
    static MethodDeclaration getMapValuesMethodDeclaration(final String methodName, final MapValues mapValues,
                                                           final ClassOrInterfaceType returnedType,
                                                           final List<ClassOrInterfaceType> parameterFields) {
        throw new KiePMMLException("MapValues not managed, yet");
    }

    /**
     * @param methodName
     * @param normContinuous
     * @param returnedType
     * @param parameterFields
     * @return
     */
    static MethodDeclaration getNormContinuousMethodDeclaration(final String methodName,
                                                                final NormContinuous normContinuous,
                                                                final ClassOrInterfaceType returnedType,
                                                                final List<ClassOrInterfaceType> parameterFields) {
        throw new KiePMMLException("NormContinuous not managed, yet");
    }

    /**
     * @param methodName
     * @param normDiscrete
     * @param returnedType
     * @param parameterFields
     * @return
     */
    static MethodDeclaration getNormDiscreteMethodDeclaration(final String methodName,
                                                              final NormDiscrete normDiscrete,
                                                              final ClassOrInterfaceType returnedType,
                                                              final List<ClassOrInterfaceType> parameterFields) {
        throw new KiePMMLException("NormDiscrete not managed, yet");
    }

    /**
     * @param methodName
     * @param textIndex
     * @param returnedType
     * @param parameterFields
     * @return
     */
    static MethodDeclaration getTextIndexMethodDeclaration(final String methodName, final TextIndex textIndex,
                                                           final ClassOrInterfaceType returnedType,
                                                           final List<ClassOrInterfaceType> parameterFields) {
        throw new KiePMMLException("TextIndex not managed, yet");
    }

    /**
     * Create a <code>List&ltClassOrInterfaceType&gt;</code> out of the given <code>List&ParameterField&gt;</code>
     * @param parameterFields
     * @return
     */
    static List<ClassOrInterfaceType> getClassOrInterfaceTypes(List<ParameterField> parameterFields) {
        List<ClassOrInterfaceType> toReturn = new ArrayList<>();
        if (parameterFields != null) {
            parameterFields.forEach(parameterField -> toReturn.add(parseClassOrInterfaceType(getBoxedClassName(parameterField))));
        }
        return toReturn;
    }
}
