package org.kie.dmn.core.compiler;

import org.kie.dmn.api.core.DMNType;
import org.kie.dmn.feel.lang.types.FEELTypeRegistry;

public interface DMNTypeRegistry extends FEELTypeRegistry {

    DMNType unknown();

    DMNType registerType(DMNType type);

    DMNType resolveType(String namespace, String name);

}
