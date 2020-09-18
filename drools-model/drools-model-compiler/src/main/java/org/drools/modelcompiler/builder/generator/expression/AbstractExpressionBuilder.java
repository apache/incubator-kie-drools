/*
 * Copyright 2005 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.modelcompiler.builder.generator.expression;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.expr.BinaryExpr;
import com.github.javaparser.ast.expr.CastExpr;
import com.github.javaparser.ast.expr.EnclosedExpr;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.FieldAccessExpr;
import com.github.javaparser.ast.expr.LambdaExpr;
import com.github.javaparser.ast.expr.LiteralExpr;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.NullLiteralExpr;
import com.github.javaparser.ast.expr.ObjectCreationExpr;
import com.github.javaparser.ast.expr.SimpleName;
import com.github.javaparser.ast.expr.StringLiteralExpr;
import com.github.javaparser.ast.nodeTypes.NodeWithOptionalScope;
import com.github.javaparser.ast.stmt.ExpressionStmt;
import com.github.javaparser.ast.type.PrimitiveType;
import com.github.javaparser.ast.type.Type;
import org.drools.model.Index;
import org.drools.modelcompiler.builder.errors.InvalidExpressionErrorResult;
import org.drools.modelcompiler.builder.generator.DeclarationSpec;
import org.drools.modelcompiler.builder.generator.DrlxParseUtil;
import org.drools.modelcompiler.builder.generator.RuleContext;
import org.drools.modelcompiler.builder.generator.TypedExpression;
import org.drools.modelcompiler.builder.generator.drlxparse.DrlxParseSuccess;
import org.drools.modelcompiler.builder.generator.drlxparse.MultipleDrlxParseSuccess;
import org.drools.modelcompiler.builder.generator.drlxparse.SingleDrlxParseSuccess;
import org.drools.modelcompiler.util.ClassUtil;
import org.drools.mvel.parser.ast.expr.BigDecimalLiteralExpr;
import org.drools.mvel.parser.ast.expr.BigIntegerLiteralExpr;

import static java.util.Optional.ofNullable;

import static org.drools.model.bitmask.BitMaskUtil.isAccessibleProperties;
import static org.drools.modelcompiler.builder.generator.DrlxParseUtil.*;
import static org.drools.modelcompiler.builder.generator.DslMethodNames.INPUT_CALL;
import static org.drools.modelcompiler.util.ClassUtil.isAssignableFrom;
import static org.drools.modelcompiler.util.ClassUtil.toNonPrimitiveType;
import static org.drools.modelcompiler.util.ClassUtil.toRawClass;
import static org.drools.mvel.parser.printer.PrintUtil.printConstraint;

public abstract class AbstractExpressionBuilder {

    protected RuleContext context;

    protected AbstractExpressionBuilder( RuleContext context ) {
        this.context = context;
    }

    public void processExpression(DrlxParseSuccess drlxParseResult) {
        if (drlxParseResult instanceof SingleDrlxParseSuccess) {
            processExpression( (SingleDrlxParseSuccess) drlxParseResult );
        } else if (drlxParseResult instanceof MultipleDrlxParseSuccess) {
            processExpression( (MultipleDrlxParseSuccess) drlxParseResult );
        } else {
            throw new UnsupportedOperationException( "Unknown expression type: " + drlxParseResult.getClass().getName() );
        }
    }

    public abstract void processExpression(SingleDrlxParseSuccess drlxParseResult);

    protected MethodCallExpr createInputExpression(String identifier) {
        MethodCallExpr exprDSL = new MethodCallExpr(null, INPUT_CALL);
        exprDSL.addArgument( context.getVarExpr(identifier) );

        return exprDSL;
    }

    public void processExpression(MultipleDrlxParseSuccess drlxParseResult) {
        if ( drlxParseResult.isValidExpression() ) {
            Expression dslExpr = buildExpressionWithIndexing(drlxParseResult);
            context.addExpression(dslExpr);
        }
    }

    protected Expression buildUnificationExpression(SingleDrlxParseSuccess drlxParseResult) {
        MethodCallExpr exprDSL = buildBinding(drlxParseResult);
        context.addDeclaration(drlxParseResult.getUnificationVariable(), drlxParseResult.getUnificationVariableType(), drlxParseResult.getUnificationName());
        return exprDSL;
    }

    public abstract MethodCallExpr buildExpressionWithIndexing(DrlxParseSuccess drlxParseResult);

    public abstract MethodCallExpr buildBinding(SingleDrlxParseSuccess drlxParseResult);

    protected Expression getConstraintExpression(SingleDrlxParseSuccess drlxParseResult) {
        if (drlxParseResult.getExpr() instanceof EnclosedExpr) {
            return buildConstraintExpression(drlxParseResult, ((EnclosedExpr) drlxParseResult.getExpr()).getInner());
        } else {
            final TypedExpression left = drlxParseResult.getLeft();
            // Can we unify it? Sometimes expression is in the left sometimes in expression
            final Expression e;
            if(left != null) {
                e = findLeftmostExpression(left.getExpression());
            } else {
                e = drlxParseResult.getExpr();
            }
            return buildConstraintExpression(drlxParseResult, drlxParseResult.getUsedDeclarationsOnLeft(), e);
        }
    }

    private Expression findLeftmostExpression(Expression expression) {
        if (expression instanceof BinaryExpr) {
            BinaryExpr be = (BinaryExpr) expression;
            return findLeftmostExpression(be.getLeft());
        }
        if (expression instanceof CastExpr) {
            CastExpr ce = (CastExpr) expression;
            return findLeftmostExpression(ce.getExpression());
        } else if (expression instanceof MethodCallExpr) {
            MethodCallExpr methodCallExpr = expression.asMethodCallExpr();
            if(!methodCallExpr.getArguments().isEmpty()) {
                return findLeftmostExpression(methodCallExpr.getArguments().iterator().next());
            } else {
                return expression;
            }
        } else if (expression instanceof FieldAccessExpr) {
            return expression;
        } else {
            context.addCompilationError(new InvalidExpressionErrorResult("Unable to Analyse Expression" + printConstraint(expression)));
            return expression;
        }
    }

    protected Expression buildConstraintExpression(SingleDrlxParseSuccess drlxParseResult, Expression expr ) {
        return buildConstraintExpression(drlxParseResult, drlxParseResult.getUsedDeclarations(), expr );
    }

    protected Expression buildConstraintExpression(SingleDrlxParseSuccess drlxParseResult, Collection<String> usedDeclarations, Expression expr ) {
        return drlxParseResult.isStatic() ? expr :
                generateLambdaWithoutParameters(usedDeclarations,
                                                expr,
                                                drlxParseResult.isSkipThisAsParam(), ofNullable(drlxParseResult.getPatternType()), context);
    }

    boolean shouldCreateIndex(SingleDrlxParseSuccess drlxParseResult ) {
        if ( drlxParseResult.getDecodeConstraintType() == Index.ConstraintType.FORALL_SELF_JOIN ) {
            return true;
        }

        TypedExpression left = drlxParseResult.getLeft();

        if(!shouldIndexConstraintWithRightScopePatternBinding(drlxParseResult)) {
            return false;
        }

        Collection<String> usedDeclarations = drlxParseResult.getUsedDeclarations();

        return left != null && left.getFieldName() != null &&
                drlxParseResult.getDecodeConstraintType() != null &&
                !isThisExpression( left.getExpression() ) &&
                ( isAlphaIndex( usedDeclarations ) || isBetaIndex( usedDeclarations, drlxParseResult.getRight() ) );
    }

    // See PatternBuilder:1198 (buildConstraintForPattern) Pattern are indexed only when the root of the right part
    // (i.e. $p in address.street == $p.name) is a Pattern binding.
    // See also IndexingTest and ExistentialTest
    protected boolean shouldIndexConstraintWithRightScopePatternBinding(SingleDrlxParseSuccess result) {
        TypedExpression right = result.getRight();

        if(right != null && right.getExpression() != null && right.getExpression() instanceof NodeWithOptionalScope) {
            NodeWithOptionalScope<?> e = (NodeWithOptionalScope<?>) (right.getExpression());
            return e.getScope()
                    .map(Object::toString)
                    .filter(context::isPatternBinding)
                    .isPresent();
        }

        return true;
    }

    boolean isAlphaIndex( Collection<String> usedDeclarations ) {
        return usedDeclarations.isEmpty();
    }

    private boolean isBetaIndex( Collection<String> usedDeclarations, TypedExpression right ) {
        // a Beta node should NOT create the index when the "right" is not just-a-symbol, the "right" is not a declaration referenced by name
        return usedDeclarations.size() == 1 && context.getDeclarationById( getExpressionSymbolForBetaIndex( right.getExpression() ) ).isPresent();
    }

    private static String getExpressionSymbolForBetaIndex(Expression expr) {
        Expression scope;
        if (expr instanceof MethodCallExpr) {
            Optional<Expression> scopeExpression = (( MethodCallExpr ) expr).getScope();
            scope = scopeExpression.orElse(expr);
        } else if (expr instanceof FieldAccessExpr ) {
            scope = (( FieldAccessExpr ) expr).getScope();
        } else {
            scope = expr;
        }
        return scope instanceof NameExpr ? (( NameExpr ) scope).getNameAsString() : null;
    }

    public static AbstractExpressionBuilder getExpressionBuilder(RuleContext context) {
        return context.isPatternDSL() ? new PatternExpressionBuilder( context ) : new FlowExpressionBuilder( context );
    }

    protected Expression narrowExpressionToType( TypedExpression right, java.lang.reflect.Type leftType ) {
        Expression expression = right.getExpression();

        if (expression instanceof NullLiteralExpr) {
            return expression;
        }

        if (leftType.equals(Double.class)) {
            return new CastExpr( PrimitiveType.doubleType(), expression );
        }

        if (leftType.equals(Long.class)) {
            if (right.getType().equals( Double.class ) || right.getType().equals( double.class )) {
                return new MethodCallExpr( expression, "longValue" );
            } else {
                return new CastExpr( PrimitiveType.longType(), expression );
            }
        }

        if (expression instanceof LiteralExpr) {
            if (expression instanceof BigDecimalLiteralExpr) {
                return toNewExpr(BigDecimal.class, new StringLiteralExpr(((BigDecimalLiteralExpr) expression).asBigDecimal().toString()));
            }
            if (expression instanceof BigIntegerLiteralExpr) {
                return toNewExpr(toRawClass(leftType), new StringLiteralExpr(((BigIntegerLiteralExpr) expression).asBigInteger().toString()));
            }
            if (leftType.equals(BigDecimal.class)) {
                final BigDecimal bigDecimal = new BigDecimal( expression.toString() );
                return toNewExpr(BigDecimal.class, new StringLiteralExpr( bigDecimal.toString() ) );
            }
            if (leftType.equals(BigInteger.class)) {
                final BigInteger bigInteger = new BigDecimal(expression.toString()).toBigInteger();
                return toNewExpr(BigInteger.class, new StringLiteralExpr(bigInteger.toString()));
            }

        }

        if (expression instanceof NameExpr) {
            if (leftType.equals(BigDecimal.class) && !right.getType().equals(BigDecimal.class)) {
                return toNewExpr(BigDecimal.class, expression);
            }
            if (leftType.equals(BigInteger.class) && !right.getType().equals(BigInteger.class)) {
                return toNewExpr(BigInteger.class, expression);
            }
        }

        if ( !isAssignableFrom( leftType, right.getType() ) && isAssignableFrom( right.getType(), leftType ) ) {
            return new CastExpr( toClassOrInterfaceType(toNonPrimitiveType(toRawClass(leftType))), expression );
        }

        return expression;
    }

    private static Expression toNewExpr(Class<?> clazz, Expression initExpression) {
        return new ObjectCreationExpr(null, toClassOrInterfaceType(clazz), NodeList.nodeList(initExpression));
    }

    protected void addIndexedByDeclaration(TypedExpression left,
                                           TypedExpression right,
                                           boolean leftContainsThis,
                                           MethodCallExpr indexedByDSL,
                                           Collection<String> usedDeclarations,
                                           java.lang.reflect.Type leftType,
                                           SingleDrlxParseSuccess drlxParseResult) {
        LambdaExpr indexedByRightOperandExtractor = new LambdaExpr();
        final TypedExpression expression;
        String declarationName = usedDeclarations.iterator().next();
        Type type;
        if (leftContainsThis) {
            expression = right;
        } else {
            expression = left;
        }

        DeclarationSpec declarationById = context.getDeclarationByIdWithException(declarationName);
        type = declarationById.getBoxedType();
        indexedByRightOperandExtractor.addParameter(new Parameter(type, declarationName));
        indexedByRightOperandExtractor.setEnclosingParameters(true);
        final Expression narrowed = narrowExpressionToType(expression, leftType);
        indexedByRightOperandExtractor.setBody(new ExpressionStmt(narrowed));
        indexedByDSL.addArgument(indexedByRightOperandExtractor);
    }

    protected Class<?> getIndexType(TypedExpression left, TypedExpression right) {
        Optional<Class<?>> leftType = ofNullable(left.getType()).map(ClassUtil::toRawClass).map(ClassUtil::toNonPrimitiveType);
        Optional<Class<?>> rightType = ofNullable(right.getType()).map(ClassUtil::toRawClass).map(ClassUtil::toNonPrimitiveType);

        // Use Number.class if they're both Numbers but different in order to use best possible type in the index
        Optional<Class<?>> numberType = leftType.flatMap(l -> rightType.map(r -> {
            if ((Number.class.isAssignableFrom(l) && Number.class.isAssignableFrom(r)) && !l.equals(r)) {
                return Number.class;
            } else {
                return l;
            }
        }));

        return numberType.orElseGet(() -> Stream.of(left, right).map(TypedExpression::getType)
                .filter(Objects::nonNull)
                .map(ClassUtil::toRawClass)
                .findFirst().orElseThrow(() -> new IllegalArgumentException("Cannot find index from: " + left.toString() + ", " + right.toString() + "!")));
    }

    String getIndexIdArgument(SingleDrlxParseSuccess drlxParseResult, TypedExpression left) {
        return isAccessibleProperties( drlxParseResult.getPatternType(), left.getFieldName() ) ?
                context.getPackageModel().getDomainClassName( drlxParseResult.getPatternType() ) + ".getPropertyIndex(\"" + left.getFieldName() + "\")" :
                "-1";
    }

    boolean shouldBuildReactOn(SingleDrlxParseSuccess drlxParseResult) {
        return !drlxParseResult.isTemporal() && !drlxParseResult.getReactOnProperties().isEmpty() && context.isPropertyReactive( drlxParseResult.getPatternType() );
    }

    protected Expression generateLambdaForTemporalConstraint(TypedExpression typedExpression, Class<?> patternType) {
        Expression expr = typedExpression.getExpression();
        Collection<String> usedDeclarations = DrlxParseUtil.collectUsedDeclarationsInExpression(expr);
        boolean containsThis = usedDeclarations.contains(THIS_PLACEHOLDER);
        if (containsThis) {
            usedDeclarations.remove(THIS_PLACEHOLDER);
        }
        Expression generatedExpr = generateLambdaWithoutParameters(usedDeclarations, expr, !containsThis, Optional.ofNullable(patternType), context);
        if (generatedExpr instanceof LambdaExpr) {
            context.getPackageModel().getLambdaReturnTypes().put((LambdaExpr) generatedExpr, typedExpression.getType());
        }
        return generatedExpr;
    }

    protected MethodCallExpr buildTemporalExpression(SingleDrlxParseSuccess drlxParseResult, MethodCallExpr exprDSL) {
        boolean thisOnRight = isThisOnRight(drlxParseResult);

        // function for "this" should be added first
        if (thisOnRight) {
            if (drlxParseResult.getRight() != null && !drlxParseResult.getRight().getExpression().isNameExpr()) {
                exprDSL.addArgument(generateLambdaForTemporalConstraint(drlxParseResult.getRight(), drlxParseResult.getPatternType()));
            }
        } else {
            if (drlxParseResult.getLeft() != null && !drlxParseResult.getLeft().getExpression().isNameExpr()) {
                exprDSL.addArgument(generateLambdaForTemporalConstraint(drlxParseResult.getLeft(), drlxParseResult.getPatternType()));
            }
        }

        final List<String> usedDeclarationsWithUnification = new ArrayList<>();
        usedDeclarationsWithUnification.addAll(drlxParseResult.getUsedDeclarations());

        usedDeclarationsWithUnification.stream()
                .filter( s -> !(drlxParseResult.isSkipThisAsParam() && s.equals( drlxParseResult.getPatternBinding() ) ) )
                .map(context::getVarExpr)
                .forEach(exprDSL::addArgument);

        if (drlxParseResult.getRightLiteral() != null) {
            exprDSL.addArgument( "" + drlxParseResult.getRightLiteral() );
        } else {
            // function for variable
            if (thisOnRight) {
                if (drlxParseResult.getLeft() != null && !drlxParseResult.getLeft().getExpression().isNameExpr()) {
                    exprDSL.addArgument(generateLambdaForTemporalConstraint(drlxParseResult.getLeft(), drlxParseResult.getPatternType()));
                }
            } else {
                if (drlxParseResult.getRight() != null && !drlxParseResult.getRight().getExpression().isNameExpr()) {
                    exprDSL.addArgument(generateLambdaForTemporalConstraint(drlxParseResult.getRight(), drlxParseResult.getPatternType()));
                }
            }
        }

        if (thisOnRight) {
            exprDSL.addArgument(buildConstraintExpression(drlxParseResult, new MethodCallExpr(drlxParseResult.getExpr(), "thisOnRight")));
        } else {
            exprDSL.addArgument(buildConstraintExpression(drlxParseResult, drlxParseResult.getExpr()));
        }
        return exprDSL;
    }

    protected boolean isThisOnRight(SingleDrlxParseSuccess drlxParseResult) {
        if (drlxParseResult.getRight() != null) {
            if (drlxParseResult.getRight().getExpression().isNameExpr()) {
                NameExpr name = drlxParseResult.getRight().getExpression().asNameExpr();
                if (name.equals(new NameExpr(THIS_PLACEHOLDER))) {
                    return true;
                }
            } else {
                return containsThis(drlxParseResult.getRight());
            }
        }
        return false;
    }

    protected boolean containsThis(TypedExpression typedExpression) {
        Expression expr = typedExpression.getExpression();
        Optional<String> opt = expr.findAll(NameExpr.class)
                .stream()
                .map(NameExpr::getName)
                .map(SimpleName::getIdentifier)
                .findFirst(); // just first one
        if (!opt.isPresent()) {
            return false;
        }
        return opt.get().equals(THIS_PLACEHOLDER);
    }
}