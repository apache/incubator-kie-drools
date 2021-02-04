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

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.AssignExpr;
import com.github.javaparser.ast.expr.BooleanLiteralExpr;
import com.github.javaparser.ast.expr.DoubleLiteralExpr;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.IntegerLiteralExpr;
import com.github.javaparser.ast.expr.LambdaExpr;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.MethodReferenceExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.NullLiteralExpr;
import com.github.javaparser.ast.expr.ObjectCreationExpr;
import com.github.javaparser.ast.expr.SimpleName;
import com.github.javaparser.ast.expr.StringLiteralExpr;
import com.github.javaparser.ast.expr.ThisExpr;
import com.github.javaparser.ast.expr.VariableDeclarationExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.ExplicitConstructorInvocationStmt;
import com.github.javaparser.ast.stmt.ExpressionStmt;
import com.github.javaparser.ast.stmt.ReturnStmt;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.github.javaparser.ast.type.Type;
import org.kie.pmml.api.exceptions.KiePMMLException;
import org.kie.pmml.api.exceptions.KiePMMLInternalException;
import org.kie.pmml.commons.model.tuples.KiePMMLNameValue;

import static com.github.javaparser.StaticJavaParser.parseClassOrInterfaceType;
import static org.kie.pmml.commons.Constants.MISSING_BODY_TEMPLATE;
import static org.kie.pmml.commons.Constants.MISSING_PARAMETER_IN_CONSTRUCTOR_INVOCATION;
import static org.kie.pmml.commons.Constants.MISSING_VARIABLE_IN_BODY;

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
     * Populate the <code>ClassOrInterfaceDeclaration</code> with the provided <code>MethodDeclaration</code>s
     *
     * @param toPopulate
     * @param methodDeclarations
     */
    public static void populateMethodDeclarations(final ClassOrInterfaceDeclaration toPopulate,
                                                  final Collection<MethodDeclaration> methodDeclarations) {
        methodDeclarations.forEach(toPopulate::addMember);
    }

    /**
     * Returns
     * <pre>
     *  Optional<KiePMMLNameValue> kiePMMLNameValue = (<i>kiePMMLNameValueListParam</i>)
     *      .stream()
     *      .filter((KiePMMLNameValue kpmmlnv) -> Objects.equals("(<i>fieldNameToRef</i>)", kpmmlnv.getName()))
     *      .findFirst();
     * </pre>
     *
     * expression, where <b>kiePMMLNameValueListParam</b> is the name of the
     * <code>List&lt;KiePMMLNameValue&gt;</code> parameter, and
     * <b>fieldNameToRef</b> is the name of the field to find, in the containing method
     *
     * @param kiePMMLNameValueListParam
     * @param fieldNameToRef
     * @param stringLiteralComparison if <code>true</code>, equals comparison is made on the String, e.g Objects.equals("(<i>fieldNameToRef</i>)", kpmmlnv.getName())),
     * otherwise, is done on object reference,  e.g Objects.equals((<i>fieldNameToRef</i>), kpmmlnv.getName())). In this latter case, a <i>fieldNameToRef</i> variable is
     * expected to exists
     *
     * @return
     */
    public static ExpressionStmt getFilteredKiePMMLNameValueExpression(final String kiePMMLNameValueListParam,
                                                                       final String fieldNameToRef,
                                                                       boolean stringLiteralComparison) {
        // kpmmlnv.getName()
        MethodCallExpr argumentBodyExpressionArgument2 = new MethodCallExpr("getName");
        argumentBodyExpressionArgument2.setScope(new NameExpr(LAMBDA_PARAMETER_NAME));
        // Objects.equals(fieldNameToRef, kpmmlnv.getName())
        MethodCallExpr argumentBodyExpression = new MethodCallExpr("equals");
        Expression equalsComparisonExpression;
        if (stringLiteralComparison) {
            equalsComparisonExpression = new StringLiteralExpr(fieldNameToRef);
        } else {
            equalsComparisonExpression = new NameExpr(fieldNameToRef);
        }
        argumentBodyExpression.setArguments(NodeList.nodeList(equalsComparisonExpression, argumentBodyExpressionArgument2));
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
     * For every entry in the given map, add
     * <pre>
     *     (<i>mapName</i>).put(<i>entry_key<i/>, this::<i>entry_value_ref</i>>);
     * </pre>
     * e.g.
     * <pre>
     *     MAP_NAME.put("KEY_0", this::METHOD_015);
     *     MAP_NAME.put("KEY_3", this::METHOD_33);
     *     MAP_NAME.put("KEY_2", this::METHOD_219);
     *     MAP_NAME.put("KEY_4", this::METHOD_46);
     * </pre>
     * inside the given <code>BlockStmt</code>
     *
     * @param toAdd
     * @param body
     * @param mapName
     */
    public static void addMapPopulation(final Map<String, MethodDeclaration> toAdd,
                                        final BlockStmt body,
                                        final String mapName) {
        toAdd.forEach((s, methodDeclaration) -> {
            MethodReferenceExpr methodReferenceExpr = new MethodReferenceExpr();
            methodReferenceExpr.setScope(new ThisExpr());
            methodReferenceExpr.setIdentifier(methodDeclaration.getNameAsString());
            NodeList<Expression> expressions = NodeList.nodeList(new StringLiteralExpr(s), methodReferenceExpr);
            body.addStatement(new MethodCallExpr(new NameExpr(mapName), "put", expressions));
        });
    }

    /**
     * For every entry in the given list, add
     * <pre>
     *     (<i>listName</i>).add(new <i>ObjectCreationExpr</i>>);
     * </pre>
     * e.g.
     * <pre>
     *     LIST_NAME.add(new OBJA());
     *     LIST_NAME.add(new OBJB());
     *     LIST_NAME.add(new OBJC());
     *     LIST_NAME.add(new OBJD());
     * </pre>
     * inside the given <code>BlockStmt</code>
     *
     * @param toAdd
     * @param body
     * @param listName
     */
    public static void addListPopulation(final List<ObjectCreationExpr> toAdd,
                                        final BlockStmt body,
                                        final String listName) {
        toAdd.forEach(objectCreationExpr -> {
            NodeList<Expression> arguments = NodeList.nodeList(objectCreationExpr);
            MethodCallExpr methodCallExpr = new MethodCallExpr();
            methodCallExpr.setScope(new NameExpr(listName));
            methodCallExpr.setName("add");
            methodCallExpr.setArguments(arguments);
            ExpressionStmt expressionStmt = new ExpressionStmt();
            expressionStmt.setExpression(methodCallExpr);
            body.addStatement(expressionStmt);
        });
    }

    /**
     * Create an empty <b>Arrays.asList()</b> <code>ExpressionStmt</code>
     *
     * @return
     */
    public static ExpressionStmt createArraysAsListExpression() {
        ExpressionStmt toReturn = new ExpressionStmt();
        MethodCallExpr arraysCallExpression = new MethodCallExpr();
        SimpleName arraysName = new SimpleName(Arrays.class.getName());
        arraysCallExpression.setScope(new NameExpr(arraysName));
        arraysCallExpression.setName(new SimpleName("asList"));
        toReturn.setExpression(arraysCallExpression);
        return toReturn;
    }

    /**
     * Create a populated <b>Arrays.asList(?... a)</b> <code>ExpressionStmt</code>
     *
     * @param source
     * @return
     */
    public static ExpressionStmt createArraysAsListFromList(List<?> source) {
        ExpressionStmt toReturn = createArraysAsListExpression();
        MethodCallExpr arraysCallExpression = toReturn.getExpression().asMethodCallExpr();
        NodeList<Expression> arguments = new NodeList<>();
        source.forEach(value -> arguments.add(getExpressionForObject(value)));
        arraysCallExpression.setArguments(arguments);
        toReturn.setExpression(arraysCallExpression);
        return toReturn;
    }

    /**
     * Returns
     * <pre>
     *     empty (<i>methodName</i>)((list of <i>parameterType</i> <i>parameter name</i>)) {
     * }
     * </pre>
     *
     *
     * a <b>multi-parameters</b> <code>MethodDeclaration</code> whose names are the <b>key</b>s of the given <code>Map</code>
     * and <b>methodArity</b>, and whose parameters types are the <b>value</b>s
     *
     * <b>The </b>
     * @param methodName
     * @param parameterNameTypeMap expecting an <b>ordered</b> map here, since parameters order matter for <i>caller</i> code
     * @return
     */
    public static MethodDeclaration getMethodDeclaration(final String methodName,
                                                         final Map<String, ClassOrInterfaceType> parameterNameTypeMap) {
        MethodDeclaration toReturn = getMethodDeclaration(methodName);
        NodeList<Parameter> typeParameters = new NodeList<>();
        parameterNameTypeMap.forEach((parameterName, classOrInterfaceType) -> {
            Parameter toAdd = new Parameter();
            toAdd.setName(parameterName);
            toAdd.setType(classOrInterfaceType);
            typeParameters.add(toAdd);
        });
        toReturn.setParameters(typeParameters);
        return toReturn;
    }

    /**
     * Returns
     * <pre>
     *     empty (<i>methodName</i>)() {
     *     }
     * </pre>
     *
     * A <b>no-parameter</b> <code>MethodDeclaration</code> whose name is derived from given <b>methodName</b>
     * and <b>methodArity</b>
     * @param methodName
     * @return
     */
    public static MethodDeclaration getMethodDeclaration(final String methodName) {
        MethodDeclaration toReturn = new MethodDeclaration();
        toReturn.setName(methodName);
        return toReturn;
    }

    /**
     * Returns
     * <pre>
     *     return (<i>returnedVariableName</i>);
     * </pre>
     *
     * e.g
     * <pre>
     *     return varOne;
     * </pre>
     * @param returnedVariableName
     * @return
     */
    public static ReturnStmt getReturnStmt(final String returnedVariableName) {
        ReturnStmt toReturn = new ReturnStmt();
        toReturn.setExpression(new NameExpr(returnedVariableName));
        return toReturn;
    }

    /**
     * Returns
     * <pre>
     *     (<i>className</i>)<(<i>comma-separated list of types</i>)>
     * </pre>
     *
     * e.g
     * <pre>
     *     CLASS_NAME<TypeA, TypeB>
     * </pre>
     * a <b>typed</b> <code>ClassOrInterfaceType</code>
     * @param className
     * @param typesName
     * @return
     */
    public static ClassOrInterfaceType getTypedClassOrInterfaceType(final String className,
                                                                    final List<String> typesName ) {
        ClassOrInterfaceType toReturn = parseClassOrInterfaceType(className);
        List<Type> types = typesName.stream()
                .map(StaticJavaParser::parseClassOrInterfaceType).collect(Collectors.toList());
        toReturn.setTypeArguments(NodeList.nodeList(types));
        return toReturn;
    }

    /**
     * Set the value of the variable with the given <b>assignExpressionName</b> in the given <code>BlockStmt</code>
     * It throws <code>KiePMMLException</code> if variable is not found
     * @param body
     * @param assignExpressionName
     * @param value
     *
     * @throws <code>KiePMMLException</code> if <code>AssignExpr</code> with given <b>assignExpressionName</b> is not found
     */
    public static void setAssignExpressionValue( final BlockStmt body, final String assignExpressionName, final Expression value) {
        AssignExpr assignExpr = getAssignExpression(body, assignExpressionName)
                .orElseThrow(() -> new KiePMMLException(String.format(MISSING_VARIABLE_IN_BODY, assignExpressionName, body)));
        assignExpr.setValue(value);
    }

    /**
     * Return an <code>Optional&lt;AssignExpr&gt;</code> with the given <b>assignExpressionName</b> from the given <code>BlockStmt</code>
     * @param body
     * @param assignExpressionName
     * @return <code>Optional&lt;AssignExpr&gt;</code> with the found <code>AssignExpr</code>, or <code>Optional.empty()</code> if no match
     * has been found
     */
    public static Optional<AssignExpr> getAssignExpression(final BlockStmt body, final String assignExpressionName) {
        final List<AssignExpr> assignExprs = body.findAll(AssignExpr.class);
        return assignExprs.stream()
                .filter(assignExpr -> assignExpressionName.equals(assignExpr.getTarget().asNameExpr().getNameAsString()))
                .findFirst();
    }

    /**
     * Return an <code>Optional&lt;ExplicitConstructorInvocationStmt&gt;</code> from the given <code>BlockStmt</code>
     * @param body
     * @return <code>Optional&lt;ExplicitConstructorInvocationStmt&gt;</code> with the found <code>ExplicitConstructorInvocationStmt</code>, or <code>Optional.empty()</code> if none is found
     *
     */
    public static Optional<ExplicitConstructorInvocationStmt> getExplicitConstructorInvocationStmt(final BlockStmt body) {
        return body.getStatements().stream()
                .filter(statement -> statement instanceof ExplicitConstructorInvocationStmt)
                .map(ExplicitConstructorInvocationStmt.class::cast)
                .findFirst();
    }

    /**
     * Return an <code>Optional&lt;NameExpr&gt;</code>  from the given <code>ExplicitConstructorInvocationStmt</code>
     * @param constructorInvocationStmt
     * @param parameterName
     * @param value
     * @return <code>Optional&lt;NameExpr&gt;</code> with the found <code>NameExpr</code>, or <code>Optional.empty()</code> if none is found
     */
    public static void setExplicitConstructorInvocationArgument(final ExplicitConstructorInvocationStmt constructorInvocationStmt, final String parameterName, final String value) {
        final NameExpr parameterExpr = getExplicitConstructorInvocationParameter(constructorInvocationStmt, parameterName)
                .orElseThrow(() -> new KiePMMLException(String.format(MISSING_PARAMETER_IN_CONSTRUCTOR_INVOCATION, parameterName, constructorInvocationStmt)));
        parameterExpr.setName(value);
    }

    /**
     * Return an <code>Optional&lt;NameExpr&gt;</code>  from the given <code>ExplicitConstructorInvocationStmt</code>
     * @param constructorInvocationStmt
     * @param parameterName
     * @return <code>Optional&lt;NameExpr&gt;</code> with the found <code>NameExpr</code>, or <code>Optional.empty()</code> if none is found
     */
    public static Optional<NameExpr> getExplicitConstructorInvocationParameter(final ExplicitConstructorInvocationStmt constructorInvocationStmt, final String parameterName) {
        return constructorInvocationStmt.getArguments()
                .stream()
                .filter(expression -> expression instanceof NameExpr && ((NameExpr)expression).getName().asString().equals(parameterName))
                .map(expression -> (NameExpr)expression )
                .findFirst();
    }

    /**
     * Return an <code>Optional&lt;MethodDeclaration&gt;</code> with the <b>first</b> method <b>methodName</b> from the given <code>ClassOrInterfaceDeclaration</code>
     * @param classOrInterfaceDeclaration
     * @param methodName
     * @return <code>Optional&lt;MethodDeclaration&gt;</code> with the first found <code>MethodDeclaration</code>, or <code>Optional.empty()</code> if no match
     * has been found
     */
    public static Optional<MethodDeclaration> getMethodDeclaration(final ClassOrInterfaceDeclaration classOrInterfaceDeclaration, final String methodName) {
        final List<MethodDeclaration> assignExprs = classOrInterfaceDeclaration.getMethodsByName(methodName);
        return assignExprs.isEmpty() ? Optional.empty() : Optional.of(assignExprs.get(0));
    }

    /**
     * Add a <code>MethodDeclaration</code> to the class
     * @param methodTemplate
     * @param tableTemplate
     * @param methodName
     * @return
     */
    public static MethodDeclaration addMethod(final MethodDeclaration methodTemplate,
                                                 final ClassOrInterfaceDeclaration tableTemplate,
                                                 final String methodName) {
        final BlockStmt body =
                methodTemplate.getBody().orElseThrow(() -> new KiePMMLInternalException(String.format(MISSING_BODY_TEMPLATE, methodTemplate.getName())));
        final MethodDeclaration toReturn = tableTemplate.addMethod(methodName).setBody(body);
        toReturn.setModifiers(methodTemplate.getModifiers());
        methodTemplate.getParameters().forEach(toReturn::addParameter);
        toReturn.setType(methodTemplate.getType());
        return toReturn;
    }

    /**
     * Return an <code>Optional&lt;VariableDeclarator&gt;</code> with the <b>first</b> variable <b>variableName</b> from the given <code>BlockStmt</code>
     * @param body
     * @param variableName
     * @return <code>Optional&lt;VariableDeclarator&gt;</code> with the first found <code>VariableDeclarator</code>, or <code>Optional.empty()</code> if no match
     * has been found
     */
    public static Optional<VariableDeclarator> getVariableDeclarator(final BlockStmt body, final String variableName) {
        return body.findAll(VariableDeclarator.class)
                .stream()
                .filter(variableDeclarator -> variableDeclarator.getName().asString().equals(variableName))
                .findFirst();
    }

    public static Expression getExpressionForObject(Object source) {
        if (source == null) {
            return new NullLiteralExpr();
        }
        String className = source.getClass().getSimpleName();
        switch(className) {
            case "String" :
            return new StringLiteralExpr((String) source);
            case "int":
            case "Integer":
                return new IntegerLiteralExpr((Integer) source);
            case "double":
            case "Double":
                return new DoubleLiteralExpr((Double) source);
            case "float":
            case "Float":
                return new DoubleLiteralExpr(((Float) source).doubleValue());
            case "boolean":
            case "Boolean":
                return new BooleanLiteralExpr((Boolean)source);
            default:
                return new NameExpr(source.toString());


        }

    }
}
