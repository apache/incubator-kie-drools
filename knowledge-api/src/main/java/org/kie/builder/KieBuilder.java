package org.kie.builder;

import org.kie.builder.Message.Level;

public interface KieBuilder {

    Results build();

    boolean hasResults(Level... levels);
                         
    Results getResults(Level... levels);
    
    Results getResults();

    KieJar getKieJar();
}
