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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import org.dmg.pmml.Aggregate;
import org.dmg.pmml.Apply;
import org.dmg.pmml.Constant;
import org.dmg.pmml.DataType;
import org.dmg.pmml.DerivedField;
import org.dmg.pmml.Discretize;
import org.dmg.pmml.Expression;
import org.dmg.pmml.FieldRef;
import org.dmg.pmml.Lag;
import org.dmg.pmml.MapValues;
import org.dmg.pmml.NormContinuous;
import org.dmg.pmml.NormDiscrete;
import org.dmg.pmml.TextIndex;
import org.kie.pmml.commons.exceptions.KiePMMLException;
import org.kie.pmml.commons.model.tuples.KiePMMLNameValue;

import static com.github.javaparser.StaticJavaParser.parseClassOrInterfaceType;
import static org.kie.pmml.compiler.commons.utils.CommonCodegenUtils.METHOD_NAME_TEMPLATE;
import static org.kie.pmml.compiler.commons.utils.CommonCodegenUtils.getTypedClassOrInterfaceType;
import static org.kie.pmml.compiler.commons.utils.ExpressionFunctionUtils.getApplyExpressionMethodDeclaration;
import static org.kie.pmml.compiler.commons.utils.ExpressionFunctionUtils.getConstantExpressionMethodDeclaration;
import static org.kie.pmml.compiler.commons.utils.ExpressionFunctionUtils.getFieldRefExpressionMethodDeclaration;
import static org.kie.pmml.compiler.commons.utils.ModelUtils.getBoxedClassName;

/**
 * Class meant to provide <i>helper</i> methods to retrieve <code>Function</code> code-generators
 * out of <code>DerivedField</code>s
 */
public class DerivedFieldFunctionUtils {

    private DerivedFieldFunctionUtils() {
        // Avoid instantiation
    }

    static Map<String, MethodDeclaration> getDerivedFieldsMethodMap(final List<DerivedField> derivedFields,
                                                                    final AtomicInteger arityCounter) {
        Map<String, MethodDeclaration> toReturn = new HashMap<>();
        derivedFields.forEach(derivedField ->
                                      toReturn.put(derivedField.getName().getValue(),
                                                   getDerivedFieldMethodDeclaration(derivedField, arityCounter)));
        return toReturn;
    }

    static MethodDeclaration getDerivedFieldMethodDeclaration(final DerivedField derivedField,
                                                              final AtomicInteger arityCounter) {
        final Expression expression = derivedField.getExpression();
        if (expression != null) {
            return getExpressionMethodDeclaration(expression, derivedField.getDataType(), arityCounter);
        } else {
            throw new KiePMMLException("Derived field without Expression are not supported, yet");
        }
    }

    static MethodDeclaration getExpressionMethodDeclaration(final Expression expression, final DataType dataType,
                                                            final AtomicInteger arityCounter) {
        final ClassOrInterfaceType returnedType = parseClassOrInterfaceType(getBoxedClassName(dataType));
        int methodArity = arityCounter.addAndGet(1);
        if (expression instanceof Aggregate) {
            return getAggregatedMethodDeclaration((Aggregate) expression, returnedType, methodArity);
        } else if (expression instanceof Apply) {
            return getApplyMethodDeclaration((Apply) expression, returnedType, methodArity);
        } else if (expression instanceof Constant) {
            return getConstantMethodDeclaration((Constant) expression, returnedType, methodArity);
        } else if (expression instanceof Discretize) {
            return getDiscretizeMethodDeclaration((Discretize) expression, returnedType, methodArity);
        } else if (expression instanceof FieldRef) {
            return getFieldRefMethodDeclaration((FieldRef) expression, returnedType, methodArity);
        } else if (expression instanceof Lag) {
            return getLagMethodDeclaration((Lag) expression, returnedType, methodArity);
        } else if (expression instanceof MapValues) {
            return getMapValuesMethodDeclaration((MapValues) expression, returnedType, methodArity);
        } else if (expression instanceof NormContinuous) {
            return getNormContinuousMethodDeclaration((NormContinuous) expression, returnedType, methodArity);
        } else if (expression instanceof NormDiscrete) {
            return getNormDiscreteMethodDeclaration((NormDiscrete) expression, returnedType, methodArity);
        } else if (expression instanceof TextIndex) {
            return getTextIndexMethodDeclaration((TextIndex) expression, returnedType, methodArity);
        } else {
            throw new IllegalArgumentException(String.format("Expression %s not managed", expression.getClass()));
        }
    }

    /**
     * @param aggregate
     * @param returnedType
     * @param methodArity
     * @return
     */
    static MethodDeclaration getAggregatedMethodDeclaration(final Aggregate aggregate,
                                                            ClassOrInterfaceType returnedType, final int methodArity) {
        throw new KiePMMLException("Aggregate not managed, yet");
    }

