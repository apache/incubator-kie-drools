package org.kie.builder;

import org.kie.builder.Message.Level;
import org.kie.io.Resource;

public interface KieBuilder {
    
    KieBuilder setDependencies(KieModule... dependencies);
    
    KieBuilder setDependencies(Resource... dependencies);

    Results build();

    boolean hasResults(Level... levels);
                         
    Results getResults(Level... levels);
    
    Results getResults();

    KieModule getKieModule();
}
