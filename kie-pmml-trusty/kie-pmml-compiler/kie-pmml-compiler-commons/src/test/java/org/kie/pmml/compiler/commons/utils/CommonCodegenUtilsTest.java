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
import org.assertj.core.data.Offset;
import org.junit.jupiter.api.Test;
import org.kie.pmml.api.enums.DATA_TYPE;
import org.kie.pmml.api.exceptions.KiePMMLException;
import org.kie.pmml.commons.model.tuples.KiePMMLNameValue;

import static com.github.javaparser.StaticJavaParser.parseBlock;
import static com.github.javaparser.StaticJavaParser.parseClassOrInterfaceType;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatExceptionOfType;
import static org.kie.pmml.compiler.commons.testutils.CodegenTestUtils.commonValidateCompilation;
import static org.kie.pmml.compiler.commons.utils.CommonCodegenUtils.LAMBDA_PARAMETER_NAME;
import static org.kie.pmml.compiler.commons.utils.CommonCodegenUtils.OPTIONAL_FILTERED_KIEPMMLNAMEVALUE_NAME;
import static org.kie.pmml.compiler.commons.utils.CommonCodegenUtils.literalExprFrom;

public class CommonCodegenUtilsTest {

    @Test
    void populateMethodDeclarations() {
        final List<MethodDeclaration> toAdd = IntStream.range(0, 5)
                .boxed()
                .map(index -> getMethodDeclaration("METHOD_" + index))
                .collect(Collectors.toList());
        final ClassOrInterfaceDeclaration toPopulate = new ClassOrInterfaceDeclaration();
        assertThat(toPopulate.getMembers()).isEmpty();
        CommonCodegenUtils.populateMethodDeclarations(toPopulate, toAdd);
        final NodeList<BodyDeclaration<?>> retrieved = toPopulate.getMembers();
        assertThat(retrieved).hasSameSizeAs(toAdd);
        assertThat(toAdd.stream().anyMatch(methodDeclaration -> retrieved.stream()
                .anyMatch(bodyDeclaration -> bodyDeclaration.equals(methodDeclaration)))).isTrue();
    }

    @Test
    void getFilteredKiePMMLNameValueExpression() {
        String kiePMMLNameValueListParam = "KIEPMMLNAMEVALUELISTPARAM";
        String fieldNameToRef = "FIELDNAMETOREF";
        ExpressionStmt retrieved = CommonCodegenUtils.getFilteredKiePMMLNameValueExpression(kiePMMLNameValueListParam, fieldNameToRef, true);
        assertThat(retrieved).isNotNull();
        String expected = String.format("%1$s<%2$s> %3$s = %4$s.stream().filter((%2$s %5$s) -> %6$s.equals(\"%7$s\", %5$s.getName())).findFirst();",
                Optional.class.getName(),
                KiePMMLNameValue.class.getName(),
                OPTIONAL_FILTERED_KIEPMMLNAMEVALUE_NAME,
                kiePMMLNameValueListParam,
                LAMBDA_PARAMETER_NAME,
                Objects.class.getName(),
                fieldNameToRef);
        String retrievedString = retrieved.toString();
        assertThat(retrievedString).isEqualTo(expected);
        BlockStmt body = new BlockStmt();
        body.addStatement(retrieved);
        Parameter listParameter = new Parameter(CommonCodegenUtils.getTypedClassOrInterfaceTypeByTypeNames(List.class.getName(), Collections.singletonList(KiePMMLNameValue.class.getName())), kiePMMLNameValueListParam);
        Parameter fieldRefParameter = new Parameter(parseClassOrInterfaceType(String.class.getName()), fieldNameToRef);
        commonValidateCompilation(body, Arrays.asList(listParameter, fieldRefParameter));
        //
        retrieved = CommonCodegenUtils.getFilteredKiePMMLNameValueExpression(kiePMMLNameValueListParam, fieldNameToRef, false);
        assertThat(retrieved).isNotNull();
        expected = String.format("%1$s<%2$s> %3$s = %4$s.stream().filter((%2$s %5$s) -> %6$s.equals(%7$s, %5$s.getName())).findFirst();",
                Optional.class.getName(),
                KiePMMLNameValue.class.getName(),
                OPTIONAL_FILTERED_KIEPMMLNAMEVALUE_NAME,
                kiePMMLNameValueListParam,
                LAMBDA_PARAMETER_NAME,
                Objects.class.getName(),
                fieldNameToRef);
        retrievedString = retrieved.toString();
        assertThat(retrievedString).isEqualTo(expected);
        body = new BlockStmt();
        body.addStatement(retrieved);
        listParameter = new Parameter(CommonCodegenUtils.getTypedClassOrInterfaceTypeByTypeNames(List.class.getName(), Collections.singletonList(KiePMMLNameValue.class.getName())), kiePMMLNameValueListParam);
        fieldRefParameter = new Parameter(parseClassOrInterfaceType(String.class.getName()), fieldNameToRef);
        commonValidateCompilation(body, Arrays.asList(listParameter, fieldRefParameter));
    }

