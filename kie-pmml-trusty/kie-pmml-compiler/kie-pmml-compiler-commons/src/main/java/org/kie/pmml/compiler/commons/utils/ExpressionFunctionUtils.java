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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.stream.Collectors;

import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.BinaryExpr;
import com.github.javaparser.ast.expr.CastExpr;
import com.github.javaparser.ast.expr.ClassExpr;
import com.github.javaparser.ast.expr.ConditionalExpr;
import com.github.javaparser.ast.expr.FieldAccessExpr;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.MethodReferenceExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.NullLiteralExpr;
import com.github.javaparser.ast.expr.StringLiteralExpr;
import com.github.javaparser.ast.expr.ThisExpr;
import com.github.javaparser.ast.expr.TypeExpr;
import com.github.javaparser.ast.expr.VariableDeclarationExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.ReturnStmt;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import org.dmg.pmml.Aggregate;
import org.dmg.pmml.Apply;
import org.dmg.pmml.Constant;
import org.dmg.pmml.Discretize;
import org.dmg.pmml.Expression;
import org.dmg.pmml.FieldRef;
import org.dmg.pmml.Lag;
import org.dmg.pmml.MapValues;
import org.dmg.pmml.NormContinuous;
import org.dmg.pmml.NormDiscrete;
import org.dmg.pmml.TextIndex;
import org.kie.pmml.api.exceptions.KiePMMLException;
import org.kie.pmml.commons.model.tuples.KiePMMLNameValue;

import static com.github.javaparser.StaticJavaParser.parseClassOrInterfaceType;
import static org.kie.pmml.compiler.commons.utils.CommonCodegenUtils.OPTIONAL_FILTERED_KIEPMMLNAMEVALUE_NAME;
import static org.kie.pmml.compiler.commons.utils.CommonCodegenUtils.getFilteredKiePMMLNameValueExpression;
import static org.kie.pmml.compiler.commons.utils.CommonCodegenUtils.getMethodDeclaration;
import static org.kie.pmml.compiler.commons.utils.CommonCodegenUtils.getReturnStmt;
import static org.kie.pmml.compiler.commons.utils.CommonCodegenUtils.getTypedClassOrInterfaceType;

/**
 * Class meant to provide <i>helper</i> methods to retrieve <code>Function</code> code-generators
 * out of <code>Expression</code>s
 */
public class ExpressionFunctionUtils {

    static final String KIEPMMLNAMEVALUE_LIST_PARAM = "param1"; // it is the first parameter
    static final String INNER_VARIABLE_NAME = "variable%s%s%s"; // it is the first parameter
    static final LinkedHashMap<String, ClassOrInterfaceType> DEFAULT_PARAMETERTYPE_MAP;
    static final FieldAccessExpr CONVERTER_TYPE_UTIL_FIELD_ACCESSOR_EXPR;

    static {
        DEFAULT_PARAMETERTYPE_MAP = new LinkedHashMap<>();
        DEFAULT_PARAMETERTYPE_MAP.put(KIEPMMLNAMEVALUE_LIST_PARAM, getTypedClassOrInterfaceType(List.class.getName(),
                                                                                                Collections.singletonList(KiePMMLNameValue.class.getName())));
        CONVERTER_TYPE_UTIL_FIELD_ACCESSOR_EXPR = new FieldAccessExpr();
        CONVERTER_TYPE_UTIL_FIELD_ACCESSOR_EXPR.setName("ConverterTypeUtil");
        CONVERTER_TYPE_UTIL_FIELD_ACCESSOR_EXPR.setScope(new NameExpr("org.kie.pmml.commons.utils"));
    }

    private ExpressionFunctionUtils() {
        // Avoid instantiation
    }

