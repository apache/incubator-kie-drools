package org.drools.modelcompiler.builder.generator.visitor.accumulate;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.drools.javaparser.ast.body.Parameter;
import org.drools.javaparser.ast.expr.Expression;
import org.drools.javaparser.ast.expr.LambdaExpr;
import org.drools.javaparser.ast.expr.MethodCallExpr;
import org.drools.javaparser.ast.expr.NameExpr;
import org.drools.modelcompiler.builder.PackageModel;
import org.drools.modelcompiler.builder.generator.RuleContext;
import org.drools.modelcompiler.builder.generator.expression.FlowExpressionBuilder;
import org.drools.modelcompiler.builder.generator.visitor.ModelGeneratorVisitor;

import static org.drools.modelcompiler.builder.generator.DrlxParseUtil.fromVar;
import static org.drools.modelcompiler.builder.generator.DrlxParseUtil.toVar;
import static org.drools.modelcompiler.builder.generator.DslMethodNames.BIND_AS_CALL;

public class AccumulateVisitorFlowDSL extends AccumulateVisitor {

    final List<NewBinding> newBindingResults = new ArrayList<>();

    public AccumulateVisitorFlowDSL(ModelGeneratorVisitor modelGeneratorVisitor, RuleContext context, PackageModel packageModel) {
        super(context, modelGeneratorVisitor, packageModel);
        expressionBuilder = new FlowExpressionBuilder(context);
    }

    @Override
    protected MethodCallExpr buildBinding(String bindingName, Collection<String> usedDeclaration, Expression expression) {
        MethodCallExpr bindDSL = new MethodCallExpr(null, FlowExpressionBuilder.BIND_CALL);
        bindDSL.addArgument(toVar(bindingName));
        MethodCallExpr bindAsDSL = new MethodCallExpr(bindDSL, BIND_AS_CALL);
        usedDeclaration.stream().map(d -> new NameExpr(toVar(d))).forEach(bindAsDSL::addArgument);
        bindAsDSL.addArgument(buildConstraintExpression(expression, usedDeclaration));
        return bindAsDSL;
    }

    @Override
    protected void processNewBinding(Optional<NewBinding> optNewBinding) {
        optNewBinding.ifPresent(newBinding -> {
            final Optional<String> patterBinding = newBinding.patternBinding;
            final List<Expression> allExpressions = context.getExpressions();
            final MethodCallExpr newBindingExpression = newBinding.bindExpression;
            replaceBindingWithPatternBinding( newBindingExpression, findLastPattern(allExpressions) );
            newBindingResults.add( newBinding );
        });
    }

    private MethodCallExpr findLastPattern(List<Expression> expressions) {
        final List<MethodCallExpr> collect = expressions.stream().flatMap( e ->
            e.findAll(MethodCallExpr.class, expr -> expr.getName().asString().equals(FlowExpressionBuilder.EXPR_CALL)).stream()
        ).collect( Collectors.toList());

        return collect.isEmpty() ? null : collect.get(collect.size() - 1);
    }

    private void replaceBindingWithPatternBinding(MethodCallExpr bindExpression, MethodCallExpr lastPattern) {
        if (lastPattern == null) {
            return;
        }

        final Expression bindingId = lastPattern.getArgument(1);

        bindExpression.findFirst(NameExpr.class, e -> e.equals(bindingId)).ifPresent( name -> {
            LambdaExpr lambda = (LambdaExpr)bindExpression.getArgument( bindExpression.getArguments().size()-1 );
            if (lambda.getParameters().size() > 1) {
                String formalArg = fromVar( name.getNameAsString() );
                for (Parameter param : lambda.getParameters()) {
                    if (param.getNameAsString().equals( formalArg )) {
                        lambda.getParameters().remove( param );
                        lambda.getParameters().add( 0, param );
                        break;
                    }
                }
            }
            bindExpression.getArguments().remove(name);
            bindExpression.getArguments().add(0, name);
        } );
    }

    @Override
    protected void postVisit() {
        // Bind expressions are outside the Accumulate Expr
        newBindingResults.forEach(e -> context.getExpressions().add(context.getExpressions().size()-1, e.bindExpression));
    }
}