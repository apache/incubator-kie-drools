/*
 * Copyright 2021 the original author or authors.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * https://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.optaplanner.rewrite.v8;

import java.util.regex.Pattern;

import org.openrewrite.ExecutionContext;
import org.openrewrite.Recipe;
import org.openrewrite.TreeVisitor;
import org.openrewrite.java.JavaIsoVisitor;
import org.openrewrite.java.JavaParser;
import org.openrewrite.java.JavaTemplate;
import org.openrewrite.java.MethodMatcher;
import org.openrewrite.java.search.UsesMethod;
import org.openrewrite.java.tree.Expression;
import org.openrewrite.java.tree.J;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AsConstraintBuilder extends Recipe {

    private static final Logger LOGGER = LoggerFactory.getLogger(AsConstraintBuilder.class);

    private static final MethodMatcher MATCHER =
            new MethodMatcher("org.optaplanner.core.api.score.stream.ConstraintStream " +
                    "penalize(java.lang.String, org.optaplanner.core.api.score.Score)");

    @Override
    public String getDisplayName() {
        return "Use `penalize().asConstraint()` instead of the deprecated `penalize()`.";
    }

    @Override
    public String getDescription() {
        return "In ConstraintStreams use the builder pattern to define the constraint name.";
    }

    @Override
    protected TreeVisitor<?, ExecutionContext> getSingleSourceApplicableTest() {
        return new JavaIsoVisitor<>() {
            @Override
            public J.CompilationUnit visitCompilationUnit(
                    J.CompilationUnit compilationUnit, ExecutionContext executionContext) {
                doAfterVisit(new UsesMethod<>(MATCHER));
                return compilationUnit;
            }
        };
    }

    @Override
    protected TreeVisitor<?, ExecutionContext> getVisitor() {
        return new JavaIsoVisitor<>() {

            private final Pattern uniConstraintStreamPattern = Pattern.compile(
                    "org.optaplanner.core.api.score.stream.uni.UniConstraintStream");
            private final Pattern biConstraintStreamPattern = Pattern.compile(
                    "org.optaplanner.core.api.score.stream.bi.BiConstraintStream");
            private final Pattern triConstraintStreamPattern = Pattern.compile(
                    "org.optaplanner.core.api.score.stream.tri.TriConstraintStream");
            private final Pattern quadConstraintStreamPattern = Pattern.compile(
                    "org.optaplanner.core.api.score.stream.quad.QuadConstraintStream");
            private final JavaTemplate uniTemplate = JavaTemplate.builder(() -> getCursor().getParentOrThrow(),
                    "#{any(org.optaplanner.core.api.score.stream.uni.UniConstraintStream)}\n" +
                            ".penalize(#{any(org.optaplanner.core.api.score.Score)})\n" +
                            ".asConstraint(#{any(java.lang.String)})")
                    .javaParser(() -> JavaParser.fromJavaVersion().classpath("optaplanner-core").build())
                    .build();
            private final JavaTemplate biTemplate = JavaTemplate.builder(() -> getCursor().getParentOrThrow(),
                    "#{any(org.optaplanner.core.api.score.stream.bi.BiConstraintStream)}\n" +
                            ".penalize(#{any(org.optaplanner.core.api.score.Score)})\n" +
                            ".asConstraint(#{any(java.lang.String)})")
                    .javaParser(() -> JavaParser.fromJavaVersion().classpath("optaplanner-core").build())
                    .build();
            private final JavaTemplate triTemplate = JavaTemplate.builder(() -> getCursor().getParentOrThrow(),
                    "#{any(org.optaplanner.core.api.score.stream.tri.TriConstraintStream)}\n" +
                            ".penalize(#{any(org.optaplanner.core.api.score.Score)})\n" +
                            ".asConstraint(#{any(java.lang.String)})")
                    .javaParser(() -> JavaParser.fromJavaVersion().classpath("optaplanner-core").build())
                    .build();
            private final JavaTemplate quadTemplate = JavaTemplate.builder(() -> getCursor().getParentOrThrow(),
                    "#{any(org.optaplanner.core.api.score.stream.quad.QuadConstraintStream)}\n" +
                            ".penalize(#{any(org.optaplanner.core.api.score.Score)})\n" +
                            ".asConstraint(#{any(java.lang.String)})")
                    .javaParser(() -> JavaParser.fromJavaVersion().classpath("optaplanner-core").build())
                    .build();

            @Override
            public Expression visitExpression(Expression expression, ExecutionContext executionContext) {
                Expression e = super.visitExpression(expression, executionContext);
                if (MATCHER.matches(e)) {
                    J.MethodInvocation mi = (J.MethodInvocation) e;
                    Expression select = mi.getSelect();
                    JavaTemplate template;
                    if (select.getType().isAssignableFrom(uniConstraintStreamPattern)) {
                        template = uniTemplate;
                    } else if (select.getType().isAssignableFrom(biConstraintStreamPattern)) {
                        template = biTemplate;
                    } else if (select.getType().isAssignableFrom(triConstraintStreamPattern)) {
                        template = triTemplate;
                    } else if (select.getType().isAssignableFrom(quadConstraintStreamPattern)) {
                        template = quadTemplate;
                    } else {
                        LOGGER.warn("The method (" + mi.getCoordinates() + ").");
                        return e;
                    }
                    e = e.withTemplate(template,
                            e.getCoordinates().replace(), select,
                            mi.getArguments().get(1), mi.getArguments().get(0));
                }
                return e;
            }
        };
    }
}
