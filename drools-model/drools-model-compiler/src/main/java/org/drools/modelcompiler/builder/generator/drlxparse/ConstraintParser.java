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

package org.drools.modelcompiler.builder.generator.drlxparse;

import java.lang.reflect.Type;
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
import com.github.javaparser.ast.expr.SimpleName;
import com.github.javaparser.ast.expr.StringLiteralExpr;
import com.github.javaparser.ast.expr.ThisExpr;
import com.github.javaparser.ast.expr.UnaryExpr;
import com.github.javaparser.ast.nodeTypes.NodeWithArguments;
import com.github.javaparser.ast.nodeTypes.NodeWithOptionalScope;
import org.drools.core.util.DateUtils;
import org.drools.model.Index;
import org.drools.modelcompiler.builder.PackageModel;
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
import org.drools.mvel.parser.ast.expr.FullyQualifiedInlineCastExpr;
import org.drools.mvel.parser.ast.expr.HalfBinaryExpr;
import org.drools.mvel.parser.ast.expr.HalfPointFreeExpr;
import org.drools.mvel.parser.ast.expr.OOPathExpr;
import org.drools.mvel.parser.ast.expr.PointFreeExpr;
import org.drools.mvelcompiler.CompiledExpressionResult;
import org.drools.mvelcompiler.ConstraintCompiler;

import static com.github.javaparser.ast.expr.BinaryExpr.Operator.AND;
import static com.github.javaparser.ast.expr.BinaryExpr.Operator.DIVIDE;
import static com.github.javaparser.ast.expr.BinaryExpr.Operator.EQUALS;
import static com.github.javaparser.ast.expr.BinaryExpr.Operator.GREATER;
import static com.github.javaparser.ast.expr.BinaryExpr.Operator.GREATER_EQUALS;
import static com.github.javaparser.ast.expr.BinaryExpr.Operator.LESS;
import static com.github.javaparser.ast.expr.BinaryExpr.Operator.LESS_EQUALS;
import static com.github.javaparser.ast.expr.BinaryExpr.Operator.MINUS;
import static com.github.javaparser.ast.expr.BinaryExpr.Operator.MULTIPLY;
import static com.github.javaparser.ast.expr.BinaryExpr.Operator.NOT_EQUALS;
import static com.github.javaparser.ast.expr.BinaryExpr.Operator.OR;
import static com.github.javaparser.ast.expr.BinaryExpr.Operator.PLUS;
import static com.github.javaparser.ast.expr.BinaryExpr.Operator.REMAINDER;
import static java.util.Arrays.asList;
import static org.drools.core.util.StringUtils.lcFirstForBean;
import static org.drools.modelcompiler.builder.generator.DrlxParseUtil.THIS_PLACEHOLDER;
import static org.drools.modelcompiler.builder.generator.DrlxParseUtil.createConstraintCompiler;
import static org.drools.modelcompiler.builder.generator.DrlxParseUtil.getLiteralExpressionType;
import static org.drools.modelcompiler.builder.generator.DrlxParseUtil.isBooleanBoxedUnboxed;
import static org.drools.modelcompiler.builder.generator.DrlxParseUtil.stripEnclosedExpr;
import static org.drools.modelcompiler.builder.generator.DrlxParseUtil.toClassOrInterfaceType;
import static org.drools.modelcompiler.builder.generator.drlxparse.SpecialComparisonCase.specialComparisonFactory;
import static org.drools.modelcompiler.builder.generator.expressiontyper.FlattenScope.transformFullyQualifiedInlineCastExpr;
import static org.drools.mvel.parser.printer.PrintUtil.printConstraint;

public class ConstraintParser {

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

    public DrlxParseResult drlxParse(Class<?> patternType, String bindingId, ConstraintExpression constraint, boolean isPositional) {
        DrlxExpression drlx = DrlxParseUtil.parseExpression( constraint.getExpression() );
        boolean hasBind = drlx.getBind() != null;
        DrlxParseResult drlxParseResult =
                getDrlxParseResult(patternType, bindingId, constraint, drlx.getExpr(), hasBind, isPositional )
                .setOriginalDrlConstraint(constraint.getExpression());

        drlxParseResult.accept(result -> {
            if (hasBind) {
                SingleDrlxParseSuccess singleResult = (SingleDrlxParseSuccess) result;
                String bindId = drlx.getBind().asString();
                addDeclaration(drlx, singleResult, bindId);
            } else if (result instanceof SingleDrlxParseSuccess) {
                SingleDrlxParseSuccess singleResult = (SingleDrlxParseSuccess) result;
                // a constraint has a binding inside its expression (not in top level DrlxExpression)
                String bindId = singleResult.getExprBinding();
                if (bindId != null) {
                    addDeclaration(drlx, singleResult, bindId);
                }
            }
        });

        return drlxParseResult;
    }

