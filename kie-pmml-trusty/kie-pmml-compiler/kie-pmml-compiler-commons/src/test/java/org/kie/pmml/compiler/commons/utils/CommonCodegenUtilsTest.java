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
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.BodyDeclaration;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.AssignExpr;
import com.github.javaparser.ast.expr.BooleanLiteralExpr;
import com.github.javaparser.ast.expr.DoubleLiteralExpr;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.IntegerLiteralExpr;
import com.github.javaparser.ast.expr.LongLiteralExpr;
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
import com.github.javaparser.ast.stmt.Statement;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import org.junit.Test;
import org.kie.pmml.api.enums.DATA_TYPE;
import org.kie.pmml.api.exceptions.KiePMMLException;
import org.kie.pmml.commons.model.tuples.KiePMMLNameValue;

import static com.github.javaparser.StaticJavaParser.parseBlock;
import static com.github.javaparser.StaticJavaParser.parseClassOrInterfaceType;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;
import static org.kie.pmml.compiler.commons.testutils.CodegenTestUtils.commonValidateCompilation;
import static org.kie.pmml.compiler.commons.utils.CommonCodegenUtils.LAMBDA_PARAMETER_NAME;
import static org.kie.pmml.compiler.commons.utils.CommonCodegenUtils.OPTIONAL_FILTERED_KIEPMMLNAMEVALUE_NAME;
import static org.kie.pmml.compiler.commons.utils.CommonCodegenUtils.literalExprFrom;

public class CommonCodegenUtilsTest {

    @Test
    public void populateMethodDeclarations() {
        final List<MethodDeclaration> toAdd = IntStream.range(0, 5)
                .boxed()
                .map(index -> getMethodDeclaration("METHOD_" + index))
                .collect(Collectors.toList());
        final ClassOrInterfaceDeclaration toPopulate = new ClassOrInterfaceDeclaration();
        assertTrue(toPopulate.getMembers().isEmpty());
        CommonCodegenUtils.populateMethodDeclarations(toPopulate, toAdd);
        final NodeList<BodyDeclaration<?>> retrieved = toPopulate.getMembers();
        assertEquals(toAdd.size(), retrieved.size());
        assertTrue(toAdd.stream().anyMatch(methodDeclaration -> retrieved.stream()
                .anyMatch(bodyDeclaration -> bodyDeclaration.equals(methodDeclaration))));
    }

    @Test
    public void getFilteredKiePMMLNameValueExpression() {
        String kiePMMLNameValueListParam = "KIEPMMLNAMEVALUELISTPARAM";
        String fieldNameToRef = "FIELDNAMETOREF";
        ExpressionStmt retrieved = CommonCodegenUtils.getFilteredKiePMMLNameValueExpression(kiePMMLNameValueListParam, fieldNameToRef, true);
        assertNotNull(retrieved);
        String expected = String.format("%1$s<%2$s> %3$s = %4$s.stream().filter((%2$s %5$s) -> %6$s.equals(\"%7$s\", %5$s.getName())).findFirst();",
                                        Optional.class.getName(),
                                        KiePMMLNameValue.class.getName(),
                                        OPTIONAL_FILTERED_KIEPMMLNAMEVALUE_NAME,
                                        kiePMMLNameValueListParam,
                                        LAMBDA_PARAMETER_NAME,
                                        Objects.class.getName(),
                                        fieldNameToRef);
        String retrievedString = retrieved.toString();
        assertEquals(expected, retrievedString);
        BlockStmt body = new BlockStmt();
        body.addStatement(retrieved);
        Parameter listParameter = new Parameter(CommonCodegenUtils.getTypedClassOrInterfaceType(List.class.getName(), Collections.singletonList(KiePMMLNameValue.class.getName())), kiePMMLNameValueListParam);
        Parameter fieldRefParameter = new Parameter(parseClassOrInterfaceType(String.class.getName()), fieldNameToRef);
        commonValidateCompilation(body, Arrays.asList(listParameter, fieldRefParameter));
        //
        retrieved = CommonCodegenUtils.getFilteredKiePMMLNameValueExpression(kiePMMLNameValueListParam, fieldNameToRef, false);
        assertNotNull(retrieved);
        expected = String.format("%1$s<%2$s> %3$s = %4$s.stream().filter((%2$s %5$s) -> %6$s.equals(%7$s, %5$s.getName())).findFirst();",
                                        Optional.class.getName(),
                                        KiePMMLNameValue.class.getName(),
                                        OPTIONAL_FILTERED_KIEPMMLNAMEVALUE_NAME,
                                        kiePMMLNameValueListParam,
                                        LAMBDA_PARAMETER_NAME,
                                        Objects.class.getName(),
                                        fieldNameToRef);
        retrievedString = retrieved.toString();
        assertEquals(expected, retrievedString);
        body = new BlockStmt();
        body.addStatement(retrieved);
        listParameter = new Parameter(CommonCodegenUtils.getTypedClassOrInterfaceType(List.class.getName(), Collections.singletonList(KiePMMLNameValue.class.getName())), kiePMMLNameValueListParam);
        fieldRefParameter = new Parameter(parseClassOrInterfaceType(String.class.getName()), fieldNameToRef);
        commonValidateCompilation(body, Arrays.asList(listParameter, fieldRefParameter));
    }