    /**
     * @param methodName
     * @param aggregate
     * @param returnedType
     * @param parameterNameTypeMap enforcing <code>LinkedHashMap</code> since insertion order matter
     * @return
     */
    static MethodDeclaration getAggregatedExpressionMethodDeclaration(final String methodName,
                                                                      final Aggregate aggregate,
                                                                      final ClassOrInterfaceType returnedType,
                                                                      final LinkedHashMap<String,
                                                                              ClassOrInterfaceType> parameterNameTypeMap) {
        throw new KiePMMLException("Aggregate not managed, yet");
    }

    /**
     * For each <code>Expression</code> generates the code to retrieve the value, and then invoke the specified
     * <b>function</b>
     * with the retrieved values.
     * e.g.
     * <pre>
     *    Object Apply10(List<KiePMMLNameValue> param1)  {
     *      Object variableVARIABLE_NAMEConstant1 = 34.6;
     *      Optional<.KiePMMLNameValue> kiePMMLNameValue = param1.stream().filter((KiePMMLNameValue lmbdParam) -> Objects.equals("FIELD_NAME", lmbdParam.getName())).findFirst();
     *      Object variableVARIABLE_NAMEFieldRef2 = kiePMMLNameValue.map(KiePMMLNameValue::getValue).orElse(null);
     *      Object VARIABLE_NAME = this.FUNCTION_NAME(variableVARIABLE_NAMEConstant1, variableVARIABLE_NAMEFieldRef2);
     *    }
     * </pre>
     * @param methodName
     * @param apply
     * @param returnedType
     * @param parameterNameTypeMap enforcing <code>LinkedHashMap</code> since insertion order matter
     * @return
     */
    static MethodDeclaration getApplyExpressionMethodDeclaration(final String methodName,
                                                                 final Apply apply,
                                                                 final ClassOrInterfaceType returnedType,
                                                                 final LinkedHashMap<String, ClassOrInterfaceType> parameterNameTypeMap) {
        String variableName = "applyVariable";
        BlockStmt body = getApplyExpressionBlockStmt(variableName, apply, returnedType,
                                                     parameterNameTypeMap);
        return getExpressionMethodDeclaration(methodName, variableName, body, returnedType, parameterNameTypeMap);
    }

    /**
     * Return
     * <pre>
     *     (<i>returnedType</i>) (<i>methodName</i>)(<i>List<KiePMMLNameValue></i>) {
     *          (<i>constant_type</i>) constantVariable = (<i>constant_value</i>);
     *          return constantVariable;
     * }
     * </pre>
     * e.g.
     * <pre>
     *     double Constant10(java.util.List<org.kie.pmml.commons.model.tuples.KiePMMLNameValue> param1) {
     *          double constantVariable = 34.6;
     *          return constantVariable;
     * }
     * </pre>
     * @param methodName
     * @param constant
     * @param returnedType
     * @param parameterNameTypeMap enforcing <code>LinkedHashMap</code> since insertion order matter
     * @return
     */
    static MethodDeclaration getConstantExpressionMethodDeclaration(final String methodName,
                                                                    final Constant constant,
                                                                    final ClassOrInterfaceType returnedType,
                                                                    final LinkedHashMap<String, ClassOrInterfaceType> parameterNameTypeMap) {
        String variableName = "constantVariable";
        final BlockStmt body = getConstantExpressionBlockStmt(variableName, constant, returnedType,
                                                              parameterNameTypeMap);
        return getExpressionMethodDeclaration(methodName, variableName, body, returnedType, parameterNameTypeMap);
    }

    /**
     * @param methodName
     * @param discretize
     * @param returnedType
     * @param parameterNameTypeMap enforcing <code>LinkedHashMap</code> since insertion order matter
     * @return
     */
    static MethodDeclaration getDiscretizeExpressionMethodDeclaration(final String methodName,
                                                                      final Discretize discretize,
                                                                      final ClassOrInterfaceType returnedType,
                                                                      final LinkedHashMap<String,
                                                                              ClassOrInterfaceType> parameterNameTypeMap) {
        throw new KiePMMLException("Discretize not managed, yet");
    }

