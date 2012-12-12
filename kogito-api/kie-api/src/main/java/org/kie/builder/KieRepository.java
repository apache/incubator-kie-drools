package org.kie.builder;

import org.kie.io.Resource;

public interface KieRepository {
    
    public ReleaseId getDefaultReleaseId();
    
    void addKieModule(KieModule kModule);
    
    KieModule addKieModule(Resource resource, Resource... dependencies);    
    
    KieModule getKieModule(ReleaseId releaseId);
}