    private void addDeclaration(DrlxExpression drlx, SingleDrlxParseSuccess singleResult, String bindId) {
        DeclarationSpec decl = context.addDeclaration( bindId, singleResult.getLeftExprRawClass() );
        if (drlx.getExpr() instanceof NameExpr) {
            decl.setBoundVariable( drlx.getExpr().toString() );
        }
        singleResult.setExprBinding( bindId );
        Type exprType = singleResult.getExprType();
        if(isBooleanBoxedUnboxed(exprType)) {
            singleResult.setIsPredicate(singleResult.getRight() != null);
        }
    }

    private DrlxParseResult getDrlxParseResult(Class<?> patternType, String bindingId, ConstraintExpression constraint, Expression drlxExpr, boolean hasBind, boolean isPositional ) {
        boolean isEnclosed = false;
        SimpleName bind = null;

        if (drlxExpr instanceof FullyQualifiedInlineCastExpr ) {
            drlxExpr = transformFullyQualifiedInlineCastExpr( context.getTypeResolver(), (FullyQualifiedInlineCastExpr) drlxExpr );
        }

        while (drlxExpr instanceof EnclosedExpr) {
            drlxExpr = (( EnclosedExpr ) drlxExpr).getInner();
            isEnclosed = true;
        }

        if ( drlxExpr instanceof DrlxExpression ) {
            bind = ((DrlxExpression) drlxExpr).getBind();
            drlxExpr = (( DrlxExpression ) drlxExpr).getExpr();
        }

        if (drlxExpr instanceof MethodCallExpr && !(( MethodCallExpr ) drlxExpr).getScope().isPresent() && (( MethodCallExpr ) drlxExpr).getNameAsString().equals( "eval" )) {
            drlxExpr = (( MethodCallExpr ) drlxExpr).getArgument( 0 );
        }

        if ( drlxExpr instanceof BinaryExpr ) {
            DrlxParseResult result = parseBinaryExpr( (BinaryExpr) drlxExpr, patternType, bindingId, constraint, drlxExpr, hasBind, isPositional, isEnclosed);
            if (result instanceof SingleDrlxParseSuccess && bind != null) {
                ((SingleDrlxParseSuccess)result).setExprBinding(bind.asString());
            }
            return result;
        }

        if ( drlxExpr instanceof UnaryExpr ) {
            return parseUnaryExpr( (UnaryExpr) drlxExpr, patternType, bindingId, constraint, drlxExpr, hasBind, isPositional);
        }

        if ( drlxExpr instanceof PointFreeExpr ) {
            return parsePointFreeExpr((PointFreeExpr) drlxExpr, patternType, bindingId, isPositional);
        }

        if (patternType == null && drlxExpr instanceof MethodCallExpr) {
            MethodCallExpr methodCallExpr = (MethodCallExpr) drlxExpr;
            Optional<MethodDeclaration> functionCall = packageModel.getFunctions().stream().filter( m -> m.getName().equals(methodCallExpr.getName())).findFirst();
            if (functionCall.isPresent()) {
                return parseFunctionInEval(methodCallExpr, patternType, bindingId, isPositional, functionCall );
            }
        }

        if (drlxExpr instanceof FieldAccessExpr) {
            return parseFieldAccessExpr( ( FieldAccessExpr ) drlxExpr, patternType, bindingId);
        }

        String expression = constraint.getExpression();

        if (drlxExpr instanceof DrlNameExpr) {
            return parseNameExpr( (DrlNameExpr) drlxExpr, patternType, bindingId, drlxExpr, hasBind, expression);
        }

        if (drlxExpr instanceof OOPathExpr ) {
            return parseOOPathExpr( (OOPathExpr) drlxExpr, patternType, bindingId, drlxExpr, hasBind, expression);
        }

        if (drlxExpr instanceof LiteralExpr ) {
            Class<?> literalExpressionType = getLiteralExpressionType(((LiteralExpr) drlxExpr));
            return new SingleDrlxParseSuccess(patternType, bindingId, drlxExpr, literalExpressionType)
                    .setIsPredicate(isBooleanBoxedUnboxed(literalExpressionType));
        }

        if (patternType != null) {
            ExpressionTyperContext expressionTyperContext = new ExpressionTyperContext();
            ExpressionTyper expressionTyper = new ExpressionTyper(context, patternType, bindingId, isPositional, expressionTyperContext);
            TypedExpressionResult leftTypedExpressionResult = expressionTyper.toTypedExpression(drlxExpr);
            Optional<TypedExpression> optLeft = leftTypedExpressionResult.getTypedExpression();
            if ( !optLeft.isPresent() ) {
                return new DrlxParseFail();
            }
            TypedExpression left = optLeft.get();
            Expression combo = left.getExpression();

            Type exprType = left.getType();
            boolean isPredicate = isBooleanBoxedUnboxed(exprType);

            if (isPredicate) {
                combo = combineExpressions( leftTypedExpressionResult, combo );
            }

            return new SingleDrlxParseSuccess(patternType, bindingId, combo, exprType)
                    .setReactOnProperties( expressionTyperContext.getReactOnProperties() )
                    .setUsedDeclarations( expressionTyperContext.getUsedDeclarations() )
                    .setImplicitCastExpression( expressionTyperContext.getInlineCastExpression() )
                    .setIsPredicate(isPredicate);
        } else {
            final ExpressionTyperContext expressionTyperContext = new ExpressionTyperContext();
            final ExpressionTyper expressionTyper = new ExpressionTyper(context, null, bindingId, isPositional, expressionTyperContext);

            TypedExpressionResult leftTypedExpressionResult = expressionTyper.toTypedExpression(drlxExpr);
            Optional<TypedExpression> optLeft = leftTypedExpressionResult.getTypedExpression();
            if ( !optLeft.isPresent() ) {
                return new DrlxParseFail();
            }

            TypedExpression left = optLeft.get();
            return new SingleDrlxParseSuccess(null, bindingId, drlxExpr, left.getType())
                    .setUsedDeclarations( expressionTyperContext.getUsedDeclarations() )
                    .setIsPredicate(true);
        }
    }

