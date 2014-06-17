package org.kie.internal.utils;

import org.kie.api.Service;

public interface KieService extends Service {
//    void preInit(T ctx);
//    void init(T ctx);
//    void postInit(T ctx);

    Class getServiceInterface();

//    ResourceType getResourceType();
//
//    RequiredResourceType[] getRequiredResourceType();
}