    /**
     * @param apply
     * @param returnedType
     * @param methodArity
     * @return
     */
    static MethodDeclaration getApplyMethodDeclaration(final Apply apply, ClassOrInterfaceType returnedType,
                                                       final int methodArity) {
        String methodName = String.format(METHOD_NAME_TEMPLATE, apply.getClass().getSimpleName(), methodArity);
        return getApplyExpressionMethodDeclaration(methodName, apply, returnedType,
                                                   Collections.singletonList(getTypedClassOrInterfaceType(List.class.getName(), Collections.singletonList(KiePMMLNameValue.class.getName()))));
    }

    /**
     * Return
     * <pre>
     *     (<i>constant_type</i>) constant(<i>methodArity</i>))(List<KiePMMLNameValue> param1) {
     *     return (<i>constant_value</i>);
     * }
     * </pre>
     * e.g.
     * <pre>
     *     double constant10(java.util.List<org.kie.pmml.commons.model.tuples.KiePMMLNameValue> param1) {
     *     return 34.6;
     * }
     * </pre>
     * @param constant
     * @param returnedType
     * @param methodArity
     * @return
     */
    static MethodDeclaration getConstantMethodDeclaration(final Constant constant, ClassOrInterfaceType returnedType,
                                                          final int methodArity) {
        String methodName = String.format(METHOD_NAME_TEMPLATE, constant.getClass().getSimpleName(), methodArity);
        return getConstantExpressionMethodDeclaration(methodName, constant, returnedType,
                                                      Collections.singletonList(getTypedClassOrInterfaceType(List.class.getName(), Collections.singletonList(KiePMMLNameValue.class.getName()))));
    }

    /**
     * @param discretize
     * @param returnedType
     * @param methodArity
     * @return
     */
    static MethodDeclaration getDiscretizeMethodDeclaration(final Discretize discretize,
                                                            ClassOrInterfaceType returnedType, final int methodArity) {
        throw new KiePMMLException("Discretize not managed, yet");
    }

    /**
     * Returns
     * <pre>
     * Object FieldRef(<i>methodArity</i>)(java.util.List<KiePMMLNameValue> param1) {
     *      Optional<KiePMMLNameValue> kiePMMLNameValue = param1.stream().filter((KiePMMLNameValue lmbdParam) -> Objects.equals(<i>(FieldRef_name)</i>, lmbdParam.getName())).findFirst();
     *      return kiePMMLNameValue.map(KiePMMLNameValue::getValue).orElse(<i>(FieldRef_mapMissingTo)</i>);
     * }
     * </pre>
     * @param fieldRef
     * @param returnedType
     * @param methodArity
     * @return
     */
    static MethodDeclaration getFieldRefMethodDeclaration(final FieldRef fieldRef, ClassOrInterfaceType returnedType,
                                                          final int methodArity) {
        String methodName = String.format(METHOD_NAME_TEMPLATE, fieldRef.getClass().getSimpleName(), methodArity);
        return getFieldRefExpressionMethodDeclaration(methodName, fieldRef, returnedType,
                                                      Collections.singletonList(getTypedClassOrInterfaceType(List.class.getName(), Collections.singletonList(KiePMMLNameValue.class.getName()))));
    }

    /**
     * @param lag
     * @param returnedType
     * @param methodArity
     * @return
     */
    static MethodDeclaration getLagMethodDeclaration(final Lag lag, ClassOrInterfaceType returnedType,
                                                     final int methodArity) {
        throw new KiePMMLException("Lag not managed, yet");
    }

    /**
     * @param mapValues
     * @param returnedType
     * @param methodArity
     * @return
     */
    static MethodDeclaration getMapValuesMethodDeclaration(final MapValues mapValues,
                                                           ClassOrInterfaceType returnedType, final int methodArity) {
        throw new KiePMMLException("MapValues not managed, yet");
    }

    /**
     * @param normContinuous
     * @param returnedType
     * @param methodArity
     * @return
     */
    static MethodDeclaration getNormContinuousMethodDeclaration(final NormContinuous normContinuous,
                                                                ClassOrInterfaceType returnedType,
                                                                final int methodArity) {
        throw new KiePMMLException("NormContinuous not managed, yet");
    }

    /**
     * @param normDiscrete
     * @param returnedType
     * @param methodArity
     * @return
     */
    static MethodDeclaration getNormDiscreteMethodDeclaration(final NormDiscrete normDiscrete,
                                                              ClassOrInterfaceType returnedType,
                                                              final int methodArity) {
        throw new KiePMMLException("NormDiscrete not managed, yet");
    }

    /**
     * @param textIndex
     * @param returnedType
     * @param methodArity
     * @return
     */
    static MethodDeclaration getTextIndexMethodDeclaration(final TextIndex textIndex,
                                                           ClassOrInterfaceType returnedType, final int methodArity) {
        throw new KiePMMLException("TextIndex not managed, yet");
    }
}
