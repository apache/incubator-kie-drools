package org.drools.modelcompiler.builder.generator.drlxparse;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.BinaryExpr;
import com.github.javaparser.ast.expr.CastExpr;
import com.github.javaparser.ast.expr.EnclosedExpr;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.FieldAccessExpr;
import com.github.javaparser.ast.expr.LiteralExpr;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.NullLiteralExpr;
import com.github.javaparser.ast.expr.ObjectCreationExpr;
import com.github.javaparser.ast.expr.StringLiteralExpr;
import com.github.javaparser.ast.expr.ThisExpr;
import com.github.javaparser.ast.expr.UnaryExpr;
import com.github.javaparser.ast.nodeTypes.NodeWithArguments;
import com.github.javaparser.ast.nodeTypes.NodeWithOptionalScope;
import org.drools.core.util.DateUtils;
import org.drools.model.Index;
import org.drools.modelcompiler.builder.PackageModel;
import org.drools.modelcompiler.builder.errors.InvalidExpressionErrorResult;
import org.drools.modelcompiler.builder.errors.ParseExpressionErrorResult;
import org.drools.modelcompiler.builder.generator.DeclarationSpec;
import org.drools.modelcompiler.builder.generator.DrlxParseUtil;
import org.drools.modelcompiler.builder.generator.ModelGenerator;
import org.drools.modelcompiler.builder.generator.RuleContext;
import org.drools.modelcompiler.builder.generator.TypedExpression;
import org.drools.modelcompiler.builder.generator.expressiontyper.ExpressionTyper;
import org.drools.modelcompiler.builder.generator.expressiontyper.ExpressionTyperContext;
import org.drools.modelcompiler.builder.generator.expressiontyper.TypedExpressionResult;
import org.drools.mvel.parser.ast.expr.BigDecimalLiteralExpr;
import org.drools.mvel.parser.ast.expr.BigIntegerLiteralExpr;
import org.drools.mvel.parser.ast.expr.DrlNameExpr;
import org.drools.mvel.parser.ast.expr.DrlxExpression;
import org.drools.mvel.parser.ast.expr.HalfBinaryExpr;
import org.drools.mvel.parser.ast.expr.HalfPointFreeExpr;
import org.drools.mvel.parser.ast.expr.OOPathExpr;
import org.drools.mvel.parser.ast.expr.PointFreeExpr;

