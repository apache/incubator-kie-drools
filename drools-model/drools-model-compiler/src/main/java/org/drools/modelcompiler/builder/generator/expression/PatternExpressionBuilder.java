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

package org.drools.modelcompiler.builder.generator.expression;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
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
import org.drools.modelcompiler.builder.generator.RuleContext;
import org.drools.modelcompiler.builder.generator.TypedExpression;
import org.drools.modelcompiler.builder.generator.drlxparse.DrlxParseSuccess;
import org.drools.modelcompiler.builder.generator.drlxparse.MultipleDrlxParseSuccess;
import org.drools.modelcompiler.builder.generator.drlxparse.SingleDrlxParseSuccess;

import static java.util.Optional.of;
import static com.github.javaparser.StaticJavaParser.parseClassOrInterfaceType;
import static com.github.javaparser.StaticJavaParser.parseType;
import static org.drools.modelcompiler.builder.generator.DrlxParseUtil.THIS_PLACEHOLDER;
import static org.drools.modelcompiler.builder.generator.DslMethodNames.ALPHA_INDEXED_BY_CALL;
import static org.drools.modelcompiler.builder.generator.DslMethodNames.BETA_INDEXED_BY_CALL;

public class PatternExpressionBuilder extends AbstractExpressionBuilder {

    public static final String EXPR_CALL = "expr";
    public static final String REACT_ON_CALL = "D.reactOn";
    public static final String BIND_CALL = "bind";
    public static final String OR_CALL = "or";
    public static final String AND_CALL = "and";
    public static final String END_OR_CALL = "endOr";
    public static final String END_AND_CALL = "endAnd";

    public PatternExpressionBuilder(RuleContext context) {
        super(context);
    }

    @Override
    public void processExpression(SingleDrlxParseSuccess drlxParseResult) {
        if (drlxParseResult.hasUnificationVariable()) {
            Expression dslExpr = buildUnificationExpression(drlxParseResult);
            context.addExpression(dslExpr);
        } else if (drlxParseResult.isValidExpression()) {
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
            MethodCallExpr exprDSL = new MethodCallExpr(null, multi.getOperator() == BinaryExpr.Operator.OR ? OR_CALL : AND_CALL);
            for (DrlxParseSuccess child : multi.getResults()) {
                MethodCallExpr childExpr = buildExpressionWithIndexing(child);
                childExpr.setScope( exprDSL );
                exprDSL = childExpr;
            }
            return new MethodCallExpr(exprDSL, multi.getOperator() == BinaryExpr.Operator.OR ? END_OR_CALL : END_AND_CALL);
        }
        return buildSingleExpressionWithIndexing((SingleDrlxParseSuccess ) drlxParseResult);
    }

    private MethodCallExpr buildSingleExpressionWithIndexing(SingleDrlxParseSuccess drlxParseResult) {
        String exprId = drlxParseResult.getExprId(context.getPackageModel().getExprIdGenerator());
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

        final List<String> usedDeclarationsWithUnification = new ArrayList<>();
        usedDeclarationsWithUnification.addAll(drlxParseResult.getUsedDeclarations());

        usedDeclarationsWithUnification.stream()
                .filter( s -> !(drlxParseResult.isSkipThisAsParam() && s.equals( drlxParseResult.getPatternBinding() ) ) )
                .map(context::getVarExpr)
                .forEach(exprDSL::addArgument);

        if (drlxParseResult.getRightLiteral() != null) {
            exprDSL.addArgument( "" + drlxParseResult.getRightLiteral() );
        }

        exprDSL.addArgument(buildConstraintExpression(drlxParseResult, drlxParseResult.getExpr()));
        return exprDSL;
    }

