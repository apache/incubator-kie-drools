package org.kie.builder;

import org.kie.io.Resource;

public interface KieBuilder {
    
    KieBuilder setDependencies(KieModule... dependencies);
    
    KieBuilder setDependencies(Resource... dependencies);

    KieBuilder buildAll();
    
    Results getResults();

    KieModule getKieModule();
}
