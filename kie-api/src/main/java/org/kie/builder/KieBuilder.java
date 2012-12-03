package org.kie.builder;

import java.util.Collection;

import org.kie.builder.Message.Level;

public interface KieBuilder {
    
    KieBuilder setDependencies(Collection<KieModule> dependencies);

    Results build();

    boolean hasResults(Level... levels);
                         
    Results getResults(Level... levels);
    
    Results getResults();

    KieModule getKieJar();
}
