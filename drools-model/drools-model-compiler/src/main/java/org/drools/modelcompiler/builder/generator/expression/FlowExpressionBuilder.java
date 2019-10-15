package org.drools.modelcompiler.builder.generator.expression;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

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
import com.github.javaparser.ast.type.UnknownType;
import org.drools.modelcompiler.builder.generator.DrlxParseUtil;
import org.drools.modelcompiler.builder.generator.RuleContext;
import org.drools.modelcompiler.builder.generator.TypedExpression;
import org.drools.modelcompiler.builder.generator.drlxparse.DrlxParseSuccess;
import org.drools.modelcompiler.builder.generator.drlxparse.MultipleDrlxParseSuccess;
import org.drools.modelcompiler.builder.generator.drlxparse.SingleDrlxParseSuccess;

import static com.github.javaparser.StaticJavaParser.parseType;
import static org.drools.modelcompiler.builder.generator.DrlxParseUtil.THIS_PLACEHOLDER;
import static org.drools.modelcompiler.builder.generator.DrlxParseUtil.generateLambdaWithoutParameters;
import static org.drools.modelcompiler.builder.generator.DslMethodNames.BIND_AS_CALL;
import static org.drools.modelcompiler.builder.generator.DslMethodNames.INDEXED_BY_CALL;
import static org.drools.modelcompiler.builder.generator.DslMethodNames.WATCH_CALL;

public class FlowExpressionBuilder extends AbstractExpressionBuilder {

    public static final String EXPR_CALL = "D.expr";
    public static final String REACT_ON_CALL = "reactOn";
    public static final String BIND_CALL = "D.bind";
    public static final String OR_CALL = "D.or";
    public static final String AND_CALL = "D.and";

    public FlowExpressionBuilder(RuleContext context) {
        super(context);
    }

    @Override
    public void processExpression(SingleDrlxParseSuccess drlxParseResult)  {
        if (drlxParseResult.hasUnificationVariable()) {
            Expression dslExpr = buildUnificationExpression(drlxParseResult);
            context.addExpression(dslExpr);
        } else if ( drlxParseResult.isValidExpression() ) {
            Expression dslExpr = buildExpressionWithIndexing(drlxParseResult);
            context.addExpression(dslExpr);
        }

        if(DrlxParseUtil.isThisExpression(drlxParseResult.getExpr())) {
            Expression inputExpr = createInputExpression(drlxParseResult.getExprBinding());
            context.addExpression(inputExpr);
        } else if (drlxParseResult.getExprBinding() != null) {
            Expression dslExpr = buildBinding(drlxParseResult);
            context.addExpression(dslExpr);
        }
    }

    @Override
    public MethodCallExpr buildExpressionWithIndexing(DrlxParseSuccess drlxParseResult) {
        if (drlxParseResult instanceof MultipleDrlxParseSuccess ) {
            MultipleDrlxParseSuccess multi = ( MultipleDrlxParseSuccess ) drlxParseResult;
            MethodCallExpr exprDSL = new MethodCallExpr(null, multi.getOperator() == BinaryExpr.Operator.OR ? OR_CALL : AND_CALL);
            for (DrlxParseSuccess child : multi.getResults()) {
                exprDSL.addArgument( buildExpressionWithIndexing(child) );
            }
            return exprDSL;
        }
        return buildSingleExpressionWithIndexing((SingleDrlxParseSuccess ) drlxParseResult);
    }

    private MethodCallExpr buildSingleExpressionWithIndexing(SingleDrlxParseSuccess drlxParseResult) {
        String exprId = drlxParseResult.getExprId();
        MethodCallExpr exprDSL = new MethodCallExpr(null, EXPR_CALL);
        if (exprId != null && !"".equals(exprId)) {
            exprDSL.addArgument( new StringLiteralExpr(exprId) );
        }

        exprDSL = buildExpression(drlxParseResult, exprDSL );
        exprDSL = buildIndexedBy(drlxParseResult, exprDSL);
        exprDSL = buildReactOn( drlxParseResult, exprDSL );
        return exprDSL;
    }

    private MethodCallExpr buildExpression(SingleDrlxParseSuccess drlxParseResult, MethodCallExpr exprDSL ) {
        final List<String> usedDeclarationsWithUnification = new ArrayList<>();
        if( drlxParseResult.isPatternBindingUnification() ) {
            usedDeclarationsWithUnification.add(drlxParseResult.getPatternBinding());
        } else {
            if (drlxParseResult.getPatternBinding() != null) {
                exprDSL.addArgument(context.getVarExpr(drlxParseResult.getPatternBinding()));
            }
        }

        if (drlxParseResult.isTemporal() && drlxParseResult.getLeft() != null && !(drlxParseResult.getLeft().getExpression() instanceof NameExpr)) {
            exprDSL.addArgument( generateLambdaWithoutParameters(drlxParseResult.getLeft().getExpression()) );
        }

        usedDeclarationsWithUnification.addAll(drlxParseResult.getUsedDeclarations());
        usedDeclarationsWithUnification.stream()
                .filter( s -> !(drlxParseResult.isSkipThisAsParam() && s.equals( drlxParseResult.getPatternBinding() ) ) )
                .map(context::getVarExpr)
                .forEach(exprDSL::addArgument);

        if (drlxParseResult.getRightLiteral() != null) {
            exprDSL.addArgument( "" + drlxParseResult.getRightLiteral() );
        } else if (drlxParseResult.isTemporal() && drlxParseResult.getRight() != null && !(drlxParseResult.getRight().getExpression() instanceof NameExpr)) {
            exprDSL.addArgument( generateLambdaWithoutParameters(drlxParseResult.getRight().getExpression()) );
        }

        exprDSL.addArgument(buildConstraintExpression( drlxParseResult, drlxParseResult.getExpr() ));
        return exprDSL;
    }

