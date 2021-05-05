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

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.BinaryExpr;
import com.github.javaparser.ast.expr.CastExpr;
import com.github.javaparser.ast.expr.ClassExpr;
import com.github.javaparser.ast.expr.ConditionalExpr;
import com.github.javaparser.ast.expr.FieldAccessExpr;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.NullLiteralExpr;
import com.github.javaparser.ast.expr.SimpleName;
import com.github.javaparser.ast.expr.StringLiteralExpr;
import com.github.javaparser.ast.expr.ThisExpr;
import com.github.javaparser.ast.expr.VariableDeclarationExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.ExpressionStmt;
import com.github.javaparser.ast.stmt.ReturnStmt;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import org.dmg.pmml.Aggregate;
import org.dmg.pmml.Apply;
import org.dmg.pmml.Constant;
import org.dmg.pmml.DataType;
import org.dmg.pmml.Discretize;
import org.dmg.pmml.Expression;
import org.dmg.pmml.FieldRef;
import org.dmg.pmml.Lag;
import org.dmg.pmml.MapValues;
import org.dmg.pmml.NormContinuous;
import org.dmg.pmml.NormDiscrete;
import org.dmg.pmml.ParameterField;
import org.dmg.pmml.TextIndex;
import org.kie.pmml.api.exceptions.KiePMMLException;
import org.kie.pmml.api.utils.ConverterTypeUtil;
import org.kie.pmml.commons.model.tuples.KiePMMLNameValue;

import static com.github.javaparser.StaticJavaParser.parseClassOrInterfaceType;
import static org.kie.pmml.commons.Constants.MISSING_BODY_TEMPLATE;
import static org.kie.pmml.commons.Constants.MISSING_RETURN_IN_METHOD;
import static org.kie.pmml.commons.Constants.MISSING_VARIABLE_INITIALIZER_TEMPLATE;
import static org.kie.pmml.commons.Constants.MISSING_VARIABLE_IN_BODY;
import static org.kie.pmml.compiler.commons.utils.CommonCodegenUtils.getTypedClassOrInterfaceType;
import static org.kie.pmml.compiler.commons.utils.JavaParserUtils.MAIN_CLASS_NOT_FOUND;
import static org.kie.pmml.compiler.commons.utils.ModelUtils.getBoxedClassName;

/**
 * Class meant to provide <i>helper</i> methods to retrieve <code>Function</code> code-generators
 * out of <code>Expression</code>s
 */
public class ExpressionFunctionUtils {

    public static final String APPLY_VARIABLE = "applyVariable";
    public static final String CONSTANT_VALUE = "constantValue";
    public static final String KIEPMMLNAMEVALUE = "kiePMMLNameValue";
    public static final String FIELDREFVARIABLE = "fieldRefVariable";

    static final String EXPRESSION_FUNCTION_UTILS_TEMPLATE_JAVA = "ExpressionFunctionUtilsTemplate.tmpl";
    static final String EXPRESSION_FUNCTION_UTILS_TEMPLATE = "ExpressionFunctionUtilsTemplate";
    static final ClassOrInterfaceDeclaration EXPRESSION_TEMPLATE;

    static final String KIEPMMLNAMEVALUE_LIST_PARAM = "param1"; // it is the first parameter
    static final String INNER_VARIABLE_NAME = "variable%s%s%s"; // it is the first parameter
    static final LinkedHashMap<String, ClassOrInterfaceType> DEFAULT_PARAMETERTYPE_MAP;
    static final FieldAccessExpr CONVERTER_TYPE_UTIL_FIELD_ACCESSOR_EXPR;

    private static final String FIELDREFEXPRESSIONFROMKIEPMMLNAMEVALUESTEMPLATE =
            "fieldRefExpressionFromKiePMMLNameValuesTemplate";
    private static final String FIELDREFEXPRESSIONFROMINPUTVALUETEMPLATE = "fieldRefExpressionFromInputValueTemplate";

