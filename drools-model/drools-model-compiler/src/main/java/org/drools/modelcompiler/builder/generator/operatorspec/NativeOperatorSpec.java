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

package org.drools.modelcompiler.builder.generator.operatorspec;

import java.util.Optional;

import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.StringLiteralExpr;
import com.github.javaparser.ast.expr.UnaryExpr;
import org.drools.model.functions.Operator;
import org.drools.modelcompiler.builder.errors.InvalidExpressionErrorResult;
import org.drools.modelcompiler.builder.generator.RuleContext;
import org.drools.modelcompiler.builder.generator.TypedExpression;
import org.drools.modelcompiler.builder.generator.drlxparse.CoercedExpression;
import org.drools.modelcompiler.builder.generator.expressiontyper.ExpressionTyper;
import org.drools.mvel.parser.ast.expr.PointFreeExpr;

import static org.drools.modelcompiler.builder.generator.DslMethodNames.EVAL_CALL;
import static org.drools.modelcompiler.builder.generator.drlxparse.ConstraintParser.getCoercedRightExpression;

public class NativeOperatorSpec implements OperatorSpec {
    public static final NativeOperatorSpec INSTANCE = new NativeOperatorSpec();

    public Expression getExpression(RuleContext context, PointFreeExpr pointFreeExpr, TypedExpression left, ExpressionTyper expressionTyper) {
        MethodCallExpr methodCallExpr = new MethodCallExpr( null, EVAL_CALL );

        String opName = pointFreeExpr.getOperator().asString();
        Operator operator = addOperatorArgument( context, methodCallExpr, opName );
        if (operator != null && !operator.isCompatibleWithType( left.getRawClass() )) {
            context.addCompilationError( new InvalidExpressionErrorResult( "Cannot use contains on class " + left.getRawClass() + " in expression '" + pointFreeExpr + "'" ) );
        }

        methodCallExpr.addArgument( left.getExpression() );
        for (Expression rightExpr : pointFreeExpr.getRight()) {
            final Optional<TypedExpression> optionalRight = expressionTyper.toTypedExpression(rightExpr).getTypedExpression();
            optionalRight.ifPresent( right -> {
                final TypedExpression coercedRight;
                if (operator != null && operator.requiresCoercion()) {
                    final CoercedExpression.CoercedExpressionResult coerced = new CoercedExpression(left, right, false).coerce();
                    coercedRight = getCoercedRightExpression( context.getPackageModel(), coerced );
                } else {
                    coercedRight = right;
                }
                methodCallExpr.addArgument(coercedRight.getExpression() );
            });
        }

        return pointFreeExpr.isNegated() ?
                new UnaryExpr( methodCallExpr, UnaryExpr.Operator.LOGICAL_COMPLEMENT ) :
                methodCallExpr;
    }

    protected Operator addOperatorArgument( RuleContext context, MethodCallExpr methodCallExpr, String opName ) {
        Operator operator = Operator.Register.getOperator( opName );
        try {
            // if the operator has an INSTANCE field avoid the operator lookup at runtime
            operator.getClass().getField( "INSTANCE" );
            methodCallExpr.addArgument( operator.getClass().getCanonicalName() + ".INSTANCE" );
        } catch (NoSuchFieldException e) {
            methodCallExpr.addArgument( new StringLiteralExpr( opName ) );
        }
        return operator;
    }

    @Override
    public boolean isStatic() {
        return false;
    }
}
