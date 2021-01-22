/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 *
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
import org.drools.modelcompiler.builder.PackageModel;
import org.drools.modelcompiler.builder.generator.DrlxParseUtil;
import org.drools.modelcompiler.builder.generator.RuleContext;
import org.drools.modelcompiler.builder.generator.expression.PatternExpressionBuilder;
import org.drools.modelcompiler.builder.generator.visitor.ModelGeneratorVisitor;
import org.drools.modelcompiler.util.LambdaUtil;

import static org.drools.modelcompiler.builder.generator.DrlxParseUtil.fromVar;
import static org.drools.modelcompiler.builder.generator.expression.PatternExpressionBuilder.BIND_CALL;

public class AccumulateVisitorPatternDSL extends AccumulateVisitor {

    public AccumulateVisitorPatternDSL(ModelGeneratorVisitor modelGeneratorVisitor, RuleContext context, PackageModel packageModel) {
        super(context, modelGeneratorVisitor, packageModel);
        this.expressionBuilder = new PatternExpressionBuilder(context);
    }

    @Override
    protected void pushAccumulateContext( MethodCallExpr accumulateExprs ) {
        context.pushExprPointer(accumulateExprs::addArgument);
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
    protected void processNewBinding(MethodCallExpr accumulateDSL) {
        optNewBinding.ifPresent(newBinding -> {
            final List<Expression> allExpressions = context.getExpressions();
            final MethodCallExpr newBindingExpression = newBinding.bindExpression;

            if (newBinding.patternBinding.size() == 1) {
                new PatternToReplace(context, newBinding.patternBinding).findFromPattern()
                        .ifPresent(pattern -> addBindAsLastChainCall(newBindingExpression, pattern));

                String binding = newBinding.patternBinding.iterator().next();
                composeTwoBindings(binding, newBindingExpression);

            } else if (newBinding.patternBinding.size() == 2) {
                String binding = newBinding.patternBinding.iterator().next();
                composeTwoBindings(binding, newBindingExpression);

            } else {
                final MethodCallExpr lastPattern = DrlxParseUtil.findLastPattern(allExpressions)
                        .orElseThrow(() -> new RuntimeException("Need the last pattern to add the binding"));
                final MethodCallExpr replacedBinding = replaceBindingWithPatternBinding(newBindingExpression, lastPattern);
                addBindAsLastChainCall(replacedBinding, lastPattern);
            }
        });
    }

    private void composeTwoBindings(String binding, MethodCallExpr newBindingExpression) {
        context.findBindingExpression(binding).ifPresent(oldBind -> {

            // compose newComposedBinding using oldBind and newBindingExpression. But still keep oldBind.

            LambdaExpr oldBindLambda = oldBind.findFirst(LambdaExpr.class).orElseThrow(RuntimeException::new);
            LambdaExpr newBindLambda = newBindingExpression.findFirst(LambdaExpr.class).orElseThrow(RuntimeException::new);

            LambdaExpr tmpOldBindLambda = oldBindLambda.clone();
            Expression newComposedLambda = LambdaUtil.appendNewLambdaToOld(tmpOldBindLambda, newBindLambda);

            MethodCallExpr newComposedBinding = new MethodCallExpr(PatternExpressionBuilder.BIND_CALL, newBindingExpression.getArgument(0), newComposedLambda);
            newComposedBinding.setScope(oldBind.getScope().orElseThrow(RuntimeException::new));

            Optional<MethodCallExpr> optReactOn = oldBind.getArguments().stream()
                                                         .filter(MethodCallExpr.class::isInstance)
                                                         .map(MethodCallExpr.class::cast)
                                                         .filter(exp -> exp.getName().asString().equals(PatternExpressionBuilder.REACT_ON_CALL))
                                                         .findFirst();
            if (optReactOn.isPresent()) {
                newComposedBinding.addArgument(optReactOn.get().clone());
            }
            oldBind.setScope(newComposedBinding); // insert newComposedBinding at the first in the chain
        });
    }

    private void addBindAsLastChainCall(MethodCallExpr newBindingExpression, MethodCallExpr pattern) {
        final Optional<Node> optParent = pattern.getParentNode();
        newBindingExpression.setScope(pattern);
        optParent.ifPresent(parent -> {
            parent.replace(pattern, newBindingExpression);
            pattern.setParentNode( newBindingExpression );
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