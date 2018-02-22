package org.drools.modelcompiler.builder.generator.expression;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

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

import static org.drools.modelcompiler.builder.generator.DrlxParseUtil.generateLambdaWithoutParameters;
import static org.drools.modelcompiler.builder.generator.DrlxParseUtil.toVar;
import static org.drools.modelcompiler.builder.generator.ModelGenerator.BIND_AS_CALL;

public class FlowExpressionBuilder extends AbstractExpressionBuilder {

    public FlowExpressionBuilder(RuleContext context) {
        super(context);
    }

    public static final String INDEXED_BY_CALL = "indexedBy";

    public Expression buildExpressionWithIndexing(DrlxParseSuccess drlxParseResult) {
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

    private MethodCallExpr buildExpression(DrlxParseSuccess drlxParseResult, MethodCallExpr exprDSL ) {
        final List<String> usedDeclarationsWithUnification = new ArrayList<>();
        if( drlxParseResult.isPatternBindingUnification() ) {
            usedDeclarationsWithUnification.add(drlxParseResult.getPatternBinding());
        } else {
            if (drlxParseResult.getPatternBinding() != null && !drlxParseResult.getUsedDeclarations().contains( drlxParseResult.getPatternBinding() )) {
                exprDSL.addArgument(new NameExpr(toVar(drlxParseResult.getPatternBinding())));
            }
        }
        usedDeclarationsWithUnification.addAll(drlxParseResult.getUsedDeclarations());
        usedDeclarationsWithUnification.stream()
                .filter( s -> !(drlxParseResult.isSkipThisAsParam() && s.equals( drlxParseResult.getPatternBinding() ) ) )
                .map(x -> QueryGenerator.substituteBindingWithQueryParameter(context, x))
                .forEach(exprDSL::addArgument);
        exprDSL.addArgument(buildConstraintExpression( drlxParseResult, drlxParseResult.getExpr() ));
        return exprDSL;
    }



    public MethodCallExpr buildBinding(DrlxParseSuccess drlxParseResult ) {
        MethodCallExpr bindDSL = new MethodCallExpr(null, BIND_CALL);
        if(drlxParseResult.hasUnificationVariable()) {
            bindDSL.addArgument(new NameExpr(toVar(drlxParseResult.getUnificationVariable())));
        } else {
            bindDSL.addArgument( new NameExpr(toVar(drlxParseResult.getExprBinding())) );
        }
        MethodCallExpr bindAsDSL = new MethodCallExpr(bindDSL, BIND_AS_CALL);
        bindAsDSL.addArgument( new NameExpr(toVar(drlxParseResult.getPatternBinding())) );
        final Expression constraintExpression = buildConstraintExpression(drlxParseResult, org.drools.modelcompiler.builder.generator.DrlxParseUtil.findLeftLeafOfMethodCall(drlxParseResult.getLeft().getExpression())  );
        bindAsDSL.addArgument(constraintExpression);
        return buildReactOn( drlxParseResult, bindAsDSL );
    }

    private MethodCallExpr buildReactOn(DrlxParseSuccess drlxParseResult, MethodCallExpr exprDSL ) {
        if ( !drlxParseResult.getReactOnProperties().isEmpty() ) {
            exprDSL = new MethodCallExpr(exprDSL, "reactOn");
            drlxParseResult.getReactOnProperties().stream()
                    .map( StringLiteralExpr::new )
                    .forEach( exprDSL::addArgument );

        }

        if ( !drlxParseResult.getWatchedProperties().isEmpty() ) {
            exprDSL = new MethodCallExpr(exprDSL, "watch");
            drlxParseResult.getWatchedProperties().stream()
                    .map( StringLiteralExpr::new )
                    .forEach( exprDSL::addArgument );
        }

        return exprDSL;
    }

    private Expression buildConstraintExpression(DrlxParseSuccess drlxParseResult, Expression expr ) {
        return drlxParseResult.isStatic() ? expr : generateLambdaWithoutParameters(drlxParseResult.getUsedDeclarations(), expr, drlxParseResult.isSkipThisAsParam());
    }

    private MethodCallExpr buildIndexedBy(DrlxParseSuccess drlxParseResult, MethodCallExpr exprDSL) {
        if ( !hasIndex( drlxParseResult ) ) {
            return exprDSL;
        }

        TypedExpression left = drlxParseResult.getLeft();
        TypedExpression right = drlxParseResult.getRight();

        Class<?> indexType = Stream.of( left, right ).map( TypedExpression::getType )
                .filter(Objects::nonNull )
                .findFirst().get();

        ClassExpr indexedBy_indexedClass = new ClassExpr(JavaParser.parseType(indexType.getCanonicalName() ) );
        FieldAccessExpr indexedBy_constraintType = new FieldAccessExpr(new NameExpr("org.drools.model.Index.ConstraintType" ), drlxParseResult.getDecodeConstraintType().toString()); // not 100% accurate as the type in "nameExpr" is actually parsed if it was JavaParsers as a big chain of FieldAccessExpr
        LambdaExpr indexedBy_leftOperandExtractor = new LambdaExpr();
        indexedBy_leftOperandExtractor.addParameter(new Parameter(new UnknownType(), "_this"));
        boolean leftContainsThis = left.getExpression().toString().contains("_this");
        indexedBy_leftOperandExtractor.setBody(new ExpressionStmt(leftContainsThis ? left.getExpression() : right.getExpression()) );

        MethodCallExpr indexedByDSL = new MethodCallExpr(exprDSL, INDEXED_BY_CALL);
        indexedByDSL.addArgument( indexedBy_indexedClass );
        indexedByDSL.addArgument( indexedBy_constraintType );
        indexedByDSL.addArgument( "" + indexIdGenerator.getFieldId(drlxParseResult.getPatternType(), left.getFieldName() ) );
        indexedByDSL.addArgument( indexedBy_leftOperandExtractor );

        Collection<String> usedDeclarations = drlxParseResult.getUsedDeclarations();
        if ( isAlphaIndex( usedDeclarations ) ) {
            indexedByDSL.addArgument( right.getExpression() );
        } else {
            LambdaExpr indexedBy_rightOperandExtractor = new LambdaExpr();
            indexedBy_rightOperandExtractor.addParameter(new Parameter(new UnknownType(), usedDeclarations.iterator().next()));
            indexedBy_rightOperandExtractor.setBody(new ExpressionStmt(!leftContainsThis ? left.getExpression() : right.getExpression()));
            indexedByDSL.addArgument(indexedBy_rightOperandExtractor);
        }

        return indexedByDSL;
    }
}
