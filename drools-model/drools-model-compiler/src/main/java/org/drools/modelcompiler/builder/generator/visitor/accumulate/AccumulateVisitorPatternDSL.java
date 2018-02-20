package org.drools.modelcompiler.builder.generator.visitor.accumulate;

import java.util.Collection;
import java.util.Optional;

import org.drools.compiler.lang.descr.AccumulateDescr;
import org.drools.compiler.lang.descr.BaseDescr;
import org.drools.compiler.lang.descr.PatternDescr;
import org.drools.javaparser.ast.expr.BinaryExpr;
import org.drools.javaparser.ast.expr.ClassExpr;
import org.drools.javaparser.ast.expr.Expression;
import org.drools.javaparser.ast.expr.MethodCallExpr;
import org.drools.javaparser.ast.expr.NameExpr;
import org.drools.modelcompiler.builder.PackageModel;
import org.drools.modelcompiler.builder.generator.DeclarationSpec;
import org.drools.modelcompiler.builder.generator.DrlxParseUtil;
import org.drools.modelcompiler.builder.generator.RuleContext;
import org.drools.modelcompiler.builder.generator.TypedExpression;
import org.drools.modelcompiler.builder.generator.drlxparse.ConstraintParser;
import org.drools.modelcompiler.builder.generator.drlxparse.DrlxParseFail;
import org.drools.modelcompiler.builder.generator.drlxparse.DrlxParseResult;
import org.drools.modelcompiler.builder.generator.drlxparse.DrlxParseSuccess;
import org.drools.modelcompiler.builder.generator.drlxparse.ParseResultVisitor;
import org.drools.modelcompiler.builder.generator.expression.PatternExpressionBuilder;
import org.drools.modelcompiler.builder.generator.visitor.ModelGeneratorVisitor;
import org.kie.api.runtime.rule.AccumulateFunction;

import static org.drools.modelcompiler.builder.generator.DrlxParseUtil.toType;
import static org.drools.modelcompiler.builder.generator.DrlxParseUtil.toVar;
import static org.drools.modelcompiler.builder.generator.expression.FlowExpressionBuilder.BIND_CALL;

public class AccumulateVisitorPatternDSL extends AccumulateVisitor {

    public AccumulateVisitorPatternDSL(ModelGeneratorVisitor modelGeneratorVisitor, RuleContext context, PackageModel packageModel) {
        super(context, modelGeneratorVisitor, packageModel);
        this.expressionBuilder = new PatternExpressionBuilder(context);
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
                final Optional<NewBinding> optNewBinding = visit2(context, function, accumulateDSL, basePattern, inputPatternHasConstraints);
                addNewBindingToRelativePattern(optNewBinding);
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
    }

    @Override
    protected MethodCallExpr buildBinding(String bindingName, Collection<String> usedDeclaration, Expression expression) {
        MethodCallExpr bindDSL = new MethodCallExpr(null, BIND_CALL);
        bindDSL.addArgument(toVar(bindingName));
        usedDeclaration.stream().map(d -> new NameExpr(toVar(d))).forEach(bindDSL::addArgument);
        bindDSL.addArgument(buildConstraintExpression(expression, usedDeclaration));
        return bindDSL;
    }

    private void addNewBindingToRelativePattern(Optional<NewBinding> optNewBinding) {
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
}