    /**
     * Returns
     * <pre>
     * (<i>returnedType</i>)  FieldRef(<i>parameterNameTypeMap</i>)(java.util.List<KiePMMLNameValue> param1) {
     *      Optional<KiePMMLNameValue> kiePMMLNameValue = param1.stream().filter((KiePMMLNameValue lmbdParam) -> Objects.equals(<i>(FieldRef_name)</i>, lmbdParam.getName())).findFirst();
     *      Object fieldRefVariable = kiePMMLNameValue.map(KiePMMLNameValue::getValue).orElse(<i>(FieldRef_mapMissingTo)</i>);
     *      return fieldRefVariable;
     * }
     * </pre>
     * @param methodName
     * @param fieldRef
     * @param returnedType
     * @param parameterNameTypeMap enforcing <code>LinkedHashMap</code> since insertion order matter
     * @return
     */
    static MethodDeclaration getFieldRefExpressionMethodDeclaration(final String methodName,
                                                                    final FieldRef fieldRef,
                                                                    final ClassOrInterfaceType returnedType,
                                                                    final LinkedHashMap<String, ClassOrInterfaceType> parameterNameTypeMap) {
        String variableName = "fieldRefVariable";
        final BlockStmt body;
        if (parameterNameTypeMap.size() == 1) {
            body = getFieldRefExpressionFromCommonDataBlockStmt(variableName, fieldRef, returnedType,
                                                                parameterNameTypeMap);
        } else {
            body = getFieldRefExpressionFromDefineFunctionBlockStmt(variableName, fieldRef, returnedType,
                                                                    parameterNameTypeMap);
        }
        return getExpressionMethodDeclaration(methodName, variableName, body, returnedType, parameterNameTypeMap);
    }

    /**
     * @param methodName
     * @param lag
     * @param returnedType
     * @param parameterNameTypeMap enforcing <code>LinkedHashMap</code> since insertion order matter
     * @return
     */
    static MethodDeclaration getLagExpressionMethodDeclaration(final String methodName,
                                                               final Lag lag,
                                                               final ClassOrInterfaceType returnedType,
                                                               final LinkedHashMap<String, ClassOrInterfaceType> parameterNameTypeMap) {
        throw new KiePMMLException("Lag not managed, yet");
    }

    /**
     * @param methodName
     * @param mapValues
     * @param returnedType
     * @param parameterNameTypeMap enforcing <code>LinkedHashMap</code> since insertion order matter
     * @return
     */
    static MethodDeclaration getMapValuesExpressionMethodDeclaration(final String methodName,
                                                                     final MapValues mapValues,
                                                                     final ClassOrInterfaceType returnedType,
                                                                     final LinkedHashMap<String,
                                                                             ClassOrInterfaceType> parameterNameTypeMap) {
        throw new KiePMMLException("MapValues not managed, yet");
    }

    /**
     * @param methodName
     * @param normContinuous
     * @param returnedType
     * @param parameterNameTypeMap enforcing <code>LinkedHashMap</code> since insertion order matter
     * @return
     */
    static MethodDeclaration getNormContinuousExpressionMethodDeclaration(final String methodName,
                                                                          final NormContinuous normContinuous,
                                                                          final ClassOrInterfaceType returnedType,
                                                                          final LinkedHashMap<String,
                                                                                  ClassOrInterfaceType> parameterNameTypeMap) {
        throw new KiePMMLException("NormContinuous not managed, yet");
    }

    /**
     * @param methodName
     * @param normDiscrete
     * @param returnedType
     * @param parameterNameTypeMap enforcing <code>LinkedHashMap</code> since insertion order matter
     * @return
     */
    static MethodDeclaration getNormDiscreteExpressionMethodDeclaration(final String methodName,
                                                                        final NormDiscrete normDiscrete,
                                                                        final ClassOrInterfaceType returnedType,
                                                                        final LinkedHashMap<String,
                                                                                ClassOrInterfaceType> parameterNameTypeMap) {
        throw new KiePMMLException("NormDiscrete not managed, yet");
    }

