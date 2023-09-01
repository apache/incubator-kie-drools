package org.kie.efesto.common.api.identifiers.componentroots;

import java.util.HashMap;
import java.util.Map;

import org.kie.efesto.common.api.identifiers.ComponentRoot;
import org.kie.efesto.common.api.identifiers.EfestoComponentRoot;

public class EfestoComponentRootBar implements EfestoComponentRoot {

    private static final Map<Class<? extends ComponentRoot>, ComponentRoot> INSTANCES;

    static {
        INSTANCES = new HashMap<>();
        INSTANCES.put(ComponentFoo.class, new ComponentFoo());
        INSTANCES.put(ComponentRootA.class, new ComponentRootA());
        INSTANCES.put(ComponentRootB.class, new ComponentRootB());
    }

    @Override
    public <T extends ComponentRoot> T get(Class<T> providerId) {
        return (T) INSTANCES.get(providerId);
    }
}
