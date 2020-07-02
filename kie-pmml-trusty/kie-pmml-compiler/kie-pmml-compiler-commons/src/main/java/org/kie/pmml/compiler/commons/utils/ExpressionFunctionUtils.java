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

import java.util.List;

import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.MethodReferenceExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.NullLiteralExpr;
import com.github.javaparser.ast.expr.StringLiteralExpr;
import com.github.javaparser.ast.expr.TypeExpr;
import com.github.javaparser.ast.expr.VariableDeclarationExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.ExpressionStmt;
import com.github.javaparser.ast.stmt.ReturnStmt;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import org.dmg.pmml.Aggregate;
import org.dmg.pmml.Apply;
import org.dmg.pmml.Constant;
import org.dmg.pmml.Discretize;
import org.dmg.pmml.FieldRef;
import org.dmg.pmml.Lag;
import org.dmg.pmml.MapValues;
import org.dmg.pmml.NormContinuous;
import org.dmg.pmml.NormDiscrete;
import org.dmg.pmml.TextIndex;
import org.kie.pmml.commons.exceptions.KiePMMLException;
import org.kie.pmml.commons.model.enums.DATA_TYPE;
import org.kie.pmml.commons.model.tuples.KiePMMLNameValue;

import static com.github.javaparser.StaticJavaParser.parseClassOrInterfaceType;
import static org.kie.pmml.commons.utils.PrimitiveBoxedUtils.getKiePMMLPrimitiveBoxed;
import static org.kie.pmml.compiler.commons.utils.CommonCodegenUtils.OPTIONAL_FILTERED_KIEPMMLNAMEVALUE_NAME;
import static org.kie.pmml.compiler.commons.utils.CommonCodegenUtils.getFilteredKiePMMLNameValueExpression;
import static org.kie.pmml.compiler.commons.utils.CommonCodegenUtils.getMethodDeclaration;
import static org.kie.pmml.compiler.commons.utils.CommonCodegenUtils.getReturnStmt;

/**
 * Class meant to provide <i>helper</i> methods to retrieve <code>Function</code> code-generators
 * out of <code>Expression</code>s
 */
public class ExpressionFunctionUtils {

    static final String KIEPMMLNAMEVALUE_LIST_PARAM = "param1"; // it is the first parameter

    private ExpressionFunctionUtils() {
        // Avoid instantiation
    }

    /**
     * @param methodName
     * @param aggregate
     * @param parameterTypes
     * @return
     */
    static MethodDeclaration getAggregatedExpressionMethodDeclaration(final String methodName, final Aggregate aggregate, final List<ClassOrInterfaceType> parameterTypes) {
        throw new KiePMMLException("Aggregate not managed, yet");
    }

    /**
     * @param methodName
     * @param apply
     * @param parameterTypes
     * @return
     */
    static MethodDeclaration getApplyExpressionMethodDeclaration(final String methodName, final Apply apply, final List<ClassOrInterfaceType> parameterTypes) {
        throw new KiePMMLException("Apply not managed, yet");
    }

    /**
     * Return
     * <pre>
     *     (<i>constant_type</i>) (<i>methodName</i>)(List<KiePMMLNameValue> param1) {
     *          (<i>constant_type</i>) constantVariable = (<i>constant_value</i>);
     *          return constantVariable;
     * }
     * </pre>
     * e.g.
     * <pre>
     *     double constant10(java.util.List<org.kie.pmml.commons.model.tuples.KiePMMLNameValue> param1) {
     *          double constantVariable = 34.6;
     *          return constantVariable;
     * }
     * </pre>
     * @param methodName
     * @param constant
     * @param parameterTypes
     * @return
     */
    static MethodDeclaration getConstantExpressionMethodDeclaration(final String methodName, final Constant constant, final List<ClassOrInterfaceType> parameterTypes) {
        String variableName = "constantVariable";
        Class<?> returnedType = constant.getDataType() != null ? DATA_TYPE.byName(constant.getDataType().value()).getMappedClass() : constant.getValue().getClass();
        String eventuallyBoxedClass = getKiePMMLPrimitiveBoxed(returnedType).map(primitiveBoxed -> primitiveBoxed.getBoxed().getName()).orElse(returnedType.getName());
        ClassOrInterfaceType classOrInterfaceType = parseClassOrInterfaceType(eventuallyBoxedClass);
        final BlockStmt body = getConstantExpressionBlockStmt(variableName, constant, classOrInterfaceType, parameterTypes);
        final ReturnStmt returnStmt = getReturnStmt(variableName);
        body.addStatement(returnStmt);
        MethodDeclaration toReturn = getExpressionMethodDeclaration(methodName, parameterTypes);
        toReturn.setType(classOrInterfaceType);
        toReturn.setBody(body);
        return toReturn;
    }