    @Test
    public void addMapPopulation() {
        final Map<String, MethodDeclaration> toAdd = IntStream.range(0, 5).boxed().collect(Collectors.toMap(index -> "KEY_" + index, index -> getMethodDeclaration("METHOD_" + index)));
        BlockStmt body = new BlockStmt();
        String mapName = "MAP_NAME";
        CommonCodegenUtils.addMapPopulation(toAdd, body, mapName);
        NodeList<Statement> statements = body.getStatements();
        assertEquals(toAdd.size(), statements.size());
        for (Statement statement : statements) {
            assertTrue(statement instanceof ExpressionStmt);
            ExpressionStmt expressionStmt = (ExpressionStmt) statement;
            com.github.javaparser.ast.expr.Expression expression = expressionStmt.getExpression();
            assertTrue(expression instanceof MethodCallExpr);
            MethodCallExpr methodCallExpr = (MethodCallExpr) expression;
            final NodeList<com.github.javaparser.ast.expr.Expression> arguments = methodCallExpr.getArguments();
            assertEquals(2, arguments.size());
            assertTrue(arguments.get(0) instanceof StringLiteralExpr);
            assertTrue(arguments.get(1) instanceof MethodReferenceExpr);
            MethodReferenceExpr methodReferenceExpr = (MethodReferenceExpr) arguments.get(1);
            assertTrue(methodReferenceExpr.getScope() instanceof ThisExpr);
            final com.github.javaparser.ast.expr.Expression scope = methodCallExpr.getScope().orElse(null);
            assertNotNull(scope);
            assertTrue(scope instanceof NameExpr);
            assertEquals(mapName, ((NameExpr) scope).getNameAsString());
        }
        for (Map.Entry<String, MethodDeclaration> entry : toAdd.entrySet()) {
            int matchingDeclarations = (int) statements.stream().filter(statement -> {
                ExpressionStmt expressionStmt = (ExpressionStmt) statement;
                com.github.javaparser.ast.expr.Expression expression = expressionStmt.getExpression();
                MethodCallExpr methodCallExpr = (MethodCallExpr) expression;
                final NodeList<com.github.javaparser.ast.expr.Expression> arguments = methodCallExpr.getArguments();
                if (!entry.getKey().equals(((StringLiteralExpr) arguments.get(0)).getValue())) {
                    return false;
                }
                MethodReferenceExpr methodReferenceExpr = (MethodReferenceExpr) arguments.get(1);
                return entry.getValue().getName().asString().equals(methodReferenceExpr.getIdentifier());
            }).count();
            assertEquals(1, matchingDeclarations);
        }
    }

