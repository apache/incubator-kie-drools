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
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
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

import static java.util.Optional.ofNullable;
import static org.drools.model.impl.VariableImpl.GENERATED_VARIABLE_PREFIX;
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

    public SingleDrlxParseSuccess(Class<?> patternType, String patternBinding, Expression expr, Type exprType) {
        this.patternType = patternType;
        this.patternBinding = patternBinding;
        this.expr = expr;
        this.exprType = exprType;
    }

    public SingleDrlxParseSuccess(SingleDrlxParseSuccess drlx) {
        // Shallow copy constructor
        this(drlx.getPatternType(), drlx.getPatternBinding(), drlx.getExpr(), drlx.getExprType());
        this.originalDrlConstraint = drlx.getOriginalDrlConstraint();
        this.accumulateBinding = drlx.getAccumulateBinding();
        this.isPatternBindingUnification = drlx.isPatternBindingUnification();
        this.exprBinding = drlx.getExprBinding();
        this.decodeConstraintType = drlx.getDecodeConstraintType();
        this.usedDeclarations = drlx.getUsedDeclarations();
        this.usedDeclarationsOnLeft = drlx.getUsedDeclarationsOnLeft();
        this.reactOnProperties = drlx.getReactOnProperties();
        this.left = drlx.getLeft();
        this.right = drlx.getRight();
        this.rightLiteral = drlx.getRightLiteral();
        this.isStatic = drlx.isStatic();
        this.isPredicate = drlx.isPredicate();
        this.skipThisAsParam = drlx.isSkipThisAsParam();
        this.betaConstraint = drlx.isBetaConstraint();
        this.requiresSplit = drlx.isRequiresSplit();
        this.unification = drlx.isUnification();
        this.temporal = drlx.isTemporal();
        this.combined = drlx.isCombined();
        this.implicitCastExpression = drlx.getImplicitCastExpression();

        this.watchedProperties = drlx.getWatchedProperties();
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
        this.isPatternBindingUnification = unification;
    }

    public SingleDrlxParseSuccess addReactOnProperty(String reactOnProperty ) {
        if ( patternType != null && getAccessibleProperties(patternType).contains( reactOnProperty ) ) {
            if ( reactOnProperties.isEmpty() ) {
                reactOnProperties = new HashSet<>();
            }
            this.reactOnProperties.add( reactOnProperty );
        }
        return this;
    }

    public SingleDrlxParseSuccess setLeft(TypedExpression left ) {
        this.left = left;
        return this;
    }

    public SingleDrlxParseSuccess setRight(TypedExpression right ) {
        this.right = right;
        return this;
    }

    public SingleDrlxParseSuccess setRightLiteral(Object rightLiteral ) {
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

    public boolean hasGeneratedPatternBinding() {
        return patternBinding != null && patternBinding.startsWith( GENERATED_VARIABLE_PREFIX );
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

    public Expression getExpr() {
        return expr;
    }

    public String getExprBinding() {
        return exprBinding;
    }

    public Type getExprType() {
        return exprType;
    }

    public Class<?> getExprRawClass() {
        return toRawClass( exprType );
    }

    public Type getLeftExprType() {
        return left != null ? left.getType() : getExprType();
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

    public Object getRightLiteral() {
        return rightLiteral;
    }

    public boolean isStatic() {
        return isStatic;
    }

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
        return this.isPredicate;
    }

    public SingleDrlxParseSuccess setIsPredicate(boolean predicate) {
        this.isPredicate = predicate;
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
        newUsedDeclarations.addAll( this.usedDeclarations );
        newUsedDeclarations.addAll( otherDrlx.usedDeclarations );

        Collection<String> newUsedDeclarationsOnLeft = null;
        if (this.usedDeclarationsOnLeft != null && otherDrlx.usedDeclarationsOnLeft != null) {
            newUsedDeclarationsOnLeft = new LinkedHashSet<>();
            newUsedDeclarationsOnLeft.addAll( this.usedDeclarationsOnLeft );
            newUsedDeclarationsOnLeft.addAll( otherDrlx.usedDeclarationsOnLeft );
        }

        Set<String> newReactOnProperties = new HashSet<>();
        newReactOnProperties.addAll( this.reactOnProperties );
        newReactOnProperties.addAll( otherDrlx.reactOnProperties );

        return new SingleDrlxParseSuccess(patternType, patternBinding, new EnclosedExpr( new BinaryExpr(expr, otherDrlx.expr, operator) ), exprType)
                .setDecodeConstraintType(Index.ConstraintType.UNKNOWN)
                .setUsedDeclarations(newUsedDeclarations)
                .setUsedDeclarationsOnLeft(newUsedDeclarationsOnLeft)
                .setUnification(this.isUnification() || otherDrlx.isUnification())
                .setCombined( true )
                .setReactOnProperties(newReactOnProperties).setBetaConstraint( betaConstraint )
                .setLeft(new TypedExpression(this.expr, left != null ? left.getType() : boolean.class))
                .setRight(new TypedExpression(otherDrlx.expr, right != null ? right.getType() : boolean.class))
                .setIsPredicate(this.isPredicate && otherDrlx.isPredicate)
                .setExprBinding(this.exprBinding); // only left exprBinding
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
    public String toString() {
        return originalDrlConstraint;
    }
}