    /**
     * @param methodName
     * @param discretize
     * @param parameterTypes
     * @return
     */
    static MethodDeclaration getDiscretizeExpressionMethodDeclaration(final String methodName, final Discretize discretize, final List<ClassOrInterfaceType> parameterTypes) {
        throw new KiePMMLException("Discretize not managed, yet");
    }

    /**
     * Returns
     * <pre>
     * Object FieldRef(<i>methodArity</i>)(java.util.List<KiePMMLNameValue> param1) {
     *      Optional<KiePMMLNameValue> kiePMMLNameValue = param1.stream().filter((KiePMMLNameValue lmbdParam) -> Objects.equals(<i>(FieldRef_name)</i>, lmbdParam.getName())).findFirst();
     *      Object fieldRefVariable = kiePMMLNameValue.map(KiePMMLNameValue::getValue).orElse(<i>(FieldRef_mapMissingTo)</i>);
     *      return fieldRefVariable;
     * }
     * </pre>
     * @param methodName
     * @param fieldRef
     * @param parameterTypes
     * @return
     */
    static MethodDeclaration getFieldRefExpressionMethodDeclaration(final String methodName, final FieldRef fieldRef, final List<ClassOrInterfaceType> parameterTypes) {
        String variableName = "fieldRefVariable";
        final BlockStmt body = getFieldRefExpressionBlockStmt(variableName, fieldRef, parameterTypes);
        final ReturnStmt returnStmt = getReturnStmt(variableName);
        body.addStatement(returnStmt);
        MethodDeclaration toReturn = getExpressionMethodDeclaration(methodName, parameterTypes);
        ClassOrInterfaceType classOrInterfaceType = parseClassOrInterfaceType(Object.class.getName());
        toReturn.setType(classOrInterfaceType);
        toReturn.setBody(body);
        return toReturn;
    }

    /**
     * @param methodName
     * @param lag
     * @param parameterTypes
     * @return
     */
    static MethodDeclaration getLagExpressionMethodDeclaration(final String methodName, final Lag lag, final List<ClassOrInterfaceType> parameterTypes) {
        throw new KiePMMLException("Lag not managed, yet");
    }

    /**
     * @param methodName
     * @param mapValues
     * @param parameterTypes
     * @return
     */
    static MethodDeclaration getMapValuesExpressionMethodDeclaration(final String methodName, final MapValues mapValues, final List<ClassOrInterfaceType> parameterTypes) {
        throw new KiePMMLException("Lag not managed, yet");
    }

    /**
     * @param methodName
     * @param normContinuous
     * @param parameterTypes
     * @return
     */
    static MethodDeclaration getNormContinuousExpressionMethodDeclaration(final String methodName, final NormContinuous normContinuous, final List<ClassOrInterfaceType> parameterTypes) {
        throw new KiePMMLException("NormContinuous not managed, yet");
    }

    /**
     * @param methodName
     * @param normDiscrete
     * @param parameterTypes
     * @return
     */
    static MethodDeclaration getNormDiscreteExpressionMethodDeclaration(final String methodName, final NormDiscrete normDiscrete, final List<ClassOrInterfaceType> parameterTypes) {
        throw new KiePMMLException("NormDiscrete not managed, yet");
    }

    /**
     * @param methodName
     * @param textIndex
     * @param parameterTypes
     * @return
     */
    static MethodDeclaration getTextIndexExpressionMethodDeclaration(final String methodName, final TextIndex textIndex, final List<ClassOrInterfaceType> parameterTypes) {
        throw new KiePMMLException("TextIndex not managed, yet");
    }

    /**
     * @param aggregate
     * @param parameterTypes
     * @return
     */
    static BlockStmt getAggregatedExpressionBlockStmt(final Aggregate aggregate, final List<ClassOrInterfaceType> parameterTypes) {
        throw new KiePMMLException("Aggregate not managed, yet");
    }

