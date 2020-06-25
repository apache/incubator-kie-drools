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
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.LambdaExpr;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.MethodReferenceExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.SimpleName;
import com.github.javaparser.ast.expr.StringLiteralExpr;
import com.github.javaparser.ast.expr.ThisExpr;
import com.github.javaparser.ast.expr.VariableDeclarationExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.ExpressionStmt;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.github.javaparser.ast.type.Type;
import org.kie.pmml.commons.model.tuples.KiePMMLNameValue;

import static com.github.javaparser.StaticJavaParser.parseClassOrInterfaceType;

/**
 * Class meant to provide <i>helper</i> methods to all <i>code-generating</i> classes
 */
public class CommonCodegenUtils {

    public static String OPTIONAL_FILTERED_KIEPMMLNAMEVALUE_NAME ="kiePMMLNameValue";
    static final String LAMBDA_PARAMETER_NAME = "lmbdParam";
    static final String METHOD_NAME_TEMPLATE = "%s%s";
    static final String PARAMETER_NAME_TEMPLATE = "param%s";

    private CommonCodegenUtils() {
        // Avoid instantiation
    }

    /**
     * This method creates
     *
     * <i><p>Optional<KiePMMLNameValue> kiePMMLNameValue = (kiePMMLNameValueListParam)</p>
     * <p>.stream()</p>
     * <p>.filter((KiePMMLNameValue kpmmlnv) -> Objects.equals((fieldNameToRef), kpmmlnv.getName()))</p>
     * <p>.findFirst();</i>
     *
     * expression, where <b>kiePMMLNameValueListParam</b> is the name of the
     * <code>List&lt;KiePMMLNameValue&gt;</code> parameter, and
     * <b>fieldNameToRef</b> is the name of the field to find, in the containing method
     *
     * @param kiePMMLNameValueListParam
     * @param fieldNameToRef
     *
     * @return
     */
    public static ExpressionStmt getFilteredKiePMMLNameValueExpression(String kiePMMLNameValueListParam, String fieldNameToRef) {
        // kpmmlnv.getName()
        MethodCallExpr argumentBodyExpressionArgument2 = new MethodCallExpr("getName");
        argumentBodyExpressionArgument2.setScope(new NameExpr(LAMBDA_PARAMETER_NAME));
        // Objects.equals(fieldNameToRef, kpmmlnv.getName())
        MethodCallExpr argumentBodyExpression = new MethodCallExpr("equals");
        argumentBodyExpression.setArguments(NodeList.nodeList(new StringLiteralExpr(fieldNameToRef), argumentBodyExpressionArgument2));
        argumentBodyExpression.setScope(new NameExpr(Objects.class.getName()));
        ExpressionStmt argumentBody = new ExpressionStmt(argumentBodyExpression);
        // (KiePMMLNameValue kpmmlnv) -> Objects.equals(fieldNameToRef, kpmmlnv.getName())
        Parameter argumentParameter = new Parameter(parseClassOrInterfaceType(KiePMMLNameValue.class.getName()), LAMBDA_PARAMETER_NAME);
        LambdaExpr argument = new LambdaExpr();
        argument.setEnclosingParameters(true).setParameters(NodeList.nodeList(argumentParameter)); // (KiePMMLNameValue kpmmlnv) ->
        argument.setBody(argumentBody); // Objects.equals(fieldNameToRef, kpmmlnv.getName())
        // kiePMMLNameValueListParam.stream()
        MethodCallExpr initializerScopeScope = new MethodCallExpr("stream");
        initializerScopeScope.setScope(new NameExpr(kiePMMLNameValueListParam));
        // kiePMMLNameValueListParam.stream().filter((KiePMMLNameValue kpmmlnv)  -> Objects.equals(fieldNameToRef, kpmmlnv.getName()))
        MethodCallExpr initializerScope = new MethodCallExpr("filter");
        initializerScope.setScope(initializerScopeScope);
        initializerScope.setArguments(NodeList.nodeList(argument));

        // kiePMMLNameValueListParam.stream().filter((KiePMMLNameValue kpmmlnv)  -> Objects.equals(fieldNameToRef, kpmmlnv.getName())).findFirst()
        MethodCallExpr initializer = new MethodCallExpr( "findFirst");
        initializer.setScope(initializerScope);
        // Optional<KiePMMLNameValue> kiePMMLNameValue
        VariableDeclarator variableDeclarator = new VariableDeclarator(getTypedClassOrInterfaceType(Optional.class.getName(), Collections.singletonList(KiePMMLNameValue.class.getName())),
                                                                       OPTIONAL_FILTERED_KIEPMMLNAMEVALUE_NAME);
        // Optional<KiePMMLNameValue> kiePMMLNameValue = kiePMMLNameValueListParam.stream().filter((KiePMMLNameValue kpmmlnv)  -> Objects.equals(fieldNameToRef, kpmmlnv.getName())).findFirst()
        variableDeclarator.setInitializer(initializer);
        //
        VariableDeclarationExpr variableDeclarationExpr = new VariableDeclarationExpr(NodeList.nodeList(variableDeclarator));
        ExpressionStmt toReturn = new ExpressionStmt();
        toReturn.setExpression(variableDeclarationExpr);
        return toReturn;
    }

