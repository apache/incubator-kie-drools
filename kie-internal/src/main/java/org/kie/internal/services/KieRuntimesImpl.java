package org.kie.internal.services;

import org.kie.api.internal.runtime.KieRuntimeService;
import org.kie.api.internal.runtime.KieRuntimes;

public class KieRuntimesImpl extends AbstractMultiService<Class<?>, KieRuntimeService> implements KieRuntimes {

    @Override
    public KieRuntimeService getRuntime(Class<?> clazz) {
        return getService(clazz);
    }


    @Override
    protected Class<KieRuntimeService> serviceClass() {
        return KieRuntimeService.class;
    }

    @Override
    protected Class<?> serviceKey(KieRuntimeService service) {
        return service.getServiceInterface();
    }
}
