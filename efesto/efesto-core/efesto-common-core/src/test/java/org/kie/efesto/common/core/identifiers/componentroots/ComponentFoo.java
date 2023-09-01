package org.kie.efesto.common.core.identifiers.componentroots;

import org.kie.efesto.common.api.identifiers.ComponentRoot;

public class ComponentFoo implements ComponentRoot {

    public LocalComponentIdFoo get(String fileName, String name, String secondName) {
        return new LocalComponentIdFoo(fileName, name, secondName);
    }

}