    @Test
    public void addMapPopulationExpression() {
        Map<String, Expression> inputMap = new HashMap<>();
        inputMap.put("one", new StringLiteralExpr("ONE"));
        inputMap.put("two", new IntegerLiteralExpr("2"));
        inputMap.put("three", new DoubleLiteralExpr("3.0"));

        BlockStmt inputBody = new BlockStmt();

        String inputMapName = "testMap";

        CommonCodegenUtils.addMapPopulationExpressions(inputMap, inputBody, inputMapName);

        NodeList<Statement> statements = inputBody.getStatements();
        assertEquals(inputMap.size(), statements.size());

        List<MethodCallExpr> methodCallExprs = new ArrayList<>(statements.size());

        for (Statement statement : statements) {
            assertTrue(statement instanceof ExpressionStmt);
            Expression expression = ((ExpressionStmt) statement).getExpression();
            assertTrue(expression instanceof  MethodCallExpr);
            MethodCallExpr methodCallExpr = (MethodCallExpr) expression;
            assertEquals(inputMapName, methodCallExpr.getScope().map(Node::toString).orElse(null));
            assertEquals("put", methodCallExpr.getName().asString());
            assertSame(2, methodCallExpr.getArguments().size());
            assertTrue(methodCallExpr.getArgument(0) instanceof StringLiteralExpr);
            methodCallExprs.add(methodCallExpr);
        }

        for (Map.Entry<String, Expression> inputEntry : inputMap.entrySet()) {
            assertEquals("Expected one and only one statement for key \"" + inputEntry.getKey() + "\"", 1,
                    methodCallExprs.stream().filter(methodCallExpr -> {
                        StringLiteralExpr arg0 = (StringLiteralExpr) methodCallExpr.getArgument(0);
                        return arg0.asString().equals(inputEntry.getKey())
                                && methodCallExpr.getArgument(1).equals(inputEntry.getValue());
                    }).count()
            );
        }
    }

    @Test
    public void addListPopulation() {
        final List<ObjectCreationExpr> toAdd = IntStream.range(0, 5)
                .mapToObj(i -> {
                    ObjectCreationExpr toReturn = new ObjectCreationExpr();
                    toReturn.setType(String.class);
                    Expression value = new StringLiteralExpr("String"+i);
                    toReturn.setArguments(NodeList.nodeList(value));
                    return toReturn;
                })
                .collect(Collectors.toList());
        BlockStmt body = new BlockStmt();
        String listName = "LIST_NAME";
        CommonCodegenUtils.addListPopulation(toAdd, body, listName);
        NodeList<Statement> statements = body.getStatements();
        assertEquals(toAdd.size(), statements.size());
        for (Statement statement : statements) {
            assertTrue(statement instanceof ExpressionStmt);
            ExpressionStmt expressionStmt = (ExpressionStmt) statement;
            assertTrue(expressionStmt.getExpression() instanceof MethodCallExpr);
            MethodCallExpr methodCallExpr = (MethodCallExpr) expressionStmt.getExpression();
            assertEquals(listName, methodCallExpr.getScope().get().asNameExpr().getNameAsString());
            NodeList<com.github.javaparser.ast.expr.Expression> arguments = methodCallExpr.getArguments();
            assertEquals(1, arguments.size());
            assertTrue(arguments.get(0) instanceof ObjectCreationExpr);
            ObjectCreationExpr objectCreationExpr = (ObjectCreationExpr) arguments.get(0);
            assertEquals(objectCreationExpr.getType().asString(), String.class.getSimpleName());
            arguments = objectCreationExpr.getArguments();
            assertEquals(1, arguments.size());
            assertTrue(arguments.get(0) instanceof StringLiteralExpr);
        }
        for (ObjectCreationExpr entry : toAdd) {
            int matchingDeclarations = (int) statements.stream().filter(statement -> {
                ExpressionStmt expressionStmt = (ExpressionStmt) statement;
                com.github.javaparser.ast.expr.Expression expression = expressionStmt.getExpression();
                MethodCallExpr methodCallExpr = (MethodCallExpr) expression;
                final NodeList<com.github.javaparser.ast.expr.Expression> arguments = methodCallExpr.getArguments();
                return entry.equals(arguments.get(0).asObjectCreationExpr());
            }).count();
            assertEquals(1, matchingDeclarations);
        }
    }

    @Test
    public void createArraysAsListExpression() {
        ExpressionStmt retrieved = CommonCodegenUtils.createArraysAsListExpression();
        assertNotNull(retrieved);
        String expected = "java.util.Arrays.asList();";
        String retrievedString = retrieved.toString();
        assertEquals(expected, retrievedString);
    }

