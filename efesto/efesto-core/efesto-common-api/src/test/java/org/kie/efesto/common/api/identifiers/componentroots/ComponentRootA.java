package org.kie.efesto.common.api.identifiers.componentroots;

import org.kie.efesto.common.api.identifiers.ComponentRoot;

public class ComponentRootA implements ComponentRoot {

    public LocalComponentIdA get(String fileName, String name) {
        return new LocalComponentIdA(fileName, name);
    }

}
