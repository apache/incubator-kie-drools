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
package org.drools.model.codegen.execmodel.generator.expression;

import java.util.Collection;
import java.util.Optional;

import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.expr.BinaryExpr;
import com.github.javaparser.ast.expr.ClassExpr;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.FieldAccessExpr;
import com.github.javaparser.ast.expr.LambdaExpr;
import com.github.javaparser.ast.expr.LiteralExpr;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.StringLiteralExpr;
import com.github.javaparser.ast.stmt.ExpressionStmt;
import org.drools.model.codegen.execmodel.generator.RuleContext;
import org.drools.model.codegen.execmodel.generator.TypedExpression;
import org.drools.model.codegen.execmodel.generator.drlxparse.DrlxParseSuccess;
import org.drools.model.codegen.execmodel.generator.drlxparse.MultipleDrlxParseSuccess;
import org.drools.model.codegen.execmodel.generator.drlxparse.SingleDrlxParseSuccess;

import static java.util.Optional.of;
import static org.drools.model.codegen.execmodel.generator.DrlxParseUtil.THIS_PLACEHOLDER;
import static org.drools.model.codegen.execmodel.generator.DrlxParseUtil.findLastMethodInChain;
import static org.drools.model.codegen.execmodel.generator.DrlxParseUtil.toJavaParserType;
import static org.drools.model.codegen.execmodel.generator.DslMethodNames.ALPHA_INDEXED_BY_CALL;
import static org.drools.model.codegen.execmodel.generator.DslMethodNames.BETA_INDEXED_BY_CALL;
import static org.drools.model.codegen.execmodel.generator.DslMethodNames.BIND_CALL;
import static org.drools.model.codegen.execmodel.generator.DslMethodNames.EXPR_AND_CALL;
import static org.drools.model.codegen.execmodel.generator.DslMethodNames.EXPR_CALL;
import static org.drools.model.codegen.execmodel.generator.DslMethodNames.EXPR_END_AND_CALL;
import static org.drools.model.codegen.execmodel.generator.DslMethodNames.EXPR_END_OR_CALL;
import static org.drools.model.codegen.execmodel.generator.DslMethodNames.EXPR_OR_CALL;
import static org.drools.model.codegen.execmodel.generator.DslMethodNames.NO_OP_EXPR;
import static org.drools.model.codegen.execmodel.generator.DslMethodNames.REACT_ON_CALL;
import static org.drools.model.codegen.execmodel.generator.DslMethodNames.createDslTopLevelMethod;

public class PatternExpressionBuilder extends AbstractExpressionBuilder {

    public PatternExpressionBuilder(RuleContext context) {
        super(context);
    }

    @Override
    public void processExpression(SingleDrlxParseSuccess drlxParseResult) {
        if (drlxParseResult.hasUnificationVariable()) {
            Expression dslExpr = buildUnificationExpression(drlxParseResult);
            context.addExpression(dslExpr);
        } else if (drlxParseResult.isPredicate()) {
            Expression dslExpr = buildExpressionWithIndexing(drlxParseResult);
            context.addExpression(dslExpr);
        }

        if (drlxParseResult.getExprBinding() != null) {
            Expression dslExpr = buildBinding(drlxParseResult);
            context.addExpression(dslExpr);
        }
    }

    @Override
    public MethodCallExpr buildExpressionWithIndexing(DrlxParseSuccess drlxParseResult) {
        if (drlxParseResult instanceof MultipleDrlxParseSuccess) {
            MultipleDrlxParseSuccess multi = ( MultipleDrlxParseSuccess ) drlxParseResult;
            MethodCallExpr exprDSL = new MethodCallExpr(null, multi.getOperator() == BinaryExpr.Operator.OR ? EXPR_OR_CALL : EXPR_AND_CALL );
            for (DrlxParseSuccess child : multi.getResults()) {
                MethodCallExpr childExpr = buildExpressionWithIndexing(child);
                setScopeToAscendantExpr(childExpr, exprDSL);
                exprDSL = childExpr;

                if (child instanceof SingleDrlxParseSuccess && child.getExprBinding() != null) {
                    SingleDrlxParseSuccess singleDrlxChild = (SingleDrlxParseSuccess) child;
                    context.addDeclaration( child.getExprBinding(), singleDrlxChild.getLeftExprRawClass() );
                    Expression dslExpr = buildBinding(singleDrlxChild);
                    context.addExpression(dslExpr);
                }
            }
            return new MethodCallExpr(exprDSL, multi.getOperator() == BinaryExpr.Operator.OR ? EXPR_END_OR_CALL : EXPR_END_AND_CALL );
        }
        return buildSingleExpressionWithIndexing((SingleDrlxParseSuccess ) drlxParseResult);
    }

