package org.drools.modelcompiler.builder.generator.drlxparse;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.drools.core.util.index.IndexUtil;
import org.drools.javaparser.ast.NodeList;
import org.drools.javaparser.ast.body.MethodDeclaration;
import org.drools.javaparser.ast.drlx.OOPathExpr;
import org.drools.javaparser.ast.drlx.expr.DrlxExpression;
import org.drools.javaparser.ast.drlx.expr.PointFreeExpr;
import org.drools.javaparser.ast.expr.BinaryExpr;
import org.drools.javaparser.ast.expr.EnclosedExpr;
import org.drools.javaparser.ast.expr.Expression;
import org.drools.javaparser.ast.expr.FieldAccessExpr;
import org.drools.javaparser.ast.expr.IntegerLiteralExpr;
import org.drools.javaparser.ast.expr.LiteralExpr;
import org.drools.javaparser.ast.expr.LiteralStringValueExpr;
import org.drools.javaparser.ast.expr.MethodCallExpr;
import org.drools.javaparser.ast.expr.NameExpr;
import org.drools.javaparser.ast.expr.StringLiteralExpr;
import org.drools.javaparser.ast.expr.ThisExpr;
import org.drools.javaparser.ast.expr.UnaryExpr;
import org.drools.javaparser.ast.nodeTypes.NodeWithArguments;
import org.drools.javaparser.ast.nodeTypes.NodeWithOptionalScope;
import org.drools.modelcompiler.builder.PackageModel;
import org.drools.modelcompiler.builder.errors.InvalidExpressionErrorResult;
import org.drools.modelcompiler.builder.errors.ParseExpressionErrorResult;
import org.drools.modelcompiler.builder.generator.DeclarationSpec;
import org.drools.modelcompiler.builder.generator.DrlxParseUtil;
import org.drools.modelcompiler.builder.generator.RuleContext;
import org.drools.modelcompiler.builder.generator.TypedExpression;
import org.drools.modelcompiler.builder.generator.expressiontyper.ExpressionTyper;
import org.drools.modelcompiler.builder.generator.expressiontyper.ExpressionTyperContext;
import org.drools.modelcompiler.builder.generator.expressiontyper.TypedExpressionResult;

import static org.drools.javaparser.ast.expr.BinaryExpr.Operator.GREATER;
import static org.drools.javaparser.ast.expr.BinaryExpr.Operator.GREATER_EQUALS;
import static org.drools.javaparser.ast.expr.BinaryExpr.Operator.LESS;
import static org.drools.javaparser.ast.expr.BinaryExpr.Operator.LESS_EQUALS;
import static org.drools.javaparser.printer.PrintUtil.toDrlx;
import static org.drools.modelcompiler.builder.generator.DrlxParseUtil.coerceLiteralExprToType;
import static org.drools.modelcompiler.builder.generator.DrlxParseUtil.getLiteralExpressionType;
import static org.drools.modelcompiler.builder.generator.DrlxParseUtil.isPrimitiveExpression;

public class ConstraintParser {

    public static final boolean GENERATE_EXPR_ID = true;

    private RuleContext context;
    private PackageModel packageModel;

    public ConstraintParser(RuleContext context, PackageModel packageModel) {
        this.context = context;
        this.packageModel = packageModel;
    }

    public DrlxParseResult drlxParse(Class<?> patternType, String bindingId, String expression) {
        return drlxParse(patternType, bindingId, expression, false);
    }

    public DrlxParseResult drlxParse(Class<?> patternType, String bindingId, String expression, boolean isPositional) {
        DrlxExpression drlx = DrlxParseUtil.parseExpression( expression );
        DrlxParseResult drlxParseResult = getDrlxParseResult(patternType, bindingId, expression, drlx, isPositional );

        drlxParseResult.accept(result -> {
            if (drlx.getBind() != null) {
                String bindId = drlx.getBind().asString();
                context.addDeclaration( new DeclarationSpec( bindId, result.getExprType() ) );
                result.setExprBinding( bindId );
            }

        });

        return drlxParseResult;
    }

