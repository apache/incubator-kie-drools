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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.LambdaExpr;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.NameExpr;
import org.drools.modelcompiler.builder.PackageModel;
import org.drools.modelcompiler.builder.generator.DrlxParseUtil;
import org.drools.modelcompiler.builder.generator.RuleContext;
import org.drools.modelcompiler.builder.generator.expression.FlowExpressionBuilder;
import org.drools.modelcompiler.builder.generator.visitor.ModelGeneratorVisitor;
import org.drools.modelcompiler.util.LambdaUtil;

import static org.drools.modelcompiler.builder.generator.DrlxParseUtil.fromVar;
import static org.drools.modelcompiler.builder.generator.DslMethodNames.BIND_AS_CALL;
import static org.drools.modelcompiler.builder.generator.DslMethodNames.INPUT_CALL;
import static org.drools.modelcompiler.builder.generator.expression.FlowExpressionBuilder.BIND_CALL;

public class AccumulateVisitorFlowDSL extends AccumulateVisitor {

    private final List<NewBinding> newBindingResults = new ArrayList<>();
    private final List<Expression> newBindingsConcatenated = new ArrayList<>();

    public AccumulateVisitorFlowDSL(ModelGeneratorVisitor modelGeneratorVisitor, RuleContext context, PackageModel packageModel) {
        super(context, modelGeneratorVisitor, packageModel);
        expressionBuilder = new FlowExpressionBuilder(context);
    }

    @Override
    protected void pushAccumulateContext( MethodCallExpr accumulateExprs ) {
        context.pushExprPointer(new FlowExpressionConsumer( accumulateExprs ));
    }

    public static class FlowExpressionConsumer implements Consumer<Expression> {

        private final MethodCallExpr accumulateExprs;

        public FlowExpressionConsumer( MethodCallExpr accumulateExprs ) {
            this.accumulateExprs = accumulateExprs;
        }

        @Override
        public void accept( Expression expression ) {
            accumulateExprs.addArgument( expression );
        }
    }

    @Override
    protected MethodCallExpr buildBinding(String bindingName, Collection<String> usedDeclaration, Expression expression) {
        MethodCallExpr bindDSL = new MethodCallExpr(null, BIND_CALL);
        bindDSL.addArgument(context.getVar(bindingName));
        MethodCallExpr bindAsDSL = new MethodCallExpr(bindDSL, BIND_AS_CALL);
        usedDeclaration.stream().map(context::getVarExpr).forEach(bindAsDSL::addArgument);
        bindAsDSL.addArgument(buildConstraintExpression(expression, usedDeclaration));
        return bindAsDSL;
    }

    @Override
    protected void processNewBinding(MethodCallExpr accumulateDSL) {
        optNewBinding.ifPresent(newBinding -> {
            final List<Expression> allExpressions = context.getExpressions();
            final MethodCallExpr newBindingExpression = newBinding.bindExpression;

            final SortedSet<String> patterBinding = new TreeSet<>(newBinding.patternBinding);
            MethodCallExpr lastBinding = findLastBinding(allExpressions);
            if (patterBinding.size() == 2 && lastBinding != null) {
                composeTwoBindingIntoExpression(newBindingExpression, lastBinding, accumulateDSL);
            } else {
                MethodCallExpr lastPattern = findLastPattern(allExpressions);
                if (lastPattern != null) {
                    replaceBindingWithPatternBinding( newBindingExpression, lastPattern );
                } else {
                    ((FlowExpressionConsumer)context.peekExprPointer()).accumulateExprs.getArguments().add( 0, newBindingExpression );
                }
                newBindingResults.add( newBinding );
            }
        });
    }

    private void composeTwoBindingIntoExpression(MethodCallExpr newBindingExpression,
                                                 MethodCallExpr lastBinding,
                                                 MethodCallExpr accumulateDSL) {
        composeTwoBindings(newBindingExpression, lastBinding).map(Node::toString)
                .ifPresent(inputName -> lastBinding.getParentNode()
                        .ifPresent(n -> replaceBindWithInput(newBindingExpression, accumulateDSL, inputName)));
    }

    private void replaceBindWithInput(MethodCallExpr newBindingExpression, MethodCallExpr accumulateDSL, String inputName) {
        Expression input = new MethodCallExpr(null, INPUT_CALL, NodeList.nodeList(new NameExpr(inputName)));
        List<MethodCallExpr> binds = findBind(accumulateDSL);
        if (!binds.isEmpty()) {
            binds.get(0).replace( input );
        }
        for (MethodCallExpr bind : findBind(accumulateDSL)) {
            bind.remove();
        }
        newBindingsConcatenated.add(newBindingExpression);
    }

    private static List<MethodCallExpr> findBind(Expression dsl) {
        return dsl.findAll(MethodCallExpr.class,
                           mc -> DrlxParseUtil.findRootNodeViaScope(mc)
                                   .filter(l -> l.isMethodCallExpr() && l.asMethodCallExpr().getNameAsString().equals(BIND_CALL)).isPresent());
    }

    private Optional<NameExpr> composeTwoBindings(MethodCallExpr newBindingExpression, MethodCallExpr pattern) {
        return pattern.getParentNode().map(oldBindExpression -> {
            MethodCallExpr oldBind = (MethodCallExpr) oldBindExpression;

            LambdaExpr oldBindLambda = oldBind.findFirst(LambdaExpr.class).orElseThrow(RuntimeException::new);
            LambdaExpr newBindLambda = newBindingExpression.findFirst(LambdaExpr.class).orElseThrow(RuntimeException::new);

            Expression newComposedLambda = LambdaUtil.appendNewLambdaToOld(oldBindLambda, newBindLambda);

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