    /**
     * @param methodName
     * @param textIndex
     * @param returnedType
     * @param parameterNameTypeMap enforcing <code>LinkedHashMap</code> since insertion order matter
     * @return
     */
    static MethodDeclaration getTextIndexExpressionMethodDeclaration(final String methodName,
                                                                     final TextIndex textIndex,
                                                                     final ClassOrInterfaceType returnedType,
                                                                     final LinkedHashMap<String,
                                                                             ClassOrInterfaceType> parameterNameTypeMap) {
        throw new KiePMMLException("TextIndex not managed, yet");
    }

    /**
     * @param variableName
     * @param expression
     * @param returnedType
     * @param parameterNameTypeMap enforcing <code>LinkedHashMap</code> since insertion order matter
     * @return
     */
    static BlockStmt getExpressionBlockStmt(final String variableName,
                                            final Expression expression,
                                            final ClassOrInterfaceType returnedType,
                                            final LinkedHashMap<String, ClassOrInterfaceType> parameterNameTypeMap) {
        if (expression instanceof Aggregate) {
            return getAggregatedExpressionBlockStmt(variableName, (Aggregate) expression, returnedType,
                                                    parameterNameTypeMap);
        } else if (expression instanceof Apply) {
            return getApplyExpressionBlockStmt(variableName, (Apply) expression, returnedType, parameterNameTypeMap);
        } else if (expression instanceof Constant) {
            return getConstantExpressionBlockStmt(variableName, (Constant) expression, returnedType,
                                                  parameterNameTypeMap);
        } else if (expression instanceof Discretize) {
            return getDiscretizeExpressionBlockStmt(variableName, (Discretize) expression, returnedType,
                                                    parameterNameTypeMap);
        } else if (expression instanceof FieldRef) {
            if (parameterNameTypeMap.size() == 1) {
                return getFieldRefExpressionFromCommonDataBlockStmt(variableName, (FieldRef) expression, returnedType,
                                                                    parameterNameTypeMap);
            } else {
                return getFieldRefExpressionFromDefineFunctionBlockStmt(variableName, (FieldRef) expression, returnedType,
                                                                    parameterNameTypeMap);
            }
        } else if (expression instanceof Lag) {
            return getLagExpressionBlockStmt(variableName, (Lag) expression, returnedType, parameterNameTypeMap);
        } else if (expression instanceof MapValues) {
            return getMapValuesExpressionBlockStmt(variableName, (MapValues) expression, returnedType,
                                                   parameterNameTypeMap);
        } else if (expression instanceof NormContinuous) {
            return getNormContinuousExpressionBlockStmt(variableName, (NormContinuous) expression, returnedType,
                                                        parameterNameTypeMap);
        } else if (expression instanceof NormDiscrete) {
            return getNormDiscreteExpressionBlockStmt(variableName, (NormDiscrete) expression, returnedType,
                                                      parameterNameTypeMap);
        } else if (expression instanceof TextIndex) {
            return getTextIndexExpressionBlockStmt(variableName, (TextIndex) expression, returnedType,
                                                   parameterNameTypeMap);
        } else {
            throw new IllegalArgumentException(String.format("Expression %s not managed", expression.getClass()));
        }
    }

    /**
     * @param variableName
     * @param aggregate
     * @param returnedType
     * @param parameterNameTypeMap enforcing <code>LinkedHashMap</code> since insertion order matter
     * @return
     */
    static BlockStmt getAggregatedExpressionBlockStmt(final String variableName,
                                                      final Aggregate aggregate,
                                                      final ClassOrInterfaceType returnedType,
                                                      final LinkedHashMap<String, ClassOrInterfaceType> parameterNameTypeMap) {
        throw new KiePMMLException("Aggregate not managed, yet");
    }