    private DrlxParseResult getDrlxParseResult(Class<?> patternType,
                                                String bindingId, String expression, DrlxExpression drlx, boolean isPositional ) {
        Expression drlxExpr = drlx.getExpr();

        while (drlxExpr instanceof EnclosedExpr) {
            drlxExpr = (( EnclosedExpr ) drlxExpr).getInner();
        }

        String exprId;
        if ( GENERATE_EXPR_ID ) {
            exprId = context.getExprId( patternType, expression );
        }

        if ( drlxExpr instanceof BinaryExpr ) {
            BinaryExpr binaryExpr = (BinaryExpr) drlxExpr;
            BinaryExpr.Operator operator = binaryExpr.getOperator();

            IndexUtil.ConstraintType decodeConstraintType = DrlxParseUtil.toConstraintType(operator );

            final ExpressionTyperContext expressionTyperContext = new ExpressionTyperContext();
            final ExpressionTyper expressionTyper = new ExpressionTyper(context, patternType, bindingId, isPositional, expressionTyperContext);

            TypedExpressionResult leftTypedExpressionResult = expressionTyper.toTypedExpression(binaryExpr.getLeft());
            Optional<TypedExpression> optLeft = leftTypedExpressionResult.getTypedExpression();
            if ( !optLeft.isPresent() ) {
                return new DrlxParseFail();
            }

            TypedExpressionResult rightExpressionResult = expressionTyper.toTypedExpression(binaryExpr.getRight());
            Optional<TypedExpression> optRight = rightExpressionResult.getTypedExpression();
            if( !optRight.isPresent()) {
                context.addCompilationError( new ParseExpressionErrorResult(drlxExpr) );
                return new DrlxParseFail();
            }

            TypedExpression left = optLeft.get();
            TypedExpression right = optRight.get();

            Expression combo;
            if ( left.isPrimitive() ) {
                if (!right.getType().isPrimitive() && !Number.class.isAssignableFrom( right.getType() ) &&
                        !Boolean.class.isAssignableFrom( right.getType() ) && !String.class.isAssignableFrom( right.getType() )) {
                    context.addCompilationError( new InvalidExpressionErrorResult("Comparison operation requires compatible types. Found " + left.getType() + " and " + right.getType()) );
                    return new DrlxParseFail();
                }
                if (right.getExpression() instanceof StringLiteralExpr) {
                    right.setExpression( new IntegerLiteralExpr( (( StringLiteralExpr ) right.getExpression()).asString() ) );
                } else if (right.getExpression() instanceof LiteralStringValueExpr ) {
                    right.setExpression( coerceLiteralExprToType( (LiteralStringValueExpr) right.getExpression(), left.getType() ) );
                }
                combo = new BinaryExpr( left.getExpression(), right.getExpression(), operator );
            } else {
                switch ( operator ) {
                    case EQUALS:
                    case NOT_EQUALS:
                        combo = getEqualityExpression( left, right, operator );
                        break;
                    default:
                        if ( left.getExpression() == null || right.getExpression() == null ) {
                            context.addCompilationError( new ParseExpressionErrorResult(drlxExpr) );
                            return new DrlxParseFail();
                        }
                        combo = handleSpecialComparisonCases( operator, left, right );
                }
            }

            for(Expression e : leftTypedExpressionResult.getPrefixExpressions()) {
                combo = new BinaryExpr(e, combo, BinaryExpr.Operator.AND );
            }


            boolean isBetaNode = false;
            if(right.getExpression() instanceof  BinaryExpr) {
                if(((BinaryExpr)right.getExpression()).getRight() instanceof MethodCallExpr) {
                    isBetaNode = true;
                }
            } else if (right.getExpression() instanceof NameExpr) {
                isBetaNode = true;
            }

            return new DrlxParseSuccess(patternType, exprId, bindingId, combo, left.getType())
                    .setDecodeConstraintType( decodeConstraintType ).setUsedDeclarations( expressionTyperContext.getUsedDeclarations() )
                    .setReactOnProperties( expressionTyperContext.getReactOnProperties() ).setLeft( left ).setRight( right ).setBetaNode(isBetaNode);
        }

        if ( drlxExpr instanceof UnaryExpr ) {
            UnaryExpr unaryExpr = (UnaryExpr) drlxExpr;

            TypedExpressionResult typedExpressionResult = new ExpressionTyper(context, patternType, bindingId, isPositional).toTypedExpression(unaryExpr);

            return typedExpressionResult.getTypedExpression().<DrlxParseResult>map(left -> {
                return new DrlxParseSuccess(patternType, exprId, bindingId, left.getExpression(), left.getType())
                        .setUsedDeclarations( typedExpressionResult.getUsedDeclarations() ).setReactOnProperties( typedExpressionResult.getReactOnProperties() ).setLeft( left );
            }).orElse(new DrlxParseFail());


        }

        if ( drlxExpr instanceof PointFreeExpr ) {
            PointFreeExpr pointFreeExpr = (PointFreeExpr) drlxExpr;

            TypedExpressionResult typedExpressionResult = new ExpressionTyper(context, patternType, bindingId, isPositional).toTypedExpression(pointFreeExpr);
            final Optional<TypedExpression> optTypedExpression =typedExpressionResult.getTypedExpression();

            return optTypedExpression.<DrlxParseResult>map(typedExpression -> {
                final Expression returnExpression = typedExpression.getExpression();
                final Class<?> returnType = typedExpression.getType();

                return new DrlxParseSuccess(patternType, exprId, bindingId, returnExpression, returnType)
                        .setUsedDeclarations(typedExpressionResult.getUsedDeclarations())
                        .setReactOnProperties(typedExpressionResult.getReactOnProperties())
                        .setLeft(typedExpression.getLeft())
                        .setStatic(typedExpression.isStatic())
                        .setValidExpression(true);
            }).orElse(new DrlxParseFail());

        }

        if (drlxExpr instanceof MethodCallExpr) {
            MethodCallExpr methodCallExpr = (MethodCallExpr) drlxExpr;

            // when the methodCallExpr will be placed in the model/DSL, any parameter being a "this" need to be implemented as _this by convention.
            List<ThisExpr> rewriteThisExprs = recurseCollectArguments(methodCallExpr).stream()
                    .filter(ThisExpr.class::isInstance)
                    .map(ThisExpr.class::cast)
                    .collect(Collectors.toList());
            for (ThisExpr t : rewriteThisExprs) {
                methodCallExpr.replace(t, new NameExpr("_this"));
            }

            Optional<MethodDeclaration> functionCall = packageModel.getFunctions().stream().filter(m -> m.getName().equals(methodCallExpr.getName())).findFirst();
            if (functionCall.isPresent()) {
                Class<?> returnType = DrlxParseUtil.getClassFromContext(context.getTypeResolver(), functionCall.get().getType().asString());
                NodeList<Expression> arguments = methodCallExpr.getArguments();
                List<String> usedDeclarations = new ArrayList<>();
                for (Expression arg : arguments) {
                    if (arg instanceof NameExpr && !arg.toString().equals("_this")) {
                        usedDeclarations.add(arg.toString());
                    } else if (arg instanceof MethodCallExpr) {
                        TypedExpressionResult typedExpressionResult = new ExpressionTyper(context, null, bindingId, isPositional)
                                .toTypedExpression(arg);
                        usedDeclarations.addAll(typedExpressionResult.getUsedDeclarations());
                    }
                }
                return new DrlxParseSuccess(patternType, exprId, bindingId, methodCallExpr, returnType).setUsedDeclarations(usedDeclarations);
            } else if (methodCallExpr.getScope().isPresent() && methodCallExpr.getScope().get() instanceof StringLiteralExpr) {
                TypedExpressionResult typedExpressionResult = new ExpressionTyper(context, String.class, bindingId, isPositional)
                        .toTypedExpression(methodCallExpr);
                Optional<TypedExpression> optConverted = typedExpressionResult.getTypedExpression();

                return optConverted.<DrlxParseResult>map(converted -> {
                    return new DrlxParseSuccess(String.class, exprId, bindingId, converted.getExpression(), converted.getType())
                            .setLeft(converted )
                            .setUsedDeclarations(typedExpressionResult.getUsedDeclarations() );
                }).orElse(new DrlxParseFail());


            } else if (patternType != null) {
                NameExpr _this = new NameExpr("_this");
                TypedExpression converted = DrlxParseUtil.toMethodCallWithClassCheck(context, methodCallExpr, bindingId, patternType, context.getTypeResolver());
                Expression withThis = DrlxParseUtil.prepend(_this, converted.getExpression());
                return new DrlxParseSuccess(patternType, exprId, bindingId, withThis, converted.getType()).setLeft(converted );
            } else {
                return new DrlxParseSuccess(patternType, exprId, bindingId, methodCallExpr, null);
            }
        }

        if (drlxExpr instanceof FieldAccessExpr) {
            FieldAccessExpr fieldCallExpr = (FieldAccessExpr) drlxExpr;

            NameExpr _this = new NameExpr("_this");
            TypedExpression converted = DrlxParseUtil.toMethodCallWithClassCheck(context, fieldCallExpr, bindingId, patternType, context.getTypeResolver());
            Expression withThis = DrlxParseUtil.prepend(_this, converted.getExpression());
            return new DrlxParseSuccess(patternType, exprId, bindingId, withThis, converted.getType()).setLeft(converted );
        }

        if (drlxExpr instanceof NameExpr) {
            NameExpr methodCallExpr = (NameExpr) drlxExpr;

            NameExpr _this = new NameExpr("_this");
            TypedExpression converted = DrlxParseUtil.toMethodCallWithClassCheck(context, methodCallExpr, bindingId, patternType, context.getTypeResolver());
            Expression withThis = DrlxParseUtil.prepend(_this, converted.getExpression());

            if (drlx.getBind() != null) {
                return new DrlxParseSuccess(patternType, exprId, bindingId, null, converted.getType() )
                        .setLeft( new TypedExpression( withThis, converted.getType() ) )
                        .addReactOnProperty( methodCallExpr.getNameAsString() );
            } else {
                return new DrlxParseSuccess(patternType, exprId, bindingId, withThis, converted.getType() )
                        .addReactOnProperty( methodCallExpr.getNameAsString() );
            }
        }

        if (drlxExpr instanceof OOPathExpr ) {
            return new DrlxParseSuccess( patternType, exprId, bindingId, drlxExpr, null );
        }

        if (drlxExpr instanceof LiteralExpr ) {
            return new DrlxParseSuccess( patternType, exprId, bindingId, drlxExpr, getLiteralExpressionType( (( LiteralExpr ) drlxExpr) ) );
        }

        throw new UnsupportedOperationException("Unknown expression: " + toDrlx(drlxExpr)); // TODO
    }

