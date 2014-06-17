package org.kie.internal.assembler;

import org.kie.api.io.ResourceType;
import org.kie.internal.utils.KieService;

import java.util.Map;

public interface KieAssemblers extends KieService {
    Map<ResourceType, KieAssemblerService> getAssemblers();
}
