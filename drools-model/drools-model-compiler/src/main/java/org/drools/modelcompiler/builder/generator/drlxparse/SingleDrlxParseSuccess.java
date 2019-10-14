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

import com.github.javaparser.ast.expr.BinaryExpr;
import com.github.javaparser.ast.expr.EnclosedExpr;
import com.github.javaparser.ast.expr.Expression;
import org.drools.model.Index;
import org.drools.modelcompiler.builder.generator.TypedExpression;

import static com.github.javaparser.ast.expr.BinaryExpr.Operator.AND;
import static com.github.javaparser.ast.expr.BinaryExpr.Operator.EQUALS;
import static com.github.javaparser.ast.expr.BinaryExpr.Operator.GREATER;
import static com.github.javaparser.ast.expr.BinaryExpr.Operator.GREATER_EQUALS;
import static com.github.javaparser.ast.expr.BinaryExpr.Operator.LESS;
import static com.github.javaparser.ast.expr.BinaryExpr.Operator.LESS_EQUALS;
import static com.github.javaparser.ast.expr.BinaryExpr.Operator.NOT_EQUALS;
import static com.github.javaparser.ast.expr.BinaryExpr.Operator.OR;
import static org.drools.modelcompiler.util.ClassUtil.getAccessibleProperties;
import static org.drools.modelcompiler.util.ClassUtil.toRawClass;

public class SingleDrlxParseSuccess extends AbstractDrlxParseSuccess {

    private final Class<?> patternType;
    private Expression expr;
    private final Type exprType;

    private final String exprId;
    private String patternBinding;
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
    private boolean isValidExpression;
    private boolean skipThisAsParam;
    private boolean isBetaNode;
    private boolean requiresSplit;
    private boolean unification;
    private boolean temporal;

    public SingleDrlxParseSuccess(Class<?> patternType, String exprId, String patternBinding, Expression expr, Type exprType) {
        this.patternType = patternType;
        this.exprId = exprId;
        this.patternBinding = patternBinding;
        this.expr = expr;
        this.exprType = exprType;
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

    public SingleDrlxParseSuccess setPatternBindingUnification(Boolean unification) {
        this.isPatternBindingUnification = unification;
        return this;
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

    public SingleDrlxParseSuccess setSkipThisAsParam(boolean skipThisAsParam ) {
        this.skipThisAsParam = skipThisAsParam;
        return this;
    }

    public String getExprId() {
        return exprId;
    }

    public String getPatternBinding() {
        return patternBinding;
    }

    public void setExpr(Expression expr) {
        this.expr = expr;
    }

    public void setPatternBinding(String patternBinding) {
        this.patternBinding = patternBinding;
    }

    public SingleDrlxParseSuccess setExprBinding(String exprBinding) {
        this.exprBinding = exprBinding;
        return this;
    }

    public boolean hasUnificationVariable() {
        return Optional.ofNullable(left).flatMap(TypedExpression::getUnificationVariable).isPresent() ||
                Optional.ofNullable(right).flatMap(TypedExpression::getUnificationVariable).isPresent();
    }

    public String getUnificationVariable() {
        return left.getUnificationVariable().orElseGet(
                () -> right.getUnificationVariable().orElseThrow(() -> new IllegalStateException("Right unification variable is not present!")));
    }

    public String getUnificationName() {
        return left.getUnificationName().orElseGet(
                () -> right.getUnificationName().orElseThrow(() -> new IllegalStateException("Right unification name is not present!")));
    }

    public Class<?> getUnificationVariableType() {
        return left.getUnificationVariable().isPresent() ? right.getRawClass() : left.getRawClass();
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

    public Class<?> getPatternType() {
        return patternType;
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

    public boolean isValidExpression( ) {
        if (this.isValidExpression) {
            return true;
        }
        if (expr != null) {
            if ( getExprType() == Boolean.class || getExprType() == boolean.class ) {
                return true;
            }
            if (expr instanceof EnclosedExpr ) {
                return isEnclosedExprValid( (( EnclosedExpr ) expr).getInner());
            }
            return right != null;
        }
        return false;
    }

    private boolean isEnclosedExprValid( Expression expr ) {
        if (expr instanceof BinaryExpr) {
            BinaryExpr.Operator op = (( BinaryExpr ) expr).getOperator();
            return op == AND || op == OR || op == EQUALS || op == NOT_EQUALS || op == LESS || op == GREATER || op == LESS_EQUALS || op == GREATER_EQUALS;
        }
        return false;
    }

    public boolean isSkipThisAsParam() {
        return skipThisAsParam;
    }

    public SingleDrlxParseSuccess setValidExpression(boolean validExpression ) {
        this.isValidExpression = validExpression;
        return this;
    }

    public SingleDrlxParseSuccess setBetaNode(boolean betaNode) {
        this.isBetaNode = betaNode;
        return this;
    }

    public boolean isBetaNode() {
        return isBetaNode;
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

        return new SingleDrlxParseSuccess(patternType, exprId, patternBinding, new BinaryExpr( expr, otherDrlx.expr, operator), exprType)
                .setDecodeConstraintType( Index.ConstraintType.UNKNOWN ).setUsedDeclarations( newUsedDeclarations ).setUsedDeclarationsOnLeft( newUsedDeclarationsOnLeft )
                .setUnification( this.isUnification() || otherDrlx.isUnification()).setReactOnProperties( newReactOnProperties ).setBetaNode(isBetaNode)
                .setLeft( new TypedExpression( this.expr, boolean.class ) )
                .setRight( new TypedExpression( otherDrlx.expr, boolean.class ) );
    }
}

