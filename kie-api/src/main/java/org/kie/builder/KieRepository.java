package org.kie.builder;

public interface KieRepository {
    
    public GAV getDefaultGAV();
    
    void addKieModule(KieModule kjar);

    Results verfyKieModule(GAV gav);
    
    KieModule getKieModule(GAV gav);
}