    @Test
    public void createArraysAsListFromList() {
        List<String> strings = IntStream.range(0, 3)
                .mapToObj(i -> "Element" + i)
                .collect(Collectors.toList());
        ExpressionStmt retrieved = CommonCodegenUtils.createArraysAsListFromList(strings);
        assertNotNull(retrieved);
        String arguments = strings.stream()
                .map(string -> "\"" + string + "\"")
                .collect(Collectors.joining(", "));
        String expected = String.format("java.util.Arrays.asList(%s);", arguments);
        String retrievedString = retrieved.toString();
        assertEquals(expected, retrievedString);
        List<Double> doubles = IntStream.range(0, 3)
                .mapToObj(i ->  i * 0.17)
                .collect(Collectors.toList());
        retrieved = CommonCodegenUtils.createArraysAsListFromList(doubles);
        assertNotNull(retrieved);
        arguments = doubles.stream()
                .map(String::valueOf)
                .collect(Collectors.joining(", "));
        expected = String.format("java.util.Arrays.asList(%s);", arguments);
        retrievedString = retrieved.toString();
        assertEquals(expected, retrievedString);
    }

    @Test
    public void getParamMethodDeclaration() {
        String methodName = "METHOD_NAME";
        final Map<String, ClassOrInterfaceType> parameterNameTypeMap = new HashMap<>();
        parameterNameTypeMap.put("stringParam", parseClassOrInterfaceType(String.class.getName()));
        parameterNameTypeMap.put("kiePMMLNameValueParam", parseClassOrInterfaceType(KiePMMLNameValue.class.getName()));
        parameterNameTypeMap.put("listParam", new ClassOrInterfaceType(null, new SimpleName(List.class.getName()), NodeList.nodeList(parseClassOrInterfaceType(KiePMMLNameValue.class.getName()))));
        MethodDeclaration retrieved = CommonCodegenUtils.getMethodDeclaration(methodName, parameterNameTypeMap);
        commonValidateMethodDeclaration(retrieved, methodName);
        commonValidateMethodDeclarationParams(retrieved, parameterNameTypeMap);
    }

    @Test
    public void getNoParamMethodDeclarationByString() {
        String methodName = "METHOD_NAME";
        MethodDeclaration retrieved = CommonCodegenUtils.getMethodDeclaration(methodName);
        commonValidateMethodDeclaration(retrieved, methodName);
    }

    @Test
    public void getReturnStmt() {
        String returnedVariable = "RETURNED_VARIABLE";
        ReturnStmt retrieved = CommonCodegenUtils.getReturnStmt(returnedVariable);
        String expected = String.format("return %s;", returnedVariable);
        assertEquals(expected, retrieved.toString());
    }

    @Test
    public void getTypedClassOrInterfaceType() {
        String className = "CLASS_NAME";
        List<String> typesName = Arrays.asList("TypeA", "TypeB");
        ClassOrInterfaceType retrieved = CommonCodegenUtils.getTypedClassOrInterfaceType(className, typesName);
        assertNotNull(retrieved);
        String expected = String.format("%1$s<%2$s,%3$s>", className, typesName.get(0), typesName.get(1));
        assertEquals(expected, retrieved.asString());
    }

    @Test
    public void setAssignExpressionValueMatch() {
        final BlockStmt body = new BlockStmt();
        AssignExpr assignExpr = new AssignExpr();
        assignExpr.setTarget(new NameExpr("MATCH"));
        body.addStatement(assignExpr);
        final Expression value = new DoubleLiteralExpr(24.22);
        CommonCodegenUtils.setAssignExpressionValue(body, "MATCH", value);
        assertEquals(value, assignExpr.getValue());
    }

    @Test(expected = KiePMMLException.class)
    public void setAssignExpressionValueNoMatch() {
        final BlockStmt body = new BlockStmt();
        AssignExpr assignExpr = new AssignExpr();
        assignExpr.setTarget(new NameExpr("MATCH"));
        body.addStatement(assignExpr);
        CommonCodegenUtils.setAssignExpressionValue(body, "NOMATCH", new DoubleLiteralExpr(24.22));
    }

