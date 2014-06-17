package org.kie.internal.weaver;

import org.kie.api.io.ResourceType;

import java.util.HashMap;
import java.util.Map;

public class KieWeaversImpl implements KieWeavers {
    private Map<ResourceType, KieWeaverService> weavers;

    public KieWeaversImpl() {
        weavers = new HashMap<ResourceType, KieWeaverService>();
    }

    public Map<ResourceType, KieWeaverService> getWeavers() {
        return this.weavers;
    }

    @Override
    public Class getServiceInterface() {
        return KieWeavers.class;
    }
}
