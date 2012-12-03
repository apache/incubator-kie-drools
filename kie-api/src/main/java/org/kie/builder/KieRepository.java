package org.kie.builder;

public interface KieRepository {
    
    public void setDefaultGAV(GAV gav);

    public GAV getDefaultGAV();    
    
    void addKieJar(KieModule kjar);

    Results verfyKieJar(GAV gav);
    
    KieModule getKieJar(GAV gav);
}
