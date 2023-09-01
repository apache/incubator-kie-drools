package org.kie.dmn.openapi.impl;

import java.net.URI;

import org.kie.dmn.api.core.DMNType;
import org.kie.dmn.core.impl.BaseDMNTypeImpl;
import org.kie.dmn.feel.codegen.feel11.CodegenStringUtil;
import org.kie.dmn.openapi.NamingPolicy;

public class DefaultNamingPolicy implements NamingPolicy {

    private final String refPrefix;

    public DefaultNamingPolicy(String refPrefix) {
        this.refPrefix = refPrefix;
    }

    @Override
    public String getName(DMNType type) {
        String name = type.getName();
        DMNType belongingType = ((BaseDMNTypeImpl) type).getBelongingType(); // internals for anonymous inner types.
        while (belongingType != null) {
            name = belongingType.getName() + "_" + name;
            belongingType = ((BaseDMNTypeImpl) belongingType).getBelongingType();
        }
        name = CodegenStringUtil.escapeIdentifier(name);
        return name;
    }

    @Override
    public String getRef(DMNType type) {
        String namePart;
        try {
            URI uri = new URI(null, null, getName(type), null);
            namePart = uri.toString();
        } catch (Exception e) {
            namePart = type.getName();
        }
        return refPrefix + namePart;
    }
}
