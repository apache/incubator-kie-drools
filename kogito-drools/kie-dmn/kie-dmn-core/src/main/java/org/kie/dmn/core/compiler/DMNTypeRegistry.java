package org.kie.dmn.core.compiler;

import org.kie.dmn.api.core.DMNType;

public interface DMNTypeRegistry {

    DMNType unknown();

    DMNType registerType(DMNType type);

    DMNType resolveType(String namespace, String name);

}