    /**
     * For each <code>Expression</code> generates the code to retrieve the value, and then invoke the specified
     * <b>function</b>
     * with the retrieved values.
     * e.g.
     * <pre>
     *    {
     *      Object variableVARIABLE_NAMEConstant1 = 34.6;
     *      Optional<.KiePMMLNameValue> kiePMMLNameValue = param1.stream().filter((KiePMMLNameValue lmbdParam) ->
     *          Objects.equals("FIELD_NAME", lmbdParam.getName())).findFirst();
     *      Object variableVARIABLE_NAMEFieldRef2 = kiePMMLNameValue.map(KiePMMLNameValue::getValue).orElse(null);
     *      Object VARIABLE_NAME = this.FUNCTION_NAME(variableVARIABLE_NAMEConstant1, variableVARIABLE_NAMEFieldRef2);
     *    }
     * </pre>
     * @param variableName
     * @param apply
     * @param returnedType
     * @param parameterNameTypeMap enforcing <code>LinkedHashMap</code> since insertion order matter
     * @return
     */
    static BlockStmt getApplyExpressionBlockStmt(final String variableName,
                                                 final Apply apply,
                                                 final ClassOrInterfaceType returnedType,
                                                 final LinkedHashMap<String, ClassOrInterfaceType> parameterNameTypeMap) {
        final BlockStmt toReturn = new BlockStmt();
        List<String> innerVariables = new ArrayList<>();
        innerVariables.add(KIEPMMLNAMEVALUE_LIST_PARAM);
        final ClassOrInterfaceType objectReturnedType = parseClassOrInterfaceType(Object.class.getName());
        if (apply.getExpressions() != null) {
            int counter = 1;
            for (Expression expression : apply.getExpressions()) {
                String innerVariable = String.format(INNER_VARIABLE_NAME, variableName,
                                                     expression.getClass().getSimpleName(), counter);
                BlockStmt innerBlockStmt = getExpressionBlockStmt(innerVariable, expression, objectReturnedType,
                                                                  parameterNameTypeMap);
                toReturn.getStatements().addAll(innerBlockStmt.getStatements());
                innerVariables.add(innerVariable);
                counter++;
            }
        }

        MethodCallExpr functionMethodCall = new MethodCallExpr();
        functionMethodCall.setScope(new ThisExpr());
        functionMethodCall.setName(apply.getFunction());
        NodeList<com.github.javaparser.ast.expr.Expression> functionCallArguments =
                NodeList.nodeList(innerVariables.stream().map(NameExpr::new).collect(Collectors.toList()));
        functionMethodCall.setArguments(functionCallArguments);
        VariableDeclarator variableDeclarator = new VariableDeclarator();
        variableDeclarator.setType(returnedType);
        variableDeclarator.setName(variableName);
        variableDeclarator.setInitializer(functionMethodCall);
        VariableDeclarationExpr variableDeclarationExpr = new VariableDeclarationExpr();
        variableDeclarationExpr.setVariables(NodeList.nodeList(variableDeclarator));
        toReturn.addStatement(variableDeclarationExpr);

        return toReturn;
    }

