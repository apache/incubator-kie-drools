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
package org.optaplanner.migration.v8;

import java.util.Arrays;
import java.util.List;
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

public class AsConstraintRecipe extends Recipe {

    private static final Logger LOGGER = LoggerFactory.getLogger(AsConstraintRecipe.class);

    private static final MatcherMeta[] MATCHER_METAS = {
            new MatcherMeta("ConstraintStream", "penalize(String, Score)"),
            new MatcherMeta("ConstraintStream", "penalize(String, String, Score)"),
            new MatcherMeta("ConstraintStream", "penalizeConfigurable(String)"),
            new MatcherMeta("ConstraintStream", "penalizeConfigurable(String, String)"),
            new MatcherMeta("ConstraintStream", "reward(String, Score)"),
            new MatcherMeta("ConstraintStream", "reward(String, String, Score)"),
            new MatcherMeta("ConstraintStream", "rewardConfigurable(String)"),
            new MatcherMeta("ConstraintStream", "rewardConfigurable(String, String)"),
            new MatcherMeta("ConstraintStream", "impact(String, Score)"),
            new MatcherMeta("ConstraintStream", "impact(String, String, Score)"),
            new MatcherMeta("ConstraintStream", "impactConfigurable(String)"),
            new MatcherMeta("ConstraintStream", "impactConfigurable(String, String)"),

            new MatcherMeta("UniConstraintStream", "penalize(String, Score, ToIntFunction)"),
            new MatcherMeta("UniConstraintStream", "penalize(String, String, Score, ToIntFunction)"),
            new MatcherMeta("UniConstraintStream", "penalizeConfigurable(String, ToIntFunction)"),
            new MatcherMeta("UniConstraintStream", "penalizeConfigurable(String, String, ToIntFunction)"),
            new MatcherMeta("UniConstraintStream", "penalizeLong(String, Score, ToLongFunction)"),
            new MatcherMeta("UniConstraintStream", "penalizeLong(String, String, Score, ToLongFunction)"),
            new MatcherMeta("UniConstraintStream", "penalizeConfigurableLong(String, ToLongFunction)"),
            new MatcherMeta("UniConstraintStream", "penalizeConfigurableLong(String, String, ToLongFunction)"),
            new MatcherMeta("UniConstraintStream", "penalizeBigDecimal(String, Score, Function)"),
            new MatcherMeta("UniConstraintStream", "penalizeBigDecimal(String, String, Score, Function)"),
            new MatcherMeta("UniConstraintStream", "penalizeConfigurableBigDecimal(String, Function)"),
            new MatcherMeta("UniConstraintStream", "penalizeConfigurableBigDecimal(String, String, Function)"),
            new MatcherMeta("UniConstraintStream", "reward(String, Score, ToIntFunction)"),
            new MatcherMeta("UniConstraintStream", "reward(String, String, Score, ToIntFunction)"),
            new MatcherMeta("UniConstraintStream", "rewardConfigurable(String, ToIntFunction)"),
            new MatcherMeta("UniConstraintStream", "rewardConfigurable(String, String, ToIntFunction)"),
            new MatcherMeta("UniConstraintStream", "rewardLong(String, Score, ToLongFunction)"),
            new MatcherMeta("UniConstraintStream", "rewardLong(String, String, Score, ToLongFunction)"),
            new MatcherMeta("UniConstraintStream", "rewardConfigurableLong(String, ToLongFunction)"),
            new MatcherMeta("UniConstraintStream", "rewardConfigurableLong(String, String, ToLongFunction)"),
            new MatcherMeta("UniConstraintStream", "rewardBigDecimal(String, Score, Function)"),
            new MatcherMeta("UniConstraintStream", "rewardBigDecimal(String, String, Score, Function)"),
            new MatcherMeta("UniConstraintStream", "rewardConfigurableBigDecimal(String, Function)"),
            new MatcherMeta("UniConstraintStream", "rewardConfigurableBigDecimal(String, String, Function)"),
            new MatcherMeta("UniConstraintStream", "impact(String, Score, ToIntFunction)"),
            new MatcherMeta("UniConstraintStream", "impact(String, String, Score, ToIntFunction)"),
            new MatcherMeta("UniConstraintStream", "impactConfigurable(String, ToIntFunction)"),
            new MatcherMeta("UniConstraintStream", "impactConfigurable(String, String, ToIntFunction)"),
            new MatcherMeta("UniConstraintStream", "impactLong(String, Score, ToLongFunction)"),
            new MatcherMeta("UniConstraintStream", "impactLong(String, String, Score, ToLongFunction)"),
            new MatcherMeta("UniConstraintStream", "impactConfigurableLong(String, ToLongFunction)"),
            new MatcherMeta("UniConstraintStream", "impactConfigurableLong(String, String, ToLongFunction)"),
            new MatcherMeta("UniConstraintStream", "impactBigDecimal(String, Score, Function)"),
            new MatcherMeta("UniConstraintStream", "impactBigDecimal(String, String, Score, Function)"),
            new MatcherMeta("UniConstraintStream", "impactConfigurableBigDecimal(String, Function)"),
            new MatcherMeta("UniConstraintStream", "impactConfigurableBigDecimal(String, String, Function)"),

            new MatcherMeta("BiConstraintStream", "penalize(String, Score, ToIntBiFunction)"),
            new MatcherMeta("BiConstraintStream", "penalize(String, String, Score, ToIntBiFunction)"),
            new MatcherMeta("BiConstraintStream", "penalizeConfigurable(String, ToIntBiFunction)"),
            new MatcherMeta("BiConstraintStream", "penalizeConfigurable(String, String, ToIntBiFunction)"),
            new MatcherMeta("BiConstraintStream", "penalizeLong(String, Score, ToLongBiFunction)"),
            new MatcherMeta("BiConstraintStream", "penalizeLong(String, String, Score, ToLongBiFunction)"),
            new MatcherMeta("BiConstraintStream", "penalizeConfigurableLong(String, ToLongBiFunction)"),
            new MatcherMeta("BiConstraintStream", "penalizeConfigurableLong(String, String, ToLongBiFunction)"),
            new MatcherMeta("BiConstraintStream", "penalizeBigDecimal(String, Score, BiFunction)"),
            new MatcherMeta("BiConstraintStream", "penalizeBigDecimal(String, String, Score, BiFunction)"),
            new MatcherMeta("BiConstraintStream", "penalizeConfigurableBigDecimal(String, BiFunction)"),
            new MatcherMeta("BiConstraintStream", "penalizeConfigurableBigDecimal(String, String, BiFunction)"),
            new MatcherMeta("BiConstraintStream", "reward(String, Score, ToIntBiFunction)"),
            new MatcherMeta("BiConstraintStream", "reward(String, String, Score, ToIntBiFunction)"),
            new MatcherMeta("BiConstraintStream", "rewardConfigurable(String, ToIntBiFunction)"),
            new MatcherMeta("BiConstraintStream", "rewardConfigurable(String, String, ToIntBiFunction)"),
            new MatcherMeta("BiConstraintStream", "rewardLong(String, Score, ToLongBiFunction)"),
            new MatcherMeta("BiConstraintStream", "rewardLong(String, String, Score, ToLongBiFunction)"),
            new MatcherMeta("BiConstraintStream", "rewardConfigurableLong(String, ToLongBiFunction)"),
            new MatcherMeta("BiConstraintStream", "rewardConfigurableLong(String, String, ToLongBiFunction)"),
            new MatcherMeta("BiConstraintStream", "rewardBigDecimal(String, Score, BiFunction)"),
            new MatcherMeta("BiConstraintStream", "rewardBigDecimal(String, String, Score, BiFunction)"),
            new MatcherMeta("BiConstraintStream", "rewardConfigurableBigDecimal(String, BiFunction)"),
            new MatcherMeta("BiConstraintStream", "rewardConfigurableBigDecimal(String, String, BiFunction)"),
            new MatcherMeta("BiConstraintStream", "impact(String, Score, ToIntBiFunction)"),
            new MatcherMeta("BiConstraintStream", "impact(String, String, Score, ToIntBiFunction)"),
            new MatcherMeta("BiConstraintStream", "impactConfigurable(String, ToIntBiFunction)"),
            new MatcherMeta("BiConstraintStream", "impactConfigurable(String, String, ToIntBiFunction)"),
            new MatcherMeta("BiConstraintStream", "impactLong(String, Score, ToLongBiFunction)"),
            new MatcherMeta("BiConstraintStream", "impactLong(String, String, Score, ToLongBiFunction)"),
            new MatcherMeta("BiConstraintStream", "impactConfigurableLong(String, ToLongBiFunction)"),
            new MatcherMeta("BiConstraintStream", "impactConfigurableLong(String, String, ToLongBiFunction)"),
            new MatcherMeta("BiConstraintStream", "impactBigDecimal(String, Score, BiFunction)"),
            new MatcherMeta("BiConstraintStream", "impactBigDecimal(String, String, Score, BiFunction)"),
            new MatcherMeta("BiConstraintStream", "impactConfigurableBigDecimal(String, BiFunction)"),
            new MatcherMeta("BiConstraintStream", "impactConfigurableBigDecimal(String, String, BiFunction)"),

            new MatcherMeta("TriConstraintStream", "penalize(String, Score, ToIntTriFunction)"),
            new MatcherMeta("TriConstraintStream", "penalize(String, String, Score, ToIntTriFunction)"),
            new MatcherMeta("TriConstraintStream", "penalizeConfigurable(String, ToIntTriFunction)"),
            new MatcherMeta("TriConstraintStream", "penalizeConfigurable(String, String, ToIntTriFunction)"),
            new MatcherMeta("TriConstraintStream", "penalizeLong(String, Score, ToLongTriFunction)"),
            new MatcherMeta("TriConstraintStream", "penalizeLong(String, String, Score, ToLongTriFunction)"),
            new MatcherMeta("TriConstraintStream", "penalizeConfigurableLong(String, ToLongTriFunction)"),
            new MatcherMeta("TriConstraintStream", "penalizeConfigurableLong(String, String, ToLongTriFunction)"),
            new MatcherMeta("TriConstraintStream", "penalizeBigDecimal(String, Score, TriFunction)"),
            new MatcherMeta("TriConstraintStream", "penalizeBigDecimal(String, String, Score, TriFunction)"),
            new MatcherMeta("TriConstraintStream", "penalizeConfigurableBigDecimal(String, TriFunction)"),
            new MatcherMeta("TriConstraintStream", "penalizeConfigurableBigDecimal(String, String, TriFunction)"),
            new MatcherMeta("TriConstraintStream", "reward(String, Score, ToIntTriFunction)"),
            new MatcherMeta("TriConstraintStream", "reward(String, String, Score, ToIntTriFunction)"),
            new MatcherMeta("TriConstraintStream", "rewardConfigurable(String, ToIntTriFunction)"),
            new MatcherMeta("TriConstraintStream", "rewardConfigurable(String, String, ToIntTriFunction)"),
            new MatcherMeta("TriConstraintStream", "rewardLong(String, Score, ToLongTriFunction)"),
            new MatcherMeta("TriConstraintStream", "rewardLong(String, String, Score, ToLongTriFunction)"),
            new MatcherMeta("TriConstraintStream", "rewardConfigurableLong(String, ToLongTriFunction)"),
            new MatcherMeta("TriConstraintStream", "rewardConfigurableLong(String, String, ToLongTriFunction)"),
            new MatcherMeta("TriConstraintStream", "rewardBigDecimal(String, Score, TriFunction)"),
            new MatcherMeta("TriConstraintStream", "rewardBigDecimal(String, String, Score, TriFunction)"),
            new MatcherMeta("TriConstraintStream", "rewardConfigurableBigDecimal(String, TriFunction)"),
            new MatcherMeta("TriConstraintStream", "rewardConfigurableBigDecimal(String, String, TriFunction)"),
            new MatcherMeta("TriConstraintStream", "impact(String, Score, ToIntTriFunction)"),
            new MatcherMeta("TriConstraintStream", "impact(String, String, Score, ToIntTriFunction)"),
            new MatcherMeta("TriConstraintStream", "impactConfigurable(String, ToIntTriFunction)"),
            new MatcherMeta("TriConstraintStream", "impactConfigurable(String, String, ToIntTriFunction)"),
            new MatcherMeta("TriConstraintStream", "impactLong(String, Score, ToLongTriFunction)"),
            new MatcherMeta("TriConstraintStream", "impactLong(String, String, Score, ToLongTriFunction)"),
            new MatcherMeta("TriConstraintStream", "impactConfigurableLong(String, ToLongTriFunction)"),
            new MatcherMeta("TriConstraintStream", "impactConfigurableLong(String, String, ToLongTriFunction)"),
            new MatcherMeta("TriConstraintStream", "impactBigDecimal(String, Score, TriFunction)"),
            new MatcherMeta("TriConstraintStream", "impactBigDecimal(String, String, Score, TriFunction)"),
            new MatcherMeta("TriConstraintStream", "impactConfigurableBigDecimal(String, TriFunction)"),
            new MatcherMeta("TriConstraintStream", "impactConfigurableBigDecimal(String, String, TriFunction)"),

            new MatcherMeta("QuadConstraintStream", "penalize(String, Score, ToIntQuadFunction)"),
            new MatcherMeta("QuadConstraintStream", "penalize(String, String, Score, ToIntQuadFunction)"),
            new MatcherMeta("QuadConstraintStream", "penalizeConfigurable(String, ToIntQuadFunction)"),
            new MatcherMeta("QuadConstraintStream", "penalizeConfigurable(String, String, ToIntQuadFunction)"),
            new MatcherMeta("QuadConstraintStream", "penalizeLong(String, Score, ToLongQuadFunction)"),
            new MatcherMeta("QuadConstraintStream", "penalizeLong(String, String, Score, ToLongQuadFunction)"),
            new MatcherMeta("QuadConstraintStream", "penalizeConfigurableLong(String, ToLongQuadFunction)"),
            new MatcherMeta("QuadConstraintStream", "penalizeConfigurableLong(String, String, ToLongQuadFunction)"),
            new MatcherMeta("QuadConstraintStream", "penalizeBigDecimal(String, Score, QuadFunction)"),
            new MatcherMeta("QuadConstraintStream", "penalizeBigDecimal(String, String, Score, QuadFunction)"),
            new MatcherMeta("QuadConstraintStream", "penalizeConfigurableBigDecimal(String, QuadFunction)"),
            new MatcherMeta("QuadConstraintStream", "penalizeConfigurableBigDecimal(String, String, QuadFunction)"),
            new MatcherMeta("QuadConstraintStream", "reward(String, Score, ToIntQuadFunction)"),
            new MatcherMeta("QuadConstraintStream", "reward(String, String, Score, ToIntQuadFunction)"),
            new MatcherMeta("QuadConstraintStream", "rewardConfigurable(String, ToIntQuadFunction)"),
            new MatcherMeta("QuadConstraintStream", "rewardConfigurable(String, String, ToIntQuadFunction)"),
            new MatcherMeta("QuadConstraintStream", "rewardLong(String, Score, ToLongQuadFunction)"),
            new MatcherMeta("QuadConstraintStream", "rewardLong(String, String, Score, ToLongQuadFunction)"),
            new MatcherMeta("QuadConstraintStream", "rewardConfigurableLong(String, ToLongQuadFunction)"),
            new MatcherMeta("QuadConstraintStream", "rewardConfigurableLong(String, String, ToLongQuadFunction)"),
            new MatcherMeta("QuadConstraintStream", "rewardBigDecimal(String, Score, QuadFunction)"),
            new MatcherMeta("QuadConstraintStream", "rewardBigDecimal(String, String, Score, QuadFunction)"),
            new MatcherMeta("QuadConstraintStream", "rewardConfigurableBigDecimal(String, QuadFunction)"),
            new MatcherMeta("QuadConstraintStream", "rewardConfigurableBigDecimal(String, String, QuadFunction)"),
            new MatcherMeta("QuadConstraintStream", "impact(String, Score, ToIntQuadFunction)"),
            new MatcherMeta("QuadConstraintStream", "impact(String, String, Score, ToIntQuadFunction)"),
            new MatcherMeta("QuadConstraintStream", "impactConfigurable(String, ToIntQuadFunction)"),
            new MatcherMeta("QuadConstraintStream", "impactConfigurable(String, String, ToIntQuadFunction)"),
            new MatcherMeta("QuadConstraintStream", "impactLong(String, Score, ToLongQuadFunction)"),
            new MatcherMeta("QuadConstraintStream", "impactLong(String, String, Score, ToLongQuadFunction)"),
            new MatcherMeta("QuadConstraintStream", "impactConfigurableLong(String, ToLongQuadFunction)"),
            new MatcherMeta("QuadConstraintStream", "impactConfigurableLong(String, String, ToLongQuadFunction)"),
            new MatcherMeta("QuadConstraintStream", "impactBigDecimal(String, Score, QuadFunction)"),
            new MatcherMeta("QuadConstraintStream", "impactBigDecimal(String, String, Score, QuadFunction)"),
            new MatcherMeta("QuadConstraintStream", "impactConfigurableBigDecimal(String, QuadFunction)"),
            new MatcherMeta("QuadConstraintStream", "impactConfigurableBigDecimal(String, String, QuadFunction)"),
    };

