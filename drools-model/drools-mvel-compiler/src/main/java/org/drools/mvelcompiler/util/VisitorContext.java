package org.drools.mvelcompiler.util;

import org.drools.mvelcompiler.ast.TypedExpression;

import java.lang.reflect.Type;
import java.util.Optional;

import static java.util.Optional.ofNullable;

public class VisitorContext {
    private final Optional<TypedExpression> scope;

    public VisitorContext(final TypedExpression scope) {
        this.scope = ofNullable(scope);
    }

    public Optional<Type> getScopeType() {
        return scope.flatMap(TypedExpression::getType);
    }

    public Optional<TypedExpression> getScope() {
        return scope;
    }
}
