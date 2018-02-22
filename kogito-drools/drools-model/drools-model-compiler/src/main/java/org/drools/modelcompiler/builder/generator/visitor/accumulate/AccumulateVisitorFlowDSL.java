package org.drools.modelcompiler.builder.generator.visitor.accumulate;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

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
        newBindingResults.forEach(e -> context.getExpressions().add(0, e.bindExpression));
    }
}