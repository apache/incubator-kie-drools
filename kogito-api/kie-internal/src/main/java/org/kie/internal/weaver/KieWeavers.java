package org.kie.internal.weaver;

import org.kie.api.io.ResourceType;
import org.kie.internal.utils.KieService;

import java.util.Map;

public interface KieWeavers extends KieService {
    public Map<ResourceType, KieWeaverService> getWeavers();
}
