package org.kie.builder;

import org.kie.io.Resource;

public interface KieRepository {
    
    public GAV getDefaultGAV();
    
    void addKieModule(KieModule kModule);
    
    KieModule addKieModule(Resource resource, Resource... dependencies);    

    Results verfyKieModule(GAV gav);
    
    KieModule getKieModule(GAV gav);     
}
