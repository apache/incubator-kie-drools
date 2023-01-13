package org.drools.core.common;

import org.kie.api.internal.utils.KieService;

public interface ObjectStoreFactory {

    static ObjectStore getObjectStoreFactory() {
        return KieService.load( ObjectStore.class );
    }

}
