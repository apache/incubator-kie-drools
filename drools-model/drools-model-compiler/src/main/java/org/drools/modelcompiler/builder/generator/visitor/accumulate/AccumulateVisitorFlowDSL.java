package org.drools.modelcompiler.builder.generator.visitor.accumulate;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.drools.compiler.lang.descr.AccumulateDescr;
import org.drools.compiler.lang.descr.BaseDescr;
import org.drools.compiler.lang.descr.PatternDescr;
import org.drools.javaparser.ast.expr.Expression;
import org.drools.javaparser.ast.expr.MethodCallExpr;
import org.drools.javaparser.ast.expr.NameExpr;
import org.drools.modelcompiler.builder.PackageModel;
import org.drools.modelcompiler.builder.generator.RuleContext;
import org.drools.modelcompiler.builder.generator.expression.FlowExpressionBuilder;
import org.drools.modelcompiler.builder.generator.visitor.ModelGeneratorVisitor;

import static org.drools.modelcompiler.builder.generator.DrlxParseUtil.toVar;
import static org.drools.modelcompiler.builder.generator.ModelGenerator.BIND_AS_CALL;
import static org.drools.modelcompiler.builder.generator.expression.FlowExpressionBuilder.BIND_CALL;

public class AccumulateVisitorFlowDSL extends AccumulateVisitor {

    final List<NewBinding> newBindingResults = new ArrayList<>();

    public AccumulateVisitorFlowDSL(ModelGeneratorVisitor modelGeneratorVisitor, RuleContext context, PackageModel packageModel) {
        super(context, modelGeneratorVisitor, packageModel);
        expressionBuilder = new FlowExpressionBuilder(context);
    }

    public void visit(AccumulateDescr descr, PatternDescr basePattern) {
        final MethodCallExpr accumulateDSL = new MethodCallExpr(null, "accumulate");
        context.addExpression(accumulateDSL);
        final MethodCallExpr accumulateExprs = new MethodCallExpr(null, "and");
        accumulateDSL.addArgument(accumulateExprs);

        context.pushExprPointer(accumulateExprs::addArgument);

        BaseDescr input = descr.getInputPattern() == null ? descr.getInput() : descr.getInputPattern();
        boolean inputPatternHasConstraints = (input instanceof PatternDescr) && (!((PatternDescr) input).getConstraint().getDescrs().isEmpty());
        input.accept(modelGeneratorVisitor);

        if (accumulateExprs.getArguments().isEmpty()) {
            accumulateDSL.remove(accumulateExprs);
        } else if (accumulateExprs.getArguments().size() == 1) {
            accumulateDSL.setArgument(0, accumulateExprs.getArguments().get(0));
        }

        if (!descr.getFunctions().isEmpty()) {
            for (AccumulateDescr.AccumulateFunctionCallDescr function : descr.getFunctions()) {
                final Optional<NewBinding> optNewBinding = visit(context, function, accumulateDSL, basePattern, inputPatternHasConstraints);
                processNewBinding(optNewBinding);
            }
        } else if (descr.getFunctions().isEmpty() && descr.getInitCode() != null) {
            // LEGACY: Accumulate with inline custom code
            if (input instanceof PatternDescr) {
                visitAccInlineCustomCode(context, descr, accumulateDSL, basePattern, (PatternDescr) input);
            } else {
                throw new UnsupportedOperationException("I was expecting input to be of type PatternDescr. " + input);
            }
        } else {
            throw new UnsupportedOperationException("Unknown type of Accumulate.");
        }

        postVisit();
    }

    @Override
    protected MethodCallExpr buildBinding(String bindingName, Collection<String> usedDeclaration, Expression expression) {
        MethodCallExpr bindDSL = new MethodCallExpr(null, BIND_CALL);
        bindDSL.addArgument(toVar(bindingName));
        MethodCallExpr bindAsDSL = new MethodCallExpr(bindDSL, BIND_AS_CALL);
        usedDeclaration.stream().map(d -> new NameExpr(toVar(d))).forEach(bindAsDSL::addArgument);
        bindAsDSL.addArgument(buildConstraintExpression(expression, usedDeclaration));
        return bindAsDSL;
    }

    @Override
    protected void processNewBinding(Optional<NewBinding> optNewBinding) {
        optNewBinding.ifPresent(newBindingResults::add);
    }

    @Override
    protected void postVisit() {
        // Bind expressions are outside the Accumulate Expr
        context.popExprPointer();
        newBindingResults.forEach(e -> context.getExpressions().add(0, e.bindExpression));
    }

}