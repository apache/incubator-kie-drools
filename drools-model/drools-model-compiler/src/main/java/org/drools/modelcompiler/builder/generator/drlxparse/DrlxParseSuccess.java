package org.drools.modelcompiler.builder.generator.drlxparse;

import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Optional;
import java.util.Set;

import org.drools.core.util.index.IndexUtil;
import org.drools.javaparser.ast.expr.BinaryExpr;
import org.drools.javaparser.ast.expr.EnclosedExpr;
import org.drools.javaparser.ast.expr.Expression;
import org.drools.modelcompiler.builder.generator.TypedExpression;

import static org.drools.javaparser.ast.expr.BinaryExpr.Operator.AND;
import static org.drools.javaparser.ast.expr.BinaryExpr.Operator.EQUALS;
import static org.drools.javaparser.ast.expr.BinaryExpr.Operator.GREATER;
import static org.drools.javaparser.ast.expr.BinaryExpr.Operator.GREATER_EQUALS;
import static org.drools.javaparser.ast.expr.BinaryExpr.Operator.LESS;
import static org.drools.javaparser.ast.expr.BinaryExpr.Operator.LESS_EQUALS;
import static org.drools.javaparser.ast.expr.BinaryExpr.Operator.NOT_EQUALS;
import static org.drools.javaparser.ast.expr.BinaryExpr.Operator.OR;
import static org.drools.modelcompiler.util.ClassUtil.getAccessibleProperties;
import static org.drools.modelcompiler.util.ClassUtil.toRawClass;

public class DrlxParseSuccess implements DrlxParseResult {

    private final Class<?> patternType;
    private Expression expr;
    private final Type exprType;

    private final String exprId;
    private String patternBinding;
    private boolean isPatternBindingUnification = false;

    private String exprBinding;

    private IndexUtil.ConstraintType decodeConstraintType;
    private Collection<String> usedDeclarations = new LinkedHashSet<>();
    private Collection<String> usedDeclarationsOnLeft;
    private Set<String> reactOnProperties = Collections.emptySet();
    private Set<String> watchedProperties = Collections.emptySet();

    private TypedExpression left;
    private TypedExpression right;
    private boolean isStatic;
    private boolean isValidExpression;
    private boolean skipThisAsParam;
    private boolean isBetaNode;
    private boolean requiresSplit;
    private boolean unification;

    public DrlxParseSuccess(Class<?> patternType, String exprId, String patternBinding, Expression expr, Type exprType) {
        this.patternType = patternType;
        this.exprId = exprId;
        this.patternBinding = patternBinding;
        this.expr = expr;
        this.exprType = exprType;
    }

    public DrlxParseSuccess setDecodeConstraintType(IndexUtil.ConstraintType decodeConstraintType ) {
        this.decodeConstraintType = decodeConstraintType;
        return this;
    }

    public DrlxParseSuccess setUsedDeclarationsOnLeft( Collection<String> usedDeclarationsOnLeft ) {
        this.usedDeclarationsOnLeft = usedDeclarationsOnLeft;
        return this;
    }

    public Collection<String> getUsedDeclarationsOnLeft() {
        return usedDeclarationsOnLeft != null ? usedDeclarationsOnLeft : usedDeclarations;
    }

    public DrlxParseSuccess setUsedDeclarations( Collection<String> usedDeclarations ) {
        this.usedDeclarations = new LinkedHashSet<>(usedDeclarations);
        skipThisAsParam = usedDeclarations.contains( patternBinding );
        return this;
    }

    public DrlxParseSuccess setWatchedProperties(Set<String> watchedProperties ) {
        this.watchedProperties = watchedProperties;
        return this;
    }

    public DrlxParseSuccess setReactOnProperties(Set<String> reactOnProperties ) {
        if ( patternType != null ) {
            reactOnProperties.retainAll( getAccessibleProperties( patternType ) );
            this.reactOnProperties = reactOnProperties;
        }
        return this;
    }

    public DrlxParseSuccess setPatternBindingUnification(Boolean unification) {
        this.isPatternBindingUnification = unification;
        return this;
    }

    public DrlxParseSuccess addAllWatchedProperties( Collection<String> watchedProperties) {
        if (watchedProperties.isEmpty()) {
            return this;
        }
        if (this.watchedProperties.isEmpty()) {
            this.watchedProperties = new HashSet<>();
        }
        this.watchedProperties.addAll(watchedProperties);
        return this;
    }

    public DrlxParseSuccess addReactOnProperty(String reactOnProperty ) {
        if ( patternType != null && getAccessibleProperties(patternType).contains( reactOnProperty ) ) {
            if ( reactOnProperties.isEmpty() ) {
                reactOnProperties = new HashSet<>();
            }
            this.reactOnProperties.add( reactOnProperty );
        }
        return this;
    }