    private Expression combineExpressions( TypedExpressionResult leftTypedExpressionResult, Expression combo ) {
        for (Expression e : leftTypedExpressionResult.getPrefixExpressions()) {
            combo = new BinaryExpr( e, combo, BinaryExpr.Operator.AND );
        }
        return combo;
    }

    private Expression combineExpressions(List<Expression> leftPrefixExpresssions, List<Expression> rightPrefixExpresssions, Expression combo) {
        Expression inner = combo;
        if (combo.isEnclosedExpr()) {
            EnclosedExpr enclosedExpr = combo.asEnclosedExpr();
            inner = stripEnclosedExpr(enclosedExpr);
        }

        BinaryExpr binaryExpr;
        if (inner.isBinaryExpr()) {
            binaryExpr = inner.asBinaryExpr();
        } else {
            throw new RuntimeException(combo + " is not nor contains BinaryExpr");
        }

        Expression left = binaryExpr.getLeft();
        for (Expression prefixExpression : leftPrefixExpresssions) {
            left = new BinaryExpr(prefixExpression, left, BinaryExpr.Operator.AND);
        }
        binaryExpr.setLeft(left);

        Expression right = binaryExpr.getRight();
        for (Expression prefixExpression : rightPrefixExpresssions) {
            right = new BinaryExpr(prefixExpression, right, BinaryExpr.Operator.AND);
        }
        binaryExpr.setRight(right);
        return combo;
    }

