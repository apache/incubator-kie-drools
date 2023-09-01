package org.kie.efesto.common.core.identifiers.componentroots;

import org.kie.efesto.common.api.identifiers.ComponentRoot;

public class ComponentRootB implements ComponentRoot {

    public LocalComponentIdB get(String fileName, String name, String secondName) {
        return new LocalComponentIdB(fileName, name, secondName);
    }

}
