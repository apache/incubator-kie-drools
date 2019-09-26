package org.drools.modelcompiler.builder.generator.visitor.accumulate;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.stream.Collectors;

import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.LambdaExpr;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.NameExpr;
import org.drools.modelcompiler.builder.PackageModel;
import org.drools.modelcompiler.builder.generator.RuleContext;
import org.drools.modelcompiler.builder.generator.expression.FlowExpressionBuilder;
import org.drools.modelcompiler.builder.generator.visitor.ModelGeneratorVisitor;
import org.drools.modelcompiler.util.LambdaUtil;

import static org.drools.modelcompiler.builder.generator.DrlxParseUtil.fromVar;
import static org.drools.modelcompiler.builder.generator.DslMethodNames.BIND_AS_CALL;
import static org.drools.modelcompiler.builder.generator.DslMethodNames.INPUT_CALL;

public class AccumulateVisitorFlowDSL extends AccumulateVisitor {

    final List<NewBinding> newBindingResults = new ArrayList<>();

    public AccumulateVisitorFlowDSL(ModelGeneratorVisitor modelGeneratorVisitor, RuleContext context, PackageModel packageModel) {
        super(context, modelGeneratorVisitor, packageModel);
        expressionBuilder = new FlowExpressionBuilder(context);
    }

    @Override
    protected MethodCallExpr buildBinding(String bindingName, Collection<String> usedDeclaration, Expression expression) {
        MethodCallExpr bindDSL = new MethodCallExpr(null, FlowExpressionBuilder.BIND_CALL);
        bindDSL.addArgument(context.getVar(bindingName));
        MethodCallExpr bindAsDSL = new MethodCallExpr(bindDSL, BIND_AS_CALL);
        usedDeclaration.stream().map(d -> context.getVarExpr(d)).forEach(bindAsDSL::addArgument);
        bindAsDSL.addArgument(buildConstraintExpression(expression, usedDeclaration));
        return bindAsDSL;
    }

    @Override
    protected Optional<Expression> processNewBinding(Optional<NewBinding> optNewBinding, MethodCallExpr accumulateDSL) {
        return optNewBinding.flatMap(newBinding -> {
            final List<Expression> allExpressions = context.getExpressions();
            final MethodCallExpr newBindingExpression = newBinding.bindExpression;

            final SortedSet<String> patterBinding = new TreeSet<>(newBinding.patternBinding);
            if (patterBinding.size() == 2 && findLastBinding(allExpressions) != null) {
                MethodCallExpr lastBinding = findLastBinding(allExpressions);
                return composeTwoBindingIntoExpression(newBindingExpression, lastBinding);
            } else {
                replaceBindingWithPatternBinding( newBindingExpression, findLastPattern(allExpressions) );
                newBindingResults.add( newBinding );
                return Optional.empty();
            }
        });
    }

    private Optional<Expression> composeTwoBindingIntoExpression(MethodCallExpr newBindingExpression, MethodCallExpr lastBinding) {
        if(lastBinding != null) {
            String inputName = composeTwoBindings(newBindingExpression, lastBinding).get().toString();

            return lastBinding.getParentNode().flatMap(n -> {
                Expression input = new MethodCallExpr(null, INPUT_CALL, NodeList.nodeList(new NameExpr(inputName)));
                n.replace(input);

                return Optional.of(newBindingExpression);
            });
        }
        return Optional.empty();
    }

    Optional<NameExpr> composeTwoBindings(MethodCallExpr newBindingExpression, MethodCallExpr pattern) {
        return pattern.getParentNode().map(oldBindExpression -> {
            MethodCallExpr oldBind = (MethodCallExpr) oldBindExpression;

            LambdaExpr oldBindLambda = (LambdaExpr) oldBind.getArgument(1);
            LambdaExpr newBindLambda = (LambdaExpr) newBindingExpression.getArgument(1);

            Expression newComposedLambda = LambdaUtil.compose(oldBindLambda, newBindLambda);

            newBindingExpression.getArguments().removeLast();
            newBindingExpression.addArgument(newComposedLambda);
            NameExpr argument = (NameExpr) oldBind.getArgument(0);
            newBindingExpression.setArgument(0, argument);
            return argument;
        });
    }

    private MethodCallExpr findLastPattern(List<Expression> expressions) {
        final List<MethodCallExpr> collect = expressions.stream().flatMap( e ->
            e.findAll(MethodCallExpr.class, expr -> expr.getName().asString().equals(FlowExpressionBuilder.EXPR_CALL)).stream()
        ).collect( Collectors.toList());

        return collect.isEmpty() ? null : collect.get(collect.size() - 1);
    }

    private MethodCallExpr findLastBinding(List<Expression> expressions) {
        final List<MethodCallExpr> collect = expressions.stream().flatMap( e ->
            e.findAll(MethodCallExpr.class, expr -> FlowExpressionBuilder.BIND_CALL.equals(expr.getNameAsString())).stream()
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

        newBindingsConcatenated.forEach(e -> context.getExpressions().add(context.getExpressions().size()-1, e));
    }
}