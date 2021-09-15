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

package org.drools.modelcompiler.builder.generator.drlxparse;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;

import com.github.javaparser.ast.expr.BinaryExpr;
import com.github.javaparser.ast.expr.EnclosedExpr;
import com.github.javaparser.ast.expr.Expression;
import org.drools.model.Index;
import org.drools.modelcompiler.builder.generator.DRLIdGenerator;
import org.drools.modelcompiler.builder.generator.TypedExpression;
import org.drools.modelcompiler.builder.generator.UnificationTypedExpression;
import org.drools.modelcompiler.util.StreamUtils;

import static java.util.Optional.ofNullable;
import static org.drools.modelcompiler.builder.generator.DrlxParseUtil.toClassOrInterfaceType;
import static org.drools.modelcompiler.util.ClassUtil.getAccessibleProperties;
import static org.drools.modelcompiler.util.ClassUtil.toNonPrimitiveType;
import static org.drools.modelcompiler.util.ClassUtil.toRawClass;

public class SingleDrlxParseSuccess extends AbstractDrlxParseSuccess {

    private final Class<?> patternType;
    private Expression expr;
    private final Type exprType;

    private String originalDrlConstraint;
    private final String patternBinding;
    private String accumulateBinding;
    private boolean isPatternBindingUnification = false;

    private String exprBinding;

    private Index.ConstraintType decodeConstraintType;
    private Collection<String> usedDeclarations = new LinkedHashSet<>();
    private Collection<String> usedDeclarationsOnLeft;
    private Set<String> reactOnProperties = Collections.emptySet();

    private TypedExpression left;
    private TypedExpression right;
    private TypedExpression boundExpr;

    private Object rightLiteral;
    private boolean isStatic;
    private boolean isPredicate = false;
    private boolean skipThisAsParam;
    private boolean betaConstraint;
    private boolean requiresSplit;
    private boolean unification;
    private boolean temporal;
    private boolean combined;

    private Optional<Expression> implicitCastExpression = Optional.empty();
    private List<Expression> nullSafeExpressions = new ArrayList<>();

    public SingleDrlxParseSuccess(Class<?> patternType, String patternBinding, Expression expr, Type exprType) {
        this.patternType = patternType;
        this.patternBinding = patternBinding;
        this.expr = expr;
        this.exprType = exprType;
    }

    public SingleDrlxParseSuccess(SingleDrlxParseSuccess drlx) {
        // Shallow copy constructor
        this(drlx.getPatternType(), drlx.getPatternBinding(), drlx.getExpr(), drlx.getExprType());
        originalDrlConstraint = drlx.getOriginalDrlConstraint();
        accumulateBinding = drlx.getAccumulateBinding();
        isPatternBindingUnification = drlx.isPatternBindingUnification();
        exprBinding = drlx.getExprBinding();
        decodeConstraintType = drlx.getDecodeConstraintType();
        usedDeclarations = drlx.getUsedDeclarations();
        usedDeclarationsOnLeft = drlx.getUsedDeclarationsOnLeft();
        reactOnProperties = drlx.getReactOnProperties();
        left = drlx.getLeft();
        right = drlx.getRight();
        boundExpr = drlx.getBoundExpr();
        rightLiteral = drlx.getRightLiteral();
        isStatic = drlx.isStatic();
        isPredicate = drlx.isPredicate();
        skipThisAsParam = drlx.isSkipThisAsParam();
        betaConstraint = drlx.isBetaConstraint();
        requiresSplit = drlx.isRequiresSplit();
        unification = drlx.isUnification();
        temporal = drlx.isTemporal();
        combined = drlx.isCombined();
        implicitCastExpression = drlx.getImplicitCastExpression();
        nullSafeExpressions = drlx.getNullSafeExpressions();

        watchedProperties = drlx.getWatchedProperties();
    }

    public SingleDrlxParseSuccess setDecodeConstraintType( Index.ConstraintType decodeConstraintType ) {
        this.decodeConstraintType = decodeConstraintType;
        return this;
    }