    private void setScopeToAscendantExpr(MethodCallExpr targetExpr, MethodCallExpr newScope) {
        Optional<Expression> optCurrentScope = targetExpr.getScope();
        if (optCurrentScope.isEmpty()) {
            targetExpr.setScope(newScope);
        } else {
            Expression currentScope = optCurrentScope.get();
            if (currentScope.isMethodCallExpr()) {
                setScopeToAscendantExpr(currentScope.asMethodCallExpr(), newScope);
            } else {
                throw new IllegalStateException("Root MethodCallExpr scope has to be null : " + targetExpr);
            }
        }
    }

    private MethodCallExpr buildSingleExpressionWithIndexing(SingleDrlxParseSuccess drlxParseResult) {
        String exprId = createExprId(drlxParseResult);
        MethodCallExpr exprDSL = new MethodCallExpr(null, EXPR_CALL);
        if (exprId != null && !"".equals(exprId)) {
            exprDSL.addArgument(new StringLiteralExpr(exprId));
        }

        exprDSL = buildExpression(drlxParseResult, exprDSL);
        MethodCallExpr finalExprDSL = exprDSL;
        buildIndexedBy(drlxParseResult).ifPresent(finalExprDSL::addArgument);
        buildReactOn(drlxParseResult).ifPresent(finalExprDSL::addArgument);
        return exprDSL;
    }

    private MethodCallExpr buildExpression(SingleDrlxParseSuccess drlxParseResult, MethodCallExpr exprDSL) {
        if (drlxParseResult.isTemporal()) {
            return buildTemporalExpression(drlxParseResult, exprDSL);
        }

        MethodCallExpr ooPathPatternExpr = null;
        for (String usedDeclarattion : drlxParseResult.getUsedDeclarations()) {
            if ( !(drlxParseResult.isSkipThisAsParam() && usedDeclarattion.equals( drlxParseResult.getPatternBinding() ) ) ) {
                MethodCallExpr ooPathExpr = context.getOOPathPatternExpr(usedDeclarattion);
                if (ooPathExpr == null) {
                    exprDSL.addArgument(context.getVarExpr(usedDeclarattion));
                } else {
                    ooPathPatternExpr = ooPathExpr;
                }
            }
        }

        if (drlxParseResult.getRightLiteral() != null) {
            exprDSL.addArgument( "" + drlxParseResult.getRightLiteral() );
        }

        if (ooPathPatternExpr != null) {
            // this constraints belongs to an external oopath pattern so transfer its evaluation there
            exprDSL.setScope(ooPathPatternExpr.clone());
            // flag the old oopath pattern as a no_op in order to remove it from the generated dsl
            findLastMethodInChain(ooPathPatternExpr).setName(NO_OP_EXPR);
            drlxParseResult.setSkipThisAsParam(true);
        }
        exprDSL.addArgument(buildConstraintExpression(drlxParseResult, drlxParseResult.getExpr()));
        return exprDSL;
    }

    private Optional<MethodCallExpr> buildReactOn(SingleDrlxParseSuccess drlxParseResult) {
        if (shouldBuildReactOn(drlxParseResult)) {
            MethodCallExpr reactOnDSL = createDslTopLevelMethod(REACT_ON_CALL);
            drlxParseResult.getReactOnProperties().stream()
                    .map(StringLiteralExpr::new)
                    .forEach(reactOnDSL::addArgument);
            return of(reactOnDSL);
        }
        return Optional.empty();
    }