    public DrlxParseSuccess setLeft(TypedExpression left ) {
        this.left = left;
        return this;
    }

    public DrlxParseSuccess setRight(TypedExpression right ) {
        this.right = right;
        return this;
    }

    public DrlxParseSuccess setStatic(boolean isStatic ) {
        this.isStatic = isStatic;
        return this;
    }

    public DrlxParseSuccess setSkipThisAsParam(boolean skipThisAsParam ) {
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

    public DrlxParseSuccess setExprBinding(String exprBinding) {
        this.exprBinding = exprBinding;
        return this;
    }

    public boolean hasUnificationVariable() {
        return Optional.ofNullable(left).flatMap(TypedExpression::getUnificationVariable).isPresent() ||
                Optional.ofNullable(right).flatMap(TypedExpression::getUnificationVariable).isPresent();
    }

    public String getUnificationVariable() {
        return left.getUnificationVariable().isPresent() ? left.getUnificationVariable().get() : right.getUnificationVariable().get();
    }

    public String getUnificationName() {
        return left.getUnificationName().isPresent() ? left.getUnificationName().get() : right.getUnificationName().get();
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

    public IndexUtil.ConstraintType getDecodeConstraintType() {
        return decodeConstraintType;
    }

    public Collection<String> getUsedDeclarations() {
        return usedDeclarations;
    }

    public Set<String> getReactOnProperties() {
        return reactOnProperties;
    }

    public Set<String> getWatchedProperties() {
        return watchedProperties;
    }

    public TypedExpression getLeft() {
        return left;
    }

    public TypedExpression getRight() {
        return right;
    }

    public boolean isStatic() {
        return isStatic;
    }

    public boolean isValidExpression( ) {
        if (this.isValidExpression) {
            return true;
        }
        if (expr != null) {
            if ( getExprType() == Boolean.class || getExprType() == boolean.class ) {
                return true;
            }
            if (expr instanceof EnclosedExpr) {
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

    public DrlxParseSuccess setValidExpression(boolean validExpression ) {
        this.isValidExpression = validExpression;
        return this;
    }

    @Override
    public void accept(ParseResultVoidVisitor parseVisitor) {
        parseVisitor.onSuccess(this);
    }

    @Override
    public <T> T acceptWithReturnValue(ParseResultVisitor<T> visitor ) {
        return visitor.onSuccess(this);
    }

    public DrlxParseSuccess setBetaNode(boolean betaNode) {
        this.isBetaNode = betaNode;
        return this;
    }

    public boolean isBetaNode() {
        return isBetaNode;
    }

    public DrlxParseSuccess setRequiresSplit(boolean requiresSplit) {
        this.requiresSplit = requiresSplit;
        return this;
    }

    public boolean isRequiresSplit() {
        return requiresSplit;
    }

    public DrlxParseSuccess setUnification(boolean unification) {
        this.unification = unification;
        return this;
    }

    public boolean isUnification() {
        return unification;
    }

    @Override
    public boolean isSuccess() {
        return true;
    }

    @Override
    public DrlxParseResult combineWith( DrlxParseResult other, BinaryExpr.Operator operator ) {
        if (!other.isSuccess()) {
            return other;
        }

        DrlxParseSuccess otherDrlx = ( DrlxParseSuccess ) other;

        Collection<String> usedDeclarations = new LinkedHashSet<>();
        usedDeclarations.addAll( this.usedDeclarations );
        usedDeclarations.addAll( otherDrlx.usedDeclarations );

        Collection<String> usedDeclarationsOnLeft = null;
        if (this.usedDeclarationsOnLeft != null && otherDrlx.usedDeclarationsOnLeft != null) {
            usedDeclarationsOnLeft = new LinkedHashSet<>();
            usedDeclarationsOnLeft.addAll( this.usedDeclarationsOnLeft );
            usedDeclarationsOnLeft.addAll( otherDrlx.usedDeclarationsOnLeft );
        }

        Set<String> reactOnProperties = new HashSet<>();
        reactOnProperties.addAll( this.reactOnProperties );
        reactOnProperties.addAll( otherDrlx.reactOnProperties );

        return new DrlxParseSuccess(patternType, exprId, patternBinding, new BinaryExpr( expr, otherDrlx.expr, operator), exprType)
                .setDecodeConstraintType( IndexUtil.ConstraintType.UNKNOWN ).setUsedDeclarations( usedDeclarations ).setUsedDeclarationsOnLeft( usedDeclarationsOnLeft )
                .setUnification( this.isUnification() || otherDrlx.isUnification()).setReactOnProperties( reactOnProperties ).setBetaNode(isBetaNode)
                .setLeft( new TypedExpression( this.expr, boolean.class ) )
                .setRight( new TypedExpression( otherDrlx.expr, boolean.class ) );
    }
}
