package org.kie.dmn.feel.lang;

import java.util.Map;

import org.kie.dmn.feel.lang.types.BuiltInType;

/**
 * A composite type interface, i.e., a type that contains fields
 */
public interface CompositeType
        extends Type {

    Map<String, Type> getFields();

    @Override
    default boolean conformsTo(Type t) {
        if (t instanceof CompositeType) {
            CompositeType ct = (CompositeType) t;
            return ct.getFields().entrySet().stream().allMatch(tField -> this.getFields().containsKey(tField.getKey()) && this.getFields().get(tField.getKey()).conformsTo(tField.getValue()));
        } else {
            return t == BuiltInType.CONTEXT;
        }
    }
}