    @Override
    public MethodCallExpr buildBinding(SingleDrlxParseSuccess drlxParseResult) {
        sortUsedDeclarations(drlxParseResult);
        MethodCallExpr bindDSL = new MethodCallExpr(null, BIND_CALL);
        String boundVar = drlxParseResult.hasUnificationVariable() ?
                drlxParseResult.getUnificationVariable() :
                drlxParseResult.getExprBinding();
        bindDSL.addArgument(context.getVarExpr(boundVar));
        final Expression constraintExpression = getBindingExpression(drlxParseResult);
        drlxParseResult.getUsedDeclarationsOnLeft().forEach(d -> bindDSL.addArgument(context.getVar(d)));
        bindDSL.addArgument(constraintExpression);
        final Optional<MethodCallExpr> methodCallExpr = buildReactOn(drlxParseResult);
        methodCallExpr.ifPresent(bindDSL::addArgument);
        context.registerBindingExpression(boundVar, bindDSL);
        return bindDSL;
    }

    private Optional<MethodCallExpr> buildIndexedBy(SingleDrlxParseSuccess drlxParseResult) {
        if (drlxParseResult.isUnification()) {
            TypedExpression left = drlxParseResult.getLeft();
            TypedExpression right = drlxParseResult.getRight();

            LambdaExpr indexedByLeftOperandExtractor = createIndexedByLambda(drlxParseResult, left, right);
            MethodCallExpr indexedByDSL = indexedByDSL(drlxParseResult, left);

            indexedByDSL.addArgument(org.drools.model.Index.ConstraintType.class.getCanonicalName() + ".EQUAL");
            indexedByDSL.addArgument("-1");
            indexedByDSL.addArgument(indexedByLeftOperandExtractor);
            indexedByDSL.addArgument("" + null);
            return Optional.of(indexedByDSL);
        }

        if ( !shouldCreateIndex( drlxParseResult ) ) {
            return Optional.empty();
        }

        TypedExpression left = drlxParseResult.getLeft();
        TypedExpression right = drlxParseResult.getRight();

        Expression rightExpression = right.getExpression();
        if (!drlxParseResult.isBetaConstraint() && !(rightExpression instanceof LiteralExpr || isStringToDateExpression(rightExpression) || isNumberToStringExpression(rightExpression))) {
            return Optional.empty();
        }

        FieldAccessExpr indexedBy_constraintType = new FieldAccessExpr(new NameExpr(org.drools.model.Index.ConstraintType.class.getCanonicalName()), drlxParseResult.getDecodeConstraintType().toString()); // not 100% accurate as the type in "nameExpr" is actually parsed if it was JavaParsers as a big chain of FieldAccessExpr

        LambdaExpr indexedByLeftOperandExtractor = createIndexedByLambda(drlxParseResult, left, right);

        MethodCallExpr indexedByDSL = indexedByDSL(drlxParseResult, left);

        indexedByDSL.addArgument( indexedBy_constraintType );
        indexedByDSL.addArgument( getIndexIdArgument( drlxParseResult, left ) );
        indexedByDSL.addArgument(indexedByLeftOperandExtractor );

        Collection<String> usedDeclarations = drlxParseResult.getUsedDeclarations();
        java.lang.reflect.Type leftType = left.getType();
        if ( drlxParseResult.isBetaConstraint() ) {
            addIndexedByDeclaration(left, right, left.containThis(), indexedByDSL, usedDeclarations);
        } else {
            indexedByDSL.addArgument( narrowExpressionToType(right, leftType));
        }

        return Optional.of(indexedByDSL);
    }

    private MethodCallExpr indexedByDSL(SingleDrlxParseSuccess drlxParseResult, TypedExpression left) {
        MethodCallExpr indexedByDSL = createDslTopLevelMethod(drlxParseResult.isBetaConstraint() ? BETA_INDEXED_BY_CALL : ALPHA_INDEXED_BY_CALL);
        indexedByDSL.addArgument(new ClassExpr(toJavaParserType(left.getRawClass())));
        return indexedByDSL;
    }

    private LambdaExpr createIndexedByLambda(SingleDrlxParseSuccess drlxParseResult, TypedExpression left, TypedExpression right) {
        LambdaExpr indexedByLeftOperandExtractor = new LambdaExpr();
        indexedByLeftOperandExtractor.setEnclosingParameters(true);
        indexedByLeftOperandExtractor.setBody(new ExpressionStmt(left.containThis() ? left.getExpression() : right.getExpression()));
        indexedByLeftOperandExtractor.addParameter(new Parameter(drlxParseResult.getPatternJPType(), THIS_PLACEHOLDER));
        return indexedByLeftOperandExtractor;
    }
}
