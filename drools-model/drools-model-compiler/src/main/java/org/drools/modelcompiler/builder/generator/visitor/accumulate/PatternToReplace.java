package org.drools.modelcompiler.builder.generator.visitor.accumulate;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.MethodCallExpr;
import org.drools.modelcompiler.builder.generator.RuleContext;

import static java.util.stream.Collectors.toList;

import static org.drools.modelcompiler.builder.generator.DslMethodNames.PATTERN_CALL;

public class PatternToReplace {

    final RuleContext context;
    final Collection<String> patternBindings;

    public PatternToReplace(RuleContext context, Collection<String> patternBindings) {
        this.context = context;
        this.patternBindings = patternBindings;
    }

    private Stream<MethodCallExpr> allMethodCallExpressions() {
        return context.getExpressions().stream().flatMap(e -> e.findAll(MethodCallExpr.class).stream());
    }

    public Optional<MethodCallExpr> findFromPattern() {
        return allMethodCallExpressions()
                .filter(expr -> expr.getName().asString().equals(PATTERN_CALL))
                .filter(this::hasBindingExprVar)
                .map(Expression::asMethodCallExpr)
                .findFirst();
    }

    private boolean hasBindingExprVar(MethodCallExpr expr) {
        List<Expression> bindingExprsVars = patternBindings.stream().map(context::getVarExpr).collect(toList());
        return !Collections.disjoint(bindingExprsVars, expr.getArguments());
    }
}
