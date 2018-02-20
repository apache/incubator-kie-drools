package org.drools.modelcompiler.builder.generator.expression;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

import org.drools.core.util.index.IndexUtil;
import org.drools.javaparser.JavaParser;
import org.drools.javaparser.ast.body.Parameter;
import org.drools.javaparser.ast.expr.ClassExpr;
import org.drools.javaparser.ast.expr.Expression;
import org.drools.javaparser.ast.expr.FieldAccessExpr;
import org.drools.javaparser.ast.expr.LambdaExpr;
import org.drools.javaparser.ast.expr.MethodCallExpr;
import org.drools.javaparser.ast.expr.NameExpr;
import org.drools.javaparser.ast.expr.StringLiteralExpr;
import org.drools.javaparser.ast.stmt.ExpressionStmt;
import org.drools.javaparser.ast.type.UnknownType;
import org.drools.modelcompiler.builder.generator.QueryGenerator;
import org.drools.modelcompiler.builder.generator.RuleContext;
import org.drools.modelcompiler.builder.generator.TypedExpression;
import org.drools.modelcompiler.builder.generator.drlxparse.DrlxParseSuccess;

import static java.util.Optional.of;

import static org.drools.modelcompiler.builder.generator.DrlxParseUtil.toVar;

public class PatternExpressionBuilder extends AbstractExpressionBuilder {

    public static final String ALPHA_INDEXED_BY_CALL = "alphaIndexedBy";
    public static final String BETA_INDEXED_BY_CALL = "betaIndexedBy";

    public PatternExpressionBuilder(RuleContext context) {
        super(context);
    }

    @Override
    public Expression buildExpressionWithIndexing(DrlxParseSuccess drlxParseResult) {
        String exprId = drlxParseResult.getExprId();
        MethodCallExpr exprDSL = new MethodCallExpr(null, EXPR_CALL);
        if (exprId != null && !"".equals(exprId)) {
            exprDSL.addArgument(new StringLiteralExpr(exprId));
        }

        exprDSL = buildExpression(drlxParseResult, exprDSL);
        MethodCallExpr finalExprDSL = exprDSL;
        Optional<MethodCallExpr> indexedByExpr = buildIndexedBy(drlxParseResult);
        indexedByExpr.ifPresent(finalExprDSL::addArgument);
        final Optional<MethodCallExpr> reactOnDSL = buildReactOn(drlxParseResult);
        reactOnDSL.ifPresent(finalExprDSL::addArgument);
        return exprDSL;
    }

    private MethodCallExpr buildExpression(DrlxParseSuccess drlxParseResult, MethodCallExpr exprDSL) {
        final List<String> usedDeclarationsWithUnification = new ArrayList<>();
        if (!drlxParseResult.isPatternBindingUnification()) {
            if (drlxParseResult.getPatternBinding() != null && !drlxParseResult.getUsedDeclarations().contains(drlxParseResult.getPatternBinding())) {
//                exprDSL.addArgument(new NameExpr(toVar(drlxParseResult.getPatternBinding())));
            }
        } else {
            usedDeclarationsWithUnification.add(drlxParseResult.getPatternBinding());
        }
        usedDeclarationsWithUnification.addAll(drlxParseResult.getUsedDeclarations());
        usedDeclarationsWithUnification.stream()
                .map(x -> QueryGenerator.substituteBindingWithQueryParameter(context, x))
                .forEach(exprDSL::addArgument);
        exprDSL.addArgument(buildConstraintExpression(drlxParseResult, drlxParseResult.getExpr()));
        return exprDSL;
    }

    private Optional<MethodCallExpr> buildReactOn(DrlxParseSuccess drlxParseResult) {
        if (!drlxParseResult.getReactOnProperties().isEmpty()) {
            MethodCallExpr reactOnDSL = new MethodCallExpr(null, "reactOn");
            drlxParseResult.getReactOnProperties().stream()
                    .map(StringLiteralExpr::new)
                    .forEach(reactOnDSL::addArgument);
            return of(reactOnDSL);
        }
        return Optional.empty();
    }