    /**
     * Add entries <b>fieldName/function</b> inside the constructor to the specified map
     * @param toAdd
     * @param body
     * @param mapName
     */
    public static void addMapPopulation(final Map<String, MethodDeclaration> toAdd, final BlockStmt body, final String mapName) {
        toAdd.forEach((s, methodDeclaration) -> {
            MethodReferenceExpr methodReferenceExpr = new MethodReferenceExpr();
            methodReferenceExpr.setScope(new ThisExpr());
            methodReferenceExpr.setIdentifier(methodDeclaration.getNameAsString());
            NodeList<Expression> expressions = NodeList.nodeList(new StringLiteralExpr(s), methodReferenceExpr);
            body.addStatement(new MethodCallExpr(new NameExpr(mapName), "put", expressions));
        });
    }

    /**
     * Returns a <b>multi-parameters</b> <code>MethodDeclaration</code> whose name is derived from given <b>expression</b>
     * and <b>methodArity</b>, and whose parameters types are defined by <b>parameterTypes</b>
     * @param expression
     * @param methodArity
     * @param parameterTypes
     * @return
     */
    public static MethodDeclaration getMethodDeclaration(final org.dmg.pmml.Expression expression, final int methodArity, final List<ClassOrInterfaceType> parameterTypes) {
        return getMethodDeclaration(expression.getClass().getSimpleName(), methodArity, parameterTypes);
    }

    /**
     * Returns a <b>multi-parameters</b> <code>MethodDeclaration</code> whose name is derived from given <b>methodName</b>
     * and <b>methodArity</b>, and whose parameters types are defined by <b>parameterTypes</b>
     * @param methodName
     * @param methodArity
     * @param parameterTypes
     * @return
     */
    public static MethodDeclaration getMethodDeclaration(final String methodName, final int methodArity, final List<ClassOrInterfaceType> parameterTypes) {
        MethodDeclaration toReturn = getMethodDeclaration(methodName, methodArity);
        NodeList<Parameter> typeParameters = new NodeList<>();
        AtomicInteger counter = new AtomicInteger(0);
        parameterTypes.forEach(classOrInterfaceType -> {
            Parameter toAdd = new Parameter();
            toAdd.setName(new SimpleName(String.format(PARAMETER_NAME_TEMPLATE, counter.addAndGet(1))));
            toAdd.setType(classOrInterfaceType);
            typeParameters.add(toAdd);
        });
        toReturn.setParameters(typeParameters);
        return toReturn;
    }

    /**
     * Returns a <b>no-parameter</b> <code>MethodDeclaration</code> whose name is derived from given <b>expression</b>
     * and <b>methodArity</b>
     * @param expression
     * @param methodArity
     * @return
     */
    public static MethodDeclaration getMethodDeclaration(final org.dmg.pmml.Expression expression, final int methodArity) {
        return getMethodDeclaration(expression.getClass().getSimpleName(), methodArity);
    }

    /**
     * Returns a <b>no-parameter</b> <code>MethodDeclaration</code> whose name is derived from given <b>methodName</b>
     * and <b>methodArity</b>
     * @param methodName
     * @param methodArity
     * @return
     */
    public static MethodDeclaration getMethodDeclaration(final String methodName, final int methodArity) {
        MethodDeclaration toReturn = new MethodDeclaration();
        toReturn.setName(String.format(METHOD_NAME_TEMPLATE, methodName, methodArity));
        return toReturn;
    }

    /**
     * Returns a <b>typed</b> <code>ClassOrInterfaceType</code>
     * @param className
     * @param typesName
     * @return
     */
    public static ClassOrInterfaceType getTypedClassOrInterfaceType(String className, List<String> typesName ) {
        ClassOrInterfaceType toReturn = parseClassOrInterfaceType(className);
        List<Type> types = typesName.stream()
                .map(StaticJavaParser::parseClassOrInterfaceType).collect(Collectors.toList());
        toReturn.setTypeArguments(NodeList.nodeList(types));
        return toReturn;
    }
}