    @Override
    public String getDisplayName() {
        return "ConstraintStreams: use asConstraint() methods to define constraints.";
    }

    @Override
    public String getDescription() {
        return "Use `penalize().asConstraint()` and `reward().asConstraint()`" +
                " instead of the deprecated `penalize()` and `reward()` methods.";
    }

    @Override
    protected TreeVisitor<?, ExecutionContext> getSingleSourceApplicableTest() {
        return new JavaIsoVisitor<>() {
            @Override
            public J.CompilationUnit visitCompilationUnit(
                    J.CompilationUnit compilationUnit, ExecutionContext executionContext) {
                for (MatcherMeta matcherMeta : MATCHER_METAS) {
                    doAfterVisit(new UsesMethod<>(matcherMeta.methodMatcher));
                }
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
                final Expression e = super.visitExpression(expression, executionContext);

                MatcherMeta matcherMeta = Arrays.stream(MATCHER_METAS).filter(m -> m.methodMatcher.matches(e))
                        .findFirst().orElse(null);
                if (matcherMeta == null) {
                    return e;
                }
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
                if (!matcherMeta.configurable) {
                    if (!matcherMeta.matchWeigherIncluded) {
                        templateCode += "." + matcherMeta.methodName + "(#{any(org.optaplanner.core.api.score.Score)})\n";
                    } else {
                        templateCode += "." + matcherMeta.methodName + "(#{any(org.optaplanner.core.api.score.Score)}," +
                                " #{any(" + matcherMeta.functionType + ")})\n";
                    }
                } else {
                    if (!matcherMeta.matchWeigherIncluded) {
                        templateCode += "." + matcherMeta.methodName + "()\n";
                    } else {
                        templateCode += "." + matcherMeta.methodName + "(" +
                                "#{any(" + matcherMeta.functionType + ")})\n";
                    }
                }
                if (!matcherMeta.constraintPackageIncluded) {
                    templateCode += ".asConstraint(#{any(String)})";
                } else {
                    templateCode += ".asConstraint(#{any(String)}, #{any(String)})";
                }
                JavaTemplate template = JavaTemplate.builder(() -> getCursor().getParentOrThrow(), templateCode)
                        .javaParser(() -> buildJavaParser().build())
                        .build();
                if (!matcherMeta.constraintPackageIncluded) {
                    if (!matcherMeta.configurable) {
                        if (!matcherMeta.matchWeigherIncluded) {
                            return e.withTemplate(template,
                                    e.getCoordinates().replace(), select,
                                    arguments.get(1), arguments.get(0));
                        } else {
                            return e.withTemplate(template,
                                    e.getCoordinates().replace(), select,
                                    arguments.get(1), arguments.get(2), arguments.get(0));
                        }
                    } else {
                        if (!matcherMeta.matchWeigherIncluded) {
                            return e.withTemplate(template,
                                    e.getCoordinates().replace(), select,
                                    arguments.get(0));
                        } else {
                            return e.withTemplate(template,
                                    e.getCoordinates().replace(), select,
                                    arguments.get(1), arguments.get(0));
                        }
                    }
                } else {
                    if (!matcherMeta.configurable) {
                        if (!matcherMeta.matchWeigherIncluded) {
                            return e.withTemplate(template,
                                    e.getCoordinates().replace(), select,
                                    arguments.get(2), arguments.get(0), arguments.get(1));
                        } else {
                            return e.withTemplate(template,
                                    e.getCoordinates().replace(), select,
                                    arguments.get(2), arguments.get(3), arguments.get(0), arguments.get(1));
                        }
                    } else {
                        if (!matcherMeta.matchWeigherIncluded) {
                            return e.withTemplate(template,
                                    e.getCoordinates().replace(), select,
                                    arguments.get(0), arguments.get(1));
                        } else {
                            return e.withTemplate(template,
                                    e.getCoordinates().replace(), select,
                                    arguments.get(2), arguments.get(0), arguments.get(1));
                        }
                    }
                }
            }
        };
    }