import static com.github.javaparser.ast.expr.BinaryExpr.Operator.GREATER;
import static com.github.javaparser.ast.expr.BinaryExpr.Operator.GREATER_EQUALS;
import static com.github.javaparser.ast.expr.BinaryExpr.Operator.LESS;
import static com.github.javaparser.ast.expr.BinaryExpr.Operator.LESS_EQUALS;
import static org.drools.core.util.StringUtils.lcFirst;
import static org.drools.modelcompiler.builder.generator.DrlxParseUtil.THIS_PLACEHOLDER;
import static org.drools.modelcompiler.builder.generator.DrlxParseUtil.getLiteralExpressionType;
import static org.drools.modelcompiler.builder.generator.DrlxParseUtil.toClassOrInterfaceType;
import static org.drools.modelcompiler.builder.generator.drlxparse.SpecialComparisonCase.specialComparisonFactory;
import static org.drools.mvel.parser.printer.PrintUtil.printConstraint;

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
        return drlxParse(patternType, bindingId, new ConstraintExpression(expression), isPositional);
    }

    public DrlxParseResult drlxParse(Class<?> patternType, String bindingId, ConstraintExpression expression, boolean isPositional) {
        DrlxExpression drlx = DrlxParseUtil.parseExpression( expression.getExpression() );
        DrlxParseResult drlxParseResult = getDrlxParseResult(patternType, bindingId, expression, drlx.getExpr(), drlx.getBind() != null, isPositional );

        drlxParseResult.accept(result -> {
            if (drlx.getBind() != null) {
                SingleDrlxParseSuccess singleResult = (SingleDrlxParseSuccess) result;
                String bindId = drlx.getBind().asString();
                context.addDeclaration( bindId, singleResult.getExprRawClass() );
                singleResult.setExprBinding( bindId );
            }

        });

        return drlxParseResult;
    }

    private DrlxParseResult getDrlxParseResult(Class<?> patternType, String bindingId, ConstraintExpression constraint, Expression drlxExpr, boolean hasBind, boolean isPositional ) {
        boolean isEnclosed = false;
        while (drlxExpr instanceof EnclosedExpr) {
            drlxExpr = (( EnclosedExpr ) drlxExpr).getInner();
            isEnclosed = true;
        }

        if (drlxExpr instanceof MethodCallExpr && !(( MethodCallExpr ) drlxExpr).getScope().isPresent() && (( MethodCallExpr ) drlxExpr).getNameAsString().equals( "eval" )) {
            drlxExpr = (( MethodCallExpr ) drlxExpr).getArgument( 0 );
        }

        String expression = constraint.getExpression();
        String exprId;
        if ( GENERATE_EXPR_ID ) {
            exprId = context.getExprId( patternType, hasBind ? expression.substring( expression.indexOf( ':' )+1 ).trim() : expression );
        }

        if ( drlxExpr instanceof BinaryExpr ) {
            return parseBinaryExpr( (BinaryExpr) drlxExpr, patternType, bindingId, constraint, drlxExpr, hasBind, isPositional, isEnclosed, expression, exprId );
        }

        if ( drlxExpr instanceof UnaryExpr ) {
            return parseUnaryExpr( (UnaryExpr) drlxExpr, patternType, bindingId, isPositional, exprId );
        }

        if ( drlxExpr instanceof PointFreeExpr ) {
            return parsePointFreeExpr( (PointFreeExpr) drlxExpr, patternType, bindingId, constraint, hasBind, isPositional, exprId );
        }

        if (patternType == null && drlxExpr instanceof MethodCallExpr) {
            MethodCallExpr methodCallExpr = (MethodCallExpr) drlxExpr;
            Optional<MethodDeclaration> functionCall = packageModel.getFunctions().stream().filter( m -> m.getName().equals(methodCallExpr.getName())).findFirst();
            if (functionCall.isPresent()) {
                return parseFunctionInEval( methodCallExpr, patternType, bindingId, exprId, isPositional, functionCall );
            }
        }

        if (drlxExpr instanceof FieldAccessExpr) {
            return parseFieldAccessExpr( ( FieldAccessExpr ) drlxExpr, patternType, bindingId, exprId );
        }

        if (drlxExpr instanceof DrlNameExpr) {
            return parseNameExpr( (DrlNameExpr) drlxExpr, patternType, bindingId, drlxExpr, hasBind, expression, exprId );
        }

        if (drlxExpr instanceof OOPathExpr ) {
            return new SingleDrlxParseSuccess( patternType, exprId, bindingId, drlxExpr, null );
        }

        if (drlxExpr instanceof LiteralExpr ) {
            return new SingleDrlxParseSuccess( patternType, exprId, bindingId, drlxExpr, getLiteralExpressionType( (( LiteralExpr ) drlxExpr) ) );
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
            Expression combo = left.getExpression();
            for(Expression e : leftTypedExpressionResult.getPrefixExpressions()) {
                combo = new BinaryExpr(e, combo, BinaryExpr.Operator.AND );
            }
            return new SingleDrlxParseSuccess(patternType, exprId, bindingId, combo, left.getType())
                    .setUsedDeclarations( expressionTyperContext.getUsedDeclarations() );
        } else {
            final ExpressionTyperContext expressionTyperContext = new ExpressionTyperContext();
            final ExpressionTyper expressionTyper = new ExpressionTyper(context, null, bindingId, isPositional, expressionTyperContext);

            TypedExpressionResult leftTypedExpressionResult = expressionTyper.toTypedExpression(drlxExpr);
            Optional<TypedExpression> optLeft = leftTypedExpressionResult.getTypedExpression();
            if ( !optLeft.isPresent() ) {
                return new DrlxParseFail( new InvalidExpressionErrorResult( "Unable to parse left part of expression: " + expression ) );
            }

            TypedExpression left = optLeft.get();
            return new SingleDrlxParseSuccess(null, exprId, bindingId, drlxExpr, left.getType()).setUsedDeclarations( expressionTyperContext.getUsedDeclarations() );
        }
    }

    private DrlxParseResult parseFunctionInEval( MethodCallExpr methodCallExpr, Class<?> patternType, String bindingId, String exprId, boolean isPositional, Optional<MethodDeclaration> functionCall ) {
        // when the methodCallExpr will be placed in the model/DSL, any parameter being a "this" need to be implemented as _this by convention.
        List<ThisExpr> rewriteThisExprs = recurseCollectArguments(methodCallExpr).stream()
                .filter(ThisExpr.class::isInstance)
                .map(ThisExpr.class::cast)
                .collect( Collectors.toList());
        for (ThisExpr t : rewriteThisExprs) {
            methodCallExpr.replace(t, new NameExpr(THIS_PLACEHOLDER));
        }

        if (functionCall.isPresent()) {
            Class<?> returnType = DrlxParseUtil.getClassFromContext(context.getTypeResolver(), functionCall.get().getType().asString());
            NodeList<Expression> arguments = methodCallExpr.getArguments();
            List<String> usedDeclarations = new ArrayList<>();
            for (Expression arg : arguments) {
                String argString = printConstraint(arg);
                if (arg instanceof DrlNameExpr && !argString.equals(THIS_PLACEHOLDER)) {
                    usedDeclarations.add(argString);
                } else if (arg instanceof CastExpr ) {
                    String s = printConstraint(((CastExpr) arg).getExpression());
                    usedDeclarations.add(s);
                } else if (arg instanceof MethodCallExpr) {
                    TypedExpressionResult typedExpressionResult = new ExpressionTyper(context, null, bindingId, isPositional).toTypedExpression(arg);
                    usedDeclarations.addAll(typedExpressionResult.getUsedDeclarations());
                }
            }
            return new SingleDrlxParseSuccess(patternType, exprId, bindingId, methodCallExpr, returnType).setUsedDeclarations(usedDeclarations);
        } else {
            throw new IllegalArgumentException("Specified function call is not present!");
        }
    }

    private DrlxParseResult parseNameExpr(DrlNameExpr nameExpr, Class<?> patternType, String bindingId, Expression drlxExpr, boolean hasBind, String expression, String exprId) {
        TypedExpression converted = DrlxParseUtil.toMethodCallWithClassCheck(context, nameExpr, bindingId, patternType, context.getTypeResolver());
        if (converted == null) {
            return new DrlxParseFail();
        }
        Expression withThis = DrlxParseUtil.prepend(new NameExpr(THIS_PLACEHOLDER), converted.getExpression());

        if (hasBind) {
            return new SingleDrlxParseSuccess(patternType, exprId, bindingId, null, converted.getType() )
                    .setLeft( new TypedExpression( withThis, converted.getType() ) )
                    .addReactOnProperty( lcFirst(nameExpr.getNameAsString()) );
        } else if (context.hasDeclaration( expression )) {
            Optional<DeclarationSpec> declarationSpec = context.getDeclarationById(expression);
            if (declarationSpec.isPresent()) {
                return new SingleDrlxParseSuccess(patternType, exprId, bindingId, context.getVarExpr(printConstraint(drlxExpr)), declarationSpec.get().getDeclarationClass() );
            } else {
                throw new IllegalArgumentException("Cannot find declaration specification by specified expression " + expression + "!");
            }
        } else {
            return new SingleDrlxParseSuccess(patternType, exprId, bindingId, withThis, converted.getType() )
                    .addReactOnProperty( nameExpr.getNameAsString() );
        }
    }

    private DrlxParseResult parseFieldAccessExpr( FieldAccessExpr fieldCallExpr, Class<?> patternType, String bindingId, String exprId ) {
        TypedExpression converted = DrlxParseUtil.toMethodCallWithClassCheck(context, fieldCallExpr, bindingId, patternType, context.getTypeResolver());
        Expression withThis = DrlxParseUtil.prepend(new NameExpr(THIS_PLACEHOLDER), converted.getExpression());
        return new SingleDrlxParseSuccess(patternType, exprId, bindingId, withThis, converted.getType()).setLeft(converted );
    }

    private DrlxParseResult parsePointFreeExpr( PointFreeExpr pointFreeExpr, Class<?> patternType, String bindingId, ConstraintExpression constraint, boolean hasBind, boolean isPositional, String exprId ) {
        TypedExpressionResult typedExpressionResult = new ExpressionTyper(context, patternType, bindingId, isPositional).toTypedExpression(pointFreeExpr);

        return typedExpressionResult.getTypedExpression().<DrlxParseResult>map(typedExpression -> {
            boolean isTemporal = ModelGenerator.temporalOperators.contains(pointFreeExpr.getOperator().asString());
            Object rightLiteral = null;
            if (isTemporal && pointFreeExpr.getRight().size() == 1) {
                Expression rightExpr = pointFreeExpr.getRight().get(0);
                if (rightExpr instanceof StringLiteralExpr ) {
                    String value = (( StringLiteralExpr ) rightExpr).getValue();
                    rightLiteral = DateUtils.parseDate(value).getTime() + "L";
                }
            }

            return new SingleDrlxParseSuccess(patternType, exprId, bindingId, typedExpression.getExpression(), typedExpression.getType())
                    .setUsedDeclarations(typedExpressionResult.getUsedDeclarations())
                    .setUsedDeclarationsOnLeft( Collections.emptyList())
                    .setReactOnProperties(typedExpressionResult.getReactOnProperties())
                    .setLeft(typedExpression.getLeft())
                    .setRight(typedExpression.getRight())
                    .setRightLiteral(rightLiteral)
                    .setStatic(typedExpression.isStatic())
                    .setTemporal( isTemporal )
                    .setValidExpression(true);
        }).orElseGet( () -> new DrlxParseFail( new ParseExpressionErrorResult(pointFreeExpr) ));
    }

    private DrlxParseResult parseUnaryExpr( UnaryExpr unaryExpr, Class<?> patternType, String bindingId, boolean isPositional, String exprId ) {
        TypedExpressionResult typedExpressionResult = new ExpressionTyper(context, patternType, bindingId, isPositional).toTypedExpression(unaryExpr);
        return typedExpressionResult.getTypedExpression().<DrlxParseResult>map(left -> new SingleDrlxParseSuccess(patternType, exprId, bindingId, left.getExpression(), left.getType())
                .setUsedDeclarations( typedExpressionResult.getUsedDeclarations() ).setReactOnProperties( typedExpressionResult.getReactOnProperties() )
                .setLeft( left ) ).orElseGet( () -> new DrlxParseFail( new ParseExpressionErrorResult(unaryExpr) ));
    }

    private DrlxParseResult parseBinaryExpr( BinaryExpr binaryExpr, Class<?> patternType, String bindingId, ConstraintExpression constraint, Expression drlxExpr,
                                             boolean hasBind, boolean isPositional, boolean isEnclosed, String expression, String exprId ) {
        BinaryExpr.Operator operator = binaryExpr.getOperator();

        if ( isLogicalOperator( operator ) && isCombinable( binaryExpr ) ) {
            DrlxParseResult leftResult = getDrlxParseResult(patternType, bindingId, constraint, binaryExpr.getLeft(), hasBind, isPositional );
            Expression rightExpr = binaryExpr.getRight() instanceof HalfPointFreeExpr ?
                    completeHalfExpr( (( PointFreeExpr ) binaryExpr.getLeft()).getLeft(), ( HalfPointFreeExpr ) binaryExpr.getRight()) :
                    binaryExpr.getRight();
            DrlxParseResult rightResult = getDrlxParseResult(patternType, bindingId, constraint, rightExpr, hasBind, isPositional );
            if (leftResult.isSuccess() && rightResult.isSuccess() && ( (( DrlxParseSuccess ) leftResult).isTemporal() || (( DrlxParseSuccess ) rightResult).isTemporal() ) ) {
                return new MultipleDrlxParseSuccess( operator, ( DrlxParseSuccess ) leftResult, ( DrlxParseSuccess ) rightResult );
            }
            return leftResult.combineWith( rightResult, operator );
        }

        final ExpressionTyperContext expressionTyperContext = new ExpressionTyperContext();
        final ExpressionTyper expressionTyper = new ExpressionTyper(context, patternType, bindingId, isPositional, expressionTyperContext);

        TypedExpressionResult leftTypedExpressionResult = expressionTyper.toTypedExpression(binaryExpr.getLeft());
        Optional<TypedExpression> optLeft = leftTypedExpressionResult.getTypedExpression();
        if ( !optLeft.isPresent() ) {
            return new DrlxParseFail( new InvalidExpressionErrorResult( "Unable to parse left part of expression: " + expression ) );
        }

        TypedExpression left = optLeft.get();
        List<String> usedDeclarationsOnLeft = hasBind ? new ArrayList<>( expressionTyperContext.getUsedDeclarations() ) : null;

        TypedExpression right;
        if (constraint.isNameClashingUnification()) {
            String name = constraint.getUnificationField();
            right = new TypedExpression( new NameExpr( name ), left.getType() );
            expressionTyperContext.addUsedDeclarations( name );
        } else {
            TypedExpressionResult rightExpressionResult = expressionTyper.toTypedExpression( binaryExpr.getRight() );
            Optional<TypedExpression> optRight = rightExpressionResult.getTypedExpression();
            if ( !optRight.isPresent() ) {
                return new DrlxParseFail( new ParseExpressionErrorResult( drlxExpr ) );
            }
            right = optRight.get();
        }

        CoercedExpression.CoercedExpressionResult coerced;
        try {
            coerced = new CoercedExpression(left, right).coerce();
        } catch (CoercedExpression.CoercedExpressionException e) {
            return new DrlxParseFail(e.getInvalidExpressionErrorResult());
        }

        left = coerced.getCoercedLeft();
        right = coerced.getCoercedRight();

        Expression combo;

        switch (operator) {
            case EQUALS:
            case NOT_EQUALS:
                combo = getEqualityExpression(left, right, operator).expression;
                break;
            default:
                if (left.getExpression() == null || right.getExpression() == null) {
                    return new DrlxParseFail(new ParseExpressionErrorResult(drlxExpr));
                }
                SpecialComparisonResult specialComparisonResult = handleSpecialComparisonCases(operator, left, right);
                combo = specialComparisonResult.expression;
                left = specialComparisonResult.coercedLeft;
                right = specialComparisonResult.coercedRight;
        }

        for(Expression e : leftTypedExpressionResult.getPrefixExpressions()) {
            combo = new BinaryExpr(e, combo, BinaryExpr.Operator.AND );
        }


        boolean isBetaNode = right.getExpression() != null && context.getDeclarationById( getExpressionSymbol( right.getExpression() ) ).isPresent();
        if (isEnclosed) {
            combo = new EnclosedExpr( combo );
        }

        Index.ConstraintType constraintType = DrlxParseUtil.toConstraintType( operator );
        if ( isForallSelfJoinConstraint( left, right, constraintType ) ) {
            constraintType = Index.ConstraintType.FORALL_SELF_JOIN;
        }

        boolean requiresSplit = operator == BinaryExpr.Operator.AND && binaryExpr.getRight() instanceof HalfBinaryExpr && !isBetaNode;
        return new SingleDrlxParseSuccess(patternType, exprId, bindingId, combo, left.getType()).setDecodeConstraintType( constraintType )
                .setUsedDeclarations( expressionTyperContext.getUsedDeclarations() ).setUsedDeclarationsOnLeft( usedDeclarationsOnLeft ).setUnification( constraint.isUnification() )
                .setReactOnProperties( expressionTyperContext.getReactOnProperties() ).setLeft( left ).setRight( right ).setBetaNode(isBetaNode).setRequiresSplit( requiresSplit );
    }

    private boolean isForallSelfJoinConstraint( TypedExpression left, TypedExpression right, Index.ConstraintType constraintType ) {
        return constraintType == Index.ConstraintType.EQUAL && context.getForallFirstIdentifier() != null &&
                left.isThisExpression() && right.getExpression() instanceof NameExpr &&
                right.getExpression().toString().equals( context.getForallFirstIdentifier() );
    }

    private boolean isCombinable( BinaryExpr binaryExpr ) {
        return !(binaryExpr.getRight() instanceof HalfBinaryExpr) && ( !(binaryExpr.getRight() instanceof HalfPointFreeExpr) || binaryExpr.getLeft() instanceof PointFreeExpr );
    }

    private static boolean isLogicalOperator( BinaryExpr.Operator operator ) {
        return operator == BinaryExpr.Operator.AND || operator == BinaryExpr.Operator.OR;
    }

    private static PointFreeExpr completeHalfExpr(Expression left, HalfPointFreeExpr halfRight) {
        return new PointFreeExpr( halfRight.getTokenRange().orElse( null ), left, halfRight.getRight(), halfRight.getOperator(), halfRight.isNegated(), halfRight.getArg1(), halfRight.getArg2(), halfRight.getArg3(), halfRight.getArg4() );
    }

    private static String getExpressionSymbol(Expression expr) {
        if (expr instanceof MethodCallExpr) {
            Optional<Expression> scopeExpression = (( MethodCallExpr ) expr).getScope();
            if (scopeExpression.isPresent()) {
                return getExpressionSymbol( scopeExpression.get() );
            }
        }
        if (expr instanceof FieldAccessExpr ) {
            return getExpressionSymbol( (( FieldAccessExpr ) expr).getScope() );
        }
        return printConstraint(expr);
    }

    private static SpecialComparisonResult getEqualityExpression(TypedExpression left, TypedExpression right, BinaryExpr.Operator operator ) {
        if((isAnyOperandBigDecimal(left, right) || isAnyOperandBigInteger(left, right)) && !isAnyOperandNullLiteral( left, right )) {
            return compareBigDecimal(operator, left, right);
        }

        String equalsMethod = isNumber(left) && isNumber(right) ?
                "org.drools.modelcompiler.util.EvaluationUtil.areNumbersNullSafeEquals" :
                "org.drools.modelcompiler.util.EvaluationUtil.areNullSafeEquals";

        MethodCallExpr methodCallExpr = new MethodCallExpr( null, equalsMethod );
        // Avoid casts, by using an helper method we leverage autoboxing and equals
        methodCallExpr.addArgument(uncastExpr(left.getExpression()));
        methodCallExpr.addArgument(uncastExpr(right.getExpression()));
        Expression expression = operator == BinaryExpr.Operator.EQUALS ? methodCallExpr : new UnaryExpr(methodCallExpr, UnaryExpr.Operator.LOGICAL_COMPLEMENT);
        return new SpecialComparisonResult(expression, left, right);
    }

    static Boolean isNumber(TypedExpression left) {
        return left.getBoxedType().map(ConstraintParser::isNumericType).orElse(false);
    }

    static Expression uncastExpr(Expression e) {
        if(e == null) { // Not sure why a null should be here - check QueryTest.testPositionalRecursiveQuery
            return null;
        }
        if(e.isCastExpr()) {
            return e.asCastExpr().getExpression();
        } else {
            return e;
        }
    }

    private static SpecialComparisonResult handleSpecialComparisonCases(BinaryExpr.Operator operator, TypedExpression left, TypedExpression right) {
        if ((isAnyOperandBigDecimal(left, right) || isAnyOperandBigInteger(left, right)) && (isComparisonOperator(operator))) {
            return compareBigDecimal(operator, left, right);
        }

        if ( isComparisonOperator( operator ) ) {
            SpecialComparisonCase methodName = specialComparisonFactory(left, right);
            return methodName.createCompareMethod(operator);
        }

        return new SpecialComparisonResult(new BinaryExpr( left.getExpression(), right.getExpression(), operator ), left, right);
    }

    static class SpecialComparisonResult {
        Expression expression;
        TypedExpression coercedLeft;
        TypedExpression coercedRight;

        SpecialComparisonResult(Expression expression, TypedExpression coercedLeft, TypedExpression coercedRight) {
            this.expression = expression;
            this.coercedLeft = coercedLeft;
            this.coercedRight = coercedRight;
        }
    }

    static String operatorToName(BinaryExpr.Operator operator) {
        switch (operator.asString()) {
            case "==" : return "equals";
            case "!=" : return "notEquals";
            case "<" : return "lessThan";
            case "<=" : return "lessOrEqual";
            case ">" : return "greaterThan";
            case ">=" : return "greaterOrEqual";
        }
        throw new RuntimeException( "unknown operator: " + operator );
    }

    private static boolean isNumericType(Class<?> type) {
        return Number.class.isAssignableFrom( type ) && type != BigInteger.class && type != BigDecimal.class;
    }

    private static boolean isAnyOperandBigDecimal(TypedExpression left, TypedExpression right) {
        return left.getType() == BigDecimal.class || right.getType() == BigDecimal.class;
    }

    private static boolean isAnyOperandBigInteger(TypedExpression left, TypedExpression right) {
        return left.getType() == BigInteger.class || right.getType() == BigInteger.class;
    }

    private static boolean isAnyOperandNullLiteral(TypedExpression left, TypedExpression right) {
        return left.getExpression() instanceof NullLiteralExpr || right.getExpression() instanceof NullLiteralExpr;
    }

    private static SpecialComparisonResult compareBigDecimal(BinaryExpr.Operator operator, TypedExpression left, TypedExpression right) {
        String methodName = "org.drools.modelcompiler.util.EvaluationUtil." + operatorToName(operator);
        MethodCallExpr compareMethod = new MethodCallExpr( null, methodName );
        compareMethod.addArgument( toBigDecimalExpression( left ) );
        compareMethod.addArgument( toBigDecimalExpression( right ) );
        return new SpecialComparisonResult(compareMethod, left, right);
    }

    private static Expression toBigDecimalExpression( TypedExpression typedExpression ) {
        MethodCallExpr toBigDecimalMethod = new MethodCallExpr( null, "org.drools.modelcompiler.util.EvaluationUtil.toBigDecimal" );
        Expression arg = typedExpression.getExpression();
        if(arg.isEnclosedExpr()) {
            arg = arg.asEnclosedExpr().getInner();
        }
        if (arg instanceof BigIntegerLiteralExpr) {
            arg = new ObjectCreationExpr(null, toClassOrInterfaceType(BigInteger.class), NodeList.nodeList( new StringLiteralExpr(((BigIntegerLiteralExpr) arg).asBigInteger().toString()) ));
        } else if (arg instanceof BigDecimalLiteralExpr ) {
            arg = new ObjectCreationExpr(null, toClassOrInterfaceType(BigDecimal.class), NodeList.nodeList( new StringLiteralExpr(((BigDecimalLiteralExpr) arg).asBigDecimal().toString()) ));
        }
        toBigDecimalMethod.addArgument( arg );
        return toBigDecimalMethod;
    }

    private static boolean isComparisonOperator( BinaryExpr.Operator op ) {
        return op == LESS || op == GREATER || op == LESS_EQUALS || op == GREATER_EQUALS;
    }

    private static List<Expression> recurseCollectArguments(NodeWithArguments<?> methodCallExpr) {
        List<Expression> res = new ArrayList<>( methodCallExpr.getArguments() );
        if ( methodCallExpr instanceof NodeWithOptionalScope ) {
            NodeWithOptionalScope<?> nodeWithOptionalScope = (NodeWithOptionalScope) methodCallExpr;
            Optional<Expression> scopeExpression = nodeWithOptionalScope.getScope();
            if ( scopeExpression.isPresent() ) {
                Object scope = scopeExpression.get();
                if (scope instanceof NodeWithArguments) {
                    res.addAll(recurseCollectArguments((NodeWithArguments<?>) scope));
                }
            }
        }
        return res;
    }
}