    private static Expression getEqualityExpression( TypedExpression left, TypedExpression right, BinaryExpr.Operator operator ) {
        if(isAnyOperandBigDecimal(left, right)) {
            return compareBigDecimal(operator, left, right);
        }

        if (isPrimitiveExpression(right.getExpression())) {
            if (left.getType() != String.class) {
                return new BinaryExpr( left.getExpression(), right.getExpression(), operator == BinaryExpr.Operator.EQUALS ? BinaryExpr.Operator.EQUALS : BinaryExpr.Operator.NOT_EQUALS );
            } else if ( right.getExpression() instanceof LiteralExpr ) {
                right.setExpression( new StringLiteralExpr( right.getExpression().toString() ) );
            }
        }

        if (left.getType() == String.class && right.getType() == Object.class) {
            right.setExpression( new MethodCallExpr( right.getExpression(), "toString" ) );
        }

        MethodCallExpr methodCallExpr = new MethodCallExpr( null, "org.drools.modelcompiler.util.EvaluationUtil.areNullSafeEquals" );
        methodCallExpr.addArgument( left.getExpression() );
        methodCallExpr.addArgument( right.getExpression() ); // don't create NodeList with static method because missing "parent for child" would null and NPE
        return operator == BinaryExpr.Operator.EQUALS ? methodCallExpr : new UnaryExpr(methodCallExpr, UnaryExpr.Operator.LOGICAL_COMPLEMENT );
    }