    private static final String CONSTANTEXPRESSIONTEMPLATE = "constantExpressionTemplate";
    private static final String METHODDECLARATIONKIEPMMLNAMEVALUETEMPLATE = "methodDeclarationKiePMMLNameValueTemplate";
    private static final String EXPRESSION_NOT_MANAGED = "Expression %s not managed";

    static {
        DEFAULT_PARAMETERTYPE_MAP = new LinkedHashMap<>();
        DEFAULT_PARAMETERTYPE_MAP.put(KIEPMMLNAMEVALUE_LIST_PARAM, getTypedClassOrInterfaceType(List.class.getName(),
                                                                                                Collections.singletonList(KiePMMLNameValue.class.getName())));
        CONVERTER_TYPE_UTIL_FIELD_ACCESSOR_EXPR = new FieldAccessExpr();
        String converterTypeUtilFullName = ConverterTypeUtil.class.getName();
        CONVERTER_TYPE_UTIL_FIELD_ACCESSOR_EXPR.setName(converterTypeUtilFullName.substring(converterTypeUtilFullName.lastIndexOf('.') + 1));
        CONVERTER_TYPE_UTIL_FIELD_ACCESSOR_EXPR.setScope(new NameExpr(converterTypeUtilFullName.substring(0,
                                                                                                          converterTypeUtilFullName.lastIndexOf('.'))));

        CompilationUnit cloneCU = JavaParserUtils.getFromFileName(EXPRESSION_FUNCTION_UTILS_TEMPLATE_JAVA);
        EXPRESSION_TEMPLATE = cloneCU.getClassByName(EXPRESSION_FUNCTION_UTILS_TEMPLATE)
                .orElseThrow(() -> new KiePMMLException(MAIN_CLASS_NOT_FOUND + ": " + EXPRESSION_FUNCTION_UTILS_TEMPLATE));
    }

    private ExpressionFunctionUtils() {
        // Avoid instantiation
    }

    /**
     * Return a <code>MethodDeclaration</code> with <code>List&lt;KiePMMLNameValue&gt; param1</code> as parameter
     * <p>
     * e.g.
     * <pre>
     *    _dataType_ _methodName_(java.util.List<org.kie.pmml.commons.model.tuples.KiePMMLNameValue> param1)  {
     *      ...
     *    }
     * </pre>
     * @param expression
     * @param dataType
     * @param methodName
     * @return
     */
    public static MethodDeclaration getKiePMMLNameValueExpressionMethodDeclaration(final Expression expression,
                                                                                   final DataType dataType,
                                                                                   final String methodName) {
        final ClassOrInterfaceType returnedType = parseClassOrInterfaceType(getBoxedClassName(dataType));
        if (expression instanceof Aggregate) {
            return getAggregatedExpressionMethodDeclaration(methodName, (Aggregate) expression, returnedType,
                                                            DEFAULT_PARAMETERTYPE_MAP);
        } else if (expression instanceof Apply) {
            return getApplyExpressionMethodDeclaration(methodName, (Apply) expression, returnedType,
                                                       DEFAULT_PARAMETERTYPE_MAP);
        } else if (expression instanceof Constant) {
            return getConstantExpressionMethodDeclaration(methodName, (Constant) expression, returnedType,
                                                          DEFAULT_PARAMETERTYPE_MAP);
        } else if (expression instanceof Discretize) {
            return getDiscretizeExpressionMethodDeclaration(methodName, (Discretize) expression, returnedType,
                                                            DEFAULT_PARAMETERTYPE_MAP);
        } else if (expression instanceof FieldRef) {
            return getFieldRefExpressionMethodDeclaration(methodName, (FieldRef) expression, returnedType,
                                                          DEFAULT_PARAMETERTYPE_MAP);
        } else if (expression instanceof Lag) {
            return getLagExpressionMethodDeclaration(methodName, (Lag) expression, returnedType,
                                                     DEFAULT_PARAMETERTYPE_MAP);
        } else if (expression instanceof MapValues) {
            return getMapValuesExpressionMethodDeclaration(methodName, (MapValues) expression, returnedType,
                                                           DEFAULT_PARAMETERTYPE_MAP);
        } else if (expression instanceof NormContinuous) {
            return getNormContinuousExpressionMethodDeclaration(methodName, (NormContinuous) expression, returnedType
                    , DEFAULT_PARAMETERTYPE_MAP);
        } else if (expression instanceof NormDiscrete) {
            return getNormDiscreteExpressionMethodDeclaration(methodName, (NormDiscrete) expression, returnedType,
                                                              DEFAULT_PARAMETERTYPE_MAP);
        } else if (expression instanceof TextIndex) {
            return getTextIndexExpressionMethodDeclaration(methodName, (TextIndex) expression, returnedType,
                                                           DEFAULT_PARAMETERTYPE_MAP);
        } else {
            throw new IllegalArgumentException(String.format(EXPRESSION_NOT_MANAGED, expression.getClass()));
        }
    }

