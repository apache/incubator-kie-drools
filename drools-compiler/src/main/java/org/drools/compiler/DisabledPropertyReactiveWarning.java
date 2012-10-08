package org.drools.compiler;

import org.drools.io.Resource;

public class DisabledPropertyReactiveWarning extends DroolsWarning {

    private final String typeName;

    public DisabledPropertyReactiveWarning(Resource resource, String typeName) {
        super(resource);
        this.typeName = typeName;
    }

    @Override
    public String getMessage() {
        return "Property Reactive cannot be supported on type " + typeName + " since it has 64 or more properties: disabling it";
    }

    @Override
    public int[] getLines() {
        return new int[0];
    }
}
