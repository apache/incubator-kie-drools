package org.kie.dmn.feel.lang.types;

import org.kie.dmn.feel.lang.Scope;
import org.kie.dmn.feel.lang.Type;

public class BuiltInTypeSymbol
        extends BaseSymbol
        implements Type {

    public BuiltInTypeSymbol(String id, Type type) {
        super( id, type );
    }

    public BuiltInTypeSymbol(String id, Type type, Scope scope) {
        super( id, type, scope );
    }

    public String getName() {
        return getId();
    }

    @Override
    public boolean isInstanceOf(Object o) {
        return getType().isInstanceOf(o);
    }

    @Override
    public boolean isAssignableValue(Object value) {
        return getType().isAssignableValue(value);
    }

    @Override
    public boolean conformsTo(Type t) {
        return getType().conformsTo(t);
    }
}