    @Test
    void addMapPopulation() {
        final Map<String, MethodDeclaration> toAdd = IntStream.range(0, 5).boxed().collect(Collectors.toMap(index -> "KEY_" + index, index -> getMethodDeclaration("METHOD_" + index)));
        BlockStmt body = new BlockStmt();
        String mapName = "MAP_NAME";
        CommonCodegenUtils.addMapPopulation(toAdd, body, mapName);
        NodeList<Statement> statements = body.getStatements();
        assertThat(statements).hasSize(toAdd.size());
        for (Statement statement : statements) {
            assertThat(statement).isInstanceOf(ExpressionStmt.class);
            ExpressionStmt expressionStmt = (ExpressionStmt) statement;
            com.github.javaparser.ast.expr.Expression expression = expressionStmt.getExpression();
            assertThat(expression).isInstanceOf(MethodCallExpr.class);
            MethodCallExpr methodCallExpr = (MethodCallExpr) expression;
            final NodeList<com.github.javaparser.ast.expr.Expression> arguments = methodCallExpr.getArguments();
            assertThat(arguments).hasSize(2);
            assertThat(arguments.get(0)).isInstanceOf(StringLiteralExpr.class);
            assertThat(arguments.get(1)).isInstanceOf(MethodReferenceExpr.class);
            MethodReferenceExpr methodReferenceExpr = (MethodReferenceExpr) arguments.get(1);
            assertThat(methodReferenceExpr.getScope()).isInstanceOf(ThisExpr.class);
            final com.github.javaparser.ast.expr.Expression scope = methodCallExpr.getScope().orElse(null);
            assertThat(scope).isNotNull();
            assertThat(scope).isInstanceOf(NameExpr.class);
            assertThat(((NameExpr) scope).getNameAsString()).isEqualTo(mapName);
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
            assertThat(matchingDeclarations).isEqualTo(1);
        }
    }

    @Test
    void addMapPopulationExpression() {
        Map<String, Expression> inputMap = new HashMap<>();
        inputMap.put("one", new StringLiteralExpr("ONE"));
        inputMap.put("two", new IntegerLiteralExpr("2"));
        inputMap.put("three", new DoubleLiteralExpr("3.0"));

        BlockStmt inputBody = new BlockStmt();

        String inputMapName = "testMap";

        CommonCodegenUtils.addMapPopulationExpressions(inputMap, inputBody, inputMapName);

        NodeList<Statement> statements = inputBody.getStatements();
        assertThat(statements).hasSize(inputMap.size());

        List<MethodCallExpr> methodCallExprs = new ArrayList<>(statements.size());

        for (Statement statement : statements) {
            assertThat(statement).isInstanceOf(ExpressionStmt.class);
            Expression expression = ((ExpressionStmt) statement).getExpression();
            assertThat(expression).isInstanceOf(MethodCallExpr.class);
            MethodCallExpr methodCallExpr = (MethodCallExpr) expression;
            assertThat(methodCallExpr.getScope().map(Node::toString).orElse(null)).isEqualTo(inputMapName);
            assertThat(methodCallExpr.getName().asString()).isEqualTo("put");
            assertThat(methodCallExpr.getArguments()).hasSize(2);
            assertThat(methodCallExpr.getArgument(0)).isInstanceOf(StringLiteralExpr.class);
            methodCallExprs.add(methodCallExpr);
        }

        for (Map.Entry<String, Expression> inputEntry : inputMap.entrySet()) {
            assertThat(
                    methodCallExprs.stream().filter(methodCallExpr -> {
                        StringLiteralExpr arg0 = (StringLiteralExpr) methodCallExpr.getArgument(0);
                        return arg0.asString().equals(inputEntry.getKey())
                                && methodCallExpr.getArgument(1).equals(inputEntry.getValue());
                    })).as("Expected one and only one statement for key \"" + inputEntry.getKey() + "\"").hasSize(1);
        }
    }

