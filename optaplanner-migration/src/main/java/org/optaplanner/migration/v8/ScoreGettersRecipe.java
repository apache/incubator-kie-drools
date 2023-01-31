package org.optaplanner.migration.v8;

import java.util.Arrays;
import java.util.List;

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

public class ScoreGettersRecipe extends Recipe {

    private static final MatcherMeta[] MATCHER_METAS = {
            new MatcherMeta("IBendableScore", "getHardLevelsSize()"),
            new MatcherMeta("IBendableScore", "getSoftLevelsSize()"),
            new MatcherMeta("IBendableScore", "getLevelsSize()"),
            new MatcherMeta("Score", "getInitScore()"),

            new MatcherMeta("BendableScore", "getHardScores()"),
            new MatcherMeta("BendableScore", "getHardScore(int)"),
            new MatcherMeta("BendableScore", "getSoftScores()"),
            new MatcherMeta("BendableScore", "getSoftScore(int)"),

            new MatcherMeta("BendableBigDecimalScore", "getHardScores()"),
            new MatcherMeta("BendableBigDecimalScore", "getHardScore(int)"),
            new MatcherMeta("BendableBigDecimalScore", "getSoftScores()"),
            new MatcherMeta("BendableBigDecimalScore", "getSoftScore(int)"),

            new MatcherMeta("BendableLongScore", "getHardScores()"),
            new MatcherMeta("BendableLongScore", "getHardScore(int)"),
            new MatcherMeta("BendableLongScore", "getSoftScores()"),
            new MatcherMeta("BendableLongScore", "getSoftScore(int)"),

            new MatcherMeta("HardMediumSoftScore", "getHardScore()"),
            new MatcherMeta("HardMediumSoftScore", "getMediumScore()"),
            new MatcherMeta("HardMediumSoftScore", "getSoftScore()"),

            new MatcherMeta("HardMediumSoftBigDecimalScore", "getHardScore()"),
            new MatcherMeta("HardMediumSoftBigDecimalScore", "getMediumScore()"),
            new MatcherMeta("HardMediumSoftBigDecimalScore", "getSoftScore()"),

            new MatcherMeta("HardMediumSoftLongScore", "getHardScore()"),
            new MatcherMeta("HardMediumSoftLongScore", "getMediumScore()"),
            new MatcherMeta("HardMediumSoftLongScore", "getSoftScore()"),

            new MatcherMeta("HardSoftScore", "getHardScore()"),
            new MatcherMeta("HardSoftScore", "getSoftScore()"),

            new MatcherMeta("HardSoftBigDecimalScore", "getHardScore()"),
            new MatcherMeta("HardSoftBigDecimalScore", "getSoftScore()"),

            new MatcherMeta("HardSoftLongScore", "getHardScore()"),
            new MatcherMeta("HardSoftLongScore", "getSoftScore()"),

            new MatcherMeta("SimpleScore", "getScore()"),

            new MatcherMeta("SimpleBigDecimalScore", "getScore()"),

            new MatcherMeta("SimpleLongScore", "getScore()"),
    };

    @Override
    public String getDisplayName() {
        return "Score: use shorter getters";
    }

    @Override
    public String getDescription() {
        return "Use `score()` instead of `getScore()` on `Score` implementations.";
    }

    @Override
    protected TreeVisitor<?, ExecutionContext> getSingleSourceApplicableTest() {
        return new JavaIsoVisitor<>() {
            @Override
            public J.CompilationUnit visitCompilationUnit(J.CompilationUnit compilationUnit,
                    ExecutionContext executionContext) {
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

                String score = "#{any(" + matcherMeta.scoreClassFqn + ")}";
                String getterWithoutGet =
                        matcherMeta.methodName.substring(3, 4).toLowerCase() +
                                matcherMeta.methodName.substring(4);
                String pattern = score + "." + getterWithoutGet;
                if (getterWithoutGet.contains("(int)")) {
                    pattern = pattern.replace("(int)", "(#{any(int)})");
                    JavaTemplate template = JavaTemplate.builder(() -> getCursor().getParentOrThrow(), pattern)
                            .javaParser(() -> buildJavaParser().build())
                            .build();
                    return e.withTemplate(template, e.getCoordinates().replace(), select, arguments.get(0));
                } else {
                    JavaTemplate template = JavaTemplate.builder(() -> getCursor().getParentOrThrow(), pattern)
                            .javaParser(() -> buildJavaParser().build())
                            .build();
                    return e.withTemplate(template, e.getCoordinates().replace(), select);
                }
            }
        };
    }

    public static JavaParser.Builder buildJavaParser() {
        return JavaParser.fromJavaVersion().classpath("optaplanner-core-impl");
    }

    private static final class MatcherMeta {

        public final String scoreClassFqn;
        public final MethodMatcher methodMatcher;
        public final String methodName;

        public MatcherMeta(String select, String method) {
            String className;
            switch (select) {
                case "Score":
                case "IBendableScore":
                    className = "org.optaplanner.core.api.score." + select;
                    break;
                default:
                    className = "org.optaplanner.core.api.score.buildin."
                            + select.toLowerCase().replace("score", "")
                            + "."
                            + select;
            }
            this.scoreClassFqn = className;
            this.methodMatcher = new MethodMatcher(scoreClassFqn + " " + method);
            this.methodName = method;
        }
    }

}