    public SingleDrlxParseSuccess setUsedDeclarationsOnLeft( Collection<String> usedDeclarationsOnLeft ) {
        this.usedDeclarationsOnLeft = usedDeclarationsOnLeft;
        return this;
    }

    public Collection<String> getUsedDeclarationsOnLeft() {
        return usedDeclarationsOnLeft != null ? usedDeclarationsOnLeft : usedDeclarations;
    }

    public SingleDrlxParseSuccess setUsedDeclarations( Collection<String> usedDeclarations ) {
        this.usedDeclarations = new LinkedHashSet<>(usedDeclarations);
        skipThisAsParam = usedDeclarations.contains( patternBinding );
        return this;
    }

    public SingleDrlxParseSuccess setReactOnProperties(Set<String> reactOnProperties ) {
        if ( patternType != null ) {
            reactOnProperties.retainAll( getAccessibleProperties( patternType ) );
            this.reactOnProperties = reactOnProperties;
        }
        return this;
    }

    public void setPatternBindingUnification(Boolean unification) {
        isPatternBindingUnification = unification;
    }

    public SingleDrlxParseSuccess addReactOnProperty(String reactOnProperty) {
        if ( patternType != null && getAccessibleProperties(patternType).contains( reactOnProperty ) ) {
            if ( reactOnProperties.isEmpty() ) {
                reactOnProperties = new HashSet<>();
            }
            reactOnProperties.add( reactOnProperty );
        }
        return this;
    }

    public SingleDrlxParseSuccess setLeft(TypedExpression left) {
        this.left = left;
        return this;
    }

    public SingleDrlxParseSuccess setRight(TypedExpression right) {
        this.right = right;
        return this;
    }

    public SingleDrlxParseSuccess setBoundExpr(TypedExpression boundExpr) {
        this.boundExpr = boundExpr;
        return this;
    }

    public SingleDrlxParseSuccess setRightLiteral(Object rightLiteral) {
        this.rightLiteral = rightLiteral;
        return this;
    }

    public SingleDrlxParseSuccess setStatic(boolean isStatic ) {
        this.isStatic = isStatic;
        return this;
    }

    public SingleDrlxParseSuccess setTemporal(boolean temporal) {
        this.temporal = temporal;
        return this;
    }

    public SingleDrlxParseSuccess setCombined(boolean combined) {
        this.combined = combined;
        return this;
    }

    public SingleDrlxParseSuccess setSkipThisAsParam(boolean skipThisAsParam ) {
        this.skipThisAsParam = skipThisAsParam;
        return this;
    }

    @Override
    public String getExprId(DRLIdGenerator exprIdGenerator) {
        String constraint;
        if(asUnificationTypedExpression(left).isPresent() || asUnificationTypedExpression(right).isPresent()) {
            constraint = originalDrlConstraint;
        } else if (expr != null) {
            constraint = expr.toString();
        } else {
            constraint = left.toString();
        }

        return exprIdGenerator.getExprId(patternType, constraint);
    }

    public Optional<UnificationTypedExpression> asUnificationTypedExpression(TypedExpression expression) {
        if(expression instanceof UnificationTypedExpression) {
            return Optional.of((UnificationTypedExpression) expression);
        }
        return Optional.empty();
    }

    public String getPatternBinding() {
        return patternBinding;
    }

    public void setExpr(Expression expr) {
        this.expr = expr;
    }

    public String getAccumulateBinding() {
        return accumulateBinding;
    }

    public void setAccumulateBinding( String accumulateBinding ) {
        this.accumulateBinding = accumulateBinding;
    }

    public SingleDrlxParseSuccess setExprBinding( String exprBinding) {
        this.exprBinding = exprBinding;
        return this;
    }

    public boolean hasUnificationVariable() {
        return ofNullable(left).flatMap(this::asUnificationTypedExpression).flatMap(UnificationTypedExpression::getUnificationVariable).isPresent() ||
                ofNullable(right).flatMap(this::asUnificationTypedExpression).flatMap(UnificationTypedExpression::getUnificationVariable).isPresent();
    }

