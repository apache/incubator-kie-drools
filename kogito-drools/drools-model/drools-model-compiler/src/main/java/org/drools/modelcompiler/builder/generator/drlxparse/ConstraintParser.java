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
import org.drools.javaparser.ast.drlx.expr.HalfBinaryExpr;
import org.drools.javaparser.ast.drlx.expr.PointFreeExpr;
import org.drools.javaparser.ast.expr.BinaryExpr;
import org.drools.javaparser.ast.expr.CastExpr;
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
import org.drools.modelcompiler.util.ClassUtil;

import static org.drools.core.util.StringUtils.lcFirst;
import static org.drools.javaparser.ast.expr.BinaryExpr.Operator.GREATER;
import static org.drools.javaparser.ast.expr.BinaryExpr.Operator.GREATER_EQUALS;
import static org.drools.javaparser.ast.expr.BinaryExpr.Operator.LESS;
import static org.drools.javaparser.ast.expr.BinaryExpr.Operator.LESS_EQUALS;
import static org.drools.modelcompiler.builder.generator.DrlxParseUtil.coerceLiteralExprToType;
import static org.drools.modelcompiler.builder.generator.DrlxParseUtil.getLiteralExpressionType;
import static org.drools.modelcompiler.builder.generator.DrlxParseUtil.isPrimitiveExpression;
import static org.drools.modelcompiler.builder.generator.expression.AbstractExpressionBuilder.getExpressionSymbol;
import static org.drools.modelcompiler.util.ClassUtil.toNonPrimitiveType;
import static org.drools.modelcompiler.util.JavaParserUtil.toJavaParserType;

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
        DrlxParseResult drlxParseResult = getDrlxParseResult(patternType, bindingId, expression, drlx.getExpr(), drlx.getBind() != null, isPositional );

        drlxParseResult.accept(result -> {
            if (drlx.getBind() != null) {
                String bindId = drlx.getBind().asString();
                context.addDeclaration( new DeclarationSpec( bindId, result.getExprType() ) );
                result.setExprBinding( bindId );
            }

        });

        return drlxParseResult;
    }

    private DrlxParseResult getDrlxParseResult(Class<?> patternType, String bindingId, String expression, Expression drlxExpr, boolean hasBind, boolean isPositional ) {
        boolean isEnclosed = false;
        while (drlxExpr instanceof EnclosedExpr) {
            drlxExpr = (( EnclosedExpr ) drlxExpr).getInner();
            isEnclosed = true;
        }

        if (drlxExpr instanceof MethodCallExpr && !(( MethodCallExpr ) drlxExpr).getScope().isPresent() && (( MethodCallExpr ) drlxExpr).getNameAsString().equals( "eval" )) {
            drlxExpr = (( MethodCallExpr ) drlxExpr).getArgument( 0 );
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
                return new DrlxParseFail( new InvalidExpressionErrorResult( "Unable to parse left part of expression: " + expression ) );
            }

            List<String> usedDeclarationsOnLeft = hasBind ? new ArrayList<>( expressionTyperContext.getUsedDeclarations() ) : null;

            TypedExpressionResult rightExpressionResult = expressionTyper.toTypedExpression(binaryExpr.getRight());
            Optional<TypedExpression> optRight = rightExpressionResult.getTypedExpression();
            if( !optRight.isPresent()) {
                return new DrlxParseFail( new ParseExpressionErrorResult(drlxExpr) );
            }

            TypedExpression left = optLeft.get();
            TypedExpression right = optRight.get();

            Expression combo;
            if ( left.isPrimitive() ) {
                if (!right.getType().isPrimitive() && !Number.class.isAssignableFrom( right.getType() ) &&
                        !Boolean.class.isAssignableFrom( right.getType() ) && !String.class.isAssignableFrom( right.getType() )) {
                    return new DrlxParseFail( new InvalidExpressionErrorResult("Comparison operation requires compatible types. Found " + left.getType() + " and " + right.getType()) );
                }
                if (right.getExpression() instanceof StringLiteralExpr) {
                    right.setExpression( new IntegerLiteralExpr( (( StringLiteralExpr ) right.getExpression()).asString() ) );
                } else if (right.getExpression() instanceof LiteralStringValueExpr ) {
                    right.setExpression( coerceLiteralExprToType( (LiteralStringValueExpr) right.getExpression(), left.getType() ) );
                }
                combo = new BinaryExpr( left.getExpression(), right.getExpression(), operator );
            } else {

                coerceRightExpression( left, right );

                switch ( operator ) {
                    case EQUALS:
                    case NOT_EQUALS:
                        combo = getEqualityExpression( left, right, operator );
                        break;
                    default:
                        if ( left.getExpression() == null || right.getExpression() == null ) {
                            return new DrlxParseFail( new ParseExpressionErrorResult(drlxExpr) );
                        }
                        combo = handleSpecialComparisonCases( operator, left, right );
                }
            }

            for(Expression e : leftTypedExpressionResult.getPrefixExpressions()) {
                combo = new BinaryExpr(e, combo, BinaryExpr.Operator.AND );
            }


            boolean isBetaNode = right.getExpression() != null && context.getDeclarationById( getExpressionSymbol( right.getExpression() ) ).isPresent();
            if (isEnclosed) {
                combo = new EnclosedExpr( combo );
            }

            boolean requiresSplit = operator == BinaryExpr.Operator.AND && binaryExpr.getRight() instanceof HalfBinaryExpr && !isBetaNode;
            return new DrlxParseSuccess(patternType, exprId, bindingId, combo, left.getType()).setDecodeConstraintType( decodeConstraintType )
                    .setUsedDeclarations( expressionTyperContext.getUsedDeclarations() ).setUsedDeclarationsOnLeft( usedDeclarationsOnLeft )
                    .setReactOnProperties( expressionTyperContext.getReactOnProperties() ).setLeft( left ).setRight( right ).setBetaNode(isBetaNode).setRequiresSplit( requiresSplit );
        }

        if ( drlxExpr instanceof UnaryExpr ) {
            UnaryExpr unaryExpr = (UnaryExpr) drlxExpr;

            TypedExpressionResult typedExpressionResult = new ExpressionTyper(context, patternType, bindingId, isPositional).toTypedExpression(unaryExpr);

            return typedExpressionResult.getTypedExpression().<DrlxParseResult>map(left -> {
                return new DrlxParseSuccess(patternType, exprId, bindingId, left.getExpression(), left.getType())
                        .setUsedDeclarations( typedExpressionResult.getUsedDeclarations() ).setReactOnProperties( typedExpressionResult.getReactOnProperties() ).setLeft( left );
            }).orElse(new DrlxParseFail( new ParseExpressionErrorResult(drlxExpr) ));
        }

        if ( drlxExpr instanceof PointFreeExpr ) {
            PointFreeExpr pointFreeExpr = (PointFreeExpr) drlxExpr;

            TypedExpressionResult typedExpressionResult = new ExpressionTyper(context, patternType, bindingId, isPositional).toTypedExpression(pointFreeExpr);
            final Optional<TypedExpression> optTypedExpression = typedExpressionResult.getTypedExpression();

            return optTypedExpression.<DrlxParseResult>map(typedExpression -> {
                final Expression returnExpression = typedExpression.getExpression();
                final Class<?> returnType = typedExpression.getType();

                return new DrlxParseSuccess(patternType, exprId, bindingId, returnExpression, returnType)
                        .setUsedDeclarations(typedExpressionResult.getUsedDeclarations())
                        .setReactOnProperties(typedExpressionResult.getReactOnProperties())
                        .setLeft(typedExpression.getLeft())
                        .setStatic(typedExpression.isStatic())
                        .setValidExpression(true);
            }).orElse(new DrlxParseFail( new ParseExpressionErrorResult(drlxExpr) ));
        }

        if (drlxExpr instanceof MethodCallExpr) {
            MethodCallExpr methodCallExpr = (MethodCallExpr) drlxExpr;

            Optional<MethodDeclaration> functionCall = packageModel.getFunctions().stream().filter(m -> m.getName().equals(methodCallExpr.getName())).findFirst();
            if (functionCall.isPresent()) {
                // when the methodCallExpr will be placed in the model/DSL, any parameter being a "this" need to be implemented as _this by convention.
                List<ThisExpr> rewriteThisExprs = recurseCollectArguments(methodCallExpr).stream()
                        .filter(ThisExpr.class::isInstance)
                        .map(ThisExpr.class::cast)
                        .collect(Collectors.toList());
                for (ThisExpr t : rewriteThisExprs) {
                    methodCallExpr.replace(t, new NameExpr("_this"));
                }

                Class<?> returnType = DrlxParseUtil.getClassFromContext(context.getTypeResolver(), functionCall.get().getType().asString());
                NodeList<Expression> arguments = methodCallExpr.getArguments();
                List<String> usedDeclarations = new ArrayList<>();
                for (Expression arg : arguments) {
                    if (arg instanceof NameExpr && !arg.toString().equals("_this")) {
                        usedDeclarations.add(arg.toString());
                    } else if (arg instanceof MethodCallExpr) {
                        TypedExpressionResult typedExpressionResult = new ExpressionTyper(context, null, bindingId, isPositional).toTypedExpression(arg);
                        usedDeclarations.addAll(typedExpressionResult.getUsedDeclarations());
                    }
                }
                return new DrlxParseSuccess(patternType, exprId, bindingId, methodCallExpr, returnType).setUsedDeclarations(usedDeclarations);
            }
        }

        if (drlxExpr instanceof FieldAccessExpr) {
            FieldAccessExpr fieldCallExpr = (FieldAccessExpr) drlxExpr;

            TypedExpression converted = DrlxParseUtil.toMethodCallWithClassCheck(context, fieldCallExpr, bindingId, patternType, context.getTypeResolver());
            Expression withThis = DrlxParseUtil.prepend(new NameExpr("_this"), converted.getExpression());
            return new DrlxParseSuccess(patternType, exprId, bindingId, withThis, converted.getType()).setLeft(converted );
        }

        if (drlxExpr instanceof NameExpr) {
            NameExpr nameExpr = (NameExpr) drlxExpr;

            TypedExpression converted = DrlxParseUtil.toMethodCallWithClassCheck(context, nameExpr, bindingId, patternType, context.getTypeResolver());
            Expression withThis = DrlxParseUtil.prepend(new NameExpr("_this"), converted.getExpression());

            if (hasBind) {
                return new DrlxParseSuccess(patternType, exprId, bindingId, null, converted.getType() )
                        .setLeft( new TypedExpression( withThis, converted.getType() ) )
                        .addReactOnProperty( lcFirst(nameExpr.getNameAsString()) );
            } else {
                return new DrlxParseSuccess(patternType, exprId, bindingId, withThis, converted.getType() )
                        .addReactOnProperty( nameExpr.getNameAsString() );
            }
        }

        if (drlxExpr instanceof OOPathExpr ) {
            return new DrlxParseSuccess( patternType, exprId, bindingId, drlxExpr, null );
        }

        if (drlxExpr instanceof LiteralExpr ) {
            return new DrlxParseSuccess( patternType, exprId, bindingId, drlxExpr, getLiteralExpressionType( (( LiteralExpr ) drlxExpr) ) );
        }

        if (patternType != null) {
            ExpressionTyperContext expressionTyperContext = new ExpressionTyperContext();
            ExpressionTyper expressionTyper = new ExpressionTyper(context, patternType, bindingId, isPositional, expressionTyperContext);
            TypedExpressionResult leftTypedExpressionResult = expressionTyper.toTypedExpression(drlxExpr);
            Optional<TypedExpression> optLeft = leftTypedExpressionResult.getTypedExpression();
            if ( !optLeft.isPresent() ) {
                return new DrlxParseFail( new InvalidExpressionErrorResult( "Unable to parse left part of expression: " + expression ) );
            }
            TypedExpression left = optLeft.get();
            return new DrlxParseSuccess(patternType, exprId, bindingId, left.getExpression(), left.getType())
                    .setUsedDeclarations( expressionTyperContext.getUsedDeclarations() );
        } else {
            return new DrlxParseSuccess(patternType, exprId, bindingId, drlxExpr, null);
        }
    }

    public static void coerceRightExpression( TypedExpression left, TypedExpression right ) {
        if (right.getExpression() == null) {
            return;
        }

        if (left.getType() == ClassUtil.NullType.class || right.getType() == ClassUtil.NullType.class ||
            left.getType() == BigDecimal.class || right.getType() == BigDecimal.class ||
            left.getType() == String.class || right.getType() == String.class) {
            return;
        }

        if (!areCompatible( left.getType(), right.getType() ) ) {
            right.setExpression( new CastExpr( toJavaParserType( left.getType(), right.getType().isPrimitive() ), right.getExpression() ) );
        }
    }

    private static boolean areCompatible( Class<?> leftType, Class<?> rightType ) {
        return toNonPrimitiveType( rightType ).isAssignableFrom( toNonPrimitiveType( leftType ) );
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
        if ( isComparisonOperator( operator ) ) {
            MethodCallExpr compareMethod = null;
            if ( left.getType() == String.class && right.getType() == String.class ) {
                compareMethod = new MethodCallExpr( null, "org.drools.modelcompiler.util.EvaluationUtil.compareStringsAsNumbers" );
            } else if ( Comparable.class.isAssignableFrom( left.getType() ) && Comparable.class.isAssignableFrom( right.getType() ) ) {
                compareMethod = new MethodCallExpr( null, "org.drools.modelcompiler.util.EvaluationUtil.compare" );
            }

            if (compareMethod != null) {
                compareMethod.addArgument( left.getExpression() );
                compareMethod.addArgument( right.getExpression() );
                compareMethod.addArgument( new StringLiteralExpr( operator.asString() ) );
                return compareMethod;
            }
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