    @Test(expected = KiePMMLException.class)
    public void setAssignExpressionValueNoAssignExpressions() {
        final BlockStmt body = new BlockStmt();
        CommonCodegenUtils.setAssignExpressionValue(body, "NOMATCH", new DoubleLiteralExpr(24.22));
    }

    @Test
    public void getAssignExpression() {
        BlockStmt body = new BlockStmt();
        Optional<AssignExpr> retrieved = CommonCodegenUtils.getAssignExpression(body, "NOMATCH");
        assertNotNull(retrieved);
        assertFalse(retrieved.isPresent());
        AssignExpr assignExpr = new AssignExpr();
        assignExpr.setTarget(new NameExpr("MATCH"));
        body.addStatement(assignExpr);
        retrieved = CommonCodegenUtils.getAssignExpression(body, "NOMATCH");
        assertNotNull(retrieved);
        assertFalse(retrieved.isPresent());
        retrieved = CommonCodegenUtils.getAssignExpression(body, "MATCH");
        assertNotNull(retrieved);
        assertTrue(retrieved.isPresent());
        AssignExpr retrievedAssignExpr = retrieved.get();
        assertEquals(assignExpr, retrievedAssignExpr);
    }

    @Test
    public void getExplicitConstructorInvocationStmt() {
        BlockStmt body = new BlockStmt();
        Optional<ExplicitConstructorInvocationStmt> retrieved = CommonCodegenUtils.getExplicitConstructorInvocationStmt(body);
        assertNotNull(retrieved);
        assertFalse(retrieved.isPresent());
        ExplicitConstructorInvocationStmt explicitConstructorInvocationStmt = new ExplicitConstructorInvocationStmt();
        body.addStatement(explicitConstructorInvocationStmt);
        retrieved = CommonCodegenUtils.getExplicitConstructorInvocationStmt(body);
        assertNotNull(retrieved);
        assertTrue(retrieved.isPresent());
        ExplicitConstructorInvocationStmt retrievedExplicitConstructorInvocationStmt = retrieved.get();
        assertEquals(explicitConstructorInvocationStmt, retrievedExplicitConstructorInvocationStmt);
    }

    @Test
    public void setExplicitConstructorInvocationStmtArgumentWithParameter() {
        final String parameterName = "PARAMETER_NAME";
        final String value = "VALUE";
        final ExplicitConstructorInvocationStmt explicitConstructorInvocationStmt = new ExplicitConstructorInvocationStmt();
        explicitConstructorInvocationStmt.setArguments(NodeList.nodeList( new NameExpr("NOT_PARAMETER"), new NameExpr(parameterName)));
        assertTrue(CommonCodegenUtils.getExplicitConstructorInvocationParameter(explicitConstructorInvocationStmt, parameterName).isPresent());
        CommonCodegenUtils.setExplicitConstructorInvocationStmtArgument(explicitConstructorInvocationStmt, parameterName, value);
        assertFalse(CommonCodegenUtils.getExplicitConstructorInvocationParameter(explicitConstructorInvocationStmt, parameterName).isPresent());
        assertTrue(CommonCodegenUtils.getExplicitConstructorInvocationParameter(explicitConstructorInvocationStmt, value).isPresent());
    }

    @Test(expected = KiePMMLException.class)
    public void setExplicitConstructorInvocationStmtArgumentNoParameter() {
        final String parameterName = "PARAMETER_NAME";
        final ExplicitConstructorInvocationStmt explicitConstructorInvocationStmt = new ExplicitConstructorInvocationStmt();
        explicitConstructorInvocationStmt.setArguments(NodeList.nodeList( new NameExpr("NOT_PARAMETER")));
        CommonCodegenUtils.setExplicitConstructorInvocationStmtArgument(explicitConstructorInvocationStmt, parameterName, "VALUE");
    }

