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
import org.drools.javaparser.ast.expr.LiteralExpr;
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
import static org.drools.modelcompiler.builder.generator.DslMethodNames.ALPHA_INDEXED_BY_CALL;
import static org.drools.modelcompiler.builder.generator.DslMethodNames.BETA_INDEXED_BY_CALL;

public class PatternExpressionBuilder extends AbstractExpressionBuilder {

    public static final String EXPR_CALL = "expr";
    public static final String REACT_ON_CALL = "D.reactOn";
    public static final String BIND_CALL = "bind";

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
        buildIndexedBy(drlxParseResult).ifPresent(finalExprDSL::addArgument);
        buildReactOn(drlxParseResult).ifPresent(finalExprDSL::addArgument);
        return exprDSL;
    }

    private MethodCallExpr buildExpression(DrlxParseSuccess drlxParseResult, MethodCallExpr exprDSL) {
        final List<String> usedDeclarationsWithUnification = new ArrayList<>();
        usedDeclarationsWithUnification.addAll(drlxParseResult.getUsedDeclarations());
        usedDeclarationsWithUnification.stream()
                .filter( s -> !(drlxParseResult.isSkipThisAsParam() && s.equals( drlxParseResult.getPatternBinding() ) ) )
                .map(x -> QueryGenerator.substituteBindingWithQueryParameter(context, x))
                .forEach(exprDSL::addArgument);
        exprDSL.addArgument(buildConstraintExpression(drlxParseResult, drlxParseResult.getExpr()));
        return exprDSL;
    }

    private Optional<MethodCallExpr> buildReactOn(DrlxParseSuccess drlxParseResult) {
        if (!drlxParseResult.getReactOnProperties().isEmpty() && context.isPropertyReactive( drlxParseResult.getPatternType() )) {
            MethodCallExpr reactOnDSL = new MethodCallExpr(null, REACT_ON_CALL);
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
        final Expression constraintExpression = getConstraintExpression(drlxParseResult);
        bindDSL.addArgument(constraintExpression);
        final Optional<MethodCallExpr> methodCallExpr = buildReactOn(drlxParseResult);
        methodCallExpr.ifPresent(bindDSL::addArgument);
        return bindDSL;
    }

    private Optional<MethodCallExpr> buildIndexedBy(DrlxParseSuccess drlxParseResult) {
        if ( !hasIndex( drlxParseResult ) ) {
            return Optional.empty();
        }

        IndexUtil.ConstraintType decodeConstraintType = drlxParseResult.getDecodeConstraintType();
        TypedExpression left = drlxParseResult.getLeft();
        TypedExpression right = drlxParseResult.getRight();

        boolean isBeta = drlxParseResult.isBetaNode();
        if (!isBeta && !(right.getExpression() instanceof LiteralExpr)) {
            return Optional.empty();
        }

        Class<?> indexType = Stream.of(left, right).map(TypedExpression::getType)
                .filter(Objects::nonNull)
                .findFirst().get();

        ClassExpr indexedBy_indexedClass = new ClassExpr(JavaParser.parseType(indexType.getCanonicalName()));
        FieldAccessExpr indexedBy_constraintType = new FieldAccessExpr(new NameExpr("org.drools.model.Index.ConstraintType"), decodeConstraintType.toString()); // not 100% accurate as the type in "nameExpr" is actually parsed if it was JavaParsers as a big chain of FieldAccessExpr
        LambdaExpr indexedBy_leftOperandExtractor = new LambdaExpr();
        indexedBy_leftOperandExtractor.addParameter(new Parameter(new UnknownType(), "_this"));
        boolean leftContainsThis = left.getExpression().toString().contains("_this");
        indexedBy_leftOperandExtractor.setBody(new ExpressionStmt(leftContainsThis ? left.getExpression() : right.getExpression()));

        MethodCallExpr indexedByDSL = new MethodCallExpr(null, isBeta ? BETA_INDEXED_BY_CALL : ALPHA_INDEXED_BY_CALL);
        indexedByDSL.addArgument(indexedBy_indexedClass);
        indexedByDSL.addArgument(indexedBy_constraintType);
        indexedByDSL.addArgument("" + indexIdGenerator.getFieldId(drlxParseResult.getPatternType(), left.getFieldName()));
        indexedByDSL.addArgument(indexedBy_leftOperandExtractor);

        Collection<String> usedDeclarations = drlxParseResult.getUsedDeclarations();
        final Class<?> leftType = left.getType();
        if ( isAlphaIndex( usedDeclarations )) {
            indexedByDSL.addArgument(narrowExpressionWithBigDecimal(right, left.getType()));
        } else if (usedDeclarations.size() == 1) {
            addIndexedByDeclaration(left, right, leftContainsThis, indexedByDSL, usedDeclarations, leftType);
        }
        return Optional.of(indexedByDSL);
    }
}
