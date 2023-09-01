package org.kie.dmn.feel.lang.types;

import org.kie.dmn.feel.lang.SimpleType;
import org.kie.dmn.feel.lang.Type;

/**
 * Useful for ItemDefinition at DMN layer redefining as an alias a basic FEEL type.
 */
public class AliasFEELType implements SimpleType {

    private String name;
    private BuiltInType wrapped;

    public AliasFEELType(String name, BuiltInType wrapped) {
        this.name = name;
        this.wrapped = wrapped;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public boolean isInstanceOf(Object o) {
        return wrapped.isInstanceOf(o);
    }

    @Override
    public boolean isAssignableValue(Object value) {
        return wrapped.isAssignableValue(value);
    }

    public BuiltInType getBuiltInType() {
        return wrapped;
    }

    @Override
    public boolean conformsTo(Type t) {
        return this.wrapped.conformsTo(t);
    }
}