    @Override
    public MethodCallExpr buildBinding(DrlxParseSuccess drlxParseResult) {
        MethodCallExpr bindDSL = new MethodCallExpr(null, BIND_CALL);
        if (drlxParseResult.hasUnificationVariable()) {
            bindDSL.addArgument(new NameExpr(toVar(drlxParseResult.getUnificationVariable())));
        } else {
            bindDSL.addArgument(new NameExpr(toVar(drlxParseResult.getExprBinding())));
        }
//        bindDSL.addArgument( new NameExpr(toVar(drlxParseResult.getPatternBinding())) );
        final Expression constraintExpression = buildConstraintExpression(drlxParseResult, org.drools.modelcompiler.builder.generator.DrlxParseUtil.findLeftLeafOfMethodCall(drlxParseResult.getLeft().getExpression()));
        bindDSL.addArgument(constraintExpression);
        final Optional<MethodCallExpr> methodCallExpr = buildReactOn(drlxParseResult);
        methodCallExpr.ifPresent(bindDSL::addArgument);
        return bindDSL;
    }

    private Expression buildConstraintExpression(DrlxParseSuccess drlxParseResult, Expression expr) {
        return drlxParseResult.isStatic() ? expr : generateLambdaWithoutParameters(drlxParseResult.getUsedDeclarations(), expr, drlxParseResult.isSkipThisAsParam());
    }

    private Optional<MethodCallExpr> buildIndexedBy(DrlxParseSuccess drlxParseResult) {
        if ( !hasIndex( drlxParseResult ) ) {
            return Optional.empty();
        }

        IndexUtil.ConstraintType decodeConstraintType = drlxParseResult.getDecodeConstraintType();
        TypedExpression left = drlxParseResult.getLeft();
        TypedExpression right = drlxParseResult.getRight();

        Class<?> indexType = Stream.of(left, right).map(TypedExpression::getType)
                .filter(Objects::nonNull)
                .findFirst().get();

        ClassExpr indexedBy_indexedClass = new ClassExpr(JavaParser.parseType(indexType.getCanonicalName()));
        FieldAccessExpr indexedBy_constraintType = new FieldAccessExpr(new NameExpr("org.drools.model.Index.ConstraintType"), decodeConstraintType.toString()); // not 100% accurate as the type in "nameExpr" is actually parsed if it was JavaParsers as a big chain of FieldAccessExpr
        LambdaExpr indexedBy_leftOperandExtractor = new LambdaExpr();
        indexedBy_leftOperandExtractor.addParameter(new Parameter(new UnknownType(), "_this"));
        boolean leftContainsThis = left.getExpression().toString().contains("_this");
        indexedBy_leftOperandExtractor.setBody(new ExpressionStmt(leftContainsThis ? left.getExpression() : right.getExpression()));

        MethodCallExpr indexedByDSL = new MethodCallExpr(null, drlxParseResult.isBetaNode() ? BETA_INDEXED_BY_CALL : ALPHA_INDEXED_BY_CALL);
        indexedByDSL.addArgument(indexedBy_indexedClass);
        indexedByDSL.addArgument(indexedBy_constraintType);
        indexedByDSL.addArgument("" + indexIdGenerator.getFieldId(drlxParseResult.getPatternType(), left.getFieldName()));
        indexedByDSL.addArgument(indexedBy_leftOperandExtractor);

        Collection<String> usedDeclarations = drlxParseResult.getUsedDeclarations();
        if ( isAlphaIndex( usedDeclarations )) {
            indexedByDSL.addArgument(right.getExpression());
        } else if (usedDeclarations.size() == 1) {
            LambdaExpr indexedBy_rightOperandExtractor = new LambdaExpr();
            indexedBy_rightOperandExtractor.addParameter(new Parameter(new UnknownType(), usedDeclarations.iterator().next()));
            indexedBy_rightOperandExtractor.setBody(new ExpressionStmt(!leftContainsThis ? left.getExpression() : right.getExpression()));
            indexedByDSL.addArgument(indexedBy_rightOperandExtractor);
        }
        return Optional.of(indexedByDSL);
    }

    public Expression generateLambdaWithoutParameters(Collection<String> usedDeclarations, Expression expr, boolean skipFirstParamAsThis) {
        if (skipFirstParamAsThis && usedDeclarations.isEmpty()) {
            return expr;
        }
        LambdaExpr lambdaExpr = new LambdaExpr();
        lambdaExpr.setEnclosingParameters(true);
        if (!skipFirstParamAsThis) {
            lambdaExpr.addParameter(new Parameter(new UnknownType(), "_this"));
        }
        usedDeclarations.stream().map(s -> new Parameter(new UnknownType(), s)).forEach(lambdaExpr::addParameter);
        lambdaExpr.setBody(new ExpressionStmt(expr));
        return lambdaExpr;
    }
}
