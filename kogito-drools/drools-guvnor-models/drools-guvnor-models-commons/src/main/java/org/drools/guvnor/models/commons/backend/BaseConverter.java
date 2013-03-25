package org.drools.guvnor.models.commons.backend;

import org.drools.compiler.kie.builder.impl.FormatConverter;

public abstract class BaseConverter implements FormatConverter {

    protected String getDestinationName(String name) {
        return getDestinationName(name, false);
    }

    protected String getDestinationName(String name, boolean hasDsl) {
        return name.substring(0, name.lastIndexOf('.')) + (hasDsl ? ".dslr" : ".drl");
    }
}
