package org.drools.modelcompiler.builder.generator.visitor.accumulate;

import java.util.Collection;
import java.util.Optional;

import org.drools.javaparser.ast.expr.Expression;
import org.drools.javaparser.ast.expr.MethodCallExpr;
import org.drools.javaparser.ast.expr.NameExpr;
import org.drools.modelcompiler.builder.PackageModel;
import org.drools.modelcompiler.builder.generator.DrlxParseUtil;
import org.drools.modelcompiler.builder.generator.RuleContext;
import org.drools.modelcompiler.builder.generator.expression.PatternExpressionBuilder;
import org.drools.modelcompiler.builder.generator.visitor.ModelGeneratorVisitor;

import static org.drools.modelcompiler.builder.generator.DrlxParseUtil.toVar;
import static org.drools.modelcompiler.builder.generator.expression.FlowExpressionBuilder.BIND_CALL;

public class AccumulateVisitorPatternDSL extends AccumulateVisitor {

    public AccumulateVisitorPatternDSL(ModelGeneratorVisitor modelGeneratorVisitor, RuleContext context, PackageModel packageModel) {
        super(context, modelGeneratorVisitor, packageModel);
        this.expressionBuilder = new PatternExpressionBuilder(context);
    }

    @Override
    protected MethodCallExpr buildBinding(String bindingName, Collection<String> usedDeclaration, Expression expression) {
        MethodCallExpr bindDSL = new MethodCallExpr(null, BIND_CALL);
        bindDSL.addArgument(toVar(bindingName));
        usedDeclaration.stream().map(d -> new NameExpr(toVar(d))).forEach(bindDSL::addArgument);
        bindDSL.addArgument(buildConstraintExpression(expression, usedDeclaration));
        return bindDSL;
    }

    @Override
    protected void processNewBinding(Optional<NewBinding> optNewBinding) {
        optNewBinding.ifPresent(newBinding -> {
            final Optional<String> patterBinding = newBinding.patternBinding;
            if (patterBinding.isPresent()) {
                final Optional<MethodCallExpr> optPattern = DrlxParseUtil.findPatternWithBinding(patterBinding.get(), context.getExpressions());
                optPattern.ifPresent(p -> p.addArgument(newBinding.bindExpression));
            } else {
                final MethodCallExpr lastPattern = DrlxParseUtil.findLastPattern(context.getExpressions())
                        .orElseThrow(() -> new RuntimeException("Need the last pattern to add the binding"));
                final Expression replaceBinding = replaceBindingWithPatternBinding(newBinding.bindExpression, lastPattern);
                lastPattern.addArgument(replaceBinding);
            }
        });
    }

    private Expression replaceBindingWithPatternBinding(Expression bindExpression, MethodCallExpr lastPattern) {
        final Expression bindingId = lastPattern.getArgument(0);
        final Optional<NameExpr> first = bindExpression.findFirst(NameExpr.class, e -> e.equals(bindingId));
        first.ifPresent(bindExpression::remove);
        return bindExpression;
    }

    @Override
    protected void postVisit() {

    }
}