    @Test
    void addListPopulation() {
        final List<ObjectCreationExpr> toAdd = IntStream.range(0, 5)
                .mapToObj(i -> {
                    ObjectCreationExpr toReturn = new ObjectCreationExpr();
                    toReturn.setType(String.class);
                    Expression value = new StringLiteralExpr("String" + i);
                    toReturn.setArguments(NodeList.nodeList(value));
                    return toReturn;
                })
                .collect(Collectors.toList());
        BlockStmt body = new BlockStmt();
        String listName = "LIST_NAME";
        CommonCodegenUtils.addListPopulationByObjectCreationExpr(toAdd, body, listName);
        NodeList<Statement> statements = body.getStatements();
        assertThat(statements).hasSameSizeAs(toAdd);
        for (Statement statement : statements) {
            assertThat(statement).isInstanceOf(ExpressionStmt.class);
            ExpressionStmt expressionStmt = (ExpressionStmt) statement;
            assertThat(expressionStmt.getExpression()).isInstanceOf(MethodCallExpr.class);
            MethodCallExpr methodCallExpr = (MethodCallExpr) expressionStmt.getExpression();
            assertThat(methodCallExpr.getScope().get().asNameExpr().getNameAsString()).isEqualTo(listName);
            NodeList<com.github.javaparser.ast.expr.Expression> arguments = methodCallExpr.getArguments();
            assertThat(arguments).hasSize(1);
            assertThat(arguments.get(0)).isInstanceOf(ObjectCreationExpr.class);
            ObjectCreationExpr objectCreationExpr = (ObjectCreationExpr) arguments.get(0);
            assertThat(String.class.getSimpleName()).isEqualTo(objectCreationExpr.getType().asString());
            arguments = objectCreationExpr.getArguments();
            assertThat(arguments).hasSize(1);
            assertThat(arguments.get(0)).isInstanceOf(StringLiteralExpr.class);
        }
        for (ObjectCreationExpr entry : toAdd) {
            int matchingDeclarations = (int) statements.stream().filter(statement -> {
                ExpressionStmt expressionStmt = (ExpressionStmt) statement;
                com.github.javaparser.ast.expr.Expression expression = expressionStmt.getExpression();
                MethodCallExpr methodCallExpr = (MethodCallExpr) expression;
                final NodeList<com.github.javaparser.ast.expr.Expression> arguments = methodCallExpr.getArguments();
                return entry.equals(arguments.get(0).asObjectCreationExpr());
            }).count();
            assertThat(matchingDeclarations).isEqualTo(1);
        }
    }

    @Test
    void createArraysAsListExpression() {
        ExpressionStmt retrieved = CommonCodegenUtils.createArraysAsListExpression();
        assertThat(retrieved).isNotNull();
        String expected = "java.util.Arrays.asList();";
        String retrievedString = retrieved.toString();
        assertThat(retrievedString).isEqualTo(expected);
    }

    @Test
    void createArraysAsListFromList() {
        List<String> strings = IntStream.range(0, 3)
                .mapToObj(i -> "Element" + i)
                .collect(Collectors.toList());
        ExpressionStmt retrieved = CommonCodegenUtils.createArraysAsListFromList(strings);
        assertThat(retrieved).isNotNull();
        String arguments = strings.stream()
                .map(string -> "\"" + string + "\"")
                .collect(Collectors.joining(", "));
        String expected = String.format("java.util.Arrays.asList(%s);", arguments);
        String retrievedString = retrieved.toString();
        assertThat(retrievedString).isEqualTo(expected);
        List<Double> doubles = IntStream.range(0, 3)
                .mapToObj(i ->  i * 0.17)
                .collect(Collectors.toList());
        retrieved = CommonCodegenUtils.createArraysAsListFromList(doubles);
        assertThat(retrieved).isNotNull();
        arguments = doubles.stream()
                .map(String::valueOf)
                .collect(Collectors.joining(", "));
        expected = String.format("java.util.Arrays.asList(%s);", arguments);
        retrievedString = retrieved.toString();
        assertThat(retrievedString).isEqualTo(expected);
    }

