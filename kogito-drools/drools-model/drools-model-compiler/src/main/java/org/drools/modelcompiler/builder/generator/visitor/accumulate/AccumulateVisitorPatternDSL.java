package org.drools.modelcompiler.builder.generator.visitor.accumulate;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.drools.javaparser.ast.Node;
import org.drools.javaparser.ast.body.Parameter;
import org.drools.javaparser.ast.expr.Expression;
import org.drools.javaparser.ast.expr.LambdaExpr;
import org.drools.javaparser.ast.expr.MethodCallExpr;
import org.drools.javaparser.ast.expr.NameExpr;
import org.drools.modelcompiler.builder.PackageModel;
import org.drools.modelcompiler.builder.generator.DrlxParseUtil;
import org.drools.modelcompiler.builder.generator.RuleContext;
import org.drools.modelcompiler.builder.generator.expression.PatternExpressionBuilder;
import org.drools.modelcompiler.builder.generator.visitor.ModelGeneratorVisitor;

import static org.drools.modelcompiler.builder.generator.DrlxParseUtil.fromVar;
import static org.drools.modelcompiler.builder.generator.DrlxParseUtil.toVar;

public class AccumulateVisitorPatternDSL extends AccumulateVisitor {

    public AccumulateVisitorPatternDSL(ModelGeneratorVisitor modelGeneratorVisitor, RuleContext context, PackageModel packageModel) {
        super(context, modelGeneratorVisitor, packageModel);
        this.expressionBuilder = new PatternExpressionBuilder(context);
    }

    @Override
    protected MethodCallExpr buildBinding(String bindingName, Collection<String> usedDeclaration, Expression expression) {
        MethodCallExpr bindDSL = new MethodCallExpr(null, PatternExpressionBuilder.BIND_CALL);
        bindDSL.addArgument(toVar(bindingName));
        usedDeclaration.stream().map(d -> new NameExpr(toVar(d))).forEach(bindDSL::addArgument);
        bindDSL.addArgument(buildConstraintExpression(expression, usedDeclaration));
        return bindDSL;
    }

    @Override
    protected void processNewBinding(Optional<NewBinding> optNewBinding) {
        optNewBinding.ifPresent(newBinding -> {
            final Optional<String> patterBinding = newBinding.patternBinding;
            final List<Expression> allExpressions = context.getExpressions();
            final MethodCallExpr newBindingExpression = newBinding.bindExpression;
            if (patterBinding.isPresent()) {
                final Optional<MethodCallExpr> optPattern = DrlxParseUtil.findPatternWithBinding(patterBinding.get(), allExpressions);
                optPattern.ifPresent(pattern -> addBindAsLastChainCall(newBindingExpression, pattern));
            } else {
                final MethodCallExpr lastPattern = DrlxParseUtil.findLastPattern(allExpressions)
                        .orElseThrow(() -> new RuntimeException("Need the last pattern to add the binding"));
                final MethodCallExpr replacedBinding = replaceBindingWithPatternBinding(newBindingExpression, lastPattern);
                addBindAsLastChainCall(replacedBinding, lastPattern);
            }
        });
    }

    private void addBindAsLastChainCall(MethodCallExpr newBindingExpression, MethodCallExpr pattern) {
        final Expression newScope = (Expression) pattern.getParentNode().orElse(pattern);
        final Optional<Node> optParent = newScope.getParentNode();
        newBindingExpression.setScope(newScope);
        optParent.ifPresent(parent -> parent.replace(newScope, newBindingExpression));
    }

    private MethodCallExpr replaceBindingWithPatternBinding(MethodCallExpr bindExpression, MethodCallExpr lastPattern) {
        final Expression bindingId = lastPattern.getArgument(0);

        bindExpression.findFirst(NameExpr.class, e -> e.equals(bindingId)).ifPresent( name -> {
            bindExpression.remove(name);
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
        } );

        return (MethodCallExpr) bindExpression;
    }

    @Override
    protected void postVisit() {
    }
}