package org.kie.dmn.feel.lang;

import java.util.Map;

/**
 * A composite type interface, i.e., a type that contains fields
 */
public interface CompositeType
        extends Type {

    Map<String, Type> getFields();

}