    /**
     * Return
     * <pre>
     *     (<i>constant_type</i>) (<i>variableName</i>) = (<i>constant_value</i>);
     * </pre>
     * e.g.
     * <pre>
     *     double doubleVar = 34.6;
     * </pre>
     * @param variableName
     * @param constant
     * @param returnedType
     * @param parameterNameTypeMap enforcing <code>LinkedHashMap</code> since insertion order matter
     * @return
     */
    static BlockStmt getConstantExpressionBlockStmt(final String variableName,
                                                    final Constant constant,
                                                    final ClassOrInterfaceType returnedType,
                                                    final LinkedHashMap<String, ClassOrInterfaceType> parameterNameTypeMap) {
        final BlockStmt toReturn = new BlockStmt();
        // Object variableName = kiePMMLNameValue.map(KiePMMLNameValue::getValue).orElse( (fieldRef.getMapMissingTo() )
        VariableDeclarator variableDeclarator = new VariableDeclarator();
        variableDeclarator.setType(returnedType);
        variableDeclarator.setName(variableName);
        final Object constantValue = constant.getValue();
        if (constantValue instanceof String) {
            variableDeclarator.setInitializer(new StringLiteralExpr((String) constantValue));
        } else {
            variableDeclarator.setInitializer(new NameExpr(constantValue.toString()));
        }
        VariableDeclarationExpr variableDeclarationExpr = new VariableDeclarationExpr();
        variableDeclarationExpr.setVariables(NodeList.nodeList(variableDeclarator));
        toReturn.addStatement(variableDeclarationExpr);
        return toReturn;
    }

    /**
     * @param variableName
     * @param discretize
     * @param returnedType
     * @param parameterNameTypeMap enforcing <code>LinkedHashMap</code> since insertion order matter
     * @return
     */
    static BlockStmt getDiscretizeExpressionBlockStmt(final String variableName,
                                                      final Discretize discretize,
                                                      final ClassOrInterfaceType returnedType,
                                                      final LinkedHashMap<String, ClassOrInterfaceType> parameterNameTypeMap) {
        throw new KiePMMLException("Discretize not managed, yet");
    }

    /**
     * Returns
     * <pre>
     *      Optional<KiePMMLNameValue> kiePMMLNameValue = param1.stream().filter((KiePMMLNameValue lmbdParam) ->
     *          Objects.equals(<i>(FieldRef_name)</i>, lmbdParam.getName())).findFirst();
     *      Object (<i>variableName</i>) = kiePMMLNameValue.map(KiePMMLNameValue::getValue).orElse(<i>(FieldRef_mapMissingTo)</i>);
     * </pre>
     * @param variableName
     * @param fieldRef
     * @param returnedType
     * @param parameterNameTypeMap enforcing <code>LinkedHashMap</code> since insertion order matter
     * @return
     */
    static BlockStmt getFieldRefExpressionFromCommonDataBlockStmt(final String variableName,
                                                                  final FieldRef fieldRef,
                                                                  final ClassOrInterfaceType returnedType,
                                                                  final LinkedHashMap<String, ClassOrInterfaceType> parameterNameTypeMap) {
        final BlockStmt toReturn = new BlockStmt();
        String fieldNameToRef = fieldRef.getField().getValue();
        toReturn.addStatement(getFilteredKiePMMLNameValueExpression(KIEPMMLNAMEVALUE_LIST_PARAM,
                                                                    fieldNameToRef,
                                                                    true));

        //KiePMMLNameValue::getValue
        MethodReferenceExpr methodReferenceExpr = new MethodReferenceExpr();
        methodReferenceExpr.setScope(new TypeExpr(parseClassOrInterfaceType(KiePMMLNameValue.class.getName())));
        methodReferenceExpr.setIdentifier("getValue");

        // kiePMMLNameValue.map
        MethodCallExpr expressionScope = new MethodCallExpr("map");
        expressionScope.setScope(new NameExpr(OPTIONAL_FILTERED_KIEPMMLNAMEVALUE_NAME));

        // kiePMMLNameValue.map(KiePMMLNameValue::getValue)
        expressionScope.setArguments(NodeList.nodeList(methodReferenceExpr));

        // kiePMMLNameValue.map(KiePMMLNameValue::getValue).orElse( (fieldRef.getMapMissingTo() )
        MethodCallExpr expression = new MethodCallExpr("orElse");
        expression.setScope(expressionScope);
        com.github.javaparser.ast.expr.Expression orElseExpression = fieldRef.getMapMissingTo() != null ?
                new StringLiteralExpr(fieldRef.getMapMissingTo()) : new NullLiteralExpr();
        expression.setArguments(NodeList.nodeList(orElseExpression));

        // (String) kiePMMLNameValue.map(KiePMMLNameValue::getValue).orElse( (fieldRef.getMapMissingTo() )
        CastExpr initializer = new CastExpr();
        initializer.setType(returnedType);
        initializer.setExpression(expression);

        // String variableName = (String) kiePMMLNameValue.map(KiePMMLNameValue::getValue).orElse( (fieldRef
        // .getMapMissingTo() )
        VariableDeclarator variableDeclarator = new VariableDeclarator();
        variableDeclarator.setType(returnedType);
        variableDeclarator.setName(variableName);
        variableDeclarator.setInitializer(initializer);

        VariableDeclarationExpr variableDeclarationExpr = new VariableDeclarationExpr();
        variableDeclarationExpr.setVariables(NodeList.nodeList(variableDeclarator));
        toReturn.addStatement(variableDeclarationExpr);

        return toReturn;
    }

