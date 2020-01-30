package org.kie.dmn.feel.lang.types;

import java.util.List;
import java.util.stream.Stream;

import org.kie.dmn.feel.lang.Scope;
import org.kie.dmn.feel.lang.Type;

public enum DefaultBuiltinFEELTypeRegistry implements FEELTypeRegistry {
    INSTANCE;

    private static final ScopeImpl BUILTIN_TYPE_SCOPE;
    static {
        BUILTIN_TYPE_SCOPE = new ScopeImpl("typeScope", null); // null intentional 
        Stream.of(BuiltInType.values()).flatMap(b -> b.getSymbols().stream()).forEach(t -> BUILTIN_TYPE_SCOPE.define(t));
    }

    @Override
    public Type resolveFEELType(List<String> qns) {
        if (qns.size() == 1) {
            return BUILTIN_TYPE_SCOPE.resolve(qns.get(0)).getType();
        } else {
            throw new IllegalStateException("Inconsistent state when resolving for qns: " + qns.toString());
        }
    }

    @Override
    public Scope getItemDefScope(Scope parent) {
        return new WrappingScopeImpl(BUILTIN_TYPE_SCOPE, parent);
    }
}
