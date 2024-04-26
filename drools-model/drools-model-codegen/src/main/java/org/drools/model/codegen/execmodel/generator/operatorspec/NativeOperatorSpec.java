/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.drools.model.codegen.execmodel.generator.operatorspec;

import java.util.Optional;

import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.UnaryExpr;
import org.drools.model.codegen.execmodel.errors.InvalidExpressionErrorResult;
import org.drools.model.codegen.execmodel.generator.RuleContext;
import org.drools.model.codegen.execmodel.generator.TypedExpression;
import org.drools.model.codegen.execmodel.generator.drlxparse.CoercedExpression;
import org.drools.model.codegen.execmodel.generator.expressiontyper.ExpressionTyper;
import org.drools.model.functions.Operator;
import org.drools.mvel.parser.ast.expr.PointFreeExpr;
import org.drools.mvel.parser.printer.PrintUtil;

import static org.drools.model.codegen.execmodel.generator.DrlxParseUtil.toStringLiteral;
import static org.drools.model.codegen.execmodel.generator.DslMethodNames.EVAL_CALL;
import static org.drools.model.codegen.execmodel.generator.DslMethodNames.createDslTopLevelMethod;
import static org.drools.model.codegen.execmodel.generator.drlxparse.ConstraintParser.getCoercedRightExpression;
import static org.drools.model.codegen.execmodel.util.ExpressionUtils.convertBigIntegerLiteralExprOrBigDecimalLiteralExpr;

public class NativeOperatorSpec implements OperatorSpec {
    public static final NativeOperatorSpec INSTANCE = new NativeOperatorSpec();

    public Expression getExpression(RuleContext context, PointFreeExpr pointFreeExpr, TypedExpression left, ExpressionTyper expressionTyper) {
        MethodCallExpr methodCallExpr = createDslTopLevelMethod( EVAL_CALL );

        String opName = pointFreeExpr.getOperator().asString();
        Operator operator = addOperatorArgument( context, methodCallExpr, opName );
        if (operator != null && !operator.isCompatibleWithType( left.getRawClass() )) {
            context.addCompilationError( new InvalidExpressionErrorResult( "Cannot use contains on class " + left.getRawClass() + " in expression '" + PrintUtil.printNode(pointFreeExpr) + "'" ) );
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
                Expression rightExpression = coercedRight.getExpression();
                rightExpression = convertBigIntegerLiteralExprOrBigDecimalLiteralExpr(rightExpression);
                methodCallExpr.addArgument(rightExpression);
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
            methodCallExpr.addArgument( toStringLiteral( opName ) );
        }
        return operator;
    }

    @Override
    public boolean isStatic() {
        return false;
    }
}
