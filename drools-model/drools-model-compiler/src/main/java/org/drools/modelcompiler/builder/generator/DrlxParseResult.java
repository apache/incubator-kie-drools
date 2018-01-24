package org.drools.modelcompiler.builder.generator;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.drools.core.util.index.IndexUtil;
import org.drools.javaparser.ast.expr.Expression;

public class DrlxParseResult {

    private final Class<?> patternType;
    private Expression expr;
    private final Class<?> exprType;

    private String exprId;
    private String patternBinding;
    private boolean isPatternBindingUnification = false;

    private String exprBinding;

    private IndexUtil.ConstraintType decodeConstraintType;
    private Collection<String> usedDeclarations = new LinkedHashSet<>();
    private Set<String> reactOnProperties = Collections.emptySet();
    private String[] watchedProperties;

    private TypedExpression left;
    private TypedExpression right;
    private boolean isStatic;
    private boolean isValidExpression;

    public DrlxParseResult( Class<?> patternType, String exprId, String patternBinding, Expression expr, Class<?> exprType) {
        this.patternType = patternType;
        this.exprId = exprId;
        this.patternBinding = patternBinding;
        this.expr = expr;
        this.exprType = exprType;
    }

    public DrlxParseResult setDecodeConstraintType( IndexUtil.ConstraintType decodeConstraintType ) {
        this.decodeConstraintType = decodeConstraintType;
        return this;
    }

    public DrlxParseResult setUsedDeclarations( List<String> usedDeclarations ) {
        this.usedDeclarations = new LinkedHashSet<>(usedDeclarations);
        return this;
    }

    public DrlxParseResult setReactOnProperties( Set<String> reactOnProperties ) {
        this.reactOnProperties = reactOnProperties;
        return this;
    }

    public DrlxParseResult setPatternBindingUnification( Boolean unification) {
        this.isPatternBindingUnification = unification;
        return this;
    }

    public DrlxParseResult addReactOnProperty( String reactOnProperty ) {
        if (reactOnProperties.isEmpty()) {
            reactOnProperties = new HashSet<>();
        }
        this.reactOnProperties.add(reactOnProperty);
        return this;
    }

    public DrlxParseResult setLeft( TypedExpression left ) {
        this.left = left;
        return this;
    }

    public DrlxParseResult setRight( TypedExpression right ) {
        this.right = right;
        return this;
    }

    public DrlxParseResult setStatic( boolean aStatic ) {
        isStatic = aStatic;
        return this;
    }

    public String getExprId() {
        return exprId;
    }

    public String getPatternBinding() {
        return patternBinding;
    }

    public void setExprId(String exprId) {
        this.exprId = exprId;
    }

    public void setExpr(Expression expr) {
        this.expr = expr;
    }

    public void setPatternBinding(String patternBinding) {
        this.patternBinding = patternBinding;
    }

    public DrlxParseResult setExprBinding(String exprBinding) {
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
        return left.getUnificationVariable().isPresent() ? right.getType() : left.getType();
    }

    public Expression getExpr() {
        return expr;
    }

    public String getExprBinding() {
        return exprBinding;
    }

    public Class<?> getExprType() {
        return exprType;
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

    public String[] getWatchedProperties() {
        return watchedProperties;
    }

    public void setWatchedProperties(String[] watchedProperties) {
        this.watchedProperties = watchedProperties;
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
        return this.isValidExpression || ( expr != null && ( right != null || getExprType() == Boolean.class || getExprType() == boolean.class ) );
    }

    public DrlxParseResult setValidExpression( boolean validExpression ) {
        this.isValidExpression = validExpression;
        return this;
    }
}