    @Test
    public void getExplicitConstructorInvocationParameter() {
        final String parameterName = "PARAMETER_NAME";
        final ExplicitConstructorInvocationStmt explicitConstructorInvocationStmt = new ExplicitConstructorInvocationStmt();
        assertFalse(CommonCodegenUtils.getExplicitConstructorInvocationParameter(explicitConstructorInvocationStmt, parameterName).isPresent());
        explicitConstructorInvocationStmt.setArguments(NodeList.nodeList( new NameExpr("NOT_PARAMETER")));
        assertFalse(CommonCodegenUtils.getExplicitConstructorInvocationParameter(explicitConstructorInvocationStmt, parameterName).isPresent());
        explicitConstructorInvocationStmt.setArguments(NodeList.nodeList( new NameExpr("NOT_PARAMETER"), new NameExpr(parameterName)));
        assertTrue(CommonCodegenUtils.getExplicitConstructorInvocationParameter(explicitConstructorInvocationStmt, parameterName).isPresent());
    }

    @Test
    public void getMethodDeclaration() {
        final String methodName = "METHOD_NAME";
        final ClassOrInterfaceDeclaration classOrInterfaceDeclaration = new ClassOrInterfaceDeclaration();
        assertFalse(CommonCodegenUtils.getMethodDeclaration(classOrInterfaceDeclaration, methodName).isPresent());
        classOrInterfaceDeclaration.addMethod("NOT_METHOD");
        assertFalse(CommonCodegenUtils.getMethodDeclaration(classOrInterfaceDeclaration, methodName).isPresent());
        classOrInterfaceDeclaration.addMethod(methodName);
        assertTrue(CommonCodegenUtils.getMethodDeclaration(classOrInterfaceDeclaration, methodName).isPresent());
    }

    @Test
    public void addMethod() {
        final MethodDeclaration methodTemplate = new MethodDeclaration();
        methodTemplate.setName("methodTemplate");
        final BlockStmt body = new BlockStmt();
        methodTemplate.setBody(body);
        final String methodName = "METHOD_NAME";
        final ClassOrInterfaceDeclaration classOrInterfaceDeclaration = new ClassOrInterfaceDeclaration();
        assertTrue(classOrInterfaceDeclaration.getMethodsByName(methodName).isEmpty());
        CommonCodegenUtils.addMethod(methodTemplate, classOrInterfaceDeclaration, methodName);
        assertEquals(1, classOrInterfaceDeclaration.getMethodsByName(methodName).size());
        assertEquals(body, classOrInterfaceDeclaration.getMethodsByName(methodName).get(0).getBody().get());
    }

    @Test
    public void getVariableDeclarator() {
        final String variableName = "variableName";
        final BlockStmt body = new BlockStmt();
        assertFalse(CommonCodegenUtils.getVariableDeclarator(body, variableName).isPresent());
        final VariableDeclarationExpr variableDeclarationExpr = new VariableDeclarationExpr(parseClassOrInterfaceType("String"), variableName);
        body.addStatement(variableDeclarationExpr);
        Optional<VariableDeclarator> retrieved =  CommonCodegenUtils.getVariableDeclarator(body, variableName);
        assertTrue(retrieved.isPresent());
        VariableDeclarator variableDeclarator = retrieved.get();
        assertEquals(variableName, variableDeclarator.getName().asString());
    }

