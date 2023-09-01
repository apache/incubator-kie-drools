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
package org.kie.openrewrite.recipe.jpmml;

import org.openrewrite.ExecutionContext;
import org.openrewrite.InMemoryExecutionContext;
import org.openrewrite.java.Java11Parser;
import org.openrewrite.java.JavaIsoVisitor;
import org.openrewrite.java.JavaParser;
import org.openrewrite.java.tree.Expression;
import org.openrewrite.java.tree.J;

import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

public class CommonTestingUtilities {

    private static final List<Path> paths = JavaParser.runtimeClasspath();

    private CommonTestingUtilities() {
    }

    public static J.CompilationUnit getCompilationUnitFromClassSource(String classSource) {
        JavaParser parser = Java11Parser.builder()
                .classpath(paths)
                .logCompilationWarningsAndErrors(true)
                .build();
        return parser.parse(classSource).get(0);
    }

    public static Optional<J.Binary> getBinaryFromClassSource(String classSource,
                                                                                  String binaryString) {
        J.CompilationUnit compilationUnit = getCompilationUnitFromClassSource(classSource);
        TestingVisitor testingVisitor = new TestingVisitor(J.Binary.class, binaryString);
        testingVisitor.visit(compilationUnit, getExecutionContext(null));
        return (Optional<J.Binary>) testingVisitor.getFoundItem();
    }

    public static Optional<J.ClassDeclaration> getClassDeclarationFromClassSource(String classSource,
                                                                                  String className) {
        J.CompilationUnit compilationUnit = getCompilationUnitFromClassSource(classSource);
        TestingVisitor testingVisitor = new TestingVisitor(J.ClassDeclaration.class, className);
        testingVisitor.visit(compilationUnit, getExecutionContext(null));
        return (Optional<J.ClassDeclaration>) testingVisitor.getFoundItem();
    }

    public static Optional<J.ClassDeclaration> getClassDeclarationFromCompilationUnit(J.CompilationUnit compilationUnit,
                                                                                  String className) {
        TestingVisitor testingVisitor = new TestingVisitor(J.ClassDeclaration.class, className);
        testingVisitor.visit(compilationUnit, getExecutionContext(null));
        return (Optional<J.ClassDeclaration>) testingVisitor.getFoundItem();
    }

    public static Optional<J.MethodInvocation> getMethodInvocationFromClassSource(String classSource,
                                                                                  String methodInvocation) {
        J.CompilationUnit compilationUnit = getCompilationUnitFromClassSource(classSource);
        TestingVisitor testingVisitor = new TestingVisitor(J.MethodInvocation.class, methodInvocation);
        testingVisitor.visit(compilationUnit, getExecutionContext(null));
        return (Optional<J.MethodInvocation>) testingVisitor.getFoundItem();
    }

    public static Optional<J.NewClass> getNewClassFromClassSource(String classSource,
                                                                  String fqdnInstantiatedClass) {
        J.CompilationUnit compilationUnit = getCompilationUnitFromClassSource(classSource);
        TestingVisitor testingVisitor = new TestingVisitor(J.NewClass.class, fqdnInstantiatedClass);
        testingVisitor.visit(compilationUnit, getExecutionContext(null));
        return (Optional<J.NewClass>) testingVisitor.getFoundItem();
    }

    public static Optional<J.VariableDeclarations> getVariableDeclarationsFromClassSource(String classSource,
                                                                                          String variableDeclaration) {
        J.CompilationUnit compilationUnit = getCompilationUnitFromClassSource(classSource);
        TestingVisitor testingVisitor = new TestingVisitor(J.VariableDeclarations.class, variableDeclaration);
        testingVisitor.visit(compilationUnit, getExecutionContext(null));
        return (Optional<J.VariableDeclarations>) testingVisitor.getFoundItem();
    }

    public static Optional<Expression> getExpressionFromClassSource(String classSource, String expression) {
        J.CompilationUnit compilationUnit = getCompilationUnitFromClassSource(classSource);
        TestingVisitor testingVisitor = new TestingVisitor(Expression.class, expression);
        testingVisitor.visit(compilationUnit, getExecutionContext(null));
        return (Optional<Expression>) testingVisitor.getFoundItem();
    }