    /**
     * Returns
     * <pre>
     *      Object (<i>variableName</i>) = (<i>fieldRef_name</i>) != null ? (<i>fieldRef_name</i>) : (<i>fieldRef_missingvalue</i>);
     * </pre>
     * @param variableName
     * @param fieldRef
     * @param returnedType
     * @param parameterNameTypeMap enforcing <code>LinkedHashMap</code> since insertion order matter
     * @return
     */
    static BlockStmt getFieldRefExpressionFromDefineFunctionBlockStmt(final String variableName,
                                                                      final FieldRef fieldRef,
                                                                      final ClassOrInterfaceType returnedType,
                                                                      final LinkedHashMap<String,
                                                                              ClassOrInterfaceType> parameterNameTypeMap) {
        final BlockStmt toReturn = new BlockStmt();
        String fieldNameToRef = fieldRef.getField().getValue();

        // field_ref
        NameExpr nameExpression = new NameExpr(fieldNameToRef);

        // condition
        BinaryExpr condition = new BinaryExpr();
        condition.setLeft(nameExpression);
        condition.setRight(new NullLiteralExpr());
        condition.setOperator(BinaryExpr.Operator.NOT_EQUALS);
        // then
        CastExpr thenExpr = new CastExpr();
        thenExpr.setType(returnedType);
        // ((returnedType)) org.kie.pmml.commons.utils.ConverterTypeUtil.convert((returnedType).class, (variable_name))
        MethodCallExpr converterCallExpression = new MethodCallExpr();
        converterCallExpression.setName("convert");
        converterCallExpression.setScope(CONVERTER_TYPE_UTIL_FIELD_ACCESSOR_EXPR);
        ClassExpr expectedClassExpression = new ClassExpr();
        expectedClassExpression.setType(returnedType);
        converterCallExpression.setArguments(NodeList.nodeList(expectedClassExpression, nameExpression));
        thenExpr.setExpression(converterCallExpression);
        // else
        com.github.javaparser.ast.expr.Expression elseExpression = fieldRef.getMapMissingTo() != null ?
                new StringLiteralExpr(fieldRef.getMapMissingTo()) : new NullLiteralExpr();
        CastExpr elseExpr = new CastExpr();
        elseExpr.setType(returnedType);
        elseExpr.setExpression(elseExpression);

        ConditionalExpr initializer = new ConditionalExpr();
        initializer.setCondition(condition);
        initializer.setThenExpr(thenExpr);
        initializer.setElseExpr(elseExpr);

        // (returnedType) (variableName) = (returnedType) field_ref;
        VariableDeclarator variableDeclarator = new VariableDeclarator();
        variableDeclarator.setType(returnedType);
        variableDeclarator.setName(variableName);
        variableDeclarator.setInitializer(initializer);

        VariableDeclarationExpr variableDeclarationExpr = new VariableDeclarationExpr();
        variableDeclarationExpr.setVariables(NodeList.nodeList(variableDeclarator));
        toReturn.addStatement(variableDeclarationExpr);

        return toReturn;
    }