    @Override
    public MethodCallExpr buildBinding(SingleDrlxParseSuccess drlxParseResult) {
        MethodCallExpr bindDSL = new MethodCallExpr(null, BIND_CALL);
        if(drlxParseResult.hasUnificationVariable()) {
            bindDSL.addArgument(context.getVarExpr(drlxParseResult.getUnificationVariable()));
        } else {
            bindDSL.addArgument(context.getVarExpr(drlxParseResult.getExprBinding()));
        }
        final Expression constraintExpression = getConstraintExpression(drlxParseResult);
        MethodCallExpr bindAsDSL = new MethodCallExpr(bindDSL, BIND_AS_CALL);
        bindAsDSL.addArgument(context.getVarExpr(drlxParseResult.getPatternBinding()));
        drlxParseResult.getUsedDeclarationsOnLeft().forEach(d -> bindAsDSL.addArgument(context.getVar(d)));
        bindAsDSL.addArgument(constraintExpression);
        return buildReactOn(drlxParseResult, bindAsDSL );
    }

    private MethodCallExpr buildReactOn(SingleDrlxParseSuccess drlxParseResult, MethodCallExpr exprDSL ) {
        if (shouldBuildReactOn(drlxParseResult)) {
            exprDSL = new MethodCallExpr(exprDSL, REACT_ON_CALL);
            drlxParseResult.getReactOnProperties().stream()
                    .map( StringLiteralExpr::new )
                    .forEach( exprDSL::addArgument );

        }

        if ( !drlxParseResult.getWatchedProperties().isEmpty() ) {
            exprDSL = new MethodCallExpr(exprDSL, WATCH_CALL);
            drlxParseResult.getWatchedProperties().stream()
                    .map( StringLiteralExpr::new )
                    .forEach( exprDSL::addArgument );
        }

        return exprDSL;
    }

    private MethodCallExpr buildIndexedBy(SingleDrlxParseSuccess drlxParseResult, MethodCallExpr exprDSL) {
        if (drlxParseResult.isUnification()) {
            TypedExpression left = drlxParseResult.getLeft();
            TypedExpression right = drlxParseResult.getRight();

            LambdaExpr indexedBy_leftOperandExtractor = new LambdaExpr();
            indexedBy_leftOperandExtractor.addParameter(new Parameter(new UnknownType(), THIS_PLACEHOLDER));
            boolean leftContainsThis = left.getExpression().toString().contains(THIS_PLACEHOLDER);
            indexedBy_leftOperandExtractor.setBody(new ExpressionStmt(leftContainsThis ? left.getExpression() : right.getExpression()));

            MethodCallExpr indexedByDSL = new MethodCallExpr(exprDSL, INDEXED_BY_CALL);
            indexedByDSL.addArgument(new ClassExpr(parseType(getIndexType(left, right ).getCanonicalName())));
            indexedByDSL.addArgument(org.drools.model.Index.ConstraintType.class.getCanonicalName() + ".EQUAL");
            indexedByDSL.addArgument("-1");
            indexedByDSL.addArgument(indexedBy_leftOperandExtractor);
            indexedByDSL.addArgument("" + null);
            return indexedByDSL;
        }

        if ( !hasIndex( drlxParseResult ) ) {
            return exprDSL;
        }

        TypedExpression left = drlxParseResult.getLeft();
        TypedExpression right = drlxParseResult.getRight();

        if (!drlxParseResult.isBetaNode() && !(right.getExpression() instanceof LiteralExpr)) {
            return exprDSL;
        }

        FieldAccessExpr indexedBy_constraintType = new FieldAccessExpr(new NameExpr("org.drools.model.Index.ConstraintType" ), drlxParseResult.getDecodeConstraintType().toString()); // not 100% accurate as the type in "nameExpr" is actually parsed if it was JavaParsers as a big chain of FieldAccessExpr
        LambdaExpr indexedBy_leftOperandExtractor = new LambdaExpr();
        indexedBy_leftOperandExtractor.addParameter(new Parameter(new UnknownType(), THIS_PLACEHOLDER));
        boolean leftContainsThis = left.getExpression().toString().contains(THIS_PLACEHOLDER);
        indexedBy_leftOperandExtractor.setBody(new ExpressionStmt(leftContainsThis ? left.getExpression() : right.getExpression()) );

        MethodCallExpr indexedByDSL = new MethodCallExpr(exprDSL, INDEXED_BY_CALL);
        indexedByDSL.addArgument(new ClassExpr(parseType(getIndexType(left, right ).getCanonicalName())));
        indexedByDSL.addArgument( indexedBy_constraintType );
        indexedByDSL.addArgument( getIndexIdArgument( drlxParseResult, left ) );
        indexedByDSL.addArgument( indexedBy_leftOperandExtractor );

        Collection<String> usedDeclarations = drlxParseResult.getUsedDeclarations();
        java.lang.reflect.Type leftType = left.getType();
        if ( isAlphaIndex( usedDeclarations ) ) {
            indexedByDSL.addArgument( narrowExpressionToType(right, leftType));
        } else {
            addIndexedByDeclaration(left, right, leftContainsThis, indexedByDSL, usedDeclarations, leftType);
        }

        return indexedByDSL;
    }
}