    private DrlxParseResult parseFunctionInEval(MethodCallExpr methodCallExpr, Class<?> patternType, String bindingId, boolean isPositional, Optional<MethodDeclaration> functionCall) {
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
            return new SingleDrlxParseSuccess(patternType, bindingId, methodCallExpr, returnType)
                    .setUsedDeclarations(usedDeclarations)
                    .setIsPredicate(isBooleanBoxedUnboxed(returnType));
        } else {
            throw new IllegalArgumentException("Specified function call is not present!");
        }
    }

    private DrlxParseResult parseOOPathExpr(OOPathExpr ooPathExpr, Class<?> patternType, String bindingId, Expression drlxExpr, boolean hasBind, String expression) {
        Type exprType = null;
        if (hasBind) {
            // if oopath expression isn't bound it is useless to discover its type
            final ExpressionTyper expressionTyper = new ExpressionTyper(context, patternType, bindingId, false, new ExpressionTyperContext());

            TypedExpressionResult typedExpressionResult = expressionTyper.toTypedExpression(ooPathExpr);
            Optional<TypedExpression> typedExpression = typedExpressionResult.getTypedExpression();
            if (!typedExpression.isPresent()) {
                return new DrlxParseFail();
            }
            exprType = typedExpression.get().getType();
        }
        return new SingleDrlxParseSuccess( patternType, bindingId, drlxExpr, exprType ).setIsPredicate(true);
    }

    private DrlxParseResult parseNameExpr(DrlNameExpr nameExpr, Class<?> patternType, String bindingId, Expression drlxExpr, boolean hasBind, String expression) {
        TypedExpression converted;
        final ExpressionTyperContext expressionTyperContext = new ExpressionTyperContext();
        final ExpressionTyper expressionTyper = new ExpressionTyper(context, patternType, bindingId, false, expressionTyperContext);

        Optional<TypedExpression> typedExpressionResult = expressionTyper.toTypedExpression(nameExpr).getTypedExpression();
        if ( !typedExpressionResult.isPresent() ) {
            return new DrlxParseFail();
        }

        converted = typedExpressionResult.get();
        Expression withThis = DrlxParseUtil.prepend(new NameExpr(THIS_PLACEHOLDER), converted.getExpression());

        if (hasBind) {
            return new SingleDrlxParseSuccess(patternType, bindingId, null, converted.getType() )
                    .setLeft( new TypedExpression( withThis, converted.getType() ) )
                    .addReactOnProperty( lcFirstForBean(nameExpr.getNameAsString()) );
        } else if (context.hasDeclaration( expression )) {
            Optional<DeclarationSpec> declarationSpec = context.getDeclarationById(expression);
            if (declarationSpec.isPresent()) {
                return new SingleDrlxParseSuccess(patternType, bindingId, context.getVarExpr(printConstraint(drlxExpr)), declarationSpec.get().getDeclarationClass() ).setIsPredicate(true);
            } else {
                throw new IllegalArgumentException("Cannot find declaration specification by specified expression " + expression + "!");
            }
        } else {
            return new SingleDrlxParseSuccess(patternType, bindingId, withThis, converted.getType() )
                    .addReactOnProperty( nameExpr.getNameAsString() )
                    .setIsPredicate(true);
        }
    }

    private DrlxParseResult parseFieldAccessExpr( FieldAccessExpr fieldCallExpr, Class<?> patternType, String bindingId ) {
        final ExpressionTyperContext expressionTyperContext = new ExpressionTyperContext();
        final ExpressionTyper expressionTyper = new ExpressionTyper(context, patternType, bindingId, false, expressionTyperContext);

        TypedExpressionResult typedExpressionResult = expressionTyper.toTypedExpression(fieldCallExpr);
        Optional<TypedExpression> typedExpression = typedExpressionResult.getTypedExpression();
        if ( !typedExpression.isPresent() ) {
            return new DrlxParseFail();
        }

        TypedExpression converted = typedExpression.get();
        Type type = converted.getType();
        boolean isPredicate = isBooleanBoxedUnboxed(type);

        Expression combo = converted.getExpression();
        if (isPredicate) {
            combo = combineExpressions( typedExpressionResult, combo );
        }

        Expression withThis = DrlxParseUtil.prepend(new NameExpr(THIS_PLACEHOLDER), combo);

        return new SingleDrlxParseSuccess(patternType, bindingId, withThis, type)
                .setUsedDeclarations(expressionTyperContext.getUsedDeclarations())
                .setLeft(converted)
                .setImplicitCastExpression(expressionTyperContext.getInlineCastExpression())
                .setIsPredicate(isPredicate);
    }

    private DrlxParseResult parsePointFreeExpr(PointFreeExpr pointFreeExpr, Class<?> patternType, String bindingId, boolean isPositional) {
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

            Expression combo = combineExpressions( typedExpressionResult, typedExpression.getExpression() );

            return new SingleDrlxParseSuccess(patternType, bindingId, combo, typedExpression.getType())
                    .setUsedDeclarations(typedExpressionResult.getUsedDeclarations())
                    .setUsedDeclarationsOnLeft( Collections.emptyList())
                    .setReactOnProperties(typedExpressionResult.getReactOnProperties())
                    .setLeft(typedExpression.getLeft())
                    .setRight(typedExpression.getRight())
                    .setRightLiteral(rightLiteral)
                    .setStatic(typedExpression.isStatic())
                    .setTemporal( isTemporal )
                    .setIsPredicate(true);
        }).orElseGet( () -> new DrlxParseFail( new ParseExpressionErrorResult(pointFreeExpr) ));
    }

    private DrlxParseResult parseUnaryExpr( UnaryExpr unaryExpr, Class<?> patternType, String bindingId, ConstraintExpression constraint, Expression drlxExpr,
                                            boolean hasBind, boolean isPositional) {
        TypedExpressionResult typedExpressionResult = new ExpressionTyper(context, patternType, bindingId, isPositional).toTypedExpression(unaryExpr);
        Optional<TypedExpression> opt = typedExpressionResult.getTypedExpression();
        if (!opt.isPresent()) {
            return new DrlxParseFail(new ParseExpressionErrorResult(drlxExpr));
        }
        TypedExpression typedExpression = opt.get();

        SingleDrlxParseSuccess innerResult = (SingleDrlxParseSuccess) getDrlxParseResult(patternType, bindingId, constraint, unaryExpr.getExpression(), hasBind, isPositional);

        Expression innerExpression;
        if (unaryExpr.getExpression() instanceof EnclosedExpr && !(innerResult.getExpr() instanceof EnclosedExpr)) {
            innerExpression = new EnclosedExpr(innerResult.getExpr()); // inner EnclosedExpr could be stripped
        } else {
            innerExpression = innerResult.getExpr();
        }

        return new SingleDrlxParseSuccess(patternType, bindingId, new UnaryExpr(innerExpression, unaryExpr.getOperator()), typedExpression.getType())
                .setDecodeConstraintType(Index.ConstraintType.UNKNOWN).setUsedDeclarations(typedExpressionResult.getUsedDeclarations())
                .setReactOnProperties(typedExpressionResult.getReactOnProperties())
                .setLeft(new TypedExpression(innerResult.getExpr(), innerResult.getExprType()))
                .setIsPredicate(innerResult.isPredicate());
    }

    private DrlxParseResult parseBinaryExpr(BinaryExpr binaryExpr, Class<?> patternType, String bindingId, ConstraintExpression constraint, Expression drlxExpr,
                                            boolean hasBind, boolean isPositional, boolean isEnclosed) {
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
            return new DrlxParseFail();
        }

        TypedExpression left = optLeft.get();
        List<String> usedDeclarationsOnLeft = hasBind ? new ArrayList<>( expressionTyperContext.getUsedDeclarations() ) : null;

        List<Expression> leftPrefixExpresssions = new ArrayList<>();
        if (isLogicalOperator(operator)) {
            leftPrefixExpresssions.addAll(expressionTyperContext.getPrefixExpresssions());
            expressionTyperContext.getPrefixExpresssions().clear();
        }

        List<Expression> rightPrefixExpresssions = new ArrayList<>();
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
            if (isLogicalOperator(operator)) {
                rightPrefixExpresssions.addAll(expressionTyperContext.getPrefixExpresssions());
                expressionTyperContext.getPrefixExpresssions().clear();
            }
        }

        boolean equalityExpr = operator == EQUALS || operator == NOT_EQUALS;

        CoercedExpression.CoercedExpressionResult coerced;
        try {
            coerced = new CoercedExpression(left, right, equalityExpr).coerce();
        } catch (CoercedExpression.CoercedExpressionException e) {
            return new DrlxParseFail(e.getInvalidExpressionErrorResult());
        }

        left = coerced.getCoercedLeft();
        right = getCoercedRightExpression( packageModel, coerced );

        Expression combo;

        boolean arithmeticExpr = asList(PLUS, MINUS, MULTIPLY, DIVIDE, REMAINDER).contains(operator);

        if (equalityExpr) {
            combo = getEqualityExpression( left, right, operator ).expression;
        } else if (arithmeticExpr && (left.isBigDecimal())) {
            ConstraintCompiler constraintCompiler = createConstraintCompiler(this.context, Optional.of(patternType));
            CompiledExpressionResult compiledExpressionResult = constraintCompiler.compileExpression(binaryExpr);

            combo = compiledExpressionResult.getExpression();
        } else {
            if (left.getExpression() == null || right.getExpression() == null) {
                return new DrlxParseFail(new ParseExpressionErrorResult(drlxExpr));
            }
            // This special comparisons
            SpecialComparisonResult specialComparisonResult = handleSpecialComparisonCases(expressionTyper, operator, left, right);
            combo = specialComparisonResult.expression;
            left = specialComparisonResult.coercedLeft;
            right = specialComparisonResult.coercedRight;
        }

        if (isLogicalOperator(operator)) {
            combo = combineExpressions( leftPrefixExpresssions, rightPrefixExpresssions, combo );
        } else {
            combo = combineExpressions( leftTypedExpressionResult, combo );
        }

        boolean isBetaConstraint = right.getExpression() != null && hasNonGlobalDeclaration( expressionTyperContext );
        if (isEnclosed) {
            combo = new EnclosedExpr( combo );
        }

        Index.ConstraintType constraintType = DrlxParseUtil.toConstraintType( operator );
        if ( isForallSelfJoinConstraint( left, right, constraintType ) ) {
            constraintType = Index.ConstraintType.FORALL_SELF_JOIN;
        }

        boolean requiresSplit = operator == BinaryExpr.Operator.AND && binaryExpr.getRight() instanceof HalfBinaryExpr && !isBetaConstraint;
        return new SingleDrlxParseSuccess(patternType, bindingId, combo, isBooleanOperator( operator ) ? boolean.class : left.getType())
                .setDecodeConstraintType( constraintType )
                .setUsedDeclarations( expressionTyperContext.getUsedDeclarations() )
                .setUsedDeclarationsOnLeft( usedDeclarationsOnLeft )
                .setUnification( constraint.isUnification() )
                .setReactOnProperties( expressionTyperContext.getReactOnProperties() )
                .setLeft( left )
                .setRight( right )
                .setBetaConstraint(isBetaConstraint)
                .setRequiresSplit( requiresSplit )
                .setIsPredicate(isPredicateBooleanExpression(binaryExpr));
    }

    private boolean isPredicateBooleanExpression(BinaryExpr expr) {
        BinaryExpr.Operator op = expr.getOperator();
        return op == AND || op == OR || op == EQUALS || op == NOT_EQUALS || op == LESS || op == GREATER || op == LESS_EQUALS || op == GREATER_EQUALS;
    }

    public static TypedExpression getCoercedRightExpression( PackageModel packageModel, CoercedExpression.CoercedExpressionResult coerced ) {
        if ( coerced.isRightAsStaticField()) {
            TypedExpression expr = coerced.getCoercedRight();
            String field = expr.getExpression().toString().replaceAll( "\\W", "_" );
            packageModel.addDateField( field, expr );
            return new TypedExpression( new NameExpr(field), expr.getType() );
        }
        return coerced.getCoercedRight();
    }

    private boolean hasNonGlobalDeclaration( ExpressionTyperContext expressionTyperContext ) {
        return expressionTyperContext.getUsedDeclarations().stream()
                .map( context::getDeclarationById )
                .anyMatch( optDecl -> optDecl.isPresent() && !optDecl.get().isGlobal() );
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

    private SpecialComparisonResult getEqualityExpression(TypedExpression left, TypedExpression right, BinaryExpr.Operator operator ) {
        if ((isAnyOperandBigDecimal(left, right) || isAnyOperandBigInteger(left, right)) && !isAnyOperandNullLiteral( left, right )) {
            return compareBigDecimal(operator, left, right);
        }

        boolean isLeftNumber = isNumber(left);
        boolean isRightNumber = isNumber(right);

        String equalsMethod = isLeftNumber && isRightNumber ?
                "org.drools.modelcompiler.util.EvaluationUtil.areNumbersNullSafeEquals" :
                "org.drools.modelcompiler.util.EvaluationUtil.areNullSafeEquals";

        Expression leftExpr = left.uncastExpression();
        Expression rightExpr = right.uncastExpression();

        if (isLeftNumber) {
            if ( isString( right ) ) {
                leftExpr = new BinaryExpr(new StringLiteralExpr(""), leftExpr, PLUS);
            }
        } else if ( isRightNumber && isString( left ) ) {
            rightExpr = new BinaryExpr(new StringLiteralExpr(""), rightExpr, PLUS);
        }

        MethodCallExpr methodCallExpr = new MethodCallExpr( null, equalsMethod );
        // Avoid casts, by using an helper method we leverage autoboxing and equals
        methodCallExpr.addArgument(leftExpr);
        methodCallExpr.addArgument(rightExpr);
        Expression expression = operator == BinaryExpr.Operator.EQUALS ? methodCallExpr : new UnaryExpr(methodCallExpr, UnaryExpr.Operator.LOGICAL_COMPLEMENT);
        return new SpecialComparisonResult(expression, left, right);
    }

    private static Boolean isString( TypedExpression right ) {
        return right.getBoxedType().map( String.class::isAssignableFrom ).orElse( false );
    }

    static Boolean isNumber(TypedExpression left) {
        return left.getBoxedType().map(ConstraintParser::isNumericType).orElse( false );
    }

    static Boolean isObject(TypedExpression te) {
        return te.getRawClass().equals(Object.class);
    }

    private SpecialComparisonResult handleSpecialComparisonCases(ExpressionTyper expressionTyper, BinaryExpr.Operator operator, TypedExpression left, TypedExpression right) {
        if (isLogicalOperator(operator)) {
            Expression rewrittenLeft = handleSpecialComparisonCases(expressionTyper, left);
            Expression rewrittenRight = handleSpecialComparisonCases(expressionTyper, right);
            if (rewrittenLeft != null && rewrittenRight != null) {
                return new SpecialComparisonResult(new EnclosedExpr(new BinaryExpr(rewrittenLeft, rewrittenRight, operator)), left, right);
            }
        }

        boolean comparison = isComparisonOperator(operator);
        if ((isAnyOperandBigDecimal(left, right) || isAnyOperandBigInteger(left, right)) && comparison) {
            return compareBigDecimal(operator, left, right);
        }

        if ( comparison ) {
            SpecialComparisonCase methodName = specialComparisonFactory(left, right);
            return methodName.createCompareMethod(operator);
        }

        return new SpecialComparisonResult(new BinaryExpr( left.getExpression(), right.getExpression(), operator ), left, right);
    }

    private Expression handleSpecialComparisonCases(ExpressionTyper expressionTyper, TypedExpression typedExpression) {
        if (typedExpression.getExpression() instanceof BinaryExpr) {
            BinaryExpr binaryExpr = (BinaryExpr) typedExpression.getExpression();
            if (!(binaryExpr.getLeft() instanceof MethodCallExpr) && !(binaryExpr.getRight() instanceof MethodCallExpr)) {
                Optional<TypedExpression> leftTyped = expressionTyper.toTypedExpression(binaryExpr.getLeft()).getTypedExpression();
                Optional<TypedExpression> rightTyped = expressionTyper.toTypedExpression(binaryExpr.getRight()).getTypedExpression();
                if (leftTyped.isPresent() && rightTyped.isPresent()) {
                    SpecialComparisonResult leftResult = handleSpecialComparisonCases(expressionTyper, binaryExpr.getOperator(), leftTyped.get(), rightTyped.get());
                    return leftResult.expression;
                }
            }
        }
        return null;
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

    private SpecialComparisonResult compareBigDecimal(BinaryExpr.Operator operator, TypedExpression left, TypedExpression right) {
        String methodName = "org.drools.modelcompiler.util.EvaluationUtil." + operatorToName(operator);
        MethodCallExpr compareMethod = new MethodCallExpr( null, methodName );
        compareMethod.addArgument( toBigDecimalExpression( left ) );
        compareMethod.addArgument( toBigDecimalExpression( right ) );
        return new SpecialComparisonResult(compareMethod, left, right);
    }

    // TODO luca this logic should be moved in Constraint compiler?
    private Expression toBigDecimalExpression( TypedExpression typedExpression) {
        MethodCallExpr toBigDecimalMethod = new MethodCallExpr( null, "org.drools.modelcompiler.util.EvaluationUtil.toBigDecimal" );
        Expression arg = typedExpression.getExpression();

        Optional<Class<?>> originalPatternType = typedExpression.getOriginalPatternType();

        ConstraintCompiler constraintCompiler = createConstraintCompiler(context, originalPatternType);

        CompiledExpressionResult compiledBlockResult = constraintCompiler.compileExpression(arg.toString());

        arg = compiledBlockResult.getExpression();

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

    private boolean isBooleanOperator( BinaryExpr.Operator op ) {
        return op == EQUALS || op == NOT_EQUALS || isComparisonOperator( op );
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