    /**
     * @param apply
     * @param parameterTypes
     * @return
     */
    static BlockStmt getApplyExpressionBlockStmt(final Apply apply, final List<ClassOrInterfaceType> parameterTypes) {
        throw new KiePMMLException("Apply not managed, yet");
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
     * @param parameterTypes
     * @return
     */
    static BlockStmt getConstantExpressionBlockStmt(final String variableName, final Constant constant, final ClassOrInterfaceType returnedType, final List<ClassOrInterfaceType> parameterTypes) {
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
     * @param discretize
     * @param parameterTypes
     * @return
     */
    static BlockStmt getDiscretizeExpressionBlockStmt(final Discretize discretize, final List<ClassOrInterfaceType> parameterTypes) {
        throw new KiePMMLException("Discretize not managed, yet");
    }

    /**
     * Returns
     * <pre>
     *      Optional<KiePMMLNameValue> kiePMMLNameValue = param1.stream().filter((KiePMMLNameValue lmbdParam) -> Objects.equals(<i>(FieldRef_name)</i>, lmbdParam.getName())).findFirst();
     *      Object (<i>variableName</i>) = kiePMMLNameValue.map(KiePMMLNameValue::getValue).orElse(<i>(FieldRef_mapMissingTo)</i>);
     * </pre>
     * @param fieldRef
     * @param parameterTypes
     * @return
     */
    static BlockStmt getFieldRefExpressionBlockStmt(final String variableName, final FieldRef fieldRef, final List<ClassOrInterfaceType> parameterTypes) {
        final BlockStmt toReturn = new BlockStmt();
        String fieldNameToRef = fieldRef.getField().getValue();
        ExpressionStmt filteredOptionalExpr = getFilteredKiePMMLNameValueExpression(KIEPMMLNAMEVALUE_LIST_PARAM, fieldNameToRef);
        toReturn.addStatement(filteredOptionalExpr);

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
        com.github.javaparser.ast.expr.Expression orElseExpression = fieldRef.getMapMissingTo() != null ? new StringLiteralExpr(fieldRef.getMapMissingTo()) : new NullLiteralExpr();
        expression.setArguments(NodeList.nodeList(orElseExpression));

        // Object variableName = kiePMMLNameValue.map(KiePMMLNameValue::getValue).orElse( (fieldRef.getMapMissingTo() )
        VariableDeclarator variableDeclarator = new VariableDeclarator();
        ClassOrInterfaceType classOrInterfaceType = parseClassOrInterfaceType(Object.class.getName());
        variableDeclarator.setType(classOrInterfaceType);
        variableDeclarator.setName(variableName);
        variableDeclarator.setInitializer(expression);
        VariableDeclarationExpr variableDeclarationExpr = new VariableDeclarationExpr();
        variableDeclarationExpr.setVariables(NodeList.nodeList(variableDeclarator));
        toReturn.addStatement(variableDeclarationExpr);

        return toReturn;
    }

    /**
     * @param lag
     * @param parameterTypes
     * @return
     */
    static BlockStmt getLagExpressionBlockStmt(final Lag lag, final List<ClassOrInterfaceType> parameterTypes) {
        throw new KiePMMLException("Lag not managed, yet");
    }

    /**
     * @param mapValues
     * @param parameterTypes
     * @return
     */
    static BlockStmt getMapValuesExpressionBlockStmt(final MapValues mapValues, final List<ClassOrInterfaceType> parameterTypes) {
        throw new KiePMMLException("Lag not managed, yet");
    }

    /**
     * @param normContinuous
     * @param parameterTypes
     * @return
     */
    static BlockStmt getNormContinuousExpressionBlockStmtn(final NormContinuous normContinuous, final List<ClassOrInterfaceType> parameterTypes) {
        throw new KiePMMLException("NormContinuous not managed, yet");
    }

    /**
     * @param normDiscrete
     * @param parameterTypes
     * @return
     */
    static BlockStmt getNormDiscreteExpressionBlockStmt(final NormDiscrete normDiscrete, final List<ClassOrInterfaceType> parameterTypes) {
        throw new KiePMMLException("NormDiscrete not managed, yet");
    }

    /**
     * @param textIndex
     * @param parameterTypes
     * @return
     */
    static BlockStmt getTextIndexExpressionBlockStmt(final TextIndex textIndex, final List<ClassOrInterfaceType> parameterTypes) {
        throw new KiePMMLException("TextIndex not managed, yet");
    }

    /**
     * Return
     * <pre>
     *     empty  methodName(List<KiePMMLNameValue> param1) {
     *     }
     * </pre>
     * @param methodName
     * @param parameterTypes
     * @return
     */
    static MethodDeclaration getExpressionMethodDeclaration(final String methodName, final List<ClassOrInterfaceType> parameterTypes) {
        return getMethodDeclaration(methodName, parameterTypes);
    }
}
