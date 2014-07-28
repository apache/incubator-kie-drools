package org.kie.internal.runtime.beliefs;

import org.kie.internal.utils.KieService;

public interface KieBeliefService extends KieService {
    public String getBeliefType();

    public Object createBeliefSystem(Object ep,
                                     Object tms);

}
