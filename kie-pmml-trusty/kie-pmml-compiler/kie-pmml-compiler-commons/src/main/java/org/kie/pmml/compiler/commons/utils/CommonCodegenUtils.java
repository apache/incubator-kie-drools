/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.kie.pmml.compiler.commons.utils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.ConstructorDeclaration;
import com.github.javaparser.ast.body.InitializerDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.AssignExpr;
import com.github.javaparser.ast.expr.BooleanLiteralExpr;
import com.github.javaparser.ast.expr.DoubleLiteralExpr;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.IntegerLiteralExpr;
import com.github.javaparser.ast.expr.LambdaExpr;
import com.github.javaparser.ast.expr.LongLiteralExpr;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.MethodReferenceExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.NullLiteralExpr;
import com.github.javaparser.ast.expr.ObjectCreationExpr;
import com.github.javaparser.ast.expr.SimpleName;
import com.github.javaparser.ast.expr.StringLiteralExpr;
import com.github.javaparser.ast.expr.ThisExpr;
import com.github.javaparser.ast.expr.TypeExpr;
import com.github.javaparser.ast.expr.VariableDeclarationExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.ExplicitConstructorInvocationStmt;
import com.github.javaparser.ast.stmt.ExpressionStmt;
import com.github.javaparser.ast.stmt.ReturnStmt;
import com.github.javaparser.ast.stmt.Statement;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.github.javaparser.ast.type.Type;
import org.dmg.pmml.DataType;
import org.dmg.pmml.OpType;
import org.kie.pmml.api.enums.DATA_TYPE;
import org.kie.pmml.api.enums.OP_TYPE;
import org.kie.pmml.api.exceptions.KiePMMLException;
import org.kie.pmml.api.exceptions.KiePMMLInternalException;
import org.kie.pmml.commons.model.tuples.KiePMMLNameValue;

import static com.github.javaparser.StaticJavaParser.parseClassOrInterfaceType;
import static org.kie.pmml.commons.Constants.MISSING_BODY_IN_METHOD;
import static org.kie.pmml.commons.Constants.MISSING_BODY_TEMPLATE;
import static org.kie.pmml.commons.Constants.MISSING_CHAINED_METHOD_DECLARATION_TEMPLATE;
import static org.kie.pmml.commons.Constants.MISSING_CONSTRUCTOR_IN_BODY;
import static org.kie.pmml.commons.Constants.MISSING_PARAMETER_IN_CONSTRUCTOR_INVOCATION;
import static org.kie.pmml.commons.Constants.MISSING_STATIC_INITIALIZER;
import static org.kie.pmml.commons.Constants.MISSING_VARIABLE_INITIALIZER_TEMPLATE;
import static org.kie.pmml.commons.Constants.MISSING_VARIABLE_IN_BODY;

/**
 * Class meant to provide <i>helper</i> methods to all <i>code-generating</i> classes
 */
public class CommonCodegenUtils {

    static final String LAMBDA_PARAMETER_NAME = "lmbdParam";
    public static String OPTIONAL_FILTERED_KIEPMMLNAMEVALUE_NAME = "kiePMMLNameValue";

    private CommonCodegenUtils() {
        // Avoid instantiation
    }

