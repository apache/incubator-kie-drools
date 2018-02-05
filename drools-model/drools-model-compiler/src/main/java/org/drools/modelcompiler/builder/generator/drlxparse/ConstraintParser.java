package org.drools.modelcompiler.builder.generator.drlxparse;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
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
import org.drools.javaparser.ast.expr.MethodCallExpr;
import org.drools.javaparser.ast.expr.NameExpr;
import org.drools.javaparser.ast.expr.StringLiteralExpr;
import org.drools.javaparser.ast.expr.ThisExpr;
import org.drools.javaparser.ast.expr.UnaryExpr;
import org.drools.javaparser.ast.nodeTypes.NodeWithArguments;
import org.drools.javaparser.ast.nodeTypes.NodeWithOptionalScope;
import org.drools.modelcompiler.builder.PackageModel;
import org.drools.modelcompiler.builder.errors.ParseExpressionErrorResult;
import org.drools.modelcompiler.builder.generator.DeclarationSpec;
import org.drools.modelcompiler.builder.generator.DrlxParseUtil;
import org.drools.modelcompiler.builder.generator.RuleContext;
import org.drools.modelcompiler.builder.generator.TypedExpression;

import static org.drools.javaparser.ast.expr.BinaryExpr.Operator.GREATER;
import static org.drools.javaparser.ast.expr.BinaryExpr.Operator.GREATER_EQUALS;
import static org.drools.javaparser.ast.expr.BinaryExpr.Operator.LESS;
import static org.drools.javaparser.ast.expr.BinaryExpr.Operator.LESS_EQUALS;
import static org.drools.javaparser.printer.PrintUtil.toDrlx;
import static org.drools.modelcompiler.builder.generator.DrlxParseUtil.isPrimitiveExpression;
import static org.drools.modelcompiler.builder.generator.DrlxParseUtil.toTypedExpression;

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
        if ( expression.startsWith( bindingId + "." ) ) {
            expression = expression.substring( bindingId.length()+1 );
        }

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
            List<String> usedDeclarations = new ArrayList<>();
            Set<String> reactOnProperties = new HashSet<>();

            TypedExpression left = DrlxParseUtil.toTypedExpression( context, packageModel, patternType, binaryExpr.getLeft(), usedDeclarations, reactOnProperties, binaryExpr, isPositional);
            if ( left == null ) {
                return new DrlxParseFail();
            }

            TypedExpression right = DrlxParseUtil.toTypedExpression( context, packageModel, patternType, binaryExpr.getRight(), usedDeclarations, reactOnProperties, binaryExpr, isPositional);

            Expression combo;
            if ( left.isPrimitive() ) {
                Expression rightExpr = right.getExpression() instanceof StringLiteralExpr ?
                        new IntegerLiteralExpr( (( StringLiteralExpr ) right.getExpression()).asString() ) :
                        right.getExpression();
                combo = new BinaryExpr( left.getExpression(), rightExpr, operator );
            } else {
                if ( left == null || right == null ) {
                    context.addCompilationError( new ParseExpressionErrorResult(drlxExpr) );
                    return new DrlxParseFail();
                }
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

            if ( left.getPrefixExpression() != null ) {
                combo = new BinaryExpr(left.getPrefixExpression(), combo, BinaryExpr.Operator.AND );
            }

            return new DrlxParseSuccess(patternType, exprId, bindingId, combo, left.getType())
                    .setDecodeConstraintType( decodeConstraintType ).setUsedDeclarations( usedDeclarations )
                    .setReactOnProperties( reactOnProperties ).setLeft( left ).setRight( right );
        }

        if ( drlxExpr instanceof UnaryExpr ) {
            UnaryExpr unaryExpr = (UnaryExpr) drlxExpr;

            List<String> usedDeclarations = new ArrayList<>();
            Set<String> reactOnProperties = new HashSet<>();
            TypedExpression left = DrlxParseUtil.toTypedExpression( context, packageModel, patternType, unaryExpr, usedDeclarations, reactOnProperties, unaryExpr, isPositional);

            return new DrlxParseSuccess(patternType, exprId, bindingId, left.getExpression(), left.getType())
                    .setUsedDeclarations( usedDeclarations ).setReactOnProperties( reactOnProperties ).setLeft( left );
        }

        if ( drlxExpr instanceof PointFreeExpr ) {
            PointFreeExpr pointFreeExpr = (PointFreeExpr) drlxExpr;

            List<String> usedDeclarations = new ArrayList<>();
            Set<String> reactOnProperties = new HashSet<>();

            final TypedExpression typedExpression = toTypedExpression(context, packageModel, patternType, pointFreeExpr, usedDeclarations, reactOnProperties, null, isPositional);
            final Expression returnExpression = typedExpression.getExpression();
            final Class<?> returnType = typedExpression.getType();

            return new DrlxParseSuccess(patternType, exprId, bindingId, returnExpression, returnType)
                    .setUsedDeclarations(usedDeclarations)
                    .setReactOnProperties(reactOnProperties)
                    .setLeft(typedExpression.getLeft())
                    .setStatic(typedExpression.isStatic())
                    .setValidExpression(true);
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
                Class<?> returnType = DrlxParseUtil.getClassFromContext(context.getPkg().getTypeResolver(), functionCall.get().getType().asString());
                NodeList<Expression> arguments = methodCallExpr.getArguments();
                List<String> usedDeclarations = new ArrayList<>();
                for (Expression arg : arguments) {
                    if (arg instanceof NameExpr && !arg.toString().equals("_this")) {
                        usedDeclarations.add(arg.toString());
                    } else if (arg instanceof MethodCallExpr) {
                        DrlxParseUtil.toTypedExpressionFromMethodCallOrField(context, null, ( MethodCallExpr ) arg, usedDeclarations, new HashSet<>(), context.getPkg().getTypeResolver());
                    }
                }
                return new DrlxParseSuccess(patternType, exprId, bindingId, methodCallExpr, returnType).setUsedDeclarations(usedDeclarations);
            } else if (methodCallExpr.getScope().isPresent() && methodCallExpr.getScope().get() instanceof StringLiteralExpr) {
                List<String> usedDeclarations = new ArrayList<>();
                TypedExpression converted = DrlxParseUtil.toTypedExpressionFromMethodCallOrField(context, String.class, methodCallExpr, usedDeclarations, new HashSet<>(), context.getPkg().getTypeResolver());
                return new DrlxParseSuccess(String.class, exprId, bindingId, converted.getExpression(), converted.getType()).setLeft(converted ).setUsedDeclarations(usedDeclarations );
            } else if (patternType != null) {
                NameExpr _this = new NameExpr("_this");
                TypedExpression converted = DrlxParseUtil.toMethodCallWithClassCheck(context, methodCallExpr, patternType, context.getPkg().getTypeResolver());
                Expression withThis = DrlxParseUtil.prepend(_this, converted.getExpression());
                return new DrlxParseSuccess(patternType, exprId, bindingId, withThis, converted.getType()).setLeft(converted );
            } else {
                return new DrlxParseSuccess(patternType, exprId, bindingId, methodCallExpr, null);
            }
        }

        if (drlxExpr instanceof FieldAccessExpr) {
            FieldAccessExpr fieldCallExpr = (FieldAccessExpr) drlxExpr;

            NameExpr _this = new NameExpr("_this");
            TypedExpression converted = DrlxParseUtil.toMethodCallWithClassCheck(context, fieldCallExpr, patternType, context.getPkg().getTypeResolver());
            Expression withThis = DrlxParseUtil.prepend(_this, converted.getExpression());
            return new DrlxParseSuccess(patternType, exprId, bindingId, withThis, converted.getType()).setLeft(converted );
        }

        if (drlxExpr instanceof NameExpr) {
            NameExpr methodCallExpr = (NameExpr) drlxExpr;

            NameExpr _this = new NameExpr("_this");
            TypedExpression converted = DrlxParseUtil.toMethodCallWithClassCheck(context, methodCallExpr, patternType, context.getPkg().getTypeResolver());
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
            return new DrlxParseSuccess(patternType, exprId, bindingId, drlxExpr, null);
        }

        throw new UnsupportedOperationException("Unknown expression: " + toDrlx(drlxExpr)); // TODO
    }

    private static Expression getEqualityExpression( TypedExpression left, TypedExpression right, BinaryExpr.Operator operator ) {
        if(isAnyOperandBigDecimal(left, right)) {
            return compareBigDecimal(operator, left, right);
        }

        Expression rightOperand = right.getExpression();
        if (isPrimitiveExpression(right.getExpression())) {
            if (left.getType() != String.class) {
                return new BinaryExpr( left.getExpression(), rightOperand, operator == BinaryExpr.Operator.EQUALS ? BinaryExpr.Operator.EQUALS : BinaryExpr.Operator.NOT_EQUALS );
            } else if ( rightOperand instanceof LiteralExpr ) {
                rightOperand = new StringLiteralExpr( rightOperand.toString() );
            }
        }

        MethodCallExpr methodCallExpr = new MethodCallExpr( null, "org.drools.modelcompiler.util.EvaluationUtil.areNullSafeEquals" );
        methodCallExpr.addArgument( left.getExpression() );
        methodCallExpr.addArgument( rightOperand ); // don't create NodeList with static method because missing "parent for child" would null and NPE
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
        final Expression leftBigDecimal = convertExpressionToBigDecimal(left);
        final Expression rightBigDecimal = convertExpressionToBigDecimal(right);
        final MethodCallExpr methodCallExpr = new MethodCallExpr(leftBigDecimal, "compareTo");
        methodCallExpr.addArgument(rightBigDecimal);
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
        List<Expression> res = new ArrayList<>();
        res.addAll(methodCallExpr.getArguments());
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
