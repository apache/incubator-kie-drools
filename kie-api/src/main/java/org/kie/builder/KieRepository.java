package org.kie.builder;

import org.kie.io.Resource;

public interface KieRepository {
    
    public GAV getDefaultGAV();
    
    void addKieModule(KieModule kjar);
    
    KieModule addKieModule(Resource resource);    

    Results verfyKieModule(GAV gav);
    
    KieModule getKieModule(GAV gav);     
}