    public String getUnificationVariable() {
        return leftOrRightAsUnificationTypedExpression(UnificationTypedExpression::getUnificationVariable);
    }

    public String getUnificationName() {
        return leftOrRightAsUnificationTypedExpression(UnificationTypedExpression::getUnificationName);
    }

    public Class<?> getUnificationVariableType() {
        // Do not use the type of unificationTypeExpression
        return asUnificationTypedExpression(left).isPresent() ? right.getRawClass() : left.getRawClass();
    }

    private <T> T leftOrRightAsUnificationTypedExpression(Function<? super UnificationTypedExpression, Optional<T>> mapper) {
        return asUnificationTypedExpression(left).flatMap(mapper)
                .map(Optional::of)
                .orElseGet(() -> asUnificationTypedExpression(right).flatMap(mapper))
                .orElseThrow(() -> new IllegalStateException("Left or Right unification not present!"));
    }

    @Override
    public Expression getExpr() {
        return expr;
    }

    @Override
    public String getExprBinding() {
        return exprBinding;
    }

    public Type getExprType() {
        return exprType;
    }

    public Class<?> getExprRawClass() {
        return toRawClass( exprType );
    }

    public Class<?> getLeftExprRawClass() {
        return left != null ? left.getRawClass() : getExprRawClass();
    }

    public Class<?> getPatternType() {
        return patternType;
    }

    public com.github.javaparser.ast.type.Type getPatternJPType() {
        return toClassOrInterfaceType(toNonPrimitiveType(patternType));
    }

    public boolean isPatternBindingUnification() {
        return isPatternBindingUnification;
    }

    public Index.ConstraintType getDecodeConstraintType() {
        return decodeConstraintType;
    }

    public Collection<String> getUsedDeclarations() {
        return usedDeclarations;
    }

    public Set<String> getReactOnProperties() {
        return reactOnProperties;
    }

    public TypedExpression getLeft() {
        return left;
    }

    public TypedExpression getRight() {
        return right;
    }

    public TypedExpression getBoundExpr() {
        return boundExpr != null ? boundExpr : left;
    }

    public Object getRightLiteral() {
        return rightLiteral;
    }

    public boolean isStatic() {
        return isStatic;
    }

    @Override
    public boolean isTemporal() {
        return temporal;
    }

    public boolean isCombined() {
        return combined;
    }

    // Used to decide whether we should generate an .expr or a .bind
    // true:  .expr
    // false: .bind
    @Override
    public boolean isPredicate() {
        return isPredicate;
    }

    public SingleDrlxParseSuccess setIsPredicate(boolean predicate) {
        isPredicate = predicate;
        return this;
    }

    public boolean isSkipThisAsParam() {
        return skipThisAsParam;
    }

    public SingleDrlxParseSuccess setBetaConstraint( boolean betaConstraint ) {
        this.betaConstraint = betaConstraint;
        return this;
    }

    public boolean isBetaConstraint() {
        return betaConstraint;
    }

    public SingleDrlxParseSuccess setRequiresSplit(boolean requiresSplit) {
        this.requiresSplit = requiresSplit;
        return this;
    }

    @Override
    public boolean isRequiresSplit() {
        return requiresSplit;
    }

    public SingleDrlxParseSuccess setUnification(boolean unification) {
        this.unification = unification;
        return this;
    }

    public boolean isUnification() {
        return unification;
    }