    @Test
    public void getExpressionForObject() {
        String string = "string";
        Expression retrieved = CommonCodegenUtils.getExpressionForObject(string);
        assertTrue(retrieved instanceof  StringLiteralExpr);
        assertEquals("\"string\"", retrieved.toString());
        int i = 1;
        retrieved = CommonCodegenUtils.getExpressionForObject(i);
        assertTrue(retrieved instanceof  IntegerLiteralExpr);
        assertEquals(i, ((IntegerLiteralExpr)retrieved).asInt());
        Integer j = 3;
        retrieved = CommonCodegenUtils.getExpressionForObject(j);
        assertTrue(retrieved instanceof  IntegerLiteralExpr);
        assertEquals(j.intValue(), ((IntegerLiteralExpr)retrieved).asInt());
        double x = 1.12;
        retrieved = CommonCodegenUtils.getExpressionForObject(x);
        assertTrue(retrieved instanceof  DoubleLiteralExpr);
        assertEquals(x, ((DoubleLiteralExpr)retrieved).asDouble(), 0.001);
        Double y = 3.12;
        retrieved = CommonCodegenUtils.getExpressionForObject(y);
        assertTrue(retrieved instanceof  DoubleLiteralExpr);
        assertEquals(y, ((DoubleLiteralExpr)retrieved).asDouble(), 0.001);
        float k = 1.12f;
        retrieved = CommonCodegenUtils.getExpressionForObject(k);
        assertTrue(retrieved instanceof  DoubleLiteralExpr);
        assertEquals(1.12, ((DoubleLiteralExpr)retrieved).asDouble(), 0.001);
        Float z = 3.12f;
        retrieved = CommonCodegenUtils.getExpressionForObject(z);
        assertTrue(retrieved instanceof  DoubleLiteralExpr);
        assertEquals(z.doubleValue(), ((DoubleLiteralExpr)retrieved).asDouble(), 0.001);
        boolean b = true;
        retrieved = CommonCodegenUtils.getExpressionForObject(b);
        assertTrue(retrieved instanceof BooleanLiteralExpr);
        assertEquals(b, ((BooleanLiteralExpr)retrieved).getValue());
        Boolean c = false;
        retrieved = CommonCodegenUtils.getExpressionForObject(c);
        assertTrue(retrieved instanceof BooleanLiteralExpr);
        assertEquals(c, ((BooleanLiteralExpr)retrieved).getValue());
    }

    @Test
    public void getNameExprsFromBlock() {
        BlockStmt toRead = new BlockStmt();
        List<NameExpr> retrieved = CommonCodegenUtils.getNameExprsFromBlock(toRead, "value");
        assertNotNull(retrieved);
        assertTrue(retrieved.isEmpty());
        toRead = getBlockStmt();
        retrieved = CommonCodegenUtils.getNameExprsFromBlock(toRead, "value");
        assertNotNull(retrieved);
        assertEquals(2, retrieved.size());
    }

    @Test
    public void literalExprFromDataType() {
        Map<DATA_TYPE, String> inputMap = new HashMap<>();
        inputMap.put(DATA_TYPE.STRING, "TEST");
        inputMap.put(DATA_TYPE.INTEGER, "1");
        inputMap.put(DATA_TYPE.FLOAT, "2.0");
        inputMap.put(DATA_TYPE.DOUBLE, "3.0");
        inputMap.put(DATA_TYPE.BOOLEAN, "true");
        inputMap.put(DATA_TYPE.DATE, "2021-06-01");
        inputMap.put(DATA_TYPE.TIME, "11:21:31");
        inputMap.put(DATA_TYPE.DATE_TIME, "2021-06-01T11:21:31");
        inputMap.put(DATA_TYPE.DATE_DAYS_SINCE_0, "10");
        inputMap.put(DATA_TYPE.DATE_DAYS_SINCE_1960, "20");
        inputMap.put(DATA_TYPE.DATE_DAYS_SINCE_1970, "30");
        inputMap.put(DATA_TYPE.DATE_DAYS_SINCE_1980, "40");
        inputMap.put(DATA_TYPE.TIME_SECONDS, "50");
        inputMap.put(DATA_TYPE.DATE_TIME_SECONDS_SINCE_0, "60");
        inputMap.put(DATA_TYPE.DATE_TIME_SECONDS_SINCE_1960, "70");
        inputMap.put(DATA_TYPE.DATE_TIME_SECONDS_SINCE_1970, "80");
        inputMap.put(DATA_TYPE.DATE_TIME_SECONDS_SINCE_1980, "90");

        for (Map.Entry<DATA_TYPE, String> input : inputMap.entrySet()) {
            assertTrue(literalExprFrom(input.getKey(), null) instanceof NullLiteralExpr);

            Expression output = literalExprFrom(input.getKey(), input.getValue());
            switch (input.getKey()) {
                case STRING:
                    assertTrue(output instanceof StringLiteralExpr);
                    break;
                case INTEGER:
                    assertTrue(output instanceof IntegerLiteralExpr);
                    break;
                case DOUBLE:
                case FLOAT:
                    assertTrue(output instanceof DoubleLiteralExpr);
                    break;
                case BOOLEAN:
                    assertTrue(output instanceof BooleanLiteralExpr);
                    break;
                case DATE:
                case TIME:
                case DATE_TIME:
                    assertTrue(output instanceof MethodCallExpr);
                    break;
                case DATE_DAYS_SINCE_0:
                case DATE_DAYS_SINCE_1960:
                case DATE_DAYS_SINCE_1970:
                case DATE_DAYS_SINCE_1980:
                case TIME_SECONDS:
                case DATE_TIME_SECONDS_SINCE_0:
                case DATE_TIME_SECONDS_SINCE_1960:
                case DATE_TIME_SECONDS_SINCE_1970:
                case DATE_TIME_SECONDS_SINCE_1980:
                    assertTrue(output instanceof LongLiteralExpr);
            }
        }

        assertThrows(IllegalArgumentException.class, () -> literalExprFrom(null, null));
        assertThrows(IllegalArgumentException.class, () -> literalExprFrom(null, "test"));
    }

