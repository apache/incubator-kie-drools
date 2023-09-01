package org.kie.drl.api.identifiers;

import java.util.Map;

import org.kie.efesto.common.api.identifiers.ComponentRoot;
import org.kie.efesto.common.api.identifiers.EfestoComponentRoot;

import static org.kie.efesto.common.api.utils.EfestoAppRootHelper.getComponentRootBySPI;

public class KieDrlComponentRoot implements EfestoComponentRoot {

    private static final Map<Class<? extends ComponentRoot>, ComponentRoot> INSTANCES;

    static {
        INSTANCES = getComponentRootBySPI(DrlComponentRoot.class);
    }

    @Override
    public <T extends ComponentRoot> T get(Class<T> providerId) {
        return (T) INSTANCES.get(providerId);
    }

}
