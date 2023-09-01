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
package org.kie.pmml.compiler.commons.testutils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.ConstructorDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.expr.AssignExpr;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.SimpleName;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.ExplicitConstructorInvocationStmt;
import org.kie.memorycompiler.KieMemoryCompiler;
import org.kie.pmml.compiler.commons.utils.CommonCodegenUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

/**
 * Utility methods for Codegen-related tests
 */
public class CodegenTestUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(CodegenTestUtils.class);

    public static void commonValidateCompilation(BlockStmt body, List<Parameter> parameters) {
        ClassOrInterfaceDeclaration classOrInterfaceType = new ClassOrInterfaceDeclaration();
        classOrInterfaceType.setName("CommCodeTest");
        MethodDeclaration toAdd = new MethodDeclaration();
        toAdd.setType("void");
        toAdd.setName("TestingMethod");
        toAdd.setParameters(NodeList.nodeList(parameters));
        toAdd.setBody(body);
        classOrInterfaceType.addMember(toAdd);
        CompilationUnit compilationUnit = StaticJavaParser.parse("");
        compilationUnit.setPackageDeclaration("org.kie.pmml.compiler.commons.utils");
        compilationUnit.addType(classOrInterfaceType);
        Map<String, String> sourcesMap = Collections.singletonMap("org.kie.pmml.compiler.commons.utils.CommCodeTest",
                                                                  compilationUnit.toString());
        try {
            KieMemoryCompiler.compile(sourcesMap, Thread.currentThread().getContextClassLoader());
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    public static void commonValidateCompilationWithImports(Expression expression, List<Class<?>> imports) {
        BlockStmt body = new BlockStmt();
        body.addStatement(expression);
        commonValidateCompilationWithImports(body, imports);
    }

    public static void commonValidateCompilationWithImports(BlockStmt body, List<Class<?>> imports) {
        ClassOrInterfaceDeclaration classOrInterfaceType = new ClassOrInterfaceDeclaration();
        classOrInterfaceType.setName("CommCodeTest");
        MethodDeclaration toAdd = new MethodDeclaration();
        toAdd.setType("void");
        toAdd.setName("TestingMethod");
        toAdd.setParameters(NodeList.nodeList());
        toAdd.setBody(body);
        classOrInterfaceType.addMember(toAdd);
        CompilationUnit compilationUnit = StaticJavaParser.parse("");
        imports.forEach(compilationUnit::addImport);
        compilationUnit.setPackageDeclaration("org.kie.pmml.compiler.commons.utils");
        compilationUnit.addType(classOrInterfaceType);
        Map<String, String> sourcesMap = Collections.singletonMap("org.kie.pmml.compiler.commons.utils.CommCodeTest",
                                                                  compilationUnit.toString());
        try {
            KieMemoryCompiler.compile(sourcesMap, Thread.currentThread().getContextClassLoader());
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    public static void commonValidateCompilation(MethodDeclaration methodDeclaration) {
        ClassOrInterfaceDeclaration classOrInterfaceType = new ClassOrInterfaceDeclaration();
        classOrInterfaceType.setName("CommCodeTest");
        classOrInterfaceType.addMember(methodDeclaration);
        CompilationUnit compilationUnit = StaticJavaParser.parse("");
        compilationUnit.setPackageDeclaration("org.kie.pmml.compiler.commons.utils");
        compilationUnit.addType(classOrInterfaceType);
        Map<String, String> sourcesMap = Collections.singletonMap("org.kie.pmml.compiler.commons.utils.CommCodeTest",
                                                                  compilationUnit.toString());
        try {
            KieMemoryCompiler.compile(sourcesMap, Thread.currentThread().getContextClassLoader());
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    public static void commonValidateCompilationWithImports(MethodDeclaration methodDeclaration, List<Class<?>> imports) {
        ClassOrInterfaceDeclaration classOrInterfaceType = new ClassOrInterfaceDeclaration();
        classOrInterfaceType.setName("CommCodeTest");
        classOrInterfaceType.addMember(methodDeclaration);
        CompilationUnit compilationUnit = StaticJavaParser.parse("");
        imports.forEach(compilationUnit::addImport);
        compilationUnit.setPackageDeclaration("org.kie.pmml.compiler.commons.utils");
        compilationUnit.addType(classOrInterfaceType);
        Map<String, String> sourcesMap = Collections.singletonMap("org.kie.pmml.compiler.commons.utils.CommCodeTest",
                                                                  compilationUnit.toString());
        try {
            KieMemoryCompiler.compile(sourcesMap, Thread.currentThread().getContextClassLoader());
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    public static void commonValidateCompilationWithImports(ClassOrInterfaceDeclaration classOrInterfaceType, List<Class<?>> imports) {
        CompilationUnit compilationUnit = StaticJavaParser.parse("");
        imports.forEach(compilationUnit::addImport);
        compilationUnit.setPackageDeclaration("org.kie.pmml.compiler.commons.utils");
        compilationUnit.addType(classOrInterfaceType);
        Map<String, String> sourcesMap = Collections.singletonMap("org.kie.pmml.compiler.commons.utils."+classOrInterfaceType.getName().asString(),
                                                                  compilationUnit.toString());
        try {
            KieMemoryCompiler.compile(sourcesMap, Thread.currentThread().getContextClassLoader());
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    public static void commonValidateCompilation(Map<String, String> sourcesMap) {
        try {
            KieMemoryCompiler.compile(sourcesMap, Thread.currentThread().getContextClassLoader());
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    public static boolean commonEvaluateConstructor(ConstructorDeclaration constructorDeclaration,
                                                 String generatedClassName,
                                                 Map<Integer, Expression> superInvocationExpressionsMap,
                                                 Map<String, Expression> assignExpressionsMap) {
        assertThat(constructorDeclaration.getName()).isEqualTo(new SimpleName(generatedClassName));
        final BlockStmt body = constructorDeclaration.getBody();
        return commonEvaluateSuperInvocationExpr(body, superInvocationExpressionsMap) &&
        commonEvaluateAssignExpr(body, assignExpressionsMap);
    }

    public static boolean commonEvaluateSuperInvocationExpr(BlockStmt body,
                                                         Map<Integer, Expression> superInvocationExpressionsMap) {
        Optional<ExplicitConstructorInvocationStmt> retrieved =
                CommonCodegenUtils.getExplicitConstructorInvocationStmt(body);
        final List<AssertionError> errors = new ArrayList<>();
        retrieved.ifPresent(explicitConstructorInvocationStmt -> superInvocationExpressionsMap.forEach((integer,
                                                                                                        expression) -> {
                                                                                                            try {
                                                                                                                assertThat(explicitConstructorInvocationStmt.getArgument(integer)).isEqualTo(expression);
                                                                                                            } catch (AssertionError e) {
                                                                                                                if (e.getMessage() != null && !e.getMessage().isEmpty()) {
                                                                                                                    LOGGER.error(e.getMessage());
                                                                                                                } else {
                                                                                                                    e.printStackTrace();
                                                                                                                }
                                                                                                                errors.add(e);
                                                                                                            }
                                                                                                        }));
        return errors.isEmpty();
    }

    public static boolean commonEvaluateAssignExpr(BlockStmt blockStmt, Map<String, Expression> assignExpressionMap) {
        List<AssignExpr> retrieved = blockStmt.findAll(AssignExpr.class);
        final List<AssertionError> errors = new ArrayList<>();
        for (Map.Entry<String, Expression> entry : assignExpressionMap.entrySet()) {
            try {
                assertThat(retrieved.stream()
                                   .filter(assignExpr -> assignExpr.getTarget().asNameExpr().equals(new NameExpr(entry.getKey())))
                                   .anyMatch(assignExpr -> assignExpr.getValue().equals(entry.getValue()))).isTrue();
            } catch (AssertionError e) {
                if (e.getMessage() != null && !e.getMessage().isEmpty()) {
                    LOGGER.error(e.getMessage());
                } else {
                    e.printStackTrace();
                }
                errors.add(e);
            }
        }
        return errors.isEmpty();
    }
}
