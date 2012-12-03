package org.kie.builder;

public interface KieRepository {
    
    //public void setDefaultGAV(GAV gav);

    public GAV getDefaultGAV();    
    
    void addKieModule(KieModule kjar);

    Results verfyKieModule(GAV gav);
    
    KieModule getKieModule(GAV gav);
}