    @Override
    public DrlxParseResult combineWith( DrlxParseResult other, BinaryExpr.Operator operator ) {
        if (!other.isSuccess()) {
            return other;
        }

        SingleDrlxParseSuccess otherDrlx = ( SingleDrlxParseSuccess ) other;

        Collection<String> newUsedDeclarations = new LinkedHashSet<>();
        newUsedDeclarations.addAll( usedDeclarations );
        newUsedDeclarations.addAll( otherDrlx.usedDeclarations );

        Collection<String> newUsedDeclarationsOnLeft = null;
        if (usedDeclarationsOnLeft != null && otherDrlx.usedDeclarationsOnLeft != null) {
            newUsedDeclarationsOnLeft = new LinkedHashSet<>();
            newUsedDeclarationsOnLeft.addAll( usedDeclarationsOnLeft );
            newUsedDeclarationsOnLeft.addAll( otherDrlx.usedDeclarationsOnLeft );
        }

        Set<String> newReactOnProperties = new HashSet<>();
        newReactOnProperties.addAll( reactOnProperties );
        newReactOnProperties.addAll( otherDrlx.reactOnProperties );

        List<Expression> newNullSafeExpressions = new ArrayList<>();
        if (operator == BinaryExpr.Operator.OR) {
            // NullSafeExpressions are combined here because the order is complex
            expr = combinePredicatesWithAnd(expr, nullSafeExpressions);
            otherDrlx.expr = combinePredicatesWithAnd(otherDrlx.expr, otherDrlx.nullSafeExpressions);
            // Also combine implicitCast earlier than null-check
            expr = combinePredicatesWithAnd(expr, StreamUtils.optionalToList(implicitCastExpression));
            otherDrlx.expr = combinePredicatesWithAnd(otherDrlx.expr, StreamUtils.optionalToList(otherDrlx.implicitCastExpression));
        } else {
            // NullSafeExpressions will be added by PatternDSL.addNullSafeExpr
            newNullSafeExpressions.addAll(nullSafeExpressions);
            newNullSafeExpressions.addAll(otherDrlx.nullSafeExpressions);
        }

        return new SingleDrlxParseSuccess(patternType, patternBinding, new EnclosedExpr( new BinaryExpr(expr, otherDrlx.expr, operator) ), exprType)
                .setDecodeConstraintType(Index.ConstraintType.UNKNOWN)
                .setUsedDeclarations(newUsedDeclarations)
                .setUsedDeclarationsOnLeft(newUsedDeclarationsOnLeft)
                .setUnification(isUnification() || otherDrlx.isUnification())
                .setCombined( true )
                .setReactOnProperties(newReactOnProperties).setBetaConstraint( betaConstraint )
                .setLeft(new TypedExpression(expr, left != null ? left.getType() : boolean.class))
                .setRight(new TypedExpression(otherDrlx.expr, right != null ? right.getType() : boolean.class))
                .setBoundExpr(left)
                .setIsPredicate(isPredicate && otherDrlx.isPredicate)
                .setNullSafeExpressions(newNullSafeExpressions)
                .setExprBinding(exprBinding); // only left exprBinding
    }

    private Expression combinePredicatesWithAnd(Expression mainPredicate, List<Expression> prefixPredicates) {
        Expression combo = mainPredicate;
        for (Expression e : prefixPredicates) {
            combo = new BinaryExpr( e, combo, BinaryExpr.Operator.AND );
        }
        return combo;
    }

    @Override
    public DrlxParseResult setOriginalDrlConstraint(String originalDrlConstraint) {
        this.originalDrlConstraint = originalDrlConstraint;
        return this;
    }

    @Override
    public String getOriginalDrlConstraint() {
        return originalDrlConstraint;
    }

    public SingleDrlxParseSuccess setImplicitCastExpression(Optional<Expression> implicitCastExpression) {
        this.implicitCastExpression = implicitCastExpression;
        return this;
    }

    @Override
    public Optional<Expression> getImplicitCastExpression() {
        return implicitCastExpression;
    }

    @Override
    public List<Expression> getNullSafeExpressions() {
        return nullSafeExpressions;
    }

    public SingleDrlxParseSuccess setNullSafeExpressions(List<Expression> nullSafeExpressions) {
        this.nullSafeExpressions = nullSafeExpressions;
        return this;
    }

    @Override
    public String toString() {
        return originalDrlConstraint;
    }
}