    public static Optional<Expression> getExpressionFromCompilationUnit(J.CompilationUnit compilationUnit, String expression) {
        TestingVisitor testingVisitor = new TestingVisitor(Expression.class, expression);
        testingVisitor.visit(compilationUnit, getExecutionContext(null));
        return (Optional<Expression>) testingVisitor.getFoundItem();
    }

    public static List<J.Import> getImportsFromClassSource(String classSource) {
        J.CompilationUnit compilationUnit = getCompilationUnitFromClassSource(classSource);
        return compilationUnit.getImports();
    }


    public static ExecutionContext getExecutionContext(Throwable expected) {
        return new InMemoryExecutionContext(throwable -> org.assertj.core.api.Assertions.assertThat(throwable).isEqualTo(expected));
    }

    private static class TestingVisitor extends JavaIsoVisitor<ExecutionContext> {

        private final Class<? extends J> SEARCHED_J;
        private final String SEARCHED_STRING;

        private Optional<? extends J> foundItem;

        public TestingVisitor(Class<? extends J> SEARCHED_J, String SEARCHED_STRING) {
            this.SEARCHED_J = SEARCHED_J;
            this.SEARCHED_STRING = SEARCHED_STRING;
            foundItem = Optional.empty();
        }

        public Optional<? extends J> getFoundItem() {
            return foundItem;
        }

        @Override
        public J.Binary visitBinary(J.Binary binary, ExecutionContext executionContext) {
            if (SEARCHED_J.equals(J.Binary.class) && binary.toString().equals(SEARCHED_STRING)) {
                foundItem = Optional.of(binary);
                return binary;
            } else {
                return super.visitBinary(binary, executionContext);
            }
        }

        @Override
        public J.CompilationUnit visitCompilationUnit(J.CompilationUnit cu, ExecutionContext executionContext) {
            if (SEARCHED_J.equals(J.CompilationUnit.class)) {
                foundItem = Optional.of(cu);
                return cu;
            } else {
                return super.visitCompilationUnit(cu, executionContext);
            }
        }

        @Override
        public J.ClassDeclaration visitClassDeclaration(J.ClassDeclaration classDecl, ExecutionContext executionContext) {
            if (SEARCHED_J.equals(J.ClassDeclaration.class) && classDecl.getSimpleName().equals(SEARCHED_STRING)) {
                foundItem = Optional.of(classDecl);
                return classDecl;
            } else {
                return super.visitClassDeclaration(classDecl, executionContext);
            }
        }

        @Override
        public J.MethodInvocation visitMethodInvocation(J.MethodInvocation method, ExecutionContext executionContext) {
            if (SEARCHED_J.equals(J.MethodInvocation.class) && method.toString().startsWith(SEARCHED_STRING + "(")) {
                foundItem = Optional.of(method);
                return method;
            } else {
                return super.visitMethodInvocation(method, executionContext);
            }
        }

        @Override
        public J.NewClass visitNewClass(J.NewClass newClass, ExecutionContext executionContext) {
            if (SEARCHED_J.equals(J.NewClass.class) && newClass.getType().toString().equals(SEARCHED_STRING)) {
                foundItem = Optional.of(newClass);
                return newClass;
            } else {
                return super.visitNewClass(newClass, executionContext);
            }
        }

        @Override
        public J.VariableDeclarations visitVariableDeclarations(J.VariableDeclarations multiVariable, ExecutionContext executionContext) {
            if (SEARCHED_J.equals(J.VariableDeclarations.class) && multiVariable.toString().startsWith(SEARCHED_STRING)) {
                foundItem = Optional.of(multiVariable);
                return multiVariable;
            } else {
                return super.visitVariableDeclarations(multiVariable, executionContext);
            }
        }

        @Override
        public Expression visitExpression(Expression expression, ExecutionContext executionContext) {
            if (SEARCHED_J.equals(Expression.class) && expression.toString().equals(SEARCHED_STRING)) {
                foundItem = Optional.of(expression);
                return expression;
            } else {
                return super.visitExpression(expression, executionContext);
            }
        }
    }
}