    private Optional<MethodCallExpr> buildReactOn(SingleDrlxParseSuccess drlxParseResult) {
        if (shouldBuildReactOn(drlxParseResult)) {
            MethodCallExpr reactOnDSL = new MethodCallExpr(null, REACT_ON_CALL);
            drlxParseResult.getReactOnProperties().stream()
                    .map(StringLiteralExpr::new)
                    .forEach(reactOnDSL::addArgument);
            return of(reactOnDSL);
        }
        return Optional.empty();
    }

    @Override
    public MethodCallExpr buildBinding(SingleDrlxParseSuccess drlxParseResult) {
        MethodCallExpr bindDSL = new MethodCallExpr(null, BIND_CALL);
        String boundVar = drlxParseResult.hasUnificationVariable() ?
                drlxParseResult.getUnificationVariable() :
                drlxParseResult.getExprBinding();
        bindDSL.addArgument(context.getVarExpr(boundVar));
        final Expression constraintExpression = getConstraintExpression(drlxParseResult);
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

            LambdaExpr indexedByLeftOperandExtractor = new LambdaExpr();
            indexedByLeftOperandExtractor.setEnclosingParameters(true);
            boolean leftContainsThis = left.getExpression().toString().contains(THIS_PLACEHOLDER);
            TypedExpression typedExpression = leftContainsThis ? left : right;
            indexedByLeftOperandExtractor.addParameter(new Parameter(drlxParseResult.getPatternJPType(), THIS_PLACEHOLDER));
            indexedByLeftOperandExtractor.setBody(new ExpressionStmt(typedExpression.getExpression()));

            MethodCallExpr indexedByDSL = new MethodCallExpr(null, drlxParseResult.isBetaNode() ? BETA_INDEXED_BY_CALL : ALPHA_INDEXED_BY_CALL);
            indexedByDSL.addArgument(new ClassExpr(parseType(getIndexType(left, right ).getCanonicalName())));
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

        boolean isBeta = drlxParseResult.isBetaNode();
        if (!isBeta && !(right.getExpression() instanceof LiteralExpr)) {
            return Optional.empty();
        }

        FieldAccessExpr indexedBy_constraintType = new FieldAccessExpr(new NameExpr(org.drools.model.Index.ConstraintType.class.getCanonicalName()), drlxParseResult.getDecodeConstraintType().toString()); // not 100% accurate as the type in "nameExpr" is actually parsed if it was JavaParsers as a big chain of FieldAccessExpr
        LambdaExpr indexedBy_leftOperandExtractor = new LambdaExpr();
        indexedBy_leftOperandExtractor.setEnclosingParameters(true);
        indexedBy_leftOperandExtractor.addParameter(new Parameter(parseClassOrInterfaceType(drlxParseResult.getPatternType().getCanonicalName()), THIS_PLACEHOLDER));
        boolean leftContainsThis = left.getExpression().toString().contains(THIS_PLACEHOLDER);
        indexedBy_leftOperandExtractor.setBody(new ExpressionStmt(leftContainsThis ? left.getExpression() : right.getExpression()));

        MethodCallExpr indexedByDSL = new MethodCallExpr(null, isBeta ? BETA_INDEXED_BY_CALL : ALPHA_INDEXED_BY_CALL);
        indexedByDSL.addArgument(new ClassExpr(parseType(getIndexType(left, right ).getCanonicalName())));
        indexedByDSL.addArgument( indexedBy_constraintType );
        indexedByDSL.addArgument( getIndexIdArgument( drlxParseResult, left ) );
        indexedByDSL.addArgument(indexedBy_leftOperandExtractor );

        Collection<String> usedDeclarations = drlxParseResult.getUsedDeclarations();
        java.lang.reflect.Type leftType = left.getType();
        if ( isAlphaIndex( usedDeclarations )) {
            indexedByDSL.addArgument( narrowExpressionToType(right, left.getType()));
        } else if (usedDeclarations.size() == 1) {
            addIndexedByDeclaration(left, right, leftContainsThis, indexedByDSL, usedDeclarations, leftType, drlxParseResult);
        }
        return Optional.of(indexedByDSL);
    }
}