    @Test
    void getParamMethodDeclaration() {
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
    void getNoParamMethodDeclarationByString() {
        String methodName = "METHOD_NAME";
        MethodDeclaration retrieved = CommonCodegenUtils.getMethodDeclaration(methodName);
        commonValidateMethodDeclaration(retrieved, methodName);
    }

    @Test
    void getReturnStmt() {
        String returnedVariable = "RETURNED_VARIABLE";
        ReturnStmt retrieved = CommonCodegenUtils.getReturnStmt(returnedVariable);
        String expected = String.format("return %s;", returnedVariable);
        assertThat(retrieved.toString()).isEqualTo(expected);
    }

    @Test
    void getTypedClassOrInterfaceType() {
        String className = "CLASS_NAME";
        List<String> typesName = Arrays.asList("TypeA", "TypeB");
        ClassOrInterfaceType retrieved = CommonCodegenUtils.getTypedClassOrInterfaceTypeByTypeNames(className, typesName);
        assertThat(retrieved).isNotNull();
        String expected = String.format("%1$s<%2$s,%3$s>", className, typesName.get(0), typesName.get(1));
        assertThat(retrieved.asString()).isEqualTo(expected);
    }

    @Test
    void setAssignExpressionValueMatch() {
        final BlockStmt body = new BlockStmt();
        AssignExpr assignExpr = new AssignExpr();
        assignExpr.setTarget(new NameExpr("MATCH"));
        body.addStatement(assignExpr);
        final Expression value = new DoubleLiteralExpr(24.22);
        CommonCodegenUtils.setAssignExpressionValue(body, "MATCH", value);
        assertThat(assignExpr.getValue()).isEqualTo(value);
    }

    @Test
    void setAssignExpressionValueNoMatch() {
        assertThatExceptionOfType(KiePMMLException.class).isThrownBy(() -> {
            final BlockStmt body = new BlockStmt();
            AssignExpr assignExpr = new AssignExpr();
            assignExpr.setTarget(new NameExpr("MATCH"));
            body.addStatement(assignExpr);
            CommonCodegenUtils.setAssignExpressionValue(body, "NOMATCH", new DoubleLiteralExpr(24.22));
        });
    }

    @Test
    void setAssignExpressionValueNoAssignExpressions() {
        assertThatExceptionOfType(KiePMMLException.class).isThrownBy(() -> {
            final BlockStmt body = new BlockStmt();
            CommonCodegenUtils.setAssignExpressionValue(body, "NOMATCH", new DoubleLiteralExpr(24.22));
        });
    }

    @Test
    void getAssignExpression() {
        BlockStmt body = new BlockStmt();
        Optional<AssignExpr> retrieved = CommonCodegenUtils.getAssignExpression(body, "NOMATCH");
        assertThat(retrieved).isNotNull();
        assertThat(retrieved).isNotPresent();
        AssignExpr assignExpr = new AssignExpr();
        assignExpr.setTarget(new NameExpr("MATCH"));
        body.addStatement(assignExpr);
        retrieved = CommonCodegenUtils.getAssignExpression(body, "NOMATCH");
        assertThat(retrieved).isNotNull();
        assertThat(retrieved).isNotPresent();
        retrieved = CommonCodegenUtils.getAssignExpression(body, "MATCH");
        assertThat(retrieved).isNotNull();
        assertThat(retrieved).isPresent();
        AssignExpr retrievedAssignExpr = retrieved.get();
        assertThat(retrievedAssignExpr).isEqualTo(assignExpr);
    }

    @Test
    void getExplicitConstructorInvocationStmt() {
        BlockStmt body = new BlockStmt();
        Optional<ExplicitConstructorInvocationStmt> retrieved = CommonCodegenUtils.getExplicitConstructorInvocationStmt(body);
        assertThat(retrieved).isNotNull();
        assertThat(retrieved).isNotPresent();
        ExplicitConstructorInvocationStmt explicitConstructorInvocationStmt = new ExplicitConstructorInvocationStmt();
        body.addStatement(explicitConstructorInvocationStmt);
        retrieved = CommonCodegenUtils.getExplicitConstructorInvocationStmt(body);
        assertThat(retrieved).isNotNull();
        assertThat(retrieved).isPresent();
        ExplicitConstructorInvocationStmt retrievedExplicitConstructorInvocationStmt = retrieved.get();
        assertThat(retrievedExplicitConstructorInvocationStmt).isEqualTo(explicitConstructorInvocationStmt);
    }

    @Test
    void setExplicitConstructorInvocationStmtArgumentWithParameter() {
        final String parameterName = "PARAMETER_NAME";
        final String value = "VALUE";
        final ExplicitConstructorInvocationStmt explicitConstructorInvocationStmt = new ExplicitConstructorInvocationStmt();
        explicitConstructorInvocationStmt.setArguments(NodeList.nodeList(new NameExpr("NOT_PARAMETER"), new NameExpr(parameterName)));
        assertThat(CommonCodegenUtils.getExplicitConstructorInvocationParameter(explicitConstructorInvocationStmt, parameterName)).isPresent();
        CommonCodegenUtils.setExplicitConstructorInvocationStmtArgument(explicitConstructorInvocationStmt, parameterName, value);
        assertThat(CommonCodegenUtils.getExplicitConstructorInvocationParameter(explicitConstructorInvocationStmt, parameterName)).isNotPresent();
        assertThat(CommonCodegenUtils.getExplicitConstructorInvocationParameter(explicitConstructorInvocationStmt, value)).isPresent();
    }

    @Test
    void setExplicitConstructorInvocationStmtArgumentNoParameter() {
        assertThatExceptionOfType(KiePMMLException.class).isThrownBy(() -> {
            final String parameterName = "PARAMETER_NAME";
            final ExplicitConstructorInvocationStmt explicitConstructorInvocationStmt = new ExplicitConstructorInvocationStmt();
            explicitConstructorInvocationStmt.setArguments(NodeList.nodeList(new NameExpr("NOT_PARAMETER")));
            CommonCodegenUtils.setExplicitConstructorInvocationStmtArgument(explicitConstructorInvocationStmt, parameterName, "VALUE");
        });
    }

    @Test
    void getExplicitConstructorInvocationParameter() {
        final String parameterName = "PARAMETER_NAME";
        final ExplicitConstructorInvocationStmt explicitConstructorInvocationStmt = new ExplicitConstructorInvocationStmt();
        assertThat(CommonCodegenUtils.getExplicitConstructorInvocationParameter(explicitConstructorInvocationStmt, parameterName)).isNotPresent();
        explicitConstructorInvocationStmt.setArguments(NodeList.nodeList(new NameExpr("NOT_PARAMETER")));
        assertThat(CommonCodegenUtils.getExplicitConstructorInvocationParameter(explicitConstructorInvocationStmt, parameterName)).isNotPresent();
        explicitConstructorInvocationStmt.setArguments(NodeList.nodeList(new NameExpr("NOT_PARAMETER"), new NameExpr(parameterName)));
        assertThat(CommonCodegenUtils.getExplicitConstructorInvocationParameter(explicitConstructorInvocationStmt, parameterName)).isPresent();
    }

    @Test
    void getMethodDeclaration() {
        final String methodName = "METHOD_NAME";
        final ClassOrInterfaceDeclaration classOrInterfaceDeclaration = new ClassOrInterfaceDeclaration();
        assertThat(CommonCodegenUtils.getMethodDeclaration(classOrInterfaceDeclaration, methodName)).isNotPresent();
        classOrInterfaceDeclaration.addMethod("NOT_METHOD");
        assertThat(CommonCodegenUtils.getMethodDeclaration(classOrInterfaceDeclaration, methodName)).isNotPresent();
        classOrInterfaceDeclaration.addMethod(methodName);
        assertThat(CommonCodegenUtils.getMethodDeclaration(classOrInterfaceDeclaration, methodName)).isPresent();
    }

    @Test
    void addMethod() {
        final MethodDeclaration methodTemplate = new MethodDeclaration();
        methodTemplate.setName("methodTemplate");
        final BlockStmt body = new BlockStmt();
        methodTemplate.setBody(body);
        final String methodName = "METHOD_NAME";
        final ClassOrInterfaceDeclaration classOrInterfaceDeclaration = new ClassOrInterfaceDeclaration();
        assertThat(classOrInterfaceDeclaration.getMethodsByName(methodName)).isEmpty();
        CommonCodegenUtils.addMethod(methodTemplate, classOrInterfaceDeclaration, methodName);
        assertThat(classOrInterfaceDeclaration.getMethodsByName(methodName)).hasSize(1);
        assertThat(classOrInterfaceDeclaration.getMethodsByName(methodName).get(0).getBody().get()).isEqualTo(body);
    }

    @Test
    void getVariableDeclarator() {
        final String variableName = "variableName";
        final BlockStmt body = new BlockStmt();
        assertThat(CommonCodegenUtils.getVariableDeclarator(body, variableName)).isNotPresent();
        final VariableDeclarationExpr variableDeclarationExpr = new VariableDeclarationExpr(parseClassOrInterfaceType("String"), variableName);
        body.addStatement(variableDeclarationExpr);
        Optional<VariableDeclarator> retrieved =  CommonCodegenUtils.getVariableDeclarator(body, variableName);
        assertThat(retrieved).isPresent();
        VariableDeclarator variableDeclarator = retrieved.get();
        assertThat(variableDeclarator.getName().asString()).isEqualTo(variableName);
    }

    @Test
    void getExpressionForObject() {
        String string = "string";
        Expression retrieved = CommonCodegenUtils.getExpressionForObject(string);
        assertThat(retrieved).isInstanceOf(StringLiteralExpr.class);
        assertThat(retrieved.toString()).isEqualTo("\"string\"");
        int i = 1;
        retrieved = CommonCodegenUtils.getExpressionForObject(i);
        assertThat(retrieved).isInstanceOf(IntegerLiteralExpr.class);
        assertThat(((IntegerLiteralExpr) retrieved).asInt()).isEqualTo(i);
        Integer j = 3;
        retrieved = CommonCodegenUtils.getExpressionForObject(j);
        assertThat(retrieved).isInstanceOf(IntegerLiteralExpr.class);
        assertThat(((IntegerLiteralExpr) retrieved).asInt()).isEqualTo(j.intValue());
        double x = 1.12;
        retrieved = CommonCodegenUtils.getExpressionForObject(x);
        assertThat(retrieved).isInstanceOf(DoubleLiteralExpr.class);
        assertThat(((DoubleLiteralExpr) retrieved).asDouble()).isCloseTo(x, Offset.offset(0.001));
        Double y = 3.12;
        retrieved = CommonCodegenUtils.getExpressionForObject(y);
        assertThat(retrieved).isInstanceOf(DoubleLiteralExpr.class);
        assertThat(((DoubleLiteralExpr) retrieved).asDouble()).isCloseTo(y, Offset.offset(0.001));
        float k = 1.12f;
        retrieved = CommonCodegenUtils.getExpressionForObject(k);
        assertThat(retrieved).isInstanceOf(DoubleLiteralExpr.class);
        assertThat(((DoubleLiteralExpr) retrieved).asDouble()).isCloseTo(1.12, Offset.offset(0.001));
        Float z = 3.12f;
        retrieved = CommonCodegenUtils.getExpressionForObject(z);
        assertThat(retrieved).isInstanceOf(DoubleLiteralExpr.class);
        assertThat(((DoubleLiteralExpr) retrieved).asDouble()).isCloseTo(z.doubleValue(), Offset.offset(0.001));
        boolean b = true;
        retrieved = CommonCodegenUtils.getExpressionForObject(b);
        assertThat(retrieved).isInstanceOf(BooleanLiteralExpr.class);
        assertThat(((BooleanLiteralExpr) retrieved).getValue()).isEqualTo(b);
        Boolean c = false;
        retrieved = CommonCodegenUtils.getExpressionForObject(c);
        assertThat(retrieved).isInstanceOf(BooleanLiteralExpr.class);
        assertThat(((BooleanLiteralExpr) retrieved).getValue()).isEqualTo(c);
    }

    @Test
    void getNameExprsFromBlock() {
        BlockStmt toRead = new BlockStmt();
        List<NameExpr> retrieved = CommonCodegenUtils.getNameExprsFromBlock(toRead, "value");
        assertThat(retrieved).isNotNull();
        assertThat(retrieved).isEmpty();
        toRead = getBlockStmt();
        retrieved = CommonCodegenUtils.getNameExprsFromBlock(toRead, "value");
        assertThat(retrieved).isNotNull();
        assertThat(retrieved).hasSize(2);
    }

    @Test
    void literalExprFromDataType() {
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
            assertThat(literalExprFrom(input.getKey(), null)).isInstanceOf(NullLiteralExpr.class);

            Expression output = literalExprFrom(input.getKey(), input.getValue());
            switch (input.getKey()) {
                case STRING:
                    assertThat(output).isInstanceOf(StringLiteralExpr.class);
                    break;
                case INTEGER:
                    assertThat(output).isInstanceOf(IntegerLiteralExpr.class);
                    break;
                case DOUBLE:
                case FLOAT:
                    assertThat(output).isInstanceOf(DoubleLiteralExpr.class);
                    break;
                case BOOLEAN:
                    assertThat(output).isInstanceOf(BooleanLiteralExpr.class);
                    break;
                case DATE:
                case TIME:
                case DATE_TIME:
                    assertThat(output).isInstanceOf(MethodCallExpr.class);
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
                    assertThat(output).isInstanceOf(LongLiteralExpr.class);
            }
        }

        assertThatIllegalArgumentException().isThrownBy(() -> literalExprFrom(null, null));
        assertThatIllegalArgumentException().isThrownBy(() -> literalExprFrom(null, "test"));
    }