    private static Expression handleSpecialComparisonCases(BinaryExpr.Operator operator, TypedExpression left, TypedExpression right ) {
        if ( left.getType() == String.class && right.getType() == String.class && isComparisonOperator( operator ) ) {
            MethodCallExpr methodCallExpr = new MethodCallExpr( null, "org.drools.modelcompiler.util.EvaluationUtil.compareStringsAsNumbers" );
            methodCallExpr.addArgument( left.getExpression() );
            methodCallExpr.addArgument( right.getExpression() );
            methodCallExpr.addArgument( new StringLiteralExpr( operator.asString() ) );
            return methodCallExpr;
        }

        if (isAnyOperandBigDecimal(left, right) && (isComparisonOperator(operator))) {
            return compareBigDecimal(operator, left, right);
        }

        return new BinaryExpr( left.getExpression(), right.getExpression(), operator );
    }

    private static boolean isAnyOperandBigDecimal(TypedExpression left, TypedExpression right) {
        return left.getType() == BigDecimal.class || right.getType() == BigDecimal.class;
    }

    public static Expression compareBigDecimal(BinaryExpr.Operator operator, TypedExpression left, TypedExpression right) {
        left.setExpression( convertExpressionToBigDecimal(left) );
        right.setExpression( convertExpressionToBigDecimal(right) );
        final MethodCallExpr methodCallExpr = new MethodCallExpr(left.getExpression(), "compareTo");
        methodCallExpr.addArgument(right.getExpression());
        return new BinaryExpr(methodCallExpr, new IntegerLiteralExpr(0), operator);
    }

    private static Expression convertExpressionToBigDecimal(TypedExpression left) {
        final Expression ret;
        if(left.getType() != BigDecimal.class) {
            ret = new MethodCallExpr(new NameExpr(BigDecimal.class.getCanonicalName()), "valueOf")
                    .addArgument(left.getExpression());
        } else {
            ret = left.getExpression();
        }
        return ret;
    }

    private static boolean isComparisonOperator( BinaryExpr.Operator op ) {
        return op == LESS || op == GREATER || op == LESS_EQUALS || op == GREATER_EQUALS;

    }

    private static List<Expression> recurseCollectArguments(NodeWithArguments<?> methodCallExpr) {
        List<Expression> res = new ArrayList<>( methodCallExpr.getArguments() );
        if ( methodCallExpr instanceof NodeWithOptionalScope ) {
            NodeWithOptionalScope<?> nodeWithOptionalScope = (NodeWithOptionalScope) methodCallExpr;
            if ( nodeWithOptionalScope.getScope().isPresent() ) {
                Object scope = nodeWithOptionalScope.getScope().get();
                if (scope instanceof NodeWithArguments) {
                    res.addAll(recurseCollectArguments((NodeWithArguments<?>) scope));
                }
            }
        }
        return res;
    }
}
