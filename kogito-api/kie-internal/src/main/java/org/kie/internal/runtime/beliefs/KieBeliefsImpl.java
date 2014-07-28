package org.kie.internal.runtime.beliefs;

import java.util.HashMap;
import java.util.Map;

public class KieBeliefsImpl implements KieBeliefs{
    private KieBeliefService[] services;

    private Map<String, KieBeliefService> beliefs;

    public KieBeliefsImpl() {
        beliefs = new HashMap<String, KieBeliefService>();
    }

    public Map<String, KieBeliefService> getBeliefs() {
        return this.beliefs;
    }

    public Class getServiceInterface() {
        return KieBeliefService.class;
    }

    public KieBeliefService[] getServices() {
        if ( services == null ) {
            synchronized ( beliefs )  {
                if ( services != null ) {
                    return services;
                }
                int size = beliefs.size();
                services = new KieBeliefService[ size ];
                int i = 0;
                for ( KieBeliefService service : beliefs.values() ) {
                    services[i++] = service;
                }
            }
        }
        return services;
    }
}
