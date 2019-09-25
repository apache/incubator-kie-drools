package org.drools.modelcompiler.builder.generator.visitor.accumulate;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.LambdaExpr;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.type.Type;
import org.drools.compiler.lang.descr.PatternDescr;
import org.drools.modelcompiler.builder.PackageModel;
import org.drools.modelcompiler.builder.generator.DeclarationSpec;
import org.drools.modelcompiler.builder.generator.DrlxParseUtil;
import org.drools.modelcompiler.builder.generator.RuleContext;
import org.drools.modelcompiler.builder.generator.expression.PatternExpressionBuilder;
import org.drools.modelcompiler.builder.generator.visitor.ModelGeneratorVisitor;
import org.drools.modelcompiler.util.LambdaUtil;

import static com.github.javaparser.StaticJavaParser.parseClassOrInterfaceType;
import static org.drools.modelcompiler.builder.generator.DrlxParseUtil.findPatternWithBinding;
import static org.drools.modelcompiler.builder.generator.DrlxParseUtil.fromVar;
import static org.drools.modelcompiler.builder.generator.expression.PatternExpressionBuilder.BIND_CALL;

public class AccumulateVisitorPatternDSL extends AccumulateVisitor {

    public AccumulateVisitorPatternDSL(ModelGeneratorVisitor modelGeneratorVisitor, RuleContext context, PackageModel packageModel) {
        super(context, modelGeneratorVisitor, packageModel);
        this.expressionBuilder = new PatternExpressionBuilder(context);
    }

    @Override
    protected MethodCallExpr buildBinding(String bindingName, Collection<String> usedDeclaration, Expression expression) {
        MethodCallExpr bindDSL = new MethodCallExpr(null, BIND_CALL);
        bindDSL.addArgument(context.getVar(bindingName));
        usedDeclaration.stream().map(context::getVarExpr).forEach(bindDSL::addArgument);
        bindDSL.addArgument(buildConstraintExpression(expression, usedDeclaration));
        return bindDSL;
    }

    @Override
    protected void processNewBinding(Optional<NewBinding> optNewBinding, Expression accumulateDSL) {
        optNewBinding.ifPresent(newBinding -> {
            final List<String> patterBinding = newBinding.patternBinding;
            final List<Expression> allExpressions = context.getExpressions();
            final MethodCallExpr newBindingExpression = newBinding.bindExpression;
            if (patterBinding.size() == 1) {
                findPatternWithBinding(context, patterBinding, allExpressions)
                        .ifPresent(pattern -> addBindAsLastChainCall(newBindingExpression, pattern));
            } else if (patterBinding.size() == 2) {
                findPatternWithBinding(context, patterBinding, allExpressions)
                        .ifPresent(pattern -> composeTwoBindings(newBindingExpression, pattern));
            } else {
                final MethodCallExpr lastPattern = DrlxParseUtil.findLastPattern(allExpressions)
                        .orElseThrow(() -> new RuntimeException("Need the last pattern to add the binding"));
                final MethodCallExpr replacedBinding = replaceBindingWithPatternBinding(newBindingExpression, lastPattern);
                addBindAsLastChainCall(replacedBinding, lastPattern);
            }
        });
    }

    private void addBindAsLastChainCall(MethodCallExpr newBindingExpression, MethodCallExpr pattern) {
        final Optional<Node> optParent = pattern.getParentNode();
        newBindingExpression.setScope(pattern);
        optParent.ifPresent(parent -> parent.replace(pattern, newBindingExpression));
    }


    private void composeTwoBindings(MethodCallExpr newBindingExpression, MethodCallExpr pattern) {
        String inputObjectType = ((PatternDescr) input).getObjectType();
        Class<?> aType = context.resolveType(inputObjectType)
                .orElseThrow(() -> new UnsupportedOperationException("Input type not found"));


        pattern.getParentNode().ifPresent(oldBindExpression -> {
            MethodCallExpr oldBind = (MethodCallExpr) oldBindExpression;

            NameExpr oldBindVar = (NameExpr) oldBind.getArgument(0);
            Type bType = context.getDeclarationById(oldBindVar.toString().replace("var_", ""))
                    .map(DeclarationSpec::getBoxedType)
                    .orElseThrow(() -> new UnsupportedOperationException("Cannot find bindingId in declaration"));
            LambdaExpr oldBindLambda = (LambdaExpr) oldBind.getArgument(1);
            LambdaExpr newBindLambda = (LambdaExpr) newBindingExpression.getArgument(1);

            MethodCallExpr newComposedLambda = LambdaUtil.compose(oldBindLambda, newBindLambda,
                                                                  parseClassOrInterfaceType(aType.getCanonicalName()), bType);

            newBindingExpression.getArguments().removeLast();
            newBindingExpression.addArgument(newComposedLambda);
            newBindingExpression.setScope(pattern);

            oldBind.replace(newBindingExpression);
        });
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

        return bindExpression;
    }

    @Override
    protected void postVisit() {
    }
}