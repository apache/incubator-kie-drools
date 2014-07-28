package org.kie.internal.runtime.beliefs;

import org.kie.api.io.ResourceType;
import org.kie.internal.assembler.KieAssemblerService;
import org.kie.internal.utils.KieService;

import java.util.Map;

public interface KieBeliefs extends KieService {
    Map<String, KieBeliefService> getBeliefs();


    public KieBeliefService[] getServices();

}
