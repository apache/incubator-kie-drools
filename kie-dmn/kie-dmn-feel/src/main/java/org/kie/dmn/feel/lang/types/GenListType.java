package org.kie.dmn.feel.lang.types;

import java.util.Collection;

import org.kie.dmn.feel.lang.SimpleType;
import org.kie.dmn.feel.lang.Type;

public class GenListType implements SimpleType {

    private final Type gen;

    public GenListType(Type gen) {
        this.gen = gen;
    }

    @Override
    public boolean isInstanceOf(Object o) {
        return o instanceof Collection && ((Collection<?>) o).stream().allMatch(gen::isInstanceOf);
    }

    @Override
    public boolean isAssignableValue(Object value) {
        if ( value == null ) {
            return true; // a null-value can be assigned to any type.
        }
        if (!(value instanceof Collection)) {
            return gen.isAssignableValue(value);
        }
        return isInstanceOf(value);
    }

    @Override
    public String getName() {
        return "[anonymous]";
    }

    public Type getGen() {
        return gen;
    }

    @Override
    public boolean conformsTo(Type t) {
        return (t instanceof GenListType && this.gen.conformsTo(((GenListType) t).gen)) || t == BuiltInType.LIST;
    }

}
