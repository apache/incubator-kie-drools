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

import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toMap;

import java.net.URI;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

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

import io.github.classgraph.ClassGraph;

public class AsConstraintRecipe extends Recipe {

    private static final Logger LOGGER = LoggerFactory.getLogger(AsConstraintRecipe.class);

    private static final MethodMatcher PENALIZE_NAME_MATCHER =
            new MethodMatcher("org.optaplanner.core.api.score.stream.ConstraintStream " +
                    "penalize(String, org.optaplanner.core.api.score.Score)");
    private static final MethodMatcher PENALIZE_ID_MATCHER =
            new MethodMatcher("org.optaplanner.core.api.score.stream.ConstraintStream " +
                    "penalize(String, String, org.optaplanner.core.api.score.Score)");
    private static final MethodMatcher PENALIZE_CONFIGURABLE_NAME_MATCHER =
            new MethodMatcher("org.optaplanner.core.api.score.stream.ConstraintStream " +
                    "penalizeConfigurable(String)");
    private static final MethodMatcher PENALIZE_CONFIGURABLE_ID_MATCHER =
            new MethodMatcher("org.optaplanner.core.api.score.stream.ConstraintStream " +
                    "penalizeConfigurable(String, String)");

    @Override
    public String getDisplayName() {
        return "ConstraintStreams: use asConstraint() methods to define constraints.";
    }

    @Override
    public String getDescription() {
        return "Uses `penalize().asConstraint()` and `reward().asConstraint()`" +
                " instead of the deprecated `penalize()` and `reward()` methods.";
    }

    @Override
    protected TreeVisitor<?, ExecutionContext> getSingleSourceApplicableTest() {
        return new JavaIsoVisitor<>() {
            @Override
            public J.CompilationUnit visitCompilationUnit(
                    J.CompilationUnit compilationUnit, ExecutionContext executionContext) {
                doAfterVisit(new UsesMethod<>(PENALIZE_NAME_MATCHER));
                doAfterVisit(new UsesMethod<>(PENALIZE_ID_MATCHER));
                doAfterVisit(new UsesMethod<>(PENALIZE_CONFIGURABLE_NAME_MATCHER));
                doAfterVisit(new UsesMethod<>(PENALIZE_CONFIGURABLE_ID_MATCHER));
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

            @Override
            public Expression visitExpression(Expression expression, ExecutionContext executionContext) {
                Expression e = super.visitExpression(expression, executionContext);
                if (PENALIZE_NAME_MATCHER.matches(e) ||
                        PENALIZE_ID_MATCHER.matches(e) ||
                        PENALIZE_CONFIGURABLE_NAME_MATCHER.matches(e) ||
                        PENALIZE_CONFIGURABLE_ID_MATCHER.matches(e)) {
                    J.MethodInvocation mi = (J.MethodInvocation) e;
                    Expression select = mi.getSelect();
                    List<Expression> arguments = mi.getArguments();

                    String templateCode;
                    if (select.getType().isAssignableFrom(uniConstraintStreamPattern)) {
                        templateCode = "#{any(org.optaplanner.core.api.score.stream.uni.UniConstraintStream)}\n";
                    } else if (select.getType().isAssignableFrom(biConstraintStreamPattern)) {
                        templateCode = "#{any(org.optaplanner.core.api.score.stream.bi.BiConstraintStream)}\n";
                    } else if (select.getType().isAssignableFrom(triConstraintStreamPattern)) {
                        templateCode = "#{any(org.optaplanner.core.api.score.stream.tri.TriConstraintStream)}\n";
                    } else if (select.getType().isAssignableFrom(quadConstraintStreamPattern)) {
                        templateCode = "#{any(org.optaplanner.core.api.score.stream.quad.QuadConstraintStream)}\n";
                    } else {
                        LOGGER.warn("Cannot refactor to asConstraint() method" +
                                " for deprecated method called in expression (" + e + ").");
                        return e;
                    }
                    if (PENALIZE_NAME_MATCHER.matches(e) ||
                            PENALIZE_ID_MATCHER.matches(e)) {
                        templateCode += ".penalize(#{any(org.optaplanner.core.api.score.Score)})\n";
                    } else if (PENALIZE_CONFIGURABLE_NAME_MATCHER.matches(e) ||
                            PENALIZE_CONFIGURABLE_ID_MATCHER.matches(e)) {
                        templateCode += ".penalizeConfigurable()\n";
                    } else {
                        throw new IllegalStateException("Impossible state");
                    }
                    if (PENALIZE_NAME_MATCHER.matches(e) ||
                            PENALIZE_CONFIGURABLE_NAME_MATCHER.matches(e)) {
                        templateCode += ".asConstraint(#{any(String)})";

                        JavaTemplate template = JavaTemplate.builder(() -> getCursor().getParentOrThrow(), templateCode)
                                .javaParser(() -> buildJavaParser().build())
                                .build();
                        e = e.withTemplate(template,
                                e.getCoordinates().replace(), select,
                                arguments.get(1), arguments.get(0));
                    } else if (PENALIZE_ID_MATCHER.matches(e) ||
                            PENALIZE_CONFIGURABLE_ID_MATCHER.matches(e)) {
                        templateCode += ".asConstraint(#{any(String)}, #{any(String)})";

                        JavaTemplate template = JavaTemplate.builder(() -> getCursor().getParentOrThrow(), templateCode)
                                .javaParser(() -> buildJavaParser().build())
                                .build();
                        e = e.withTemplate(template,
                                e.getCoordinates().replace(), select,
                                arguments.get(2), arguments.get(0), arguments.get(1));
                    } else {
                        throw new IllegalStateException("Impossible state");
                    }
                }
                return e;
            }
        };
    }

    public static JavaParser.Builder buildJavaParser() {
        return JavaParser.fromJavaVersion().classpath(workaroundDependenciesFromClasspath("optaplanner-core-impl"));
    }

    // TODO Remove if https://github.com/openrewrite/rewrite/pull/2407 is merged and released
    static List<Path> workaroundDependenciesFromClasspath(String... artifactNames) {
        List<URI> runtimeClasspath = new ClassGraph().getClasspathURIs();
        List<Path> artifacts = new ArrayList<>(artifactNames.length);
        List<String> missingArtifactNames = new ArrayList<>(artifactNames.length);
        for (String artifactName : artifactNames) {
            Pattern jarPattern = Pattern.compile(artifactName + "-.*?\\.jar$");
            // In a multiproject IDE classpath, some classpath entries aren't jars
            Pattern explodedPattern = Pattern.compile("/" + artifactName + "/");
            boolean lacking = true;
            for (URI cpEntry : runtimeClasspath) {
                String cpEntryString = cpEntry.toString();
                if (jarPattern.matcher(cpEntryString).find()
                        || (explodedPattern.matcher(cpEntryString).find())
                        && Paths.get(cpEntry).toFile().isDirectory()) {
                    artifacts.add(Paths.get(cpEntry));
                    lacking = false;
                    break;
                }
            }
            if (lacking) {
                missingArtifactNames.add(artifactName);
            }
        }

        if (!missingArtifactNames.isEmpty()) {
            throw new IllegalArgumentException("Unable to find runtime dependencies beginning with: " +
                    missingArtifactNames.stream().map(a -> "'" + a + "'").sorted().collect(joining(", ")));
        }

        return artifacts;
    }

}