    /**
     * @param variableName
     * @param lag
     * @param returnedType
     * @param parameterNameTypeMap enforcing <code>LinkedHashMap</code> since insertion order matter
     * @return
     */
    static BlockStmt getLagExpressionBlockStmt(final String variableName,
                                               final Lag lag,
                                               final ClassOrInterfaceType returnedType,
                                               final LinkedHashMap<String, ClassOrInterfaceType> parameterNameTypeMap) {
        throw new KiePMMLException("Lag not managed, yet");
    }

    /**
     * @param variableName
     * @param mapValues
     * @param returnedType
     * @param parameterNameTypeMap enforcing <code>LinkedHashMap</code> since insertion order matter
     * @return
     */
    static BlockStmt getMapValuesExpressionBlockStmt(final String variableName,
                                                     final MapValues mapValues,
                                                     final ClassOrInterfaceType returnedType,
                                                     final LinkedHashMap<String, ClassOrInterfaceType> parameterNameTypeMap) {
        throw new KiePMMLException("MapValues not managed, yet");
    }

    /**
     * @param variableName
     * @param normContinuous
     * @param returnedType
     * @param parameterNameTypeMap enforcing <code>LinkedHashMap</code> since insertion order matter
     * @return
     */
    static BlockStmt getNormContinuousExpressionBlockStmt(final String variableName,
                                                          final NormContinuous normContinuous,
                                                          final ClassOrInterfaceType returnedType,
                                                          final LinkedHashMap<String, ClassOrInterfaceType> parameterNameTypeMap) {
        throw new KiePMMLException("NormContinuous not managed, yet");
    }

    /**
     * @param variableName
     * @param normDiscrete
     * @param returnedType
     * @param parameterNameTypeMap enforcing <code>LinkedHashMap</code> since insertion order matter
     * @return
     */
    static BlockStmt getNormDiscreteExpressionBlockStmt(final String variableName,
                                                        final NormDiscrete normDiscrete,
                                                        final ClassOrInterfaceType returnedType,
                                                        final LinkedHashMap<String, ClassOrInterfaceType> parameterNameTypeMap) {
        throw new KiePMMLException("NormDiscrete not managed, yet");
    }

    /**
     * @param variableName
     * @param textIndex
     * @param returnedType
     * @param parameterNameTypeMap enforcing <code>LinkedHashMap</code> since insertion order matter
     * @return
     */
    static BlockStmt getTextIndexExpressionBlockStmt(final String variableName,
                                                     final TextIndex textIndex,
                                                     final ClassOrInterfaceType returnedType,
                                                     final LinkedHashMap<String, ClassOrInterfaceType> parameterNameTypeMap) {
        throw new KiePMMLException("TextIndex not managed, yet");
    }

    /**
     * Return
     * <pre>
     *     (<i>returnedType</i>) (<i>methodName</i>)(List<KiePMMLNameValue> <i>param name</i>>) {
     *              <i>body</i>
     *              return <i>variableName</i>;
     *     }
     *
     * @param methodName
     * @param variableName
     * @param body
     * @param returnedType
     * @param parameterNameTypeMap enforcing <code>LinkedHashMap</code> since insertion order matter
     * @return
     */
    static MethodDeclaration getExpressionMethodDeclaration(final String methodName, final String variableName,
                                                            final BlockStmt body,
                                                            final ClassOrInterfaceType returnedType,
                                                            final LinkedHashMap<String, ClassOrInterfaceType> parameterNameTypeMap) {
        final ReturnStmt returnStmt = getReturnStmt(variableName);
        body.addStatement(returnStmt);
        MethodDeclaration toReturn = getMethodDeclaration(methodName, parameterNameTypeMap);
        toReturn.setType(returnedType);
        toReturn.setBody(body);
        return toReturn;
    }
}