    public static JavaParser.Builder buildJavaParser() {
        return JavaParser.fromJavaVersion().classpath("optaplanner-core-impl");
        // TODO Remove this workaround if https://github.com/openrewrite/rewrite/pull/2407 is merged and released
        // WORKAROUND to run tests in IntelliJ:
        // return JavaParser.fromJavaVersion().classpath(workaroundDependenciesFromClasspath("optaplanner-core-impl"));
    }

    // TODO Remove this workaround if https://github.com/openrewrite/rewrite/pull/2407 is merged and released
    //    static List<Path> workaroundDependenciesFromClasspath(String... artifactNames) {
    //        List<URI> runtimeClasspath = new ClassGraph().getClasspathURIs();
    //        List<Path> artifacts = new ArrayList<>(artifactNames.length);
    //        List<String> missingArtifactNames = new ArrayList<>(artifactNames.length);
    //        for (String artifactName : artifactNames) {
    //            Pattern jarPattern = Pattern.compile(artifactName + "-.*?\\.jar$");
    //            // In a multiproject IDE classpath, some classpath entries aren't jars
    //            Pattern explodedPattern = Pattern.compile("/" + artifactName + "/");
    //            boolean lacking = true;
    //            for (URI cpEntry : runtimeClasspath) {
    //                String cpEntryString = cpEntry.toString();
    //                if (jarPattern.matcher(cpEntryString).find()
    //                        || (explodedPattern.matcher(cpEntryString).find())
    //                        && Paths.get(cpEntry).toFile().isDirectory()) {
    //                    artifacts.add(Paths.get(cpEntry));
    //                    lacking = false;
    //                    break;
    //                }
    //            }
    //            if (lacking) {
    //                missingArtifactNames.add(artifactName);
    //            }
    //        }
    //
    //        if (!missingArtifactNames.isEmpty()) {
    //            throw new IllegalArgumentException("Unable to find runtime dependencies beginning with: " +
    //                    missingArtifactNames.stream().map(a -> "'" + a + "'").sorted().collect(joining(", ")));
    //        }
    //
    //        return artifacts;
    //    }

    private static class MatcherMeta {
        public MethodMatcher methodMatcher;
        public boolean constraintPackageIncluded;
        public boolean configurable;
        public boolean matchWeigherIncluded;
        public String methodName; // penalize, reward or impact
        public String functionType;

        public MatcherMeta(String select, String method) {
            String signature;
            if (select.equals("ConstraintStream")) {
                signature = "org.optaplanner.core.api.score.stream.ConstraintStream";
            } else if (select.equals("UniConstraintStream")) {
                signature = "org.optaplanner.core.api.score.stream.uni.UniConstraintStream";
            } else if (select.equals("BiConstraintStream")) {
                signature = "org.optaplanner.core.api.score.stream.bi.BiConstraintStream";
            } else if (select.equals("TriConstraintStream")) {
                signature = "org.optaplanner.core.api.score.stream.tri.TriConstraintStream";
            } else if (select.equals("QuadConstraintStream")) {
                signature = "org.optaplanner.core.api.score.stream.quad.QuadConstraintStream";
            } else {
                throw new IllegalArgumentException("Invalid select (" + select + ").");
            }
            signature += " " + method.replace(" Score", " org.optaplanner.core.api.score.Score")
                    .replace(" ToIntFunction", " java.util.function.ToIntFunction")
                    .replace(" ToLongFunction", " java.util.function.ToLongFunction")
                    .replace(" Function", " java.util.function.Function")
                    .replace(" ToIntBiFunction", " java.util.function.ToIntBiFunction")
                    .replace(" ToLongBiFunction", " java.util.function.ToLongBiFunction")
                    .replace(" BiFunction", " java.util.function.BiFunction")
                    .replace(" ToIntTriFunction", " org.optaplanner.core.api.function.ToIntTriFunction")
                    .replace(" ToLongTriFunction", " org.optaplanner.core.api.function.ToLongTriFunction")
                    .replace(" TriFunction", " org.optaplanner.core.api.function.TriFunction")
                    .replace(" ToIntQuadFunction", " org.optaplanner.core.api.function.ToIntQuadFunction")
                    .replace(" ToLongQuadFunction", " org.optaplanner.core.api.function.ToLongQuadFunction")
                    .replace(" QuadFunction", " org.optaplanner.core.api.function.QuadFunction");
            methodMatcher = new MethodMatcher(signature);
            constraintPackageIncluded = method.contains("String, String");
            configurable = method.contains("Configurable");
            matchWeigherIncluded = method.contains("Function");
            if (matchWeigherIncluded) {
                this.functionType = signature.replaceFirst("^.* ([\\w\\.]+Function)\\)$", "$1");
            }
            this.methodName = method.replaceFirst("\\(.*$", "");
        }
    }

}