    /**
     * Return a <code>MethodDeclaration</code> with parameters retrieved from <b>parameterFields</b>
     * <p>
     * e.g.
     * <pre>
     *    _dataType_ _methodName_(java.util.Map<String, Object> param1, Object param2)  {
     *      ...
     *    }
     * </pre>
     * @param methodName
     * @param expression
     * @param dataType
     * @param parameterFields
     * @return
     */
    public static MethodDeclaration getVariableParametersExpressionMethodDeclaration(final String methodName,
                                                                                     final Expression expression,
                                                                                     final DataType dataType,
                                                                                     final List<ParameterField> parameterFields) {
        final ClassOrInterfaceType returnedType = parseClassOrInterfaceType(getBoxedClassName(dataType));
        final LinkedHashMap<String, ClassOrInterfaceType> parameterNameTypeMap =
                new LinkedHashMap<>(DEFAULT_PARAMETERTYPE_MAP); // Must be an ordered Map
        parameterNameTypeMap.putAll(getNameClassOrInterfaceTypeMap(parameterFields));
        if (expression instanceof Aggregate) {
            return getAggregatedExpressionMethodDeclaration(methodName, (Aggregate) expression, returnedType,
                                                            parameterNameTypeMap);
        } else if (expression instanceof Apply) {
            return getApplyExpressionMethodDeclaration(methodName, (Apply) expression, returnedType,
                                                       parameterNameTypeMap);
        } else if (expression instanceof Constant) {
            return getConstantExpressionMethodDeclaration(methodName, (Constant) expression, returnedType,
                                                          parameterNameTypeMap);
        } else if (expression instanceof Discretize) {
            return getDiscretizeExpressionMethodDeclaration(methodName, (Discretize) expression, returnedType,
                                                            parameterNameTypeMap);
        } else if (expression instanceof FieldRef) {
            return getFieldRefExpressionMethodDeclaration(methodName, (FieldRef) expression, returnedType,
                                                          parameterNameTypeMap);
        } else if (expression instanceof Lag) {
            return getLagExpressionMethodDeclaration(methodName, (Lag) expression, returnedType, parameterNameTypeMap);
        } else if (expression instanceof MapValues) {
            return getMapValuesExpressionMethodDeclaration(methodName, (MapValues) expression, returnedType,
                                                           parameterNameTypeMap);
        } else if (expression instanceof NormContinuous) {
            return getNormContinuousExpressionMethodDeclaration(methodName, (NormContinuous) expression, returnedType,
                                                                parameterNameTypeMap);
        } else if (expression instanceof NormDiscrete) {
            return getNormDiscreteExpressionMethodDeclaration(methodName, (NormDiscrete) expression, returnedType,
                                                              parameterNameTypeMap);
        } else if (expression instanceof TextIndex) {
            return getTextIndexExpressionMethodDeclaration(methodName, (TextIndex) expression, returnedType,
                                                           parameterNameTypeMap);
        } else {
            throw new IllegalArgumentException(String.format(EXPRESSION_NOT_MANAGED, expression.getClass()));
        }
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
                                                                        final LinkedHashMap<String,
                                                                                ClassOrInterfaceType> parameterNameTypeMap) {
        String variableName = APPLY_VARIABLE;
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
     * @return
     */
    static MethodDeclaration getConstantExpressionMethodDeclaration(final String methodName,
                                                                           final Constant constant,
                                                                           final ClassOrInterfaceType returnedType,
                                                                           final LinkedHashMap<String,
                                                                                   ClassOrInterfaceType> parameterNameTypeMap) {

        final BlockStmt body = getConstantExpressionBlockStmt(CONSTANT_VALUE, constant, returnedType);
        return getExpressionMethodDeclaration(methodName, CONSTANT_VALUE, body, returnedType, parameterNameTypeMap);
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
                                                                           final LinkedHashMap<String,
                                                                                   ClassOrInterfaceType> parameterNameTypeMap) {
        String variableName = FIELDREFVARIABLE;
        final BlockStmt body;
        if (parameterNameTypeMap.size() == 1) {
            body = getFieldRefExpressionFromKiePMMLNameValuesBlockStmt(variableName, fieldRef, returnedType);
        } else {
            body = getFieldRefExpressionFromInputValueBlockStmt(variableName, fieldRef, returnedType);
        }
        return getExpressionMethodDeclaration(methodName, variableName, body, returnedType, parameterNameTypeMap);
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
            return getConstantExpressionBlockStmt(variableName, (Constant) expression, returnedType);
        } else if (expression instanceof Discretize) {
            return getDiscretizeExpressionBlockStmt(variableName, (Discretize) expression, returnedType,
                                                    parameterNameTypeMap);
        } else if (expression instanceof FieldRef) {
            if (parameterNameTypeMap.size() == 1) {
                return getFieldRefExpressionFromKiePMMLNameValuesBlockStmt(variableName, (FieldRef) expression,
                                                                           returnedType);
            } else {
                return getFieldRefExpressionFromInputValueBlockStmt(variableName, (FieldRef) expression,
                                                                    returnedType);
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
            throw new IllegalArgumentException(String.format(EXPRESSION_NOT_MANAGED, expression.getClass()));
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
     * @return
     */
    static BlockStmt getConstantExpressionBlockStmt(final String variableName,
                                                    final Constant constant,
                                                    final ClassOrInterfaceType returnedType) {
        final MethodDeclaration methodDeclaration = EXPRESSION_TEMPLATE
                .getMethodsByName(CONSTANTEXPRESSIONTEMPLATE)
                .get(0)
                .clone();
        final BlockStmt toReturn = methodDeclaration.getBody()
                .orElseThrow(() -> new KiePMMLException(String.format(MISSING_BODY_TEMPLATE, methodDeclaration)));

        final Object constantValue = constant.getValue();
        com.github.javaparser.ast.expr.Expression initializer;
        if (constantValue instanceof String) {
            initializer = new StringLiteralExpr((String) constantValue);
        } else {
            initializer = new NameExpr(constantValue.toString());
        }
        final VariableDeclarator variableDeclarator = CommonCodegenUtils.getVariableDeclarator(toReturn, CONSTANT_VALUE)
                .orElseThrow(() -> new KiePMMLException(String.format(MISSING_VARIABLE_IN_BODY, CONSTANT_VALUE,
                                                                      toReturn)));
        variableDeclarator.setName(new SimpleName(variableName));
        variableDeclarator.setType(returnedType);
        variableDeclarator.setInitializer(initializer);
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
     * This method is created when the actual value of the referred field must be looked upn inside <code>List&lt;
     * KiePMMLNameValue&gt;</code> parameter.
     * Returns
     * <pre>
     *      Optional<KiePMMLNameValue> kiePMMLNameValue = param1.stream().filter((KiePMMLNameValue lmbdParam) ->
     *          Objects.equals(<i>(FieldRef_name)</i>, lmbdParam.getName())).findFirst();
     *      Object (<i>variableName</i>) = kiePMMLNameValue.map(KiePMMLNameValue::getValue).orElse(<i>(FieldRef_mapMissingTo)</i>);
     * </pre>
     * @param variableName
     * @param fieldRef
     * @param returnedType
     * @return
     */
    static BlockStmt getFieldRefExpressionFromKiePMMLNameValuesBlockStmt(final String variableName,
                                                                         final FieldRef fieldRef,
                                                                         final ClassOrInterfaceType returnedType) {
        final MethodDeclaration methodDeclaration = EXPRESSION_TEMPLATE
                .getMethodsByName(FIELDREFEXPRESSIONFROMKIEPMMLNAMEVALUESTEMPLATE)
                .get(0)
                .clone();
        final BlockStmt toReturn = methodDeclaration.getBody()
                .orElseThrow(() -> new KiePMMLException(String.format(MISSING_BODY_TEMPLATE, methodDeclaration)));
        final VariableDeclarator optionalKiePMMLNameValue = CommonCodegenUtils
                .getVariableDeclarator(toReturn, KIEPMMLNAMEVALUE)
                .orElseThrow(() -> new KiePMMLException(String.format(MISSING_VARIABLE_IN_BODY, KIEPMMLNAMEVALUE,
                                                                      toReturn)));
        final MethodCallExpr optionalKiePMMLNameValueInitializer = optionalKiePMMLNameValue.getInitializer()
                .orElseThrow(() -> new KiePMMLException(String.format(MISSING_VARIABLE_INITIALIZER_TEMPLATE,
                                                                      KIEPMMLNAMEVALUE, toReturn)))
                .asMethodCallExpr();

        final ExpressionStmt expressionStmt = optionalKiePMMLNameValueInitializer.findAll(ExpressionStmt.class).get(0);
        final MethodCallExpr methodCallExpr = expressionStmt.getExpression().asMethodCallExpr();
        String fieldNameToRef = fieldRef.getField().getValue();
        final NameExpr nameExpr = new NameExpr(String.format("\"%s\"", fieldNameToRef));
        methodCallExpr.getArguments().set(0, nameExpr);
        final VariableDeclarator variableDeclarator = CommonCodegenUtils
                .getVariableDeclarator(toReturn, FIELDREFVARIABLE)
                .orElseThrow(() -> new KiePMMLException(String.format(MISSING_VARIABLE_IN_BODY, FIELDREFVARIABLE,
                                                                      toReturn)));
        variableDeclarator.setType(returnedType);
        variableDeclarator.setName(variableName);
        final CastExpr initializer = variableDeclarator.getInitializer()
                .orElseThrow(() -> new KiePMMLException(String.format(MISSING_VARIABLE_INITIALIZER_TEMPLATE,
                                                                      FIELDREFVARIABLE, toReturn)))
                .asCastExpr();
        initializer.setType(returnedType);
        final MethodCallExpr expression = initializer.getExpression().asMethodCallExpr();
        com.github.javaparser.ast.expr.Expression orElseExpression = fieldRef.getMapMissingTo() != null ?
                new StringLiteralExpr(fieldRef.getMapMissingTo()) : new NullLiteralExpr();
        expression.setArguments(NodeList.nodeList(orElseExpression));
        return toReturn;
    }

    /**
     * This method is created when the actual value of the referred field is passed as parameter.
     * If such value is not null, it is (eventually casted) assigned to the returned variable, otherwise the
     * <b>missing value</b>.
     * <p>
     * Returns
     * <pre>
     *      Object (<i>variableName</i>) = (<i>referred_field_value</i>) != null ? (<i>referred_field_value</i>) : (<i>fieldRef_missingvalue</i>);
     * </pre>
     * @param variableName
     * @param fieldRef
     * @param returnedType
     * @return
     */
    static BlockStmt getFieldRefExpressionFromInputValueBlockStmt(final String variableName,
                                                                  final FieldRef fieldRef,
                                                                  final ClassOrInterfaceType returnedType) {
        final MethodDeclaration methodDeclaration = EXPRESSION_TEMPLATE
                .getMethodsByName(FIELDREFEXPRESSIONFROMINPUTVALUETEMPLATE)
                .get(0)
                .clone();
        final BlockStmt toReturn = methodDeclaration.getBody()
                .orElseThrow(() -> new KiePMMLException(String.format(MISSING_BODY_TEMPLATE, methodDeclaration)));
        String fieldNameToRef = fieldRef.getField().getValue();
        final VariableDeclarator variableDeclarator = CommonCodegenUtils
                .getVariableDeclarator(toReturn, FIELDREFVARIABLE)
                .orElseThrow(() -> new KiePMMLException(String.format(MISSING_VARIABLE_IN_BODY, FIELDREFVARIABLE,
                                                                      toReturn)));
        variableDeclarator.setType(returnedType);
        variableDeclarator.setName(variableName);
        final ConditionalExpr initializer = variableDeclarator.getInitializer()
                .orElseThrow(() -> new KiePMMLException(String.format(MISSING_VARIABLE_INITIALIZER_TEMPLATE,
                                                                      FIELDREFVARIABLE, toReturn)))
                .asConditionalExpr();
        final BinaryExpr condition = initializer.getCondition().asBinaryExpr();
        // condition
        // field_ref
        NameExpr nameExpression = new NameExpr(fieldNameToRef);
        condition.setLeft(nameExpression);
        // then
        final CastExpr thenExpr = initializer.getThenExpr().asCastExpr();
        thenExpr.setType(returnedType);
        final MethodCallExpr converterCallExpression = thenExpr.getExpression().asMethodCallExpr();
        final ClassExpr expectedClassExpression = new ClassExpr();
        expectedClassExpression.setType(returnedType);
        converterCallExpression.setArguments(NodeList.nodeList(expectedClassExpression, nameExpression));
        // else
        final CastExpr elseExpr = initializer.getElseExpr().asCastExpr();
        com.github.javaparser.ast.expr.Expression elseExpression = fieldRef.getMapMissingTo() != null ?
                new StringLiteralExpr(fieldRef.getMapMissingTo()) : new NullLiteralExpr();
        elseExpr.setType(returnedType);
        elseExpr.setExpression(elseExpression);
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
    static MethodDeclaration getExpressionMethodDeclaration(final String methodName,
                                                            final String variableName,
                                                            final BlockStmt body,
                                                            final ClassOrInterfaceType returnedType,
                                                            final LinkedHashMap<String, ClassOrInterfaceType> parameterNameTypeMap) {
        final MethodDeclaration toReturn =
                EXPRESSION_TEMPLATE.getMethodsByName(METHODDECLARATIONKIEPMMLNAMEVALUETEMPLATE).get(0).clone();
        final NodeList<Parameter> typeParameters = new NodeList<>();
        parameterNameTypeMap.forEach((parameterName, classOrInterfaceType) -> {
            Parameter toAdd = new Parameter();
            toAdd.setName(parameterName);
            toAdd.setType(classOrInterfaceType);
            typeParameters.add(toAdd);
        });
        toReturn.setParameters(typeParameters);

        final ReturnStmt returnStmt = toReturn.getBody()
                .orElseThrow(() -> new KiePMMLException(String.format(MISSING_BODY_TEMPLATE, toReturn)))
                .findFirst(ReturnStmt.class)
                .orElseThrow(() -> new KiePMMLException(String.format(MISSING_RETURN_IN_METHOD, toReturn)));
        returnStmt.setExpression(new NameExpr(variableName));
        body.addStatement(returnStmt);
        toReturn.setName(methodName);
        toReturn.setType(returnedType);
        toReturn.setBody(body);
        return toReturn;
    }

    /**
     * Create a <code>LinkedHashMap&lt;String, ClassOrInterfaceType&gt;</code> out of the given <code>List&lt;
     * ParameterField&gt;</code>
     * @param parameterNameTypeMap
     * @return
     */
    static LinkedHashMap<String, ClassOrInterfaceType> getNameClassOrInterfaceTypeMap(final List<ParameterField> parameterNameTypeMap) {
        final LinkedHashMap<String, ClassOrInterfaceType> toReturn = new LinkedHashMap<>();
        if (parameterNameTypeMap != null) {
            parameterNameTypeMap.forEach(parameterField -> toReturn.put(parameterField.getName().toString(), parseClassOrInterfaceType(getBoxedClassName(parameterField))));
        }
        return toReturn;
    }
}
