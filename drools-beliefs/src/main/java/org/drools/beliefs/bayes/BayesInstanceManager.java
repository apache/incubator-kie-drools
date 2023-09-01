package org.drools.beliefs.bayes;

import org.kie.api.KieBase;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class BayesInstanceManager {
    private KieBase kBase;

    private ConcurrentHashMap<String, BayesInstance> instances;

    public BayesInstanceManager() {
        instances  = new ConcurrentHashMap<>();
    }

    public Map<String, BayesInstance> getInstances() {
        return instances;
    }

    public BayesInstance getBayesInstance( String pkgName, String name ) {

//        instances.

        return null;
    }


}
