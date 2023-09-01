package org.kie.api.internal.utils;

import org.kie.api.internal.assembler.KieAssemblerService;
import org.kie.api.io.ResourceType;

public class MockChildAssemblerService implements KieAssemblerService {

    @Override
    public ResourceType getResourceType() {
        return ResourceType.DRL;
    }


}