    @Test
    void replaceNodesInBlock() {
        final BlockStmt toRead = getBlockStmt();
        final List<NameExpr> retrieved = CommonCodegenUtils.getNameExprsFromBlock(toRead, "value");
        assertThat(retrieved).hasSize(2);
        final List<NullLiteralExpr> nullExprs =  toRead.stream()
                .filter(node -> node instanceof NullLiteralExpr)
                .map(NullLiteralExpr.class::cast)
                .collect(Collectors.toList());
        assertThat(nullExprs).isNotNull();
        assertThat(nullExprs).isEmpty();

        final List<CommonCodegenUtils.ReplacementTuple> replacementTuples =
                retrieved.stream()
                        .map(nameExpr -> {
                            NullLiteralExpr toAdd = new NullLiteralExpr();
                            nullExprs.add(toAdd);
                            return new CommonCodegenUtils.ReplacementTuple(nameExpr, toAdd);
                        })
                        .collect(Collectors.toList());
        CommonCodegenUtils.replaceNodesInStatement(toRead, replacementTuples);
        final List<NameExpr> newRetrieved = CommonCodegenUtils.getNameExprsFromBlock(toRead, "value");
        assertThat(newRetrieved).isEmpty();

        final List<NullLiteralExpr> retrievedNullExprs =  toRead.stream()
                .filter(node -> node instanceof NullLiteralExpr)
                .map(NullLiteralExpr.class::cast)
                .collect(Collectors.toList());
        assertThat(nullExprs).isNotNull();
        assertThat(retrievedNullExprs).hasSameSizeAs(nullExprs);
        nullExprs.forEach(nullExpr -> assertThat(retrievedNullExprs).contains(nullExpr));
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
        assertThat(toValidate).isNotNull();
        assertThat(toValidate.getName().asString()).isEqualTo(methodName);
    }

    private void commonValidateMethodDeclarationParams(MethodDeclaration toValidate, Map<String, ClassOrInterfaceType> parameterNameTypeMap) {
        final NodeList<Parameter> retrieved = toValidate.getParameters();
        assertThat(retrieved).isNotNull();
        assertThat(retrieved).hasSize(parameterNameTypeMap.size());
        for (Parameter parameter : retrieved) {
            assertThat(parameterNameTypeMap).containsKey(parameter.getNameAsString());
            assertThat(parameter.getType()).isEqualTo(parameterNameTypeMap.get(parameter.getNameAsString()));
        }
    }

    private MethodDeclaration getMethodDeclaration(String methodName) {
        return CommonCodegenUtils.getMethodDeclaration(methodName);
    }
}