    /**
     * Populate the <code>ClassOrInterfaceDeclaration</code> with the provided <code>MethodDeclaration</code>s
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
     * <p>
     * expression, where <b>kiePMMLNameValueListParam</b> is the name of the
     * <code>List&lt;KiePMMLNameValue&gt;</code> parameter, and
     * <b>fieldNameToRef</b> is the name of the field to find, in the containing method
     * @param kiePMMLNameValueListParam
     * @param fieldNameToRef
     * @param stringLiteralComparison if <code>true</code>, equals comparison is made on the String, e.g Objects
     * .equals("(<i>fieldNameToRef</i>)", kpmmlnv.getName())),
     * otherwise, is done on object reference,  e.g Objects.equals((<i>fieldNameToRef</i>), kpmmlnv.getName())). In
     * this latter case, a <i>fieldNameToRef</i> variable is
     * expected to exists
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
        argumentBodyExpression.setArguments(NodeList.nodeList(equalsComparisonExpression,
                                                              argumentBodyExpressionArgument2));
        argumentBodyExpression.setScope(new NameExpr(Objects.class.getName()));
        ExpressionStmt argumentBody = new ExpressionStmt(argumentBodyExpression);
        // (KiePMMLNameValue kpmmlnv) -> Objects.equals(fieldNameToRef, kpmmlnv.getName())
        Parameter argumentParameter = new Parameter(parseClassOrInterfaceType(KiePMMLNameValue.class.getName()),
                                                    LAMBDA_PARAMETER_NAME);
        LambdaExpr argument = new LambdaExpr();
        argument.setEnclosingParameters(true).setParameters(NodeList.nodeList(argumentParameter)); //
        // (KiePMMLNameValue kpmmlnv) ->
        argument.setBody(argumentBody); // Objects.equals(fieldNameToRef, kpmmlnv.getName())
        // kiePMMLNameValueListParam.stream()
        MethodCallExpr initializerScopeScope = new MethodCallExpr("stream");
        initializerScopeScope.setScope(new NameExpr(kiePMMLNameValueListParam));
        // kiePMMLNameValueListParam.stream().filter((KiePMMLNameValue kpmmlnv)  -> Objects.equals(fieldNameToRef,
        // kpmmlnv.getName()))
        MethodCallExpr initializerScope = new MethodCallExpr("filter");
        initializerScope.setScope(initializerScopeScope);
        initializerScope.setArguments(NodeList.nodeList(argument));

        // kiePMMLNameValueListParam.stream().filter((KiePMMLNameValue kpmmlnv)  -> Objects.equals(fieldNameToRef,
        // kpmmlnv.getName())).findFirst()
        MethodCallExpr initializer = new MethodCallExpr("findFirst");
        initializer.setScope(initializerScope);
        // Optional<KiePMMLNameValue> kiePMMLNameValue
        VariableDeclarator variableDeclarator =
                new VariableDeclarator(getTypedClassOrInterfaceTypeByTypeNames(Optional.class.getName(),
                                                                               Collections.singletonList(KiePMMLNameValue.class.getName())),
                                       OPTIONAL_FILTERED_KIEPMMLNAMEVALUE_NAME);
        // Optional<KiePMMLNameValue> kiePMMLNameValue = kiePMMLNameValueListParam.stream().filter((KiePMMLNameValue
        // kpmmlnv)  -> Objects.equals(fieldNameToRef, kpmmlnv.getName())).findFirst()
        variableDeclarator.setInitializer(initializer);
        //
        VariableDeclarationExpr variableDeclarationExpr =
                new VariableDeclarationExpr(NodeList.nodeList(variableDeclarator));
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
     * @param toAdd
     * @param body
     * @param mapName
     */
    public static void addMapPopulation(final Map<String, MethodDeclaration> toAdd,
                                        final BlockStmt body,
                                        final String mapName) {
        Map<String, Expression> toAddExpr = toAdd.entrySet().stream().collect(Collectors.toMap(
                Map.Entry::getKey,
                entry -> {
                    MethodReferenceExpr methodReferenceExpr = new MethodReferenceExpr();
                    methodReferenceExpr.setScope(new ThisExpr());
                    methodReferenceExpr.setIdentifier(entry.getValue().getNameAsString());
                    return methodReferenceExpr;
                }
        ));
        addMapPopulationExpressions(toAddExpr, body, mapName);
    }

    /**
     * Declare and initialize a new <code>Map</code> in the given <code>BlockStmt</code>
     * @param body
     * @param mapName
     * @param mapTypes
     */
    public static void createHashMap(final BlockStmt body,
                                     final String mapName,
                                     final List<String> mapTypes) {
        createMap(body, mapName, mapTypes, HashMap.class);
    }

    /**
     * Declare and initialize a new <code>LinkedHashMap</code> in the given <code>BlockStmt</code>
     * @param body
     * @param mapName
     * @param mapTypes
     */
    public static void createLinkedHashMap(final BlockStmt body,
                                           final String mapName,
                                           final List<String> mapTypes) {
        createMap(body, mapName, mapTypes, LinkedHashMap.class);
    }

    /**
     * Declare, initialize and populate a new <code>HashMap</code> in the given <code>BlockStmt</code>
     * @param body
     * @param mapName
     * @param mapTypes
     */
    public static void createPopulatedHashMap(final BlockStmt body,
                                              final String mapName,
                                              final List<String> mapTypes,
                                              final Map<String, Expression> toAdd) {
        createHashMap(body, mapName, mapTypes);
        addMapPopulationExpressions(toAdd, body, mapName);
    }

    /**
     * Declare, initialize and populate a new <code>LinkedHashMap</code> in the given <code>BlockStmt</code>
     * @param body
     * @param mapName
     * @param mapTypes
     */
    public static void createPopulatedLinkedHashMap(final BlockStmt body,
                                                    final String mapName,
                                                    final List<String> mapTypes,
                                                    final Map<String, Expression> toAdd) {
        createLinkedHashMap(body, mapName, mapTypes);
        addMapPopulationExpressions(toAdd, body, mapName);
    }

    /**
     * For every entry in the given map, add a "put" statement to the provided {@link BlockStmt} body.
     * @param toAdd the map containing the input values to process
     * @param body the destination body
     * @param mapName the name of the map to populate in the codegenerated statements
     */
    public static void addMapPopulationExpressions(Map<String, Expression> toAdd, BlockStmt body, String mapName) {
        toAdd.forEach((key, value) -> {
            NodeList<Expression> expressions = NodeList.nodeList(new StringLiteralExpr(key), value);
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
     * @param toAdd
     * @param body
     * @param listName
     */
    public static void addListPopulationByObjectCreationExpr(final List<ObjectCreationExpr> toAdd,
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
     * Method to be used to populate a <code>List</code> inside a getter method meant to return only that
     * <code>List</code>
     * @param toAdd
     * @param methodDeclaration
     * @param listName
     */
    public static void populateListInListGetter(final List<? extends Expression> toAdd,
                                                final MethodDeclaration methodDeclaration,
                                                final String listName) {
        final BlockStmt body =
                methodDeclaration.getBody().orElseThrow(() -> new KiePMMLInternalException(String.format(MISSING_BODY_IN_METHOD, methodDeclaration)));
        Optional<ReturnStmt> oldReturn = body.getStatements().parallelStream().filter(ReturnStmt.class::isInstance)
                .map(ReturnStmt.class::cast)
                .findFirst();
        oldReturn.ifPresent(Node::remove);

        toAdd.forEach(expression -> {
            NodeList<Expression> arguments = NodeList.nodeList(expression);
            MethodCallExpr methodCallExpr = new MethodCallExpr();
            methodCallExpr.setScope(new NameExpr(listName));
            methodCallExpr.setName("add");
            methodCallExpr.setArguments(arguments);
            ExpressionStmt expressionStmt = new ExpressionStmt();
            expressionStmt.setExpression(methodCallExpr);
            body.addStatement(expressionStmt);
        });
        body.addStatement(getReturnStmt(listName));
    }

    /**
     * For every entry in the given list, add
     * <pre>
     *     (<i>listName</i>).add(<i>MethodCallExpr</i>>);
     * </pre>
     * e.g.
     * <pre>
     *     LIST_NAME.add(ObjectA.builder().build());
     *     LIST_NAME.add(ObjectB.builder().build());
     *     LIST_NAME.add(ObjectC.builder().build());
     *     LIST_NAME.add(ObjectD.builder().build());
     * </pre>
     * inside the given <code>BlockStmt</code>
     * @param toAdd
     * @param body
     * @param listName
     */
    public static void addListPopulationByMethodCallExpr(final List<MethodCallExpr> toAdd,
                                                         final BlockStmt body,
                                                         final String listName) {
        toAdd.forEach(methodCallExpr1 -> {
            NodeList<Expression> arguments = NodeList.nodeList(methodCallExpr1);
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
     * <p>
     * <p>
     * a <b>multi-parameters</b> <code>MethodDeclaration</code> whose names are the <b>key</b>s of the given
     * <code>Map</code>
     * and <b>methodArity</b>, and whose parameters types are the <b>value</b>s
     *
     * <b>The </b>
     * @param methodName
     * @param parameterNameTypeMap expecting an <b>ordered</b> map here, since parameters order matter for
     * <i>caller</i> code
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
     * <p>
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
     * <p>
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
     * <p>
     * e.g
     * <pre>
     *     CLASS_NAME<TypeA, TypeB>
     * </pre>
     * a <b>typed</b> <code>ClassOrInterfaceType</code>
     * @param className
     * @param typesName
     * @return
     */
    public static ClassOrInterfaceType getTypedClassOrInterfaceTypeByTypeNames(final String className,
                                                                               final List<String> typesName) {
        List<Type> types = typesName.stream()
                .map(StaticJavaParser::parseClassOrInterfaceType).collect(Collectors.toList());
        return getTypedClassOrInterfaceTypeByTypes(className, types);
    }

    /**
     * Returns
     * <pre>
     *     (<i>className</i>)<(<i>comma-separated list of types</i>)>
     * </pre>
     * <p>
     * e.g
     * <pre>
     *     CLASS_NAME<TypeA, TypeB>
     * </pre>
     * a <b>typed</b> <code>ClassOrInterfaceType</code>
     * @param className
     * @param types
     * @return
     */
    public static ClassOrInterfaceType getTypedClassOrInterfaceTypeByTypes(final String className,
                                                                           final List<Type> types) {
        ClassOrInterfaceType toReturn = parseClassOrInterfaceType(className);
        toReturn.setTypeArguments(NodeList.nodeList(types));
        return toReturn;
    }

    /**
     * Set the value of the variable with the given <b>assignExpressionName</b> in the given <code>BlockStmt</code>
     * It throws <code>KiePMMLException</code> if variable is not found
     * @param body
     * @param assignExpressionName
     * @param value
     * @throws <code>KiePMMLException</code> if <code>AssignExpr</code> with given <b>assignExpressionName</b> is not
     * found
     */
    public static void setAssignExpressionValue(final BlockStmt body, final String assignExpressionName,
                                                final Expression value) {
        AssignExpr assignExpr = getAssignExpression(body, assignExpressionName)
                .orElseThrow(() -> new KiePMMLException(String.format(MISSING_VARIABLE_IN_BODY, assignExpressionName,
                                                                      body)));
        assignExpr.setValue(value);
    }

    /**
     * Return an <code>Optional&lt;AssignExpr&gt;</code> with the given <b>assignExpressionName</b> from the given
     * <code>BlockStmt</code>
     * @param body
     * @param assignExpressionName
     * @return <code>Optional&lt;AssignExpr&gt;</code> with the found <code>AssignExpr</code>, or <code>Optional
     * .empty()</code> if no match
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
     * @return <code>Optional&lt;ExplicitConstructorInvocationStmt&gt;</code> with the found
     * <code>ExplicitConstructorInvocationStmt</code>, or <code>Optional.empty()</code> if none is found
     */
    public static Optional<ExplicitConstructorInvocationStmt> getExplicitConstructorInvocationStmt(final BlockStmt body) {
        return body.getStatements().stream()
                .filter(ExplicitConstructorInvocationStmt.class::isInstance)
                .map(ExplicitConstructorInvocationStmt.class::cast)
                .findFirst();
    }

    /**
     * Set the <b>value</b> of the given <b>parameterName</b> in the given <code>ConstructorDeclaration</code>
     * @param constructorDeclaration
     * @param parameterName
     * @param value
     * @throws KiePMMLException if the given parameter is not found
     */
    public static void setConstructorDeclarationParameterArgument(final ConstructorDeclaration constructorDeclaration,
                                                                  final String parameterName, final String value) {
        final BlockStmt body = constructorDeclaration.getBody();
        final ExplicitConstructorInvocationStmt superStatement =
                CommonCodegenUtils.getExplicitConstructorInvocationStmt(body)
                        .orElseThrow(() -> new KiePMMLException(String.format(MISSING_CONSTRUCTOR_IN_BODY, body)));
        final NameExpr parameterExpr = getExplicitConstructorInvocationParameter(superStatement, parameterName)
                .orElseThrow(() -> new KiePMMLException(String.format(MISSING_PARAMETER_IN_CONSTRUCTOR_INVOCATION,
                                                                      parameterName, constructorDeclaration)));
        if (value != null) {
            parameterExpr.setName(value);
        } else {
            superStatement.getArguments().replace(parameterExpr, new NullLiteralExpr());
        }
    }

    /**
     * Set the <b>value</b> of the given <b>parameterName</b> in the given <code>ConstructorDeclaration</code>
     * @param constructorDeclaration
     * @param referenceName
     * @param value
     * @throws KiePMMLException if the given parameter is not found
     */
    public static void setConstructorDeclarationReferenceArgument(final ConstructorDeclaration constructorDeclaration,
                                                                  final String referenceName, final String value) {
        final BlockStmt body = constructorDeclaration.getBody();
        final ExplicitConstructorInvocationStmt superStatement =
                CommonCodegenUtils.getExplicitConstructorInvocationStmt(body)
                        .orElseThrow(() -> new KiePMMLException(String.format(MISSING_CONSTRUCTOR_IN_BODY, body)));
        final MethodReferenceExpr methodReferenceExpr = getExplicitConstructorInvocationMethodReference(superStatement,
                                                                                                        referenceName)
                .orElseThrow(() -> new KiePMMLException(String.format(MISSING_PARAMETER_IN_CONSTRUCTOR_INVOCATION,
                                                                      referenceName, constructorDeclaration)));
        if (value != null) {
            methodReferenceExpr.setScope(new TypeExpr(parseClassOrInterfaceType(value)));
        } else {
            superStatement.getArguments().replace(methodReferenceExpr, new NullLiteralExpr());
        }
    }

    /**
     * Set the <b>value</b> of the given <b>parameterName</b> in the given
     * <code>ExplicitConstructorInvocationStmt</code>
     * @param constructorInvocationStmt
     * @param parameterName
     * @param value
     * @throws KiePMMLException if the given parameter is not found
     */
    public static void setExplicitConstructorInvocationStmtArgument(final ExplicitConstructorInvocationStmt constructorInvocationStmt, final String parameterName, final String value) {
        final NameExpr parameterExpr = getExplicitConstructorInvocationParameter(constructorInvocationStmt,
                                                                                 parameterName)
                .orElseThrow(() -> new KiePMMLException(String.format(MISSING_PARAMETER_IN_CONSTRUCTOR_INVOCATION,
                                                                      parameterName, constructorInvocationStmt)));
        parameterExpr.setName(value);
    }

    /**
     * Return an <code>BlockStmt</code>  from the given <code>ClassOrInterfaceDeclaration</code>
     * @param classOrInterfaceDeclaration
     * @throws KiePMMLException if none is found
     */
    public static BlockStmt getInitializerBlockStmt(final ClassOrInterfaceDeclaration classOrInterfaceDeclaration) {
        return getInitializerDeclaration(classOrInterfaceDeclaration).getBody();
    }

    /**
     * Return an <code>InitializerDeclaration</code>  from the given <code>ClassOrInterfaceDeclaration</code>
     * @param classOrInterfaceDeclaration
     * @throws KiePMMLException if none is found
     */
    public static InitializerDeclaration getInitializerDeclaration(final ClassOrInterfaceDeclaration classOrInterfaceDeclaration) {
        return classOrInterfaceDeclaration.getMembers()
                .stream()
                .filter(InitializerDeclaration.class::isInstance)
                .map(InitializerDeclaration.class::cast)
                .findFirst()
                .orElseThrow(() -> new KiePMMLException(String.format(MISSING_STATIC_INITIALIZER,
                                                                      classOrInterfaceDeclaration)));
    }

    /**
     * Return an <code>Optional&lt;NameExpr&gt;</code>  from the given <code>ExplicitConstructorInvocationStmt</code>
     * @param constructorInvocationStmt
     * @param parameterName
     * @return <code>Optional&lt;NameExpr&gt;</code> with the found <code>NameExpr</code>, or <code>Optional.empty()
     * </code> if none is found
     */
    public static Optional<NameExpr> getExplicitConstructorInvocationParameter(final ExplicitConstructorInvocationStmt constructorInvocationStmt, final String parameterName) {
        return constructorInvocationStmt.getArguments()
                .stream()
                .filter(expression -> expression instanceof NameExpr && ((NameExpr) expression).getName().asString().equals(parameterName))
                .map(NameExpr.class::cast)
                .findFirst();
    }

    /**
     * Return an <code>Optional&lt;MethodReferenceExpr&gt;</code>  from the given
     * <code>ExplicitConstructorInvocationStmt</code>
     * @param constructorInvocationStmt
     * @param typeName
     * @return <code>Optional&lt;MethodReferenceExpr&gt;</code> with the found <code>MethodReferenceExpr</code>, or
     * <code>Optional.empty()</code> if none is found
     */
    public static Optional<MethodReferenceExpr> getExplicitConstructorInvocationMethodReference(final ExplicitConstructorInvocationStmt constructorInvocationStmt, final String typeName) {
        return constructorInvocationStmt.getArguments()
                .stream()
                .filter(expression -> expression instanceof MethodReferenceExpr && ((MethodReferenceExpr) expression).getScope().asTypeExpr().getType().asString().equals(typeName))
                .map(MethodReferenceExpr.class::cast)
                .findFirst();
    }

    /**
     * Return an <code>BlockStmt</code> for the method <b>methodName</b> from the given
     * <code>ClassOrInterfaceDeclaration</code>
     * @param classOrInterfaceDeclaration
     * @param methodName
     * @throws KiePMMLException if none is found
     */
    public static BlockStmt getMethodDeclarationBlockStmt(final ClassOrInterfaceDeclaration classOrInterfaceDeclaration, final String methodName) {
        return getMethodDeclaration(classOrInterfaceDeclaration, methodName)
                .map(MethodDeclaration::getBody)
                .map(Optional::get)
                .orElseThrow(() -> new KiePMMLInternalException(String.format(MISSING_BODY_IN_METHOD, methodName)));
    }

    /**
     * Return an <code>Optional&lt;MethodDeclaration&gt;</code> with the <b>first</b> method <b>methodName</b> from
     * the given <code>ClassOrInterfaceDeclaration</code>
     * @param classOrInterfaceDeclaration
     * @param methodName
     * @return <code>Optional&lt;MethodDeclaration&gt;</code> with the first found <code>MethodDeclaration</code>, or
     * <code>Optional.empty()</code> if no match
     * has been found
     */
    public static Optional<MethodDeclaration> getMethodDeclaration(final ClassOrInterfaceDeclaration classOrInterfaceDeclaration, final String methodName) {
        final List<MethodDeclaration> methodDeclarations = classOrInterfaceDeclaration.getMethodsByName(methodName);
        return methodDeclarations.isEmpty() ? Optional.empty() : Optional.of(methodDeclarations.get(0));
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
     * Set the value of the variable with the given <b>variableDeclaratorName</b> in the given <code>BlockStmt</code>
     * It throws <code>KiePMMLException</code> if variable is not found
     * @param body
     * @param variableDeclaratorName
     * @param value
     * @throws <code>KiePMMLException</code> if <code>VariableDeclarator</code> with given
     * <b>variableDeclaratorName</b> is not
     * found
     */
    public static void setVariableDeclaratorValue(final BlockStmt body, final String variableDeclaratorName,
                                                  final Expression value) {
        VariableDeclarator variableDeclarator = getVariableDeclarator(body, variableDeclaratorName)
                .orElseThrow(() -> new KiePMMLException(String.format(MISSING_VARIABLE_IN_BODY, variableDeclaratorName,
                                                                      body)));
        variableDeclarator.setInitializer(value);
    }

    /**
     * Return an <code>Optional&lt;VariableDeclarator&gt;</code> with the <b>first</b> variable <b>variableName</b>
     * from the given <code>MethodDeclaration</code>
     * @param methodDeclaration
     * @param variableName
     * @return <code>Optional&lt;VariableDeclarator&gt;</code> with the first found <code>VariableDeclarator</code>,
     * or <code>Optional.empty()</code> if no match
     * has been found
     */
    public static Optional<VariableDeclarator> getVariableDeclarator(final MethodDeclaration methodDeclaration,
                                                                     final String variableName) {
        final BlockStmt body = methodDeclaration.getBody()
                .orElseThrow(() -> new KiePMMLException(String.format(MISSING_BODY_TEMPLATE, methodDeclaration)));
        return getVariableDeclarator(body, variableName);
    }

    /**
     * Return an <code>Optional&lt;VariableDeclarator&gt;</code> with the <b>first</b> variable <b>variableName</b>
     * from the given <code>BlockStmt</code>
     * @param body
     * @param variableName
     * @return <code>Optional&lt;VariableDeclarator&gt;</code> with the first found <code>VariableDeclarator</code>,
     * or <code>Optional.empty()</code> if no match
     * has been found
     */
    public static Optional<VariableDeclarator> getVariableDeclarator(final BlockStmt body, final String variableName) {
        return body.findAll(VariableDeclarator.class)
                .stream()
                .filter(variableDeclarator -> variableDeclarator.getName().asString().equals(variableName))
                .findFirst();
    }

    public static Expression getExpressionForDataType(DataType dataTypeParam) {
        final Expression toReturn;
        if (dataTypeParam != null) {
            final DATA_TYPE dataType = DATA_TYPE.byName(dataTypeParam.value());
            toReturn = new NameExpr(DATA_TYPE.class.getName() + "." + dataType.name());
        } else {
            toReturn = new NullLiteralExpr();
        }
        return toReturn;
    }

    public static Expression getExpressionForOpType(OpType opTypeParam) {
        final Expression toReturn;
        if (opTypeParam != null) {
            final OP_TYPE opType = OP_TYPE.byName(opTypeParam.value());
            toReturn = new NameExpr(OP_TYPE.class.getName() + "." + opType.name());
        } else {
            toReturn = new NullLiteralExpr();
        }
        return toReturn;
    }

    public static Expression getExpressionForObject(Object source) {
        if (source == null) {
            return new NullLiteralExpr();
        }
        String className = source.getClass().getSimpleName();
        switch (className) {
            case "String":
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
                return new BooleanLiteralExpr((Boolean) source);
            default:
                return new NameExpr(source.toString());
        }
    }

    /**
     * Return a <code>lit&lt;NameExpr&gt;</code> with all the instances of the given <b>exprName</b>
     * @param toRead
     * @param exprName
     * @return
     */
    public static List<NameExpr> getNameExprsFromBlock(final BlockStmt toRead, final String exprName) {
        return toRead.stream()
                .filter(node -> node instanceof NameExpr &&
                        ((NameExpr) node).getName().asString().equals(exprName))
                .map(NameExpr.class::cast)
                .collect(Collectors.toList());
    }

    /**
     * Return a new {@link AssignExpr} from a target name and a generic {@link Expression}.
     * @param target {@link String} containing the name to assign the expression to
     * @param value the value to be assigned
     * @return the new {@link AssignExpr}
     */
    public static AssignExpr assignExprFrom(String target, Expression value) {
        return new AssignExpr(new NameExpr(target), value, AssignExpr.Operator.ASSIGN);
    }

    /**
     * Return a new {@link AssignExpr} from a target name and an enum literal.
     * @param target {@link String} containing the name to assign the expression to
     * @param value the enum value to be assigned
     * @return the new {@link AssignExpr}
     */
    public static AssignExpr assignExprFrom(String target, Enum<?> value) {
        return assignExprFrom(target, literalExprFrom(value));
    }

    /**
     * Return a new {@link AssignExpr} from a target name and {@link String} literal.
     * @param target {@link String} containing the name to assign the expression to
     * @param value the {@link String} value to be assigned
     * @return the new {@link AssignExpr}
     */
    public static AssignExpr assignExprFrom(String target, String value) {
        return assignExprFrom(target, literalExprFrom(value));
    }

    /**
     * Return a new {@link Expression} containing an enum literal.
     * @param input the enum value to be assigned
     * @return the new {@link Expression}
     */
    public static Expression literalExprFrom(Enum<?> input) {
        return input == null ? new NullLiteralExpr() :
                new NameExpr(input.getClass().getCanonicalName() + "." + input.name());
    }

    /**
     * Return a new {@link Expression} containing an {@link String}.
     * @param input the {@link String} value to be assigned
     * @return the new {@link Expression}
     */
    public static Expression literalExprFrom(String input) {
        return input == null ? new NullLiteralExpr() : new StringLiteralExpr(input);
    }

    /**
     * Return a new {@link Expression} containing an object with a specific value of a specific {@link DATA_TYPE}.
     * This can either be a new object (for date and time) or a literal.
     * @param type the {@link DATA_TYPE} of the specified value
     * @param value the value represented as {@link String}
     * @return the new {@link Expression}
     */
    public static Expression literalExprFrom(DATA_TYPE type, String value) {
        if (type == null) {
            throw new IllegalArgumentException("Invalid \"null\" data type");
        }
        if (value == null) {
            return new NullLiteralExpr();
        }
        switch (type) {
            case STRING:
                return new StringLiteralExpr(value);
            case INTEGER:
                return new IntegerLiteralExpr(value);
            case DOUBLE:
            case FLOAT:
                return new DoubleLiteralExpr(value);
            case BOOLEAN:
                return new BooleanLiteralExpr(Boolean.parseBoolean(value));
            case DATE:
                return new MethodCallExpr(new NameExpr(LocalDate.class.getName()), "parse",
                                          NodeList.nodeList(new StringLiteralExpr(value)));
            case TIME:
                return new MethodCallExpr(new NameExpr(LocalTime.class.getName()), "parse",
                                          NodeList.nodeList(new StringLiteralExpr(value)));
            case DATE_TIME:
                return new MethodCallExpr(new NameExpr(LocalDateTime.class.getName()), "parse",
                                          NodeList.nodeList(new StringLiteralExpr(value)));
            case DATE_DAYS_SINCE_0:
            case DATE_DAYS_SINCE_1960:
            case DATE_DAYS_SINCE_1970:
            case DATE_DAYS_SINCE_1980:
            case TIME_SECONDS:
            case DATE_TIME_SECONDS_SINCE_0:
            case DATE_TIME_SECONDS_SINCE_1960:
            case DATE_TIME_SECONDS_SINCE_1970:
            case DATE_TIME_SECONDS_SINCE_1980:
                return new LongLiteralExpr(value);
            default:
                throw new IllegalArgumentException("Can't create literal from " + type.getName() + " data type");
        }
    }

    /**
     * Return a new {@link MethodCallExpr} from scope, name and arguments.
     * @param scope the scope of the method to call
     * @param name the name of the method to call
     * @param arguments vararg list of {@link Expression} arguments
     * @return the new {@link MethodCallExpr}
     */
    public static MethodCallExpr methodCallExprFrom(String scope, String name, Expression... arguments) {
        return new MethodCallExpr(new NameExpr(scope), name, new NodeList<>(arguments));
    }

    /**
     * Return a "chained" {@link MethodCallExpr} by name <b>parent</b> one.
     * @param name the name of the method to call
     * @param parent vararg list of {@link Expression} arguments
     * @return the found {@link MethodCallExpr}
     */
    public static MethodCallExpr getChainedMethodCallExprFrom(String name, MethodCallExpr parent) {
        return parent.stream()
                .filter(expr -> expr instanceof MethodCallExpr &&
                        ((MethodCallExpr) expr).getName().toString().equals(name))
                .map(MethodCallExpr.class::cast)
                .findFirst()
                .orElseThrow(() -> new KiePMMLException(String.format(MISSING_CHAINED_METHOD_DECLARATION_TEMPLATE,
                                                                      name, parent)));
    }

    /**
     * Replace <code>StringLiteralExpresion</code>s in the given <code>Statement</code>
     * @param container
     * @param toReplace
     * @param replacement
     */
    public static void replaceStringLiteralExpressionInStatement(final Statement container,
                                                                 final String toReplace,
                                                                 final String replacement) {
        final StringLiteralExpr toReplaceExpr = new StringLiteralExpr(toReplace);
        final StringLiteralExpr replacementExpr = new StringLiteralExpr(replacement);
        container.walk(node -> {
            if (node.equals(toReplaceExpr)) {
                node.getParentNode()
                        .ifPresent(parentNode -> parentNode.replace(node, replacementExpr));
            }
        });
    }

    /**
     * Replace <code>Node</code>s in the given <code>Statement</code>
     * @param container
     * @param replacementTuples
     */
    public static void replaceNodesInStatement(final Statement container,
                                               final List<ReplacementTuple> replacementTuples) {
        replacementTuples.forEach(replacementTuple -> replaceNodeInStatement(container, replacementTuple));
    }

    /**
     * Replace <code>Node</code> in the given <code>Statement</code>
     * @param container
     * @param replacementTuple
     */
    public static void replaceNodeInStatement(final Statement container,
                                              final ReplacementTuple replacementTuple) {
        container.walk(node -> {
            if (node.equals(replacementTuple.toReplace)) {
                node.getParentNode()
                        .ifPresent(parentNode -> parentNode.replace(replacementTuple.toReplace,
                                                                    replacementTuple.replacement));
            }
        });
    }

    /**
     * Add a <code>MethodDeclaration</code>s to the given <code>ClassOrInterfaceDeclaration</code>
     * @param classOrInterfaceDeclaration
     * @param toAdd
     */
    public static void addMethodDeclarationsToClass(final ClassOrInterfaceDeclaration classOrInterfaceDeclaration,
                                                    final List<MethodDeclaration> toAdd) {
        toAdd.forEach(methodDeclaration -> addMethodDeclarationToClass(classOrInterfaceDeclaration, methodDeclaration));
    }

    /**
     * Add a <code>MethodDeclaration</code> to the given <code>ClassOrInterfaceDeclaration</code>
     * @param classOrInterfaceDeclaration
     * @param toAdd
     */
    public static void addMethodDeclarationToClass(final ClassOrInterfaceDeclaration classOrInterfaceDeclaration,
                                                   final MethodDeclaration toAdd) {
        classOrInterfaceDeclaration.addMethod(toAdd.getName().asString())
                .setModifiers(toAdd.getModifiers())
                .setType(toAdd.getType())
                .setParameters(toAdd.getParameters())
                .setBody(toAdd.getBody().get());
    }

    /**
     * Retrieve the <b>initializer</b> of the given <b>variableName</b> from the given <code>MethodDeclaration</code>
     * @return
     */
    public static Expression getVariableInitializer(final MethodDeclaration methodDeclaration,
                                                    final String variableName) {
        return getOptionalVariableInitializer(methodDeclaration, variableName)
                .orElseThrow(() -> new KiePMMLException(String.format(MISSING_VARIABLE_INITIALIZER_TEMPLATE,
                                                                      variableName, methodDeclaration)));
    }

    /**
     * Retrieve the <b>initializer</b> of the given <b>variableName</b> from the given <code>MethodDeclaration</code>
     * @return
     */
    public static Optional<Expression> getOptionalVariableInitializer(final MethodDeclaration methodDeclaration,
                                                                      final String variableName) {
        final BlockStmt blockStmt = methodDeclaration.getBody()
                .orElseThrow(() -> new KiePMMLException(String.format(MISSING_BODY_TEMPLATE, methodDeclaration)));
        return getVariableInitializer(blockStmt, variableName);
    }

    /**
     * Retrieve the <b>initializer</b> of the given <b>variableName</b> from the given <code>MethodDeclaration</code>
     * @return
     */
    public static Optional<Expression> getVariableInitializer(final BlockStmt blockStmt, final String variableName) {
        final VariableDeclarator variableDeclarator = getVariableDeclarator(blockStmt, variableName)
                .orElseThrow(() -> new KiePMMLException(String.format(MISSING_VARIABLE_IN_BODY, variableName,
                                                                      blockStmt)));
        return variableDeclarator.getInitializer();
    }

    /**
     * Replace the <code>List&lt;NameExpr&gt;</code>s in the given <code>Statement</code> with
     * <code>NullLiteralExpr</code>
     * @param container
     * @param toReplace
     */
    public static void replaceNameExprWithNullInStatement(final Statement container,
                                                          final List<NameExpr> toReplace) {
        final List<ReplacementTuple> replacementTuples =
                toReplace.stream()
                        .map(nameExpr -> {
                            NullLiteralExpr toAdd = new NullLiteralExpr();
                            return new ReplacementTuple(nameExpr, toAdd);
                        })
                        .collect(Collectors.toList());
        replacementTuples.forEach(replacementTuple -> replaceNodeInStatement(container, replacementTuple));
    }

    public static MethodCallExpr getArraysAsListInvocationMethodCall(NodeList<Expression> arguments) {
        MethodCallExpr methodCallExpr = new MethodCallExpr();
        methodCallExpr.setScope(new NameExpr(Arrays.class.getSimpleName()));
        methodCallExpr.setName("asList");
        methodCallExpr.setArguments(arguments);
        return methodCallExpr;
    }

    public static NodeList<Expression> getArraysAsListInvocation(NodeList<Expression> arguments) {
        return NodeList.nodeList(getArraysAsListInvocationMethodCall(arguments));
    }

    public static class ReplacementTuple {

        final Node toReplace;
        final Node replacement;

        public ReplacementTuple(Node toReplace, Node replacement) {
            this.toReplace = toReplace;
            this.replacement = replacement;
        }
    }

    private static void createMap(final BlockStmt body,
                                  final String mapName,
                                  final List<String> mapTypes,
                                  final Class<? extends Map> mapClass) {
        final VariableDeclarator mapDeclarator =
                new VariableDeclarator(getTypedClassOrInterfaceTypeByTypeNames(Map.class.getName(), mapTypes),
                                       mapName);
        final ObjectCreationExpr mapInitializer = new ObjectCreationExpr();
        mapInitializer.setType(getTypedClassOrInterfaceTypeByTypeNames(mapClass.getName(), mapTypes));
        mapDeclarator.setInitializer(mapInitializer);
        final VariableDeclarationExpr mapDeclarationExpr =
                new VariableDeclarationExpr(mapDeclarator);
        body.addStatement(mapDeclarationExpr);
    }
}