    @Test
    public void replaceNodesInBlock() {
        final BlockStmt toRead = getBlockStmt();
        final List<NameExpr> retrieved = CommonCodegenUtils.getNameExprsFromBlock(toRead, "value");
        assertEquals(2, retrieved.size());
        final List<NullLiteralExpr> nullExprs =  toRead.stream()
                .filter(node -> node instanceof NullLiteralExpr)
                .map(NullLiteralExpr.class::cast)
                .collect(Collectors.toList());
        assertNotNull(nullExprs);
        assertTrue(nullExprs.isEmpty());

        final List<CommonCodegenUtils.ReplacementTupla> replacementTuplas =
                retrieved.stream()
                        .map(nameExpr -> {
                            NullLiteralExpr toAdd = new NullLiteralExpr();
                            nullExprs.add(toAdd);
                            return new CommonCodegenUtils.ReplacementTupla(nameExpr, toAdd);
                        })
                .collect(Collectors.toList());
        CommonCodegenUtils.replaceNodesInStatement(toRead, replacementTuplas);
        final List<NameExpr> newRetrieved = CommonCodegenUtils.getNameExprsFromBlock(toRead, "value");
        assertTrue(newRetrieved.isEmpty());

        final List<NullLiteralExpr> retrievedNullExprs =  toRead.stream()
                .filter(node -> node instanceof NullLiteralExpr)
                .map(NullLiteralExpr.class::cast)
                .collect(Collectors.toList());
        assertNotNull(nullExprs);
        assertEquals(nullExprs.size(), retrievedNullExprs.size());
        nullExprs.forEach(nullExpr -> assertTrue(retrievedNullExprs.contains(nullExpr)));
    }

    private BlockStmt getBlockStmt() {
        String blockStatement = "{\nObject inputValue = 12;\n" +
                "        if (stringObjectMap.containsKey(\"avalue\")) {\n" +
                "            inputValue = stringObjectMap.get(\"avalue\");\n" +
                "        } else {\n" +
                "            return false;\n" +
                "        }\n" +
                "        if (inputValue instanceof Number && value instanceof Number) {\n" +
                "            return ((Number) inputValue).doubleValue() >= ((Number) value).doubleValue();\n" +
                "        } else {\n" +
                "            return false;\n" +
                "        }\n}";
        return parseBlock(blockStatement);
    }

    private void commonValidateMethodDeclaration(MethodDeclaration toValidate, String methodName) {
        assertNotNull(toValidate);
        assertEquals(methodName, toValidate.getName().asString());
    }

    private void commonValidateMethodDeclarationParams(MethodDeclaration toValidate, Map<String, ClassOrInterfaceType> parameterNameTypeMap) {
        final NodeList<Parameter> retrieved = toValidate.getParameters();
        assertNotNull(retrieved);
        assertEquals(parameterNameTypeMap.size(), retrieved.size());
        for (Parameter parameter : retrieved) {
            assertTrue(parameterNameTypeMap.containsKey(parameter.getNameAsString()));
            assertEquals(parameterNameTypeMap.get(parameter.getNameAsString()), parameter.getType());
        }
    }

    private MethodDeclaration getMethodDeclaration(String methodName) {
        return CommonCodegenUtils.getMethodDeclaration(methodName);
